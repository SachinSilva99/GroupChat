import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
    }
}
