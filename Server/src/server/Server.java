package server;

import controller.ChatFormController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
Author : Sachin Silva
*/
public class Server {
    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    public void startServer(){
        try{
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                ChatFormController.clientConnectedMsg();
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void closeServerSocket(){
        try{
            if(serverSocket!=null){
                serverSocket.close();
                System.out.println("closed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
