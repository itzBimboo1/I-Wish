package com.iwish.server;

import com.iwish.database.UserDAO;
import com.iwish.model.User;

public class RequestHandler {

    private final UserDAO userDAO = new UserDAO();

    public String handle(String request) {

        if (request == null || request.isBlank()) {
            return "INVALID_REQUEST";
        }

        String[] parts = request.split("\\|", -1);

        String command = parts[0];

        switch (command) {

            case "PING":
                return "PONG";

            case "REGISTER":
                return register(parts);

            case "LOGIN":
                return login(parts);

            default:
                return "UNKNOWN_REQUEST";
        }
    }

    private String register(String[] parts) {

        if (parts.length != 4) {
            return "INVALID_REGISTER_REQUEST";
        }

        String username = parts[1];
        String email = parts[2];
        String password = parts[3];

        if (username.isBlank()
                || email.isBlank()
                || password.isBlank()) {
            return "INVALID_REGISTER_DATA";
        }

        User user = new User(
                username,
                email,
                password
        );

        boolean registered = userDAO.register(user);

        if (registered) {
            return "REGISTER_SUCCESS";
        }

        return "REGISTER_FAILED";
    }

    private String login(String[] parts) {

        if (parts.length != 3) {
            return "INVALID_LOGIN_REQUEST";
        }

        String username = parts[1];
        String password = parts[2];

        if (username.isBlank() || password.isBlank()) {
            return "INVALID_LOGIN_DATA";
        }

        User user = userDAO.login(username, password);

        if (user != null) {
            return "LOGIN_SUCCESS|" + user.getId()
                    + "|" + user.getUsername()
                    + "|" + user.getEmail();
        }

        return "LOGIN_FAILED";
    }
}