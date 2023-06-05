package server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/*
Author : Sachin Silva
*/

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();
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
            broadcastMessage("SEVER : " + clientUsername + " has entered the chat");
        } catch (IOException e) {
            closeEverything(socket);
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                String messageFromClient = dataInputStream.readUTF();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket);
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
                closeEverything(socket);
            }
        }
    }


    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER : " + clientUsername + " has left the chat");
    }

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

