package com.iwish.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;

    private final RequestHandler requestHandler = new RequestHandler();

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())
            );

            output = new PrintWriter(
                    clientSocket.getOutputStream(),
                    true
            );

            output.println("CONNECTED");

            String request;

            while ((request = input.readLine()) != null) {

                System.out.println(
                        "Client request: " + request
                );

                handleRequest(request);
            }

        } catch (IOException e) {
            System.out.println("Client disconnected.");

        } finally {
            closeConnection();
        }
    }

    private void handleRequest(String request) {
        String response = requestHandler.handle(request);
        output.println(response);
    }

    private void closeConnection() {
        try {
            if (input != null) {
                input.close();
            }

            if (output != null) {
                output.close();
            }

            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}