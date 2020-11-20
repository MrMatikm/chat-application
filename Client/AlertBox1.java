package Client;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox1 {
    public static void display() {
        Stage stage = new Stage();
        stage.setTitle("Username taken");
        stage.initModality(Modality.APPLICATION_MODAL);

        Label label1 = new Label("This username is taken.");
        Label label2 = new Label("Please choose a different one.");
        Button button = new Button("OK");
        button.setOnAction(e -> stage.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label1, label2, button);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 250, 100);
        stage.setScene(scene);
        stage.showAndWait();
    }
}