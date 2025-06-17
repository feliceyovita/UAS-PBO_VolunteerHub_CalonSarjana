package com.example.uasvolunteerhub;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
import com.example.uasvolunteerhub.NavigationUtil;


public class VolunteerDashboardController {

    @FXML
    private Button accountBtn;

    @FXML
    private Button recommendationBtn;

    @FXML
    private Button historyBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private TextField searchField;

    @FXML
    private GridPane activitiesGrid;

    @FXML
    private Label totalActivitiesLabel;

    @FXML
    private Label totalVolunteersLabel;

    // Database connection details - sesuaikan dengan konfigurasi database Anda
    private static final String DB_URL = "jdbc:mysql://localhost:3306/volunteerhub";
    private static final String DB_USER = "root"; // sesuaikan dengan username database Anda
    private static final String DB_PASSWORD = ""; // sesuaikan dengan password database Anda

    // Static variable to store selected activity for detail view
    private static Activity selectedActivity;

    // Activity data model
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

        // Constructor
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

    // Static method to get selected activity
    public static Activity getSelectedActivity() {
        return selectedActivity;
    }

    @FXML
    private void initialize() {
        // Load activities from database when the dashboard initializes
        loadActivitiesFromDatabase();
        updateStatistics();
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

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
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
                Activity activity = new Activity(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("date"),
                        rs.getString("benefits"),
                        rs.getString("location"),
                        rs.getString("contact"),
                        rs.getInt("slot"),
                        rs.getString("description"),
                        rs.getString("type_of_volunteer"),
                        rs.getDouble("donation_amount"),
                        rs.getString("image")
                );
                activities.add(activity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load activities from database: " + e.getMessage());
        }

        return activities;
    }

    private void displayActivities(List<Activity> activities) {
        activitiesGrid.getChildren().clear();

        int column = 0;
        int row = 0;
        int maxColumns = 3; // Jumlah kolom maksimal per baris

        for (Activity activity : activities) {
            VBox activityCard = createActivityCard(activity);
            activitiesGrid.add(activityCard, column, row);

            column++;
            if (column >= maxColumns) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createActivityCard(Activity activity) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefWidth(220);

        // Image
        ImageView imageView = new ImageView();
        imageView.setFitHeight(120);
        imageView.setFitWidth(220);
        imageView.setPreserveRatio(false);

        // Load image dengan perbaikan
        loadActivityImage(imageView, activity);

        // Content VBox
        VBox content = new VBox(6);
        content.setPadding(new Insets(12));

        // Title
        Label titleLabel = new Label(activity.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #666666;");
        titleLabel.setPrefWidth(185);
        titleLabel.setMaxHeight(20);

        // Location
        Label locationLabel = new Label(activity.getLocation());
        locationLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 10px;");

        // Details VBox
        VBox details = new VBox(2);

        Label slotsLabel = new Label("• Slots: " + activity.getSlot());
        slotsLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666666;");

        Label purposeLabel = new Label("• Purpose: " + activity.getTypeOfVolunteer());
        purposeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666666;");

        Label dateLabel = new Label("• Date: " + activity.getDate());
        dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666666;");

        Label benefitsLabel = new Label("• Benefits: " + activity.getBenefits());
        benefitsLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666666;");

        String donationText = activity.getDonationAmount() > 0 ?
                "• Donation: $" + String.format("%.2f", activity.getDonationAmount()) :
                "• No donation required";
        Label donationLabel = new Label(donationText);
        donationLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666666;");

        details.getChildren().addAll(slotsLabel, purposeLabel, dateLabel, benefitsLabel, donationLabel);

        // Details Button
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        Button detailsBtn = new Button("Details");
        detailsBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-font-weight: bold; " +
                "-fx-font-size: 11px; -fx-padding: 5 10;");

        // Add click handler for details button
        detailsBtn.setOnAction(e -> handleActivityDetails(e, activity));

        buttonBox.getChildren().add(detailsBtn);

        content.getChildren().addAll(titleLabel, locationLabel, details, buttonBox);
        card.getChildren().addAll(imageView, content);

        return card;
    }

    /**
     * Method terpisah untuk loading image dengan error handling yang lebih baik
     */
    private void loadActivityImage(ImageView imageView, Activity activity) {
        String imagePath = activity.getImage();

        if (imagePath == null || imagePath.trim().isEmpty()) {
            loadDefaultImage(imageView);
            return;
        }

        try {
            // Coba load dari resources dengan path yang benar
            String resourcePath = "/ImgActivity/" + imagePath;
            InputStream imageStream = getClass().getResourceAsStream(resourcePath);

            if (imageStream != null) {
                Image image = new Image(imageStream);
                if (!image.isError()) {
                    imageView.setImage(image);
                    System.out.println("Successfully loaded image: " + resourcePath);
                    return;
                }
            }

            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                if (!image.isError()) {
                    imageView.setImage(image);
                    System.out.println("Successfully loaded image from file: " + imagePath);
                    return;
                }
            }

            loadDefaultImage(imageView);
            System.out.println("Image not found, using default for: " + imagePath);

        } catch (Exception e) {
            System.err.println("Error loading image for activity '" + activity.getTitle() + "': " + e.getMessage());
            loadDefaultImage(imageView);
        }
    }

    private void loadDefaultImage(ImageView imageView) {
        try {
            InputStream defaultStream = getClass().getResourceAsStream("ImgActivity/img1.png");
            if (defaultStream != null) {
                Image defaultImage = new Image(defaultStream);
                if (!defaultImage.isError()) {
                    imageView.setImage(defaultImage);
                    return;
                }
            }

            imageView.setStyle("-fx-background-color: #f0f0f0;");

        } catch (Exception e) {
            System.err.println("Error loading default image: " + e.getMessage());
            imageView.setStyle("-fx-background-color: #f0f0f0;");
        }
    }

    private void handleActivityDetails(ActionEvent event, Activity activity) {
        try {
            // Navigate to activity detail page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/uasvolunteerhub/activity-detail.fxml"));
            Parent root = loader.load();

            // Get the controller and set the activity ID
            ActivityDetailController controller = loader.getController();
            controller.setActivityId(activity.getId());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Activity Details - " + activity.getTitle());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to load activity details page: " + e.getMessage());
        }
    }

    private void updateStatistics() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Update total activities
            String activityCountSql = "SELECT COUNT(*) FROM activity";
            try (PreparedStatement stmt = conn.prepareStatement(activityCountSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int totalActivities = rs.getInt(1);
                    if (totalActivitiesLabel != null) {
                        totalActivitiesLabel.setText("Total Activities: " + totalActivities);
                    }
                }
            }

            // Update total volunteers (assuming you have user table or volunteer registrations)
            // You might need to adjust this query based on your database schema
            String volunteerCountSql = "SELECT COUNT(DISTINCT contact) FROM activity";
            try (PreparedStatement stmt = conn.prepareStatement(volunteerCountSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int totalVolunteers = rs.getInt(1);
                    if (totalVolunteersLabel != null) {
                        totalVolunteersLabel.setText("Total Volunteers: " + totalVolunteers);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (totalActivitiesLabel != null) {
                totalActivitiesLabel.setText("Total Activities: N/A");
            }
            if (totalVolunteersLabel != null) {
                totalVolunteersLabel.setText("Total Volunteers: N/A");
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
        NavigationUtil.goTo(event, "/com/example/uasvolunteerhub/recommendation-view.fxml", "Volunteer Recommendation");
    }

    @FXML
    private void handleHistory(ActionEvent event) {
        NavigationUtil.goTo(event, "/com/example/uasvolunteerhub/history-view.fxml", "Activity History");
    }


    @FXML
    private void handleSearch() {
        // Handle search functionality
        String searchText = searchField.getText();
        System.out.println("Searching for: " + searchText);

        // Load activities based on search query
        loadActivitiesFromDatabase(searchText);
    }
}