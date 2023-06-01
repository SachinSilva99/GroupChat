package client;

import controller.ChatFormController;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.Image;

import java.io.*;
import java.net.Socket;

/*
Author : Sachin Silva
*/
public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage(String messageToSend) {
        String formattedMessage = username + ": " + messageToSend;
        try {
            bufferedWriter.write(formattedMessage);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            System.out.println("Error sending message to server");
            e.printStackTrace();
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String messageFromServer = bufferedReader.readLine();
                    if (messageFromServer != null) {
                        int separatorIndex = messageFromServer.indexOf(":");
                        if (separatorIndex != -1 && separatorIndex < messageFromServer.length() - 1) {
                            String sender = messageFromServer.substring(0, separatorIndex).trim();
                            String content = messageFromServer.substring(separatorIndex + 1).trim();
                            Platform.runLater(() -> {
                                ChatFormController.addLabel(sender + ": " + content, Pos.CENTER_LEFT, "-fx-background-color: #DDE6ED;");
                            });
                        }
                    } else {
                        break;
                    }
                } catch (IOException e) {
                    System.out.println("Error receiving message from server");
                    e.printStackTrace();
                    break;
                }
            }
        }).start();

    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}