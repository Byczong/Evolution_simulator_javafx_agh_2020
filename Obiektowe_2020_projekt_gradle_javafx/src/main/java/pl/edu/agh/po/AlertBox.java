package pl.edu.agh.po;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox {

    public static void display(String title, String message1, String message2) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(400);

        Label label1 = new Label();
        label1.setText(message1);
        Label label2 = new Label();
        label2.setText(message2);
        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> window.close());

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(25, 25, 25, 25));
        layout.getChildren().addAll(label1, label2, closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}
