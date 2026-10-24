package controller;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

public class ConfirmRegistrationDialog {

    public static boolean showAndWait() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Confirm Registration");
        dialog.setHeaderText("Please enter your 6-digit confirmation code:");

        // This code handles the OK / Cancel buttons
        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        // This code handles the Text field for code
        TextField codeField = new TextField();
        codeField.setPromptText("Enter 6-digit code");

        // This code handles the layout setup
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        dialog.getDialogPane().setContent(grid);

        // This code disables and confirm unless valid
        Button confirmButton = (Button) dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDisable(true);

        codeField.textProperty().addListener((obs, oldVal, newVal) -> {
            confirmButton.setDisable(!newVal.matches("\\d{6}"));
        });

        // This code converts the result to boolean
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return true;
            }
            return false;
        });

        return dialog.showAndWait().orElse(false);
    }
}