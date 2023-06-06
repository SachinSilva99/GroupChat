package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private DataOutputStream dataOutputStream;
    private Socket socket;
    private String clientUsername;
    private DataInputStream dataInputStream;



    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.clientUsername = dataInputStream.readUTF();
            clientHandlers.add(this);
            // broadcastMessage("SERVER: " + clientUsername + " has entered the chat");
        } catch (IOException e) {
            closeEverything(socket, dataOutputStream, dataInputStream);
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                String messageType = dataInputStream.readUTF();
                if (messageType.equals("IMAGE_DATA")) {
                    receiveAndBroadcastImage();
                } else {
                    int separatorIndex = messageType.indexOf(":");
                    if (separatorIndex != -1 && separatorIndex < messageType.length() - 1) {
                        String sender = messageType.substring(0, separatorIndex).trim();
                        String content = messageType.substring(separatorIndex + 1).trim();
                        broadcastMessage(sender + ": " + content);
                    }
                }
            } catch (IOException e) {
                closeEverything(socket, dataOutputStream, dataInputStream);
                removeClientHandler();
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend) {

        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.dataOutputStream.writeUTF(messageToSend);
                    clientHandler.dataOutputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                closeEverything(socket, dataOutputStream, dataInputStream);
            }
        }
    }

    public void receiveAndBroadcastImage() {
        try {
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

            for (ClientHandler clientHandler : clientHandlers) {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.dataOutputStream.writeUTF("IMAGE_DATA");
                    clientHandler.dataOutputStream.writeInt(imageSize);
                    clientHandler.dataOutputStream.write(imageData);
                    clientHandler.dataOutputStream.flush();
                }
            }

            System.out.println("Image sent successfully");
        } catch (IOException e) {
            System.out.println("Error receiving image from client");
            e.printStackTrace();
            closeEverything(socket, dataOutputStream, dataInputStream);
        }
    }


    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat");
    }

    public void closeEverything(Socket socket, DataOutputStream dataOutputStream, DataInputStream dataInputStream) {
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
