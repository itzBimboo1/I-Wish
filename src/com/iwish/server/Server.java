package com.iwish.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int PORT = 5000;
    private ServerSocket serverSocket;
    private boolean running;

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;

            System.out.println("I-Wish Server started on port " + PORT);

            while (running) {
                Socket clientSocket = serverSocket.accept();

                System.out.println(
                        "Client connected: "
                                + clientSocket.getInetAddress()
                );

                ClientHandler clientHandler =
                        new ClientHandler(clientSocket);

                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            if (running) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running = false;

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            System.out.println("I-Wish Server stopped.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}