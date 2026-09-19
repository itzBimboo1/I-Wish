package com.iwish.client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginApp extends Application {

    private TextField usernameField;
    private PasswordField passwordField;
    private Label statusLabel;

    @Override
    public void start(Stage stage) {

        Label titleLabel = new Label("I-Wish");
        titleLabel.setStyle(
                "-fx-font-size: 32px; -fx-font-weight: bold;"
        );

        Label subtitleLabel = new Label("Welcome back!");

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(280);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(280);

        Button loginButton = new Button("Sign In");
        loginButton.setPrefWidth(280);

        Button registerButton = new Button("Create Account");
        registerButton.setPrefWidth(280);

        statusLabel = new Label();

        loginButton.setOnAction(event -> login());

        registerButton.setOnAction(event -> {

            RegisterApp registerApp =
                    new RegisterApp();

            registerApp.show(
                    (Stage) usernameField
                            .getScene()
                            .getWindow()
            );
        });

        VBox layout = new VBox(
                15,
                titleLabel,
                subtitleLabel,
                usernameField,
                passwordField,
                loginButton,
                registerButton,
                statusLabel
        );

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        Scene scene = new Scene(layout, 500, 600);

        stage.setTitle("I-Wish");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void login() {

        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password.");
            return;
        }

        Client client = new Client();

        try {

            client.connect();

            client.sendRequest(
                    "LOGIN|" + username + "|" + password
            );

            String response = client.receiveResponse();

            if (response != null && response.startsWith("LOGIN_SUCCESS")) {

                String[] parts = response.split("\\|");

                int userId = Integer.parseInt(parts[1]);
                String loggedInUsername = parts[2];

                DashboardApp dashboard =
                        new DashboardApp(userId, loggedInUsername);

                dashboard.show(
                        (Stage) usernameField.getScene().getWindow()
                );

            } else {

                statusLabel.setText("Invalid username or password.");
            }

        } catch (IOException e) {

            statusLabel.setText("Could not connect to server.");
            e.printStackTrace();

        } finally {

            client.disconnect();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}