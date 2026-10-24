import java.io.IOException;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.fxml.FXMLLoader;

import model.Model;
import controller.LoginController;
import dao.Database;

public class Main extends Application {
	private Model model;

	@Override
	public void init() {
		model = new Model();
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			// This code initialises the database
			Database.initialize();

			// This code imports the CSV data ( This is the path inside src/main/resources/view/)
			Database.importProjectsFromCSV("/view/projects.csv");

			// This code adds a default user
			Database.addDefaultUser();

			model.setup();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
			// This code connects with the Login Controller file
			LoginController loginController = new LoginController(primaryStage, model);
			loader.setController(loginController);

			GridPane root = loader.load();
			loginController.showStage(root);
		// This code handles and displays any database or loading errors in a simple error window
		} catch (IOException | SQLException e) {
			Scene scene = new Scene(new Label(e.getMessage()), 300, 100);
			primaryStage.setTitle("Error");
			primaryStage.setScene(scene);
			primaryStage.show();
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}