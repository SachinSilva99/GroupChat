// ChatFormController.java (Client-side)
package controller;

import client.Client;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

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
                Platform.runLater(()->{
                    new Alert(Alert.AlertType.CONFIRMATION, "Sever not connected ", ButtonType.OK).show();
                    System.exit(0);
                });
            }
            client = new Client(socket, username);
            lblUsername.setText(username);
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


    public static void addImage(ImageView imageView, Pos pos, boolean imageSentByOwner, String color) {
        HBox hBox = new HBox();
        hBox.setAlignment(pos);
        hBox.setPadding(new Insets(5, 5, 5, 10));

        Label usernameLabel = new Label(imageSentByOwner ? "" : UsernameForm.getTitle() + " : ");
        usernameLabel.setStyle("-fx-fill: Blue");
        usernameLabel.setStyle("-fx-font-weight: bold;");
        usernameLabel.setPadding(new Insets(0, 5, 0, 0));

        VBox imageBox = new VBox();
        imageBox.setAlignment(Pos.CENTER);
        imageView.setFitHeight(100);
        imageView.setFitWidth(300);
        imageBox.getChildren().add(usernameLabel);
        imageBox.getChildren().add(imageView);
        TextFlow textFlow = new TextFlow(imageBox);
        textFlow.setStyle(color + "-fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5, 10, 5, 10));

        // Set alignment and vertical alignment for the username label
        usernameLabel.setTextAlignment(TextAlignment.CENTER);
        usernameLabel.setAlignment(Pos.TOP_CENTER);

        hBox.getChildren().add(textFlow);

        Platform.runLater(() -> {
            ChatFormController.getInstance().vbox.getChildren().add(hBox);
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
        String imagePath = selectedFile.toURI().toString();
        Image image = new Image(imagePath);
        addImage(new ImageView(image), Pos.CENTER_RIGHT, true, "-fx-background-color: #79E0EE;");
        client.sendImage(selectedFile);
    }

    @FXML
    public void onClose() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to close the chat?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            Stage stage = (Stage) lblUsername.getScene().getWindow();
            stage.close();
            client.closeEverything();
            System.exit(0);
        }
    }

    public void close() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Sever Connection Lost!", ButtonType.OK);
            alert.setHeaderText(null);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.OK) {
                Stage stage = (Stage) lblUsername.getScene().getWindow();
                stage.close();
                client.closeEverything();
                System.exit(0);
            }
        });
    }

    public static void alert() {
        ChatFormController.getInstance().close();
    }

}
