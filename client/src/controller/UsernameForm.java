package controller;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/*
Author : Sachin Silva
*/
public class UsernameForm {
    private static String username;
    @FXML
    public JFXTextField txtUsername;



    @FXML
    public void btnEnterGroupChatOnAction(ActionEvent actionEvent) throws IOException {
        username = txtUsername.getText();

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ChatForm.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("Client " + username);
        stage.show();
        stage.setOnCloseRequest(event -> {
            ChatFormController.getInstance().onClose();
            event.consume();
        });
        Stage ownStage = (Stage) txtUsername.getScene().getWindow();
        ownStage.close();
    }

    public static String getTitle(){
        return username;
    }
}
