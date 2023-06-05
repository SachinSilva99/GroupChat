package client;

import controller.ChatFormController;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

/*
Author : Sachin Silva
*/
public class Client {
    private DataInputStream dataInputStream;
    private Socket socket;
    private DataOutputStream dataOutputStream;


    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket);
        }
    }

    public void sendMessage(String messageToSend) {
        String formattedMessage = username + ": " + messageToSend;
        try {
            dataOutputStream.writeUTF(formattedMessage);
            dataOutputStream.flush();
        } catch (IOException e) {
            System.out.println("Error sending message to server");
            e.printStackTrace();
        }
    }


    public void listenForMessage() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String messageFromServer = dataInputStream.readUTF();
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


//    public void sendImage(File file) {
//        try {
//            byte[] imageBytes = Files.readAllBytes(file.toPath());
//
//            // Send the image size to the server
//            int imageSize = imageBytes.length;
//            bufferedWriter.write("IMAGE_SIZE:" + imageSize);
//            bufferedWriter.newLine();
//            bufferedWriter.flush();
//
//            // Send the image data to the server
//            bufferedWriter.write("IMAGE_DATA:");
//            bufferedWriter.flush();
//
//            // Determine the chunk size dynamically based on the image size
//            int maxChunkSize = 1024; // Maximum chunk size you want to use
//            int chunkSize = Math.min(maxChunkSize, imageSize); // Set the chunk size as imageSize if it is smaller than maxChunkSize
//
//            int offset = 0;
//            while (offset < imageSize) {
//                int remainingBytes = imageSize - offset;
//                int bytesToSend = Math.min(remainingBytes, chunkSize);
//                bufferedWriter.write(new String(imageBytes, offset, bytesToSend));
//                bufferedWriter.flush();
//                offset += bytesToSend;
//            }
//            System.out.println("Image sent successfully");
//        } catch (IOException e) {
//            System.out.println("Error sending image to server");
//            e.printStackTrace();
//        }
//    }


    public void closeEverything(Socket socket) {
        try {

            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}