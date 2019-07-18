package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientConnection implements Runnable {
    private Server server;
    private PrintWriter out;
    private Scanner in;
    private static final int PORT = 1234;
    private static final String HOST = "localhost";

    private Socket clientSocket = null;
    private static int clientCount = 0;
    ClientConnection(Socket clientSocket, Server server)  {
        try {
            clientCount++;
            this.server = server;
            this.clientSocket = clientSocket;
            this.out = new PrintWriter(clientSocket.getOutputStream());
            this.in = new Scanner(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            server.sendMessageToAll("New client joined!");
            server.sendMessageToAll("Number of clients: " + clientCount);
            while (true) {
                if (in.hasNext()) {
                    String clientMsg = in.nextLine();
                    if (clientMsg.endsWith("exit")) {
                        break;
                    }
                    System.out.println(clientMsg);
                    server.sendMessageToAll(clientMsg);
                }
                Thread.sleep(100);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();

        } finally {
            this.close();
        }
    }

    public void sendMsg(String msg) {
        try{
            out.println(msg);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        server.deleteClient(this);
        clientCount--;
        server.sendMessageToAll("Number of clients: " + clientCount + "\n");
    }
}
