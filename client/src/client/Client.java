package client;

import controller.ChatFormController;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

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
            dataOutputStream.writeUTF(username);
            dataOutputStream.flush();
        } catch (IOException e) {
            closeEverything();
            e.printStackTrace();
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String messageType = dataInputStream.readUTF();
                    if (messageType.equals("IMAGE_DATA")) {
                        int imageSize = dataInputStream.readInt();
                        byte[] imageData = new byte[imageSize];
                        int bytesRead = 0;
                        int totalBytesRead = 0;
                        while (totalBytesRead < imageSize && bytesRead != -1) {
                            bytesRead = dataInputStream.read(imageData, totalBytesRead, imageSize - totalBytesRead);
                            if (bytesRead >= 0) {
                                totalBytesRead += bytesRead;
                            }
                        }

                        Platform.runLater(() -> {
                            Image image = new Image(new ByteArrayInputStream(imageData));
                            ImageView imageView = new ImageView(image);
                            ChatFormController.addImage(imageView, Pos.CENTER_LEFT,false, "-fx-background-color: #DDE6ED;");
                        });
                    } else {
                        int separatorIndex = messageType.indexOf(":");
                        if (separatorIndex != -1 && separatorIndex < messageType.length() - 1) {
                            String sender = messageType.substring(0, separatorIndex).trim();
                            String content = messageType.substring(separatorIndex + 1).trim();
                            Platform.runLater(() -> {
                                ChatFormController.addLabel(sender + ": " + content, Pos.CENTER_LEFT, "-fx-background-color: #DDE6ED;");
                            });
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error receiving message from server");
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }


    public void sendMessage(String message) {
        try {
            dataOutputStream.writeUTF(username + ": " + message);
            dataOutputStream.flush();
        } catch (IOException e) {
            System.out.println("Error sending message to server");
            e.printStackTrace();
        }
    }

    public void sendImage(File file) {
        try {
            byte[] imageBytes = Files.readAllBytes(file.toPath());

            // Send the image data to the server
            dataOutputStream.writeUTF("IMAGE_DATA");
            dataOutputStream.writeInt(imageBytes.length);
            dataOutputStream.write(imageBytes);
            dataOutputStream.flush();

            System.out.println("Image sent successfully");
        } catch (IOException e) {
            System.out.println("Error sending image to server");
            e.printStackTrace();
        }
    }

    public void closeEverything() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (dataInputStream != null) {
                dataInputStream.close();
            }
            if (dataOutputStream != null) {
                dataOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
