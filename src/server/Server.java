package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private static final int PORT = 1234;                                           // port which will be used in connection
    private ArrayList<ClientConnection> clientConnectionsList = new ArrayList<ClientConnection>(); // list of clients that have been connected

    public Server() {
        Socket clientSocket = null;                     // client socket
        ServerSocket serverSocket = null;               // server socket

        try {
            serverSocket = new ServerSocket(PORT);      // creating server socket
            System.out.println("Server started");
            while (true) {
                clientSocket = serverSocket.accept();   // server is waiting for new connection
                ClientConnection client = new ClientConnection(clientSocket, this);
                clientConnectionsList.add(client);
                new Thread(client).start();             // creating a new thread for client
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                System.out.println("Server closed\n");
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessageToAll(String msg){
        for (ClientConnection i : clientConnectionsList) {
            i.sendMsg(msg);
        }
    }

    public void deleteClient(ClientConnection client){
        clientConnectionsList.remove(client);
    }
}
