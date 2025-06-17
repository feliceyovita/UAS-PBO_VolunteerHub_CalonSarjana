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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

public class ActivityDetailController {

    @FXML
    private Button backBtn;

    @FXML
    private Button accountBtn;

    @FXML
    private Button recommendationBtn;

    @FXML
    private Button historyBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private ScrollPane contentScrollPane;

    // Activity data
    private int activityId;
    private Activity currentActivity;

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
        // Initialize will be called after FXML loading
        // Activity data will be loaded when setActivityId is called
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
        loadActivityData();
        displayActivityDetails();
    }

    private void loadActivityData() {
        String sql = "SELECT * FROM activity WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, activityId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                currentActivity = new Activity(
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load activity data: " + e.getMessage());
        }
    }

    private void displayActivityDetails() {
        if (currentActivity == null) {
            showAlert("Error", "Activity data not found");
            return;
        }

        VBox contentBox = createActivityDetailContent();
        contentScrollPane.setContent(contentBox);
    }

    private VBox createActivityDetailContent() {
        VBox mainContainer = new VBox();
        mainContainer.setSpacing(0);
        mainContainer.setStyle("-fx-background-color: #B2D3C2;");
        mainContainer.setPadding(new Insets(20));

        // Create main content container - same background as main container
        VBox contentContainer = new VBox();
        contentContainer.setStyle("-fx-background-color: #B2D3C2;");
        contentContainer.setPadding(new Insets(20));
        contentContainer.setSpacing(0);

        // Create layout similar to Figma design
        HBox mainLayout = new HBox();
        mainLayout.setSpacing(30);
        mainLayout.setAlignment(Pos.TOP_LEFT);

        // Left content area
        VBox leftContent = createLeftContentArea();
        leftContent.setPrefWidth(450);

        // Right content area (images)
        VBox rightContent = createRightContentArea();
        rightContent.setPrefWidth(280);

        mainLayout.getChildren().addAll(leftContent, rightContent);
        contentContainer.getChildren().add(mainLayout);
        mainContainer.getChildren().add(contentContainer);

        return mainContainer;
    }

    private VBox createLeftContentArea() {
        VBox leftContent = new VBox();
        leftContent.setSpacing(15);

        // Activity title
        Label titleLabel = new Label(currentActivity.getTitle());
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");
        titleLabel.setWrapText(true);

        // Location
        Label locationLabel = new Label(currentActivity.getLocation());
        locationLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #2E7D32; -fx-font-style: italic;");

        // Description section
        Label descriptionTitle = new Label("Description:");
        descriptionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        Label descriptionContent = new Label(currentActivity.getDescription());
        descriptionContent.setWrapText(true);
        descriptionContent.setStyle("-fx-font-size: 14px; -fx-text-fill: #2E7D32;");

        // Benefits section
        Label benefitsTitle = new Label("Benefits:");
        benefitsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        Label benefitsContent = new Label(currentActivity.getBenefits());
        benefitsContent.setWrapText(true);
        benefitsContent.setStyle("-fx-font-size: 14px; -fx-text-fill: #2E7D32;");

        // Price information if available
        if (currentActivity.getDonationAmount() > 0) {
            Label priceLabel = new Label("Price: $" + String.format("%.0f", currentActivity.getDonationAmount()) + " / person");
            priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2E7D32; -fx-font-weight: bold;");
            leftContent.getChildren().add(priceLabel);
        }

        // Details section
        VBox detailsSection = createDetailsSection();

        // Status and buttons section
        VBox statusSection = createStatusSection();

        leftContent.getChildren().addAll(
                titleLabel, locationLabel,
                descriptionTitle, descriptionContent,
                benefitsTitle, benefitsContent,
                detailsSection, statusSection
        );

        return leftContent;
    }

    private VBox createDetailsSection() {
        VBox detailsBox = new VBox();
        detailsBox.setSpacing(5);

        // Slot information
        if (currentActivity.getSlot() > 0) {
            Label statusLabel = new Label("Status: In need of " + currentActivity.getSlot() + " volunteer(s).");
            statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2E7D32; -fx-font-weight: bold;");
            detailsBox.getChildren().add(statusLabel);
        }

        // Application deadline (you can add this field to database if needed)
        Label deadlineLabel = new Label("Application deadline: 25th September, 2023");
        deadlineLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2E7D32;");

        // Start date
        Label startDateLabel = new Label("Start date: " + currentActivity.getDate());
        startDateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2E7D32;");

        // Contact information
        Label contactLabel = new Label("Contact information: " + currentActivity.getContact());
        contactLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2E7D32;");

        detailsBox.getChildren().addAll(deadlineLabel, startDateLabel, contactLabel);
        return detailsBox;
    }

    private VBox createStatusSection() {
        VBox statusSection = new VBox();
        statusSection.setSpacing(12);
        statusSection.setPadding(new Insets(15, 0, 0, 0));

        // Check if activity is open for donation
        if ("donate".equalsIgnoreCase(currentActivity.getTypeOfVolunteer())) {
            // Open for donation
            Label donationStatusLabel = new Label("Open for donation");
            donationStatusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

            // Donation amount with money icon
            HBox donationAmountBox = new HBox();
            donationAmountBox.setSpacing(8);
            donationAmountBox.setAlignment(Pos.CENTER_LEFT);

            Label moneyIcon = new Label("💰");
            moneyIcon.setStyle("-fx-font-size: 14px;");

            Label totalNeededLabel = new Label("Total Needed $" + String.format("%.0f", currentActivity.getDonationAmount()));
            totalNeededLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #333333; -fx-font-weight: bold;");

            donationAmountBox.getChildren().addAll(moneyIcon, totalNeededLabel);

            // Progress indicator
            Label progressLabel = new Label("17% funded");
            progressLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2E7D32; -fx-background-color: #E8F5E8; " +
                    "-fx-background-radius: 10; -fx-padding: 4 12; -fx-font-weight: bold;");

            // Buttons section
            HBox buttonSection = new HBox();
            buttonSection.setSpacing(15);
            buttonSection.setPadding(new Insets(10, 0, 0, 0));

            Button registerBtn = createStyledButton("Register", "#4CAF50");
            registerBtn.setOnAction(this::handleRegister);

            Button donateBtn = createStyledButton("Donate", "#2E7D32");
            donateBtn.setOnAction(this::handleDonate);

            buttonSection.getChildren().addAll(registerBtn, donateBtn);

            statusSection.getChildren().addAll(donationStatusLabel, donationAmountBox, progressLabel, buttonSection);

        } else {
            // Not open for donation
            Label notOpenLabel = new Label("Not open for donation");
            notOpenLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666; -fx-font-weight: bold;");

            Button registerBtn = createStyledButton("Register", "#4CAF50");
            registerBtn.setOnAction(this::handleRegister);
            registerBtn.setPadding(new Insets(10, 0, 0, 0));

            statusSection.getChildren().addAll(notOpenLabel, registerBtn);
        }

        return statusSection;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 20; " +
                "-fx-padding: 10 25; " +
                "-fx-font-size: 13px; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");

        // Add hover effect
        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle().replace("-fx-scale-x: 1.05; -fx-scale-y: 1.05;", "")));

        return button;
    }

    private VBox createRightContentArea() {
        VBox rightContent = new VBox();
        rightContent.setSpacing(15);
        rightContent.setAlignment(Pos.TOP_CENTER);

        // Main activity image
        ImageView mainImage = new ImageView();
        mainImage.setFitHeight(200);
        mainImage.setFitWidth(280);
        mainImage.setPreserveRatio(true);
        mainImage.setStyle("-fx-background-radius: 10;");
        loadActivityImage(mainImage, currentActivity);

        // Secondary image
        ImageView secondaryImage = new ImageView();
        secondaryImage.setFitHeight(130);
        secondaryImage.setFitWidth(280);
        secondaryImage.setPreserveRatio(true);
        secondaryImage.setStyle("-fx-background-radius: 10;");
        loadActivityImage(secondaryImage, currentActivity);

        rightContent.getChildren().addAll(mainImage, secondaryImage);
        return rightContent;
    }

    private void loadActivityImage(ImageView imageView, Activity activity) {
        String imagePath = activity.getImage();

        if (imagePath == null || imagePath.trim().isEmpty()) {
            loadDefaultImage(imageView);
            return;
        }

        try {
            // Try to load from resources
            String resourcePath = "/ImgActivity/" + imagePath;
            InputStream imageStream = getClass().getResourceAsStream(resourcePath);

            if (imageStream != null) {
                Image image = new Image(imageStream);
                if (!image.isError()) {
                    imageView.setImage(image);
                    return;
                }
            }

            // Try to load from file system
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                if (!image.isError()) {
                    imageView.setImage(image);
                    return;
                }
            }

            loadDefaultImage(imageView);
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            loadDefaultImage(imageView);
        }
    }

    private void loadDefaultImage(ImageView imageView) {
        try {
            InputStream defaultStream = getClass().getResourceAsStream("/ImgActivity/img1.png");
            if (defaultStream != null) {
                Image defaultImage = new Image(defaultStream);
                if (!defaultImage.isError()) {
                    imageView.setImage(defaultImage);
                    return;
                }
            }
            // If no default image, set a placeholder background
            imageView.setStyle("-fx-background-color: #E0E0E0; -fx-background-radius: 10;");
        } catch (Exception e) {
            System.err.println("Error loading default image: " + e.getMessage());
            imageView.setStyle("-fx-background-color: #E0E0E0; -fx-background-radius: 10;");
        }
    }

    private void handleRegister(ActionEvent event) {
        // Implement registration logic here
        showAlert("Registration", "Registration functionality will be implemented here.");
    }

    private void handleDonate(ActionEvent event) {
        // Implement donation logic here
        showAlert("Donation", "Donation functionality will be implemented here.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/uasvolunteerhub/Volunteer-dashboard-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Volunteer Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to return to dashboard: " + e.getMessage());
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
        NavigationUtil.goTo(event, "/com/example/uasvolunteerhub/recommendation-view.fxml", "Volunteer Recommendation");
    }

    @FXML
    private void handleHistory(ActionEvent event) {
        NavigationUtil.goTo(event, "/com/example/uasvolunteerhub/history-view.fxml", "Activity History");
    }
}