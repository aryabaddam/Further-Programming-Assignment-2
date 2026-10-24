package controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Project;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import java.util.List;
import model.Model;
import javafx.fxml.FXMLLoader;

import java.sql.Connection;
import dao.RegistrationDao;
import dao.RegistrationDaoImpl;
import dao.Database;

/**
 * Controller for the cart view where users can review their selected projects,
 * confirm registrations, and navigate back to the dashboard.
 */
public class CartController {

    public CartController() {
        // Default constructor required for FXMLLoader
    }

    @FXML private TableView<Project> cartTable;
    @FXML private TableColumn<Project, String> titleColumn;
    @FXML private TableColumn<Project, String> locationColumn;
    @FXML private TableColumn<Project, String> dayColumn;
    @FXML private TableColumn<Project, Number> hourlyValueColumn;
    @FXML private TableColumn<Project, String> hoursColumn;
    @FXML private Button confirmButton;
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    private Model model;
    private Stage stage;

    public CartController(Stage stage, Model model) {
        this.stage = stage;
        this.model = model;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Allows external controllers to inject the model after FXML loading.
     * Also refreshes the cart view.
     */
    public void setModel(Model model) {
        this.model = model;
        // This code implements refreshCart();
    }

    @FXML
    public void initialize() {
        // This code sets up table columns to display project properties
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        locationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLocation()));
        dayColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDay()));
        hourlyValueColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getHourlyValue()));
        if (hoursColumn != null) {
            // This code displays volunteer hours or a dash if null
            hoursColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getVolunteerHours() != null ? data.getValue().getVolunteerHours().toString() : "—"
            ));
        }

        // This code handles the confirm button click: confirm registration and save to database
        confirmButton.setOnAction(e -> {
            boolean confirmed = ConfirmRegistrationDialog.showAndWait();

            if (!confirmed) {
                statusLabel.setText("❌ Registration cancelled.");
                return;
            }

            if (model == null) {
                statusLabel.setText("⚠️ Model not initialized!");
                return;
            }
            List<Project> currentCart = model.getCart();
            if (currentCart == null || currentCart.isEmpty()) {
                statusLabel.setText("⚠️ Your cart is empty!");
                return;
            }

            try (Connection conn = Database.getConnection()) {
                RegistrationDao regDao = new RegistrationDaoImpl(conn);
                regDao.saveRegistrations(model.getCurrentUser(), currentCart);
            } catch (Exception ex) {
                statusLabel.setText("⚠️ Error saving registrations: " + ex.getMessage());
                return;
            }

            // This code calculates the total volunteer hours and earnings
            double totalHours = currentCart.stream()
                    .mapToInt(p -> p.getVolunteerHours() != null ? p.getVolunteerHours() : 0)
                    .sum();

            double totalEarnings = currentCart.stream()
                    .mapToDouble(p -> (p.getVolunteerHours() != null ? p.getVolunteerHours() : 0) * p.getHourlyValue())
                    .sum();

            // This code shows success alert with registration summary
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText("Your registration has been saved!");
            alert.setContentText(String.format("You have registered for %d projects.\nTotal volunteer hours: %.0f\nTotal earnings: $%.2f",
                    currentCart.size(), totalHours, totalEarnings));
            alert.showAndWait();

            // This code clears the cart after successful registration
            model.clearCart();
            cartTable.getItems().clear();

            statusLabel.setText(String.format("✅ Confirmed and saved %d projects | %.0f hours | $%.2f total value",
                    currentCart.size(), totalHours, totalEarnings));
        });

        // This code handles back button click: return to dashboard
        backButton.setOnAction(e -> handleBackButton());

        // This code refreshs cart view if model is already set
        if (model != null) {
            refreshCart();
        }
    }

    /**
     * This code loads the dashboard view and closes the cart window.
     */
    public void handleBackButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/HomeView.fxml"));

            // This code creates new stage for dashboard and set controller
            Stage newStage = new Stage();
            HomeController homeController = new HomeController(newStage, model);
            loader.setController(homeController);

            VBox root = loader.load();
            homeController.showStage(root);

            // This code closes the current cart window
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("⚠️ Error returning to Dashboard: " + e.getMessage());
        }
    }

    /**
     * This code refreshes the cart table with current items from the model.
     * Clears the table if cart is empty.
     */
    private void refreshCart() {
        if (model != null) {
            List<Project> cartItems = model.getCart();
            if (cartItems != null && !cartItems.isEmpty()) {
                cartTable.getItems().setAll(cartItems);
            } else {
                cartTable.getItems().clear();
            }
        }
    }

    /**
     * This code sets up and shows the cart stage.
     */
    public void showStage(VBox root) {
        Scene scene = new Scene(root, 450, 400);
        stage.setScene(scene);
        stage.setTitle("My Cart");
        stage.show();
    }
}