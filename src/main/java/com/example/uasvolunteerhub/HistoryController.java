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

        String query = """
            SELECT a.id, a.title, a.date, a.location, v.type AS type
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
                            ", Type: " + debugRs.getString("type"));
                }
            }

            // Debug: Cek data di tabel activity
            String activityQuery = "SELECT * FROM activity";
            try (PreparedStatement activityStmt = conn.prepareStatement(activityQuery)) {
                ResultSet activityRs = activityStmt.executeQuery();

                System.out.println("=== Data di tabel activity ===");
                while (activityRs.next()) {
                    System.out.println("Activity ID: " + activityRs.getInt("id") +
                            ", Title: " + activityRs.getString("title") +
                            ", Date: " + activityRs.getString("date"));
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
                    String historyType = rs.getString("type");

                    System.out.println("ID: " + id);
                    System.out.println("Title: " + title);
                    System.out.println("Date String: " + dateStr);
                    System.out.println("Location: " + location);
                    System.out.println("Type: " + historyType);

                    try {
                        LocalDate date = LocalDate.parse(dateStr);
                        HBox card = createCard(id, date, title, location, historyType);
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

                    // Tambahkan pesan jika tidak ada data
                    Text noDataText = new Text("Belum ada history aktivitas");
                    noDataText.setFont(Font.font("Arial", 14));
                    historyContainer.getChildren().add(noDataText);
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=== DEBUG: Selesai loadVolunteerHistory ===");
    }

    private HBox createCard(int activityId, LocalDate date, String title, String location, String historyType) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #B2D3C2;");
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(card, Priority.ALWAYS);

        VBox dateBox = new VBox();
        Text day = new Text(String.valueOf(date.getDayOfMonth()));
        day.setFont(Font.font("Arial", 16));
        Text monthYear = new Text(date.format(DateTimeFormatter.ofPattern("MMM, yyyy")));
        monthYear.setFont(Font.font("Arial", 11));
        dateBox.getChildren().addAll(day, monthYear);
        dateBox.setPrefWidth(60);

        VBox detailBox = new VBox(3);
        Text titleText = new Text(title);
        titleText.setFont(Font.font("Arial", 15));
        Text locationText = new Text("Location: " + location);
        locationText.setFont(Font.font("Arial", 12));
        Text typeText = new Text("Type: " + historyType);
        typeText.setFont(Font.font("Arial", 12));
        detailBox.getChildren().addAll(titleText, locationText, typeText);
        detailBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(detailBox, Priority.ALWAYS);

        Button detailBtn = new Button("Detail");
        detailBtn.setPrefWidth(100);
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