package Client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LogInBox {
    private static String userName;
    private static Stage stage;
    public static String logIn() {
        stage = new Stage();
        stage.setTitle("Choose username");
        stage.initModality(Modality.APPLICATION_MODAL);

        Scene scene = new Scene(createLayout(), 300, 100);
        stage.setScene(scene);
        stage.showAndWait();

        return userName;
    }

    private static VBox createLayout() {
        VBox layout = new VBox(10);
        Label label = new Label("Enter user name");
        TextField textField = new TextField();
        textField.setOnAction(e -> {
            userName = textField.getText();
            stage.close();
        });
        textField.setPromptText("min. 3 characters");
        Button button = new Button("Join chat");
        button.setOnAction(e -> {
            userName = textField.getText();
            stage.close();
        });
        layout.getChildren().addAll(label, textField, button);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10, 10, 10, 10));
        return layout;
    }
}
