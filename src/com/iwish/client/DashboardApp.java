package com.iwish.client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardApp {

    private final int userId;
    private final String username;

    public DashboardApp(int userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public void show(Stage stage) {

        Label title = new Label("I-Wish");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        Label welcome = new Label("Welcome, " + username + "!");

        Button wishlistButton = new Button("My Wishlist");
        Button friendsButton = new Button("Friends");
        Button notificationsButton = new Button("Notifications");
        Button logoutButton = new Button("Logout");

        wishlistButton.setPrefWidth(250);
        friendsButton.setPrefWidth(250);
        notificationsButton.setPrefWidth(250);
        logoutButton.setPrefWidth(250);

        wishlistButton.setOnAction(e -> {

            WishListApp wishlistApp =
                    new WishListApp(userId);

            wishlistApp.show(stage);
        });

        friendsButton.setOnAction(e -> {

            FriendsApp friendsApp =
                    new FriendsApp(userId);

            friendsApp.show(stage);
        });

        notificationsButton.setOnAction(e -> {

            NotificationsApp notificationsApp =
                    new NotificationsApp(userId);

            notificationsApp.show(stage);
        });

        logoutButton.setOnAction(e -> {

            LoginApp loginApp = new LoginApp();

            loginApp.start(stage);
        });

        VBox layout = new VBox(
                15,
                title,
                welcome,
                wishlistButton,
                friendsButton,
                notificationsButton,
                logoutButton
        );

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        Scene scene = new Scene(layout, 500, 600);

        stage.setTitle("I-Wish - Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}