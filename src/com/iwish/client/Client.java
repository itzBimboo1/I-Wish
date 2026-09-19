package com.iwish.client;

import java.io.*;
import java.net.Socket;

public class Client {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    public void connect() throws IOException {
        socket = new Socket(SERVER_HOST, SERVER_PORT);

        input = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );

        output = new PrintWriter(
                socket.getOutputStream(),
                true
        );

        System.out.println("Connected to I-Wish Server");

        String response = input.readLine();

        System.out.println("Server: " + response);
    }

    public void sendRequest(String request) {
        output.println(request);
    }

    public String receiveResponse() throws IOException {
        return input.readLine();
    }

    public void disconnect() {
        try {
            if (input != null) {
                input.close();
            }

            if (output != null) {
                output.close();
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}