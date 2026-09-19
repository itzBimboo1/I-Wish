package com.iwish.client;

public class ClientTest {

    public static void main(String[] args) {
        Client client = new Client();

        try {
            client.connect();

            client.sendRequest("PING");

            String response = client.receiveResponse();

            System.out.println("Server response: " + response);

            client.sendRequest("DISCONNECT");

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            client.disconnect();
        }
    }
}