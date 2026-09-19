package com.iwish.client;

import com.iwish.database.FriendDAO;
import com.iwish.database.FriendRequestDAO;
import com.iwish.database.UserDAO;
import com.iwish.model.FriendRequest;
import com.iwish.model.User;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class FriendsApp {

    private final int userId;

    private final FriendDAO friendDAO = new FriendDAO();
    private final FriendRequestDAO friendRequestDAO =
            new FriendRequestDAO();
    private final UserDAO userDAO = new UserDAO();

    private final ListView<User> friendsView = new ListView<>();
    private final ListView<FriendRequest> requestsView =
            new ListView<>();

    private final TextField friendUsernameField = new TextField();
    private final Label statusLabel = new Label();

    public FriendsApp(int userId) {
        this.userId = userId;
    }

    public void show(Stage stage) {

        loadFriends();
        loadRequests();

        Label title = new Label("Friends");
        title.setStyle(
                "-fx-font-size: 28px; -fx-font-weight: bold;"
        );

        Label friendsLabel = new Label("My Friends");
        friendsLabel.setStyle(
                "-fx-font-size: 18px; -fx-font-weight: bold;"
        );

        Label requestsLabel = new Label("Friend Requests");
        requestsLabel.setStyle(
                "-fx-font-size: 18px; -fx-font-weight: bold;"
        );

        friendUsernameField.setPromptText("Friend username");
        friendUsernameField.setPrefWidth(220);

        Button sendButton = new Button("Send Request");

        sendButton.setOnAction(e -> sendRequest());

        Button viewWishlistButton =
                new Button("View Wishlist");

        viewWishlistButton.setOnAction(e ->
                viewFriendWishlist(stage)
        );

        Button acceptButton = new Button("Accept");

        acceptButton.setOnAction(e -> acceptRequest());

        Button declineButton = new Button("Decline");

        declineButton.setOnAction(e -> declineRequest());

        Button backButton = new Button("Back");

        backButton.setOnAction(e -> {

            DashboardApp dashboard =
                    new DashboardApp(userId, "User");

            dashboard.show(stage);
        });

        HBox sendBox = new HBox(
                10,
                friendUsernameField,
                sendButton
        );

        HBox friendButtons = new HBox(
                10,
                viewWishlistButton
        );

        HBox requestButtons = new HBox(
                10,
                acceptButton,
                declineButton
        );

        VBox layout = new VBox(
                15,
                title,
                friendsLabel,
                friendsView,
                friendButtons,
                requestsLabel,
                requestsView,
                requestButtons,
                sendBox,
                statusLabel,
                backButton
        );

        layout.setPadding(new Insets(25));

        Scene scene = new Scene(layout, 800, 750);

        stage.setTitle("I-Wish - Friends");
        stage.setScene(scene);
        stage.show();
    }

    private void loadFriends() {

        List<User> friends =
                friendDAO.getFriends(userId);

        friendsView.getItems().clear();
        friendsView.getItems().addAll(friends);
    }

    private void loadRequests() {

        List<FriendRequest> requests =
                friendRequestDAO.getPendingRequests(userId);

        requestsView.getItems().clear();
        requestsView.getItems().addAll(requests);
    }

    private void sendRequest() {

        String username =
                friendUsernameField.getText().trim();

        if (username.isEmpty()) {
            statusLabel.setText(
                    "Enter a username."
            );
            return;
        }

        User targetUser =
                findUserByUsername(username);

        if (targetUser == null) {
            statusLabel.setText(
                    "User not found."
            );
            return;
        }

        if (targetUser.getId() == userId) {
            statusLabel.setText(
                    "You cannot add yourself."
            );
            return;
        }

        boolean sent =
                friendRequestDAO.sendRequest(
                        userId,
                        targetUser.getId()
                );

        if (sent) {

            statusLabel.setText(
                    "Friend request sent."
            );

            friendUsernameField.clear();

        } else {

            statusLabel.setText(
                    "Could not send friend request."
            );
        }
    }

    private User findUserByUsername(String username) {

        return userDAO.getUserByUsername(username);
    }

    private void acceptRequest() {

        FriendRequest request =
                requestsView
                        .getSelectionModel()
                        .getSelectedItem();

        if (request == null) {
            statusLabel.setText(
                    "Select a request first."
            );
            return;
        }

        boolean accepted =
                friendRequestDAO.acceptRequest(
                        request.getId()
                );

        if (accepted) {

            statusLabel.setText(
                    "Friend request accepted."
            );

            loadRequests();
            loadFriends();

        } else {

            statusLabel.setText(
                    "Could not accept request."
            );
        }
    }

    private void declineRequest() {

        FriendRequest request =
                requestsView
                        .getSelectionModel()
                        .getSelectedItem();

        if (request == null) {
            statusLabel.setText(
                    "Select a request first."
            );
            return;
        }

        boolean declined =
                friendRequestDAO.declineRequest(
                        request.getId()
                );

        if (declined) {

            statusLabel.setText(
                    "Friend request declined."
            );

            loadRequests();

        } else {

            statusLabel.setText(
                    "Could not decline request."
            );
        }
    }

    private void viewFriendWishlist(Stage stage) {

        User selectedFriend =
                friendsView
                        .getSelectionModel()
                        .getSelectedItem();

        if (selectedFriend == null) {

            statusLabel.setText(
                    "Select a friend first."
            );

            return;
        }

        FriendWishListApp friendWishlistApp =
                new FriendWishListApp(
                        userId,
                        selectedFriend.getId(),
                        selectedFriend.getUsername()
                );

        friendWishlistApp.show(stage);
    }
}