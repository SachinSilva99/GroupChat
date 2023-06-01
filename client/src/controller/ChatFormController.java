// ChatFormController.java (Client-side)
package controller;

import client.Client;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ChatFormController {
    public JFXTextArea txtMsg;
    public VBox vbox;
    public ScrollPane spMain;

    private Client client = null;
    @FXML
    public Label lblUsername;
    private static ChatFormController instance;


    public static ChatFormController getInstance() {
        return instance;
    }

    public ChatFormController() {
        instance = this;
    }



    public void initialize() {
        new Thread(() -> {
            String username = UsernameForm.getTitle();
            Socket socket = null;
            try {
                socket = new Socket("localhost", 1234);
            } catch (IOException e) {
                e.printStackTrace();
            }
            client = new Client(socket, username);
            client.sendMessage(username + " connected");
            client.listenForMessage();
        }).start();
        vbox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                spMain.setVvalue((Double) newValue);
            }
        });
    }

    public void btnSendOnAction(ActionEvent actionEvent) throws IOException {
        client.sendMessage(txtMsg.getText());
        addLabel(txtMsg.getText(), Pos.BASELINE_RIGHT, "-fx-background-color: #79E0EE;");
        txtMsg.clear();
        Platform.runLater(() -> {
            txtMsg.positionCaret(0);
        });
    }

    public static void addLabel(String message, Pos pos, String color) {
        HBox hBox = new HBox();
        hBox.setAlignment(pos);
        hBox.setPadding(new Insets(5, 5, 5, 10));
        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle(color + "-fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        hBox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ChatFormController.getInstance().vbox.getChildren().add(hBox);
            }
        });
    }



    @FXML
    public void txtMsgOnKeyPressed(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            keyEvent.consume();
            btnSendOnAction(null);
        }
    }

    public void onImageSelect(MouseEvent mouseEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
    }
}
