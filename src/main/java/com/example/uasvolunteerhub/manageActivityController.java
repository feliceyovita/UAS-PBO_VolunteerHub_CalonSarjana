package com.example.uasvolunteerhub;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class manageActivityController implements Initializable {

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private FlowPane activitiesFlowPane;
    @FXML private Button backButton;
    @FXML private Button dashboardButton;
    @FXML private Button addActivityButton;
    @FXML private Button logoutButton;

    private List<Activity> allActivities;
    private List<Activity> filteredActivities;
    private Database database; // Instance dari class Database

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = new Database(); // Initialize database connection
        loadActivities();
        displayActivities(allActivities);
        setupSearchListener();
    }

    private void navigateToPage(String fxmlPath, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    private void loadActivities() {
        allActivities = new ArrayList<>();

        String query = "SELECT id, title, date, benefits, location, contact, slot, description, type_of_volunteer, donation_amount, image FROM activity ORDER BY date DESC";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Activity activity = new Activity(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getDate("date"),
                        rs.getString("benefits"),
                        rs.getString("location"),
                        rs.getString("contact"),
                        rs.getInt("slot"),
                        rs.getString("description"),
                        rs.getString("type_of_volunteer"),
                        rs.getDouble("donation_amount"),
                        rs.getString("image")
                );
                allActivities.add(activity);
            }

            filteredActivities = new ArrayList<>(allActivities);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load activities from database: " + e.getMessage());

        }
    }

    private void displayActivities(List<Activity> activities) {
        activitiesFlowPane.getChildren().clear();

        for (Activity activity : activities) {
            VBox activityCard = createActivityCard(activity);
            activitiesFlowPane.getChildren().add(activityCard);
        }
    }

    private VBox createActivityCard(Activity activity) {
        VBox card = new VBox(10);
        card.setPrefWidth(200);
        card.setPrefHeight(300);
        card.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 8; " +
                "-fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPadding(new Insets(0));

        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(false);

        Rectangle clip = new Rectangle(200, 120);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        imageView.setClip(clip);

        loadActivityImage(imageView, activity);


        // Content container
        VBox content = new VBox(8);
        content.setPadding(new Insets(15));

        // Activity Title
        Label titleLabel = new Label(activity.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;");
        titleLabel.setWrapText(true);
        titleLabel.setFont(Font.font("SansSerif", 14));

        // Location
        Label locationLabel = new Label(activity.getLocation());
        locationLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        locationLabel.setWrapText(true);

        // Details
        VBox details = new VBox(3);

        Label slotsLabel = new Label("• Slots: " + activity.getSlot());
        slotsLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #333;");

        Label purposeLabel = new Label("• Purpose: " + activity.getTypeOfVolunteer());
        purposeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #333;");

        // Format dates
        String startDate = activity.getDate().toLocalDate().format(DateTimeFormatter.ofPattern("d MMMM, yyyy"));
        Label startDateLabel = new Label("• Start date: " + startDate);
        startDateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #333;");

        Label donationTargetLabel;
        if (activity.getDonationAmount() > 0) {
            donationTargetLabel = new Label("• Donation Target: Rp" + String.format("%,.0f", activity.getDonationAmount()));
        } else {
            donationTargetLabel = new Label("• No donation needed");
        }
        donationTargetLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #333;");

        Label donationInfoLabel;

        if ("donate".equalsIgnoreCase(activity.getTypeOfVolunteer()) && activity.getDonationAmount() > 0) {
            donationInfoLabel = new Label("• Donation Target: Rp" + String.format("%,.0f", activity.getDonationAmount()));
        } else {
            donationInfoLabel = new Label("• No donation needed");
        }
        donationInfoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #333;");

        details.getChildren().addAll(slotsLabel, purposeLabel, startDateLabel, donationInfoLabel);

        HBox buttonContainer = new HBox(10);

        // Add components to content
        content.getChildren().addAll(titleLabel, locationLabel, details, buttonContainer);

        // Add components to card
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
            Image defaultImage = new Image(getClass().getResourceAsStream("/ImgActivity/default-placeholder.png"));
            imageView.setImage(defaultImage);
        } catch (Exception e) {
            System.err.println("Failed to load default image: " + e.getMessage());
        }
    }

    // Method untuk get activity by ID (untuk keperluan edit)
    public Activity getActivityById(int activityId) {
        String query = "SELECT id, title, date, benefits, location, contact, slot, description, type_of_volunteer, donation_amount, image FROM activity WHERE id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, activityId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Activity(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getDate("date"),
                            rs.getString("benefits"),
                            rs.getString("location"),
                            rs.getString("contact"),
                            rs.getInt("slot"),
                            rs.getString("description"),
                            rs.getString("type_of_volunteer"),
                            rs.getDouble("donation_amount"),
                            rs.getString("image")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to get activity: " + e.getMessage());
        }

        return null;
    }

    private void setupSearchListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterActivities(newValue);
        });
    }

    private void filterActivities(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            filteredActivities = new ArrayList<>(allActivities);
        } else {
            String lowerCaseFilter = searchText.toLowerCase().trim();
            filteredActivities = allActivities.stream()
                    .filter(activity -> {
                        String title = activity.getTitle();
                        String location = activity.getLocation();
                        String description = activity.getDescription();
                        return (title != null && title.toLowerCase().contains(lowerCaseFilter)) ||
                                (location != null && location.toLowerCase().contains(lowerCaseFilter)) ||
                                (description != null && description.toLowerCase().contains(lowerCaseFilter));
                    })
                    .collect(Collectors.toList());
        }
        displayActivities(filteredActivities);
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String searchText = searchField.getText();
        filterActivities(searchText);
    }

    @FXML
    private void handleAdminDashboard(ActionEvent event) {
        try {
            navigateToPage("/com/example/uasvolunteerhub/adminHome-view.fxml", event);
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to open Dashboard Admin page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddActivity(ActionEvent event) {
        try {
            navigateToPage("/com/example/uasvolunteerhub/addActivity-view.fxml", event);
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to open Dashboard Admin page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEditActivity(Activity activity) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit-activity.fxml"));
            Parent root = loader.load();


            Stage stage = (Stage) activitiesFlowPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Activity");
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to open edit activity page: " + e.getMessage());
        }
    }

    private void navigateToPage(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Stage stage = (Stage) activitiesFlowPane.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to refresh activities (can be called after adding/editing)
    public void refreshActivities() {
        loadActivities();
        displayActivities(allActivities);

        // Reset search field jika ada text
        if (searchField != null && !searchField.getText().isEmpty()) {
            searchField.clear();
        }
    }
    @FXML
    private void handleLogout(ActionEvent event) {
        NavigationUtil.logout(event);
    }
}