package controller;

import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import model.Model;
import model.User;

/**
 * This code manages the user signup process.
 */
public class SignupController {

    // This code declares UI components used in the signup form.
    @FXML private TextField fullname;
    @FXML private TextField username;
    @FXML private TextField email;
    @FXML private PasswordField password;
    @FXML private Button createUser;
    @FXML private Button close;
    @FXML private Label status;

    private final Stage stage;
    private final Stage parentStage;
    private final Model model;

    // This code initializes the controller and keeps references to stages and model.
    public SignupController(Stage parentStage, Model model) {
        this.stage = new Stage();
        this.parentStage = parentStage;
        this.model = model;
    }

    // This code binds button actions to their handlers.
    @FXML
    public void initialize() {
        createUser.setOnAction(event -> handleCreateUser());
        close.setOnAction(event -> {
            stage.close();
            parentStage.show();
        });
    }

    // This code validates input and creates a new user.
    private void handleCreateUser() {
        String fullNameText = fullname.getText();
        String usernameText = username.getText();
        String emailText = email.getText();
        String passwordText = password.getText();

        if (fullNameText.isEmpty() || usernameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty()) {
            showStatus("All fields are required!", Color.RED);
            return;
        }

        try {
            User user = model.getUserDao().createUser(fullNameText, usernameText, emailText, passwordText);
            if (user != null) {
                showStatus("✅ User created successfully!", Color.GREEN);
            } else {
                showStatus("⚠️ Could not create user.", Color.RED);
            }
        } catch (SQLException e) {
            // This code displays an error message when a database issue occurs during user creation.
            showStatus("Database error: " + e.getMessage(), Color.RED);
        }
    }

    // This code displays feedback messages to the user.
    private void showStatus(String message, Color color) {
        status.setText(message);
        status.setTextFill(color);
    }

    // This code displays the signup window.
    public void showStage(Pane root) {
        Scene scene = new Scene(root, 500, 400);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Sign Up");
        stage.show();
    }
}