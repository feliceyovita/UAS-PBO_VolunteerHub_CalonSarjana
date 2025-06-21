package com.example.uasvolunteerhub;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VolunteerDashboardController {

    @FXML private Button accountBtn;
    @FXML private Button recommendationBtn;
    @FXML private Button historyBtn;
    @FXML private Button logoutBtn;
    @FXML private TextField searchField;
    @FXML private FlowPane activitiesGrid; // Tipe sudah benar (FlowPane)
    @FXML private Label totalActivitiesLabel;
    @FXML private Label totalVolunteersLabel;

    // ... (Kelas Activity dan variabel koneksi DB tidak berubah)
    public static class Activity {
        private int id;
        private String title;
        private String date;
        private String benefits;
        private String location;
        private String contact;
        private int slot;
        private String description;
        private String typeOfVolunteer;
        private double donationAmount;
        private String image;

        public Activity(int id, String title, String date, String benefits, String location,
                        String contact, int slot, String description, String typeOfVolunteer,
                        double donationAmount, String image) {
            this.id = id;
            this.title = title;
            this.date = date;
            this.benefits = benefits;
            this.location = location;
            this.contact = contact;
            this.slot = slot;
            this.description = description;
            this.typeOfVolunteer = typeOfVolunteer;
            this.donationAmount = donationAmount;
            this.image = image;
        }

        // Getters
        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getDate() { return date; }
        public String getBenefits() { return benefits; }
        public String getLocation() { return location; }
        public String getContact() { return contact; }
        public int getSlot() { return slot; }
        public String getDescription() { return description; }
        public String getTypeOfVolunteer() { return typeOfVolunteer; }
        public double getDonationAmount() { return donationAmount; }
        public String getImage() { return image; }
    }


    @FXML
    private void initialize() {
        if(totalActivitiesLabel != null) { // Tambahkan null check untuk keamanan
            updateStatistics();
        }
        loadActivitiesFromDatabase();
    }

    private void loadActivitiesFromDatabase() {
        loadActivitiesFromDatabase("");
    }

    private void loadActivitiesFromDatabase(String searchQuery) {
        List<Activity> activities = getActivitiesFromDatabase(searchQuery);
        displayActivities(activities);
    }

    private List<Activity> getActivitiesFromDatabase(String searchQuery) {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT * FROM activity";

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql += " WHERE title LIKE ? OR location LIKE ? OR description LIKE ? OR type_of_volunteer LIKE ?";
        }

        // Gunakan try-with-resources dengan Database.getConnection() untuk konsistensi
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String searchPattern = "%" + searchQuery + "%";
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
                stmt.setString(3, searchPattern);
                stmt.setString(4, searchPattern);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                activities.add(new Activity(
                        rs.getInt("id"), rs.getString("title"), rs.getString("date"),
                        rs.getString("benefits"), rs.getString("location"), rs.getString("contact"),
                        rs.getInt("slot"), rs.getString("description"), rs.getString("type_of_volunteer"),
                        rs.getDouble("donation_amount"), rs.getString("image")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Gagal memuat aktivitas dari database: " + e.getMessage());
        }
        return activities;
    }

    private void displayActivities(List<Activity> activities) {
        activitiesGrid.getChildren().clear();

        if (activities.isEmpty()){
            activitiesGrid.getChildren().add(new Label("Aktivitas tidak ditemukan."));
            return;
        }

        // Logika baris dan kolom dihapus, FlowPane menanganinya secara otomatis
        for (Activity activity : activities) {
            VBox activityCard = createActivityCard(activity);
            // Cukup tambahkan kartu, FlowPane akan mengaturnya
            activitiesGrid.getChildren().add(activityCard);
        }
    }

    private VBox createActivityCard(Activity activity) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        // Hapus prefWidth agar ukurannya fleksibel di dalam FlowPane
        // card.setPrefWidth(220);
        card.setMinWidth(220); // Beri ukuran minimum agar tidak terlalu kecil
        card.setMaxWidth(300); // Beri ukuran maksimum

        ImageView imageView = new ImageView();
        imageView.setFitHeight(120);
        // Ikat lebar gambar ke lebar kartu agar responsif
        imageView.fitWidthProperty().bind(card.widthProperty());
        imageView.setPreserveRatio(false);
        loadActivityImage(imageView, activity);

        VBox content = new VBox(6);
        content.setPadding(new Insets(12));

        Label titleLabel = new Label(activity.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #666666;");
        titleLabel.setWrapText(true);

        Label locationLabel = new Label(activity.getLocation());
        locationLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 10px;");
        locationLabel.setWrapText(true);

        VBox details = new VBox(2);
        details.getChildren().addAll(
                new Label("• Slots: " + activity.getSlot()),
                new Label("• Purpose: " + activity.getTypeOfVolunteer()),
                new Label("• Date: " + activity.getDate())
        );
        details.getChildren().forEach(node -> node.setStyle("-fx-font-size: 10px; -fx-text-fill: #666666;"));

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        Button detailsBtn = new Button("Details");
        detailsBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 10;");
        detailsBtn.setOnAction(e -> handleActivityDetails(e, activity));
        buttonBox.getChildren().add(detailsBtn);

        VBox.setVgrow(content, Priority.ALWAYS); // Pastikan konten mengisi ruang vertikal
        content.getChildren().addAll(titleLabel, locationLabel, details, buttonBox);
        card.getChildren().addAll(imageView, content);
        return card;
    }

    private void loadActivityImage(ImageView imageView, Activity activity) {
        String imagePath = activity.getImage();
        if (imagePath == null || imagePath.trim().isEmpty()) {
            loadDefaultImage(imageView);
            return;
        }
        try {
            String resourcePath = "/ImgActivity/" + new File(imagePath).getName();
            InputStream imageStream = getClass().getResourceAsStream(resourcePath);
            if (imageStream != null) {
                imageView.setImage(new Image(imageStream));
            } else {
                loadDefaultImage(imageView);
            }
        } catch (Exception e) {
            loadDefaultImage(imageView);
        }
    }

    private void loadDefaultImage(ImageView imageView) {
        try {
            InputStream defaultStream = getClass().getResourceAsStream("/ImgActivity/default-placeholder.png");
            if (defaultStream != null) imageView.setImage(new Image(defaultStream));
            else imageView.setStyle("-fx-background-color: #E0E0E0;");
        } catch (Exception e) {
            imageView.setStyle("-fx-background-color: #E0E0E0;");
        }
    }

    private void handleActivityDetails(ActionEvent event, Activity activity) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/uasvolunteerhub/activity-detail.fxml"));
            Parent root = loader.load();
            ActivityDetailController controller = loader.getController();
            controller.setActivityId(activity.getId());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Activity Details - " + activity.getTitle());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Gagal memuat halaman detail.");
        }
    }

    private void updateStatistics() {
        try (Connection conn = Database.getConnection()) {
            String activityCountSql = "SELECT COUNT(*) FROM activity";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(activityCountSql)) {
                if (rs.next()) {
                    if (totalActivitiesLabel != null) totalActivitiesLabel.setText("Total Activities: " + rs.getInt(1));
                }
            }
            String volunteerCountSql = "SELECT COUNT(DISTINCT id_user) FROM volunteer";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(volunteerCountSql)) {
                if (rs.next()) {
                    if (totalVolunteersLabel != null) totalVolunteersLabel.setText("Total Volunteers: " + rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML private void handleLogout(ActionEvent event) { NavigationUtil.logout(event); }
    @FXML public void handleAccount(ActionEvent event) { NavigationUtil.goTo(event, "/com/example/uasvolunteerhub/account-Volunteer.fxml", "Profile Account"); }
    @FXML private void handleRecommendation(ActionEvent event) { /* Anda sudah di halaman ini */ }
    @FXML private void handleHistory(ActionEvent event) { NavigationUtil.goTo(event, "/com/example/uasvolunteerhub/History.fxml", "Activity History"); }
    @FXML private void handleSearch() { loadActivitiesFromDatabase(searchField.getText()); }
}