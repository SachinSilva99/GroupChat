import controller.ChatFormController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/*
Author : Sachin Silva
*/
public class ServerInitializer extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("view/ChatForm.fxml"))));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to close the chat?", ButtonType.YES, ButtonType.NO);
            alert.setHeaderText(null);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                ChatFormController.getInstance().onClose();
                primaryStage.close();
                System.exit(0);
            }

        });
    }
}
