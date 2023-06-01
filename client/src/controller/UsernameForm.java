package controller;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
    private Stage ownStage;

    @FXML
    public void btnEnterGroupChatOnAction(ActionEvent actionEvent) throws IOException {
        username = txtUsername.getText();
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/ChatForm.fxml"))));
        stage.show();
        stage.setTitle("Client " + username);
        ownStage = (Stage) txtUsername.getScene().getWindow();
        ownStage.close();
    }
    public static String getTitle(){
        return username;
    }
}
