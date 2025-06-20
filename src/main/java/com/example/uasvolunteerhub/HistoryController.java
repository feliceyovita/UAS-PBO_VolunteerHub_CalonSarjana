package com.example.uasvolunteerhub;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HistoryController implements Initializable {

    @FXML
    private VBox historyContainer;

    @FXML
    private ScrollPane scrollPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadVolunteerHistory();

        // Gunakan runLater agar lookup dipanggil setelah scene sudah dimuat
        javafx.application.Platform.runLater(() -> {
            if (scrollPane != null) {
                scrollPane.setStyle("-fx-background-color:transparent;");
                Node viewport = scrollPane.lookup(".viewport");
                if (viewport != null) {
                    viewport.setStyle("-fx-background-color:transparent;");
                }
            }
        });
    }

    private void loadVolunteerHistory() {
        historyContainer.getChildren().clear();
        System.out.println("=== DEBUG: Memulai loadVolunteerHistory ===");
        System.out.println("Current User ID: " + Session.currentUserId);

        // Cek apakah currentUserId valid
        if (Session.currentUserId <= 0) {
            System.out.println("ERROR: currentUserId tidak valid!");
            return;
        }

        // Query tanpa kolom 'type' karena tidak ada di tabel volunteer
        String query = """
            SELECT a.id, a.title, a.date, a.location, a.type_of_volunteer
            FROM activity a
            JOIN volunteer v ON a.id = v.id_activity
            WHERE v.id_user = ?
            ORDER BY a.date DESC
        """;

        try (Connection conn = Database.getConnection()) {
            System.out.println("Database connection: " + (conn != null ? "SUCCESS" : "FAILED"));

            // Debug: Cek data di tabel volunteer untuk user ini
            String debugQuery = "SELECT * FROM volunteer WHERE id_user = ?";
            try (PreparedStatement debugStmt = conn.prepareStatement(debugQuery)) {
                debugStmt.setInt(1, Session.currentUserId);
                ResultSet debugRs = debugStmt.executeQuery();

                System.out.println("=== Data di tabel volunteer untuk user " + Session.currentUserId + " ===");
                while (debugRs.next()) {
                    System.out.println("Volunteer ID: " + debugRs.getInt("id") +
                            ", Activity ID: " + debugRs.getInt("id_activity") +
                            ", Name: " + debugRs.getString("name"));
                }
            }

            // Eksekusi query utama
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                System.out.println("Session.currentUserId = " + Session.currentUserId);
                stmt.setInt(1, Session.currentUserId);
                System.out.println("Executing query: " + query);
                System.out.println("With parameter: " + Session.currentUserId);

                ResultSet rs = stmt.executeQuery();

                if (!rs.isBeforeFirst()) {
                    System.out.println("❌ ResultSet kosong: tidak ada data ditemukan untuk user ID: " + Session.currentUserId);
                    showNoDataMessage();
                } else {
                    System.out.println("✅ ResultSet punya data!");
                }

                boolean hasData = false;
                while (rs.next()) {
                    hasData = true;
                    System.out.println("=== Found history record ===");

                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    String dateStr = rs.getString("date");
                    String location = rs.getString("location");
                    String volunteerType = rs.getString("type_of_volunteer"); // Ambil dari tabel activity

                    System.out.println("ID: " + id);
                    System.out.println("Title: " + title);
                    System.out.println("Date String: " + dateStr);
                    System.out.println("Location: " + location);
                    System.out.println("Volunteer Type: " + volunteerType);

                    try {
                        LocalDate date = LocalDate.parse(dateStr);
                        HBox card = createCard(id, date, title, location, volunteerType);
                        Region line = new Region();
                        line.setPrefHeight(1);
                        line.setStyle("-fx-background-color: #5c8f5c;");
                        line.setMaxWidth(Double.MAX_VALUE);
                        VBox.setMargin(line, new Insets(0, 0, 5, 0));
                        historyContainer.getChildren().addAll(card, line);
                    } catch (Exception dateException) {
                        System.out.println("ERROR parsing date: " + dateStr);
                        System.out.println("Date parse error: " + dateException.getMessage());
                    }
                }

                if (!hasData) {
                    System.out.println("WARNING: No history data found for user " + Session.currentUserId);
                    showNoDataMessage();
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL ERROR: " + e.getMessage());
            e.printStackTrace();
            showNoDataMessage();
        }

        System.out.println("=== DEBUG: Selesai loadVolunteerHistory ===");
    }

    private void showNoDataMessage() {
        historyContainer.getChildren().clear(); // Hapus semua dulu biar tidak dobel

        VBox noDataBox = new VBox(10);
        noDataBox.setAlignment(Pos.CENTER);
        noDataBox.setPadding(new Insets(50));
        VBox.setVgrow(noDataBox, Priority.ALWAYS); // Agar bisa "mengisi" ruang kosong

        Text noDataText = new Text("Anda belum melakukan volunteer");
        noDataText.setFont(Font.font("Arial", 18));
        noDataText.setStyle("-fx-fill: #216516; -fx-font-weight: bold;");

        Text suggestionText = new Text("Silakan daftar di aktivitas volunteer yang tersedia");
        suggestionText.setFont(Font.font("Arial", 14));
        suggestionText.setStyle("-fx-fill: #666666;");

        Button goToDashboardBtn = new Button("Lihat Aktivitas Volunteer");
        goToDashboardBtn.setStyle("-fx-background-color: #216516; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 10 20;");
        goToDashboardBtn.setCursor(javafx.scene.Cursor.HAND);
        goToDashboardBtn.setOnAction(e -> {
            try {
                NavigationUtil.goTo(e, "/com/example/uasvolunteerhub/Volunteer-dashboard-view.fxml", "Volunteer Dashboard");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        noDataBox.getChildren().addAll(noDataText, suggestionText, goToDashboardBtn);
        historyContainer.getChildren().add(noDataBox);
    }

    private HBox createCard(int activityId, LocalDate date, String title, String location, String volunteerType) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(card, Priority.ALWAYS);

        // Date Box
        VBox dateBox = new VBox();
        dateBox.setAlignment(Pos.CENTER);
        dateBox.setStyle("-fx-background-color: #216516; -fx-background-radius: 8; -fx-padding: 10;");
        Text day = new Text(String.valueOf(date.getDayOfMonth()));
        day.setFont(Font.font("Arial", 20));
        day.setStyle("-fx-fill: white; -fx-font-weight: bold;");
        Text monthYear = new Text(date.format(DateTimeFormatter.ofPattern("MMM yyyy")));
        monthYear.setFont(Font.font("Arial", 12));
        monthYear.setStyle("-fx-fill: white;");
        dateBox.getChildren().addAll(day, monthYear);
        dateBox.setPrefWidth(70);

        // Detail Box
        VBox detailBox = new VBox(5);
        String modifiedTitle = title;
        if ("donate".equalsIgnoreCase(volunteerType)) {
            modifiedTitle += " (donation)";
        }
        Text titleText = new Text(modifiedTitle);

        titleText.setFont(Font.font("Arial", 16));
        titleText.setStyle("-fx-fill: #216516; -fx-font-weight: bold;");

        Text locationText = new Text("📍 " + location);
        locationText.setFont(Font.font("Arial", 13));
        locationText.setStyle("-fx-fill: #666666;");

        Text typeText = new Text("🎯 " + (volunteerType.equals("donate") ? "Donation Required" : "No Donation"));
        typeText.setFont(Font.font("Arial", 13));
        typeText.setStyle("-fx-fill: #666666;");

        detailBox.getChildren().addAll(titleText, locationText, typeText);
        detailBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(detailBox, Priority.ALWAYS);

        // Detail Button
        Button detailBtn = new Button("Detail");
        detailBtn.setPrefWidth(80);
        detailBtn.setStyle("-fx-background-color: #216516; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-weight: bold;");
        detailBtn.setCursor(javafx.scene.Cursor.HAND);
        detailBtn.setOnAction(e -> openActivityDetail(activityId));

        card.getChildren().addAll(dateBox, detailBox, detailBtn);
        return card;
    }

    private void openActivityDetail(int activityId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/uasvolunteerhub/activity-detail.fxml"));
            Parent root = loader.load();

            ActivityDetailController controller = loader.getController();
            controller.setActivityId(activityId);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Activity Details");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        NavigationUtil.logout(event);
    }

    @FXML
    public void handleAccount(ActionEvent event) {
        NavigationUtil.goTo(event, "/com/example/uasvolunteerhub/account-Volunteer.fxml", "Profile Account");
    }

    @FXML
    private void handleRecommendation(ActionEvent event) {
        NavigationUtil.goTo(event, "/com/example/uasvolunteerhub/Volunteer-dashboard-view.fxml", "Volunteer Recommendation");
    }

    @FXML
    private void handleHistory(ActionEvent event) {
        NavigationUtil.goTo(event, "/com/example/uasvolunteerhub/History.fxml", "Activity History");
    }
}