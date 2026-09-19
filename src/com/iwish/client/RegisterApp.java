package com.iwish.client;

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

public class RegisterApp {

    private TextField usernameField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Label statusLabel;

    public void show(Stage stage) {

        Label titleLabel = new Label("Create Account");

        titleLabel.setStyle(
                "-fx-font-size: 30px; -fx-font-weight: bold;"
        );

        Label subtitleLabel =
                new Label("Join I-Wish");

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(280);

        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(280);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(280);

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setMaxWidth(280);

        Button registerButton =
                new Button("Create Account");

        registerButton.setPrefWidth(280);

        Button backButton =
                new Button("Back to Sign In");

        backButton.setPrefWidth(280);

        statusLabel = new Label();

        registerButton.setOnAction(
                event -> register()
        );

        backButton.setOnAction(event -> {

            LoginApp loginApp =
                    new LoginApp();

            loginApp.start(stage);
        });

        VBox layout = new VBox(
                15,
                titleLabel,
                subtitleLabel,
                usernameField,
                emailField,
                passwordField,
                confirmPasswordField,
                registerButton,
                backButton,
                statusLabel
        );

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        Scene scene =
                new Scene(layout, 500, 650);

        stage.setTitle(
                "I-Wish - Create Account"
        );

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void register() {

        String username =
                usernameField.getText().trim();

        String email =
                emailField.getText().trim();

        String password =
                passwordField.getText();

        String confirmPassword =
                confirmPasswordField.getText();

        if (username.isEmpty()
                || email.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()) {

            statusLabel.setText(
                    "Please fill in all fields."
            );

            return;
        }

        if (!password.equals(confirmPassword)) {

            statusLabel.setText(
                    "Passwords do not match."
            );

            return;
        }

        if (!email.contains("@")) {

            statusLabel.setText(
                    "Enter a valid email."
            );

            return;
        }

        Client client = new Client();

        try {

            client.connect();

            client.sendRequest(
                    "REGISTER|"
                            + username
                            + "|"
                            + email
                            + "|"
                            + password
            );

            String response =
                    client.receiveResponse();

            if ("REGISTER_SUCCESS".equals(response)) {

                statusLabel.setText(
                        "Account created successfully!"
                );

                usernameField.clear();
                emailField.clear();
                passwordField.clear();
                confirmPasswordField.clear();

            } else {

                statusLabel.setText(
                        "Username or email already exists."
                );
            }

        } catch (IOException e) {

            statusLabel.setText(
                    "Could not connect to server."
            );

            e.printStackTrace();

        } finally {

            client.disconnect();
        }
    }
}