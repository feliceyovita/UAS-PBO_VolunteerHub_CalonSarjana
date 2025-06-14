package com.example.uasvolunteerhub;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.application.Platform;
import javafx.scene.Node;

import java.sql.*;

public class adminHomeController {

    @FXML private TableView<ActivityRow> activityTable;
    @FXML private TableColumn<ActivityRow, String> colActNo;
    @FXML private TableColumn<ActivityRow, String> colActTitle;
    @FXML private TableColumn<ActivityRow, Void> colActAction;
    @FXML private Label totalActivitiesLabel;

    @FXML private TableView<VolunteerRow> volunteerTable;
    @FXML private TableColumn<VolunteerRow, String> colVolNo;
    @FXML private TableColumn<VolunteerRow, String> colVolName;
    @FXML private TableColumn<VolunteerRow, String> colVolActivity;
    @FXML private Label totalVolunteersLabel;

    private ObservableList<ActivityRow> activityList = FXCollections.observableArrayList();
    private ObservableList<VolunteerRow> volunteerList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        styleTableView(activityTable);
        styleTableView(volunteerTable);
        loadActivities();
        loadVolunteers();

        // Styling khusus buat header
        styleTableViewHeaders(activityTable);
        styleTableViewHeaders(volunteerTable);
    }


    private void styleTableView(TableView<?> table) {
        table.setStyle(
                "-fx-background-color: #BFDACC;" +
                        "-fx-control-inner-background: #BFDACC;" +
                        "-fx-table-cell-border-color: transparent transparent transparent #ACB7B7;" +
                        "-fx-table-header-border-color: #ACB7B7;" +
                        "-fx-selection-bar: #8FC1A9;" +
                        "-fx-selection-bar-non-focused: #A9D1C3;" +
                        "-fx-border-color: #ACB7B7;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );

        table.lookupAll(".column-header").forEach(header -> {
            header.setStyle("-fx-background-color: #BFDACC;");
        });

        table.lookupAll(".column-header-background").forEach(bg -> {
            bg.setStyle("-fx-background-color: #BFDACC;");
        });
    }

    private void styleTableViewHeaders(TableView<?> tableView) {
        Platform.runLater(() -> {
            // Background kolom judul
            for (Node header : tableView.lookupAll(".column-header")) {
                header.setStyle("-fx-background-color: #BFDACC;");
            }

            // Background baris header keseluruhan
            Node headerBg = tableView.lookup(".column-header-background");
            if (headerBg != null) {
                headerBg.setStyle("-fx-background-color: #BFDACC;");
            }
        });
    }


    private void loadActivities() {
        activityList.clear();
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, title FROM activity")) {

            int index = 1;
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                activityList.add(new ActivityRow(index++, id, title));
            }
            activityTable.setItems(activityList);
            colActNo.setCellValueFactory(data -> data.getValue().snProperty());
            colActTitle.setCellValueFactory(data -> data.getValue().titleProperty());

            // Set cell factory to center SN
            colActNo.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            });

            // Set cell factory to center Activity Title
            colActTitle.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            });

            // Set cell factory to center Action buttons
            colActAction.setCellFactory(param -> new TableCell<>() {
                private final Button editBtn = new Button("✏ Edit");
                private final Button delBtn = new Button("🗑 Delete");
                private final HBox hBox = new HBox(10, editBtn, delBtn);

                {
                    editBtn.setStyle("-fx-background-color: #216516; -fx-text-fill: white; -fx-background-radius: 5;");
                    delBtn.setStyle("-fx-background-color: #216516; -fx-text-fill: white; -fx-background-radius: 5;");
                    hBox.setAlignment(javafx.geometry.Pos.CENTER); // Align buttons in HBox

                    editBtn.setOnAction(event -> {
                        ActivityRow row = getTableView().getItems().get(getIndex());
                        System.out.println("Edit: " + row.getId());
                        // open edit form here
                    });

                    delBtn.setOnAction(event -> {
                        ActivityRow row = getTableView().getItems().get(getIndex());
                        deleteActivity(row.getId());
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(hBox);
                        setAlignment(javafx.geometry.Pos.CENTER); // Center the whole cell
                    }
                }
            });

            totalActivitiesLabel.setText("Total: " + activityList.size() + " Activities");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private Callback<TableColumn<ActivityRow, Void>, TableCell<ActivityRow, Void>> getActionCellFactory() {
        return param -> new TableCell<>() {
            private final Button editBtn = new Button("✏ Edit");
            private final Button delBtn = new Button("🗑 Delete");
            private final HBox hBox = new HBox(10, editBtn, delBtn);

            {
                editBtn.setStyle("-fx-background-color: #216516; -fx-text-fill: white; -fx-background-radius: 5;");
                delBtn.setStyle("-fx-background-color: #216516; -fx-text-fill: white; -fx-background-radius: 5;");
                editBtn.setOnAction(event -> {
                    ActivityRow row = getTableView().getItems().get(getIndex());
                    System.out.println("Edit: " + row.getId());
                    // open edit form here
                });
                delBtn.setOnAction(event -> {
                    ActivityRow row = getTableView().getItems().get(getIndex());
                    deleteActivity(row.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hBox);
            }
        };
    }

    private void deleteActivity(int id) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM activity WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            loadActivities();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadVolunteers() {
        volunteerList.clear();
        String query = """
            SELECT v.name, a.title FROM volunteer v
            JOIN activity a ON v.id_activity = a.id
        """;
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            int index = 1;
            while (rs.next()) {
                String name = rs.getString("name");
                String activity = rs.getString("title");
                volunteerList.add(new VolunteerRow(index++, name, activity));
            }

            volunteerTable.setItems(volunteerList);
            colVolNo.setCellValueFactory(data -> data.getValue().snProperty());
            colVolName.setCellValueFactory(data -> data.getValue().nameProperty());
            colVolActivity.setCellValueFactory(data -> data.getValue().activityProperty());
            totalVolunteersLabel.setText("Total: " + volunteerList.size() + " Volunteers");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        System.out.println("Back button clicked");
        // Implementasi navigasi jika perlu
    }


}
