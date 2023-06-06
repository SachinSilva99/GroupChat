// ChatFormController.java (server.Server-side)
package controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import server.Server;

import java.io.IOException;
import java.net.ServerSocket;

public class ChatFormController {

    @FXML
    public JFXButton btnStartStop;
    @FXML
    public Label lblServer;
    @FXML
    public Label lblMsg;
    private Server server = null;
    private boolean serverState = false;

    private static ChatFormController instance;

    public ChatFormController() {
        instance = this;
    }

    public void initialize() {
    }

    @FXML
    public void btnStartStopOnAction() {
        new Thread(() -> {
            if (!serverState) {
                startServer();
            } else {
                stopServer();
            }
        }).start();
    }

    private void stopServer() {
        serverState = false;
        onClose();
        Platform.runLater(() -> {

            btnStartStop.setText("Start Server");
            lblServer.setText("Server Stopped...");
        });
    }

    private void startServer() {
        System.out.println(serverState);
        if (serverState) {
            stopServer();
            return;
        }
        serverState = true;
        Platform.runLater(() -> {
            btnStartStop.setText("Stop Server");
            lblServer.setText("Server Running...");
        });

        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            server = new Server(serverSocket);
            server.startServer();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clientConnectedMsg() {
        Platform.runLater(() -> {
            String text = ChatFormController.getInstance().lblMsg.getText();
            ChatFormController.getInstance().lblMsg.setText(text + "\nA new Client Joined!");
        });
    }


    public static ChatFormController getInstance() {
        return instance;
    }


    public void onClose() {
        server.closeServerSocket();
        server.closeServer();
    }
}
