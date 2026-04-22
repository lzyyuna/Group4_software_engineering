package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.User;
import com.group4.tarecruitment.service.AdminService;
import com.group4.tarecruitment.service.AdminServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class AdminUserView {

    private final AdminService adminService = new AdminServiceImpl();
    private final Runnable onBack;
    private final ObservableList<User> userData = FXCollections.observableArrayList();

    public AdminUserView(Stage stage, Runnable onBack) {
        this.onBack = onBack;
    }

    public Parent createContent() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f5f6fa;");

        // 1. Header Area
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("User Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Spacer
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button backBtn = new Button("Back to Dashboard");
        backBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");
        backBtn.setOnAction(e -> onBack.run());
        
        header.getChildren().addAll(title, spacer, backBtn);

        // 2. White Card with Search & Table
        VBox mainCard = new VBox(15);
        mainCard.setPadding(new Insets(20));
        mainCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        VBox.setVgrow(mainCard, Priority.ALWAYS);

        // Search Controls
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by Username...");
        searchField.setPrefWidth(250);

        ComboBox<String> roleFilter = new ComboBox<>();
        roleFilter.getItems().addAll("All Roles", "Admin", "TA", "MO");
        roleFilter.getSelectionModel().selectFirst();
        roleFilter.setPrefWidth(150);

        controls.getChildren().addAll(new Label("Filter users:"), searchField, roleFilter);

        // Table
        TableView<User> userTable = new TableView<>();
        
        TableColumn<User, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        userCol.setPrefWidth(250);

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(200);

        userTable.getColumns().addAll(userCol, roleCol);
        VBox.setVgrow(userTable, Priority.ALWAYS);

        // Wrap in FilteredList
        javafx.collections.transformation.FilteredList<User> filteredData = new javafx.collections.transformation.FilteredList<>(userData, p -> true);

        // Bind Predicate to controls
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePredicate(filteredData, newValue, roleFilter.getValue());
        });
        roleFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            updatePredicate(filteredData, searchField.getText(), newValue);
        });

        // Wrap in SortedList to allow sorting
        javafx.collections.transformation.SortedList<User> sortedData = new javafx.collections.transformation.SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(userTable.comparatorProperty());
        userTable.setItems(sortedData);

        mainCard.getChildren().addAll(controls, userTable);

        root.getChildren().addAll(header, mainCard);

        loadUsers();

        return root;
    }

    private void updatePredicate(javafx.collections.transformation.FilteredList<User> filteredList, String username, String role) {
        filteredList.setPredicate(user -> {
            // Check username match
            boolean matchesName = (username == null || username.isEmpty()) || 
                                  user.getUsername().toLowerCase().contains(username.toLowerCase());
            
            // Check role match
            boolean matchesRole = (role == null || role.equals("All Roles")) ||
                                  user.getRole().equalsIgnoreCase(role);
            
            return matchesName && matchesRole;
        });
    }

    private void loadUsers() {
        try {
            List<User> users = adminService.getAllUsers();
            userData.setAll(users);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load user data from user.csv.");
            alert.showAndWait();
        }
    }
}
