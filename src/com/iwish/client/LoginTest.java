package com.iwish.client;

import java.io.*;
import java.net.Socket;

public class LoginTest {

    public static void main(String[] args) {

        try (Socket socket = new Socket("localhost", 5000);
             BufferedReader input = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(
                     socket.getOutputStream(), true)) {

            System.out.println("Server: " + input.readLine());

            output.println(
                    "LOGIN|MohamedTest|123456"
            );

            String response = input.readLine();

            System.out.println("Server: " + response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}