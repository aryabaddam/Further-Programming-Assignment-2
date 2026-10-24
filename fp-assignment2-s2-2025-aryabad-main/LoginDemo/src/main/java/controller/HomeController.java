package controller;

import dao.Database;
import dao.ProjectDao;
import dao.ProjectDaoImpl;
import model.Project;
import model.Model;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.List;

// This code manages the main dashboard screen logic.
public class HomeController {
    @FXML private javafx.scene.control.TextField hoursField;
    @FXML private TableView<Project> projectsTable;
    @FXML private TableColumn<Project, String> titleColumn;
    @FXML private TableColumn<Project, String> locationColumn;
    @FXML private TableColumn<Project, String> dayColumn;
    @FXML private TableColumn<Project, Number> hourlyValueColumn;
    @FXML private TableColumn<Project, String> slotsColumn;
    @FXML private Button addToCartButton;
    @FXML private Button viewCartButton;
    @FXML private Label statusLabel;
    @FXML private Label welcomeLabel;

    private ProjectDao projectDao;
    private final Stage stage;
    private final Model model;

    public HomeController(Stage stage, Model model) {
        this.stage = stage;
        this.model = model;
    }

    // This code shows an information alert with a given message.
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // This code initializes the table, loads data, and sets button actions.
    @FXML
    public void initialize() {
        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                statusLabel.setText("Error: Could not connect to the database.");
                return;
            }

            projectDao = new ProjectDaoImpl(conn);

            // This code maps the table columns
            titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
            locationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLocation()));
            dayColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDay()));
            hourlyValueColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getHourlyValue()));
            slotsColumn.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().getRegisteredSlots() + "/" + data.getValue().getTotalSlots())
            );

            // This code loads the data into the projects table
            projectsTable.getItems().setAll(projectDao.getEnabledProjects());
            statusLabel.setText("Projects loaded successfully!");

            // This code implements the Add to Cart button action
            addToCartButton.setOnAction(e -> handleAddToCart());

            // This code implements the View Cart button action
            viewCartButton.setOnAction(e -> handleViewCart());

        } catch (Exception e) {
            statusLabel.setText("Unexpected error occurred while loading projects. Please restart the app.");
            e.printStackTrace();
        }
    }

    // This code handles adding the selected project with specified hours to the cart.
    private void handleAddToCart() {
        Project selected = projectsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("No project selected!");
            return;
        }

        String hoursText = hoursField.getText();
        if (hoursText.isEmpty()) {
            showInfo("Please enter volunteer hours before adding to cart.");
            return;
        }

        int hours;
        try {
            hours = Integer.parseInt(hoursText.trim());
            if (hours < 1 || hours > 3) {
                showInfo("Volunteer hours must be between 1 and 3.");
                return;
            }
            selected.setVolunteerHours(hours);
        } catch (NumberFormatException ex) {
            showInfo("Please enter a valid number for hours.");
            return;
        }

        List<Project> cart = model.getCart();
        if (!cart.contains(selected)) {
            cart.add(selected);
            showInfo("Added to cart: " + selected.getTitle() + " (" + hours + " hour(s))");
        } else {
            statusLabel.setText("Project already in cart: " + selected.getTitle());
        }
    }

    // This code handles opening the cart view window to display selected projects.
    private void handleViewCart() {
        List<Project> cart = model.getCart();
        if (cart.isEmpty()) {
            statusLabel.setText("Cart is empty.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CartView.fxml"));

            // This code manually creates and assign the CartController before loading
            CartController cartController = new CartController();
            cartController.setModel(model);
            loader.setController(cartController);

            VBox root = loader.load();

            Stage cartStage = new Stage();
            cartController.setStage(cartStage);
            cartController.showStage(root);

        } catch (Exception ex) {
            statusLabel.setText("⚠️ Error opening cart: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // This code sets the welcome message label with the username.
    public void setWelcomeMessage(String username) {
        welcomeLabel.setText("Welcome, " + username + "!");
    }

    // This code displays the main dashboard stage with the given root layout.
    public void showStage(VBox root) {
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Dashboard");
        stage.show();
    }
}