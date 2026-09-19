package com.iwish.client;

import com.iwish.database.ContributionDAO;
import com.iwish.database.WishListDAO;
import com.iwish.database.WishListItemDAO;
import com.iwish.model.WishList;
import com.iwish.model.WishListItem;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class FriendWishListApp {

    private final int currentUserId;
    private final int friendId;
    private final String friendUsername;

    private final WishListDAO wishListDAO =
            new WishListDAO();

    private final WishListItemDAO wishListItemDAO =
            new WishListItemDAO();

    private final ContributionDAO contributionDAO =
            new ContributionDAO();

    private final ListView<WishListItem> wishlistView =
            new ListView<>();

    private final Label statusLabel =
            new Label();

    public FriendWishListApp(
            int currentUserId,
            int friendId,
            String friendUsername
    ) {
        this.currentUserId = currentUserId;
        this.friendId = friendId;
        this.friendUsername = friendUsername;
    }

    public void show(Stage stage) {

        Label title =
                new Label(friendUsername + "'s Wishlist");

        title.setStyle(
                "-fx-font-size: 28px; -fx-font-weight: bold;"
        );

        wishlistView.setCellFactory(
                list -> new WishListItemCell()
        );

        Button contributeButton =
                new Button("Contribute");

        contributeButton.setOnAction(
                e -> handleContribute()
        );

        Button backButton =
                new Button("Back");

        backButton.setOnAction(e -> {

            FriendsApp friendsApp =
                    new FriendsApp(currentUserId);

            friendsApp.show(stage);
        });

        loadWishlist();

        VBox layout = new VBox(
                15,
                title,
                wishlistView,
                contributeButton,
                statusLabel,
                backButton
        );

        layout.setPadding(
                new Insets(25)
        );

        Scene scene =
                new Scene(layout, 800, 600);

        stage.setTitle(
                "I-Wish - " +
                        friendUsername +
                        "'s Wishlist"
        );

        stage.setScene(scene);
        stage.show();
    }

    private void loadWishlist() {

        wishlistView.getItems().clear();
        statusLabel.setText("");

        WishList wishlist =
                wishListDAO.getWishlistByUserId(friendId);

        if (wishlist == null) {

            statusLabel.setText(
                    "This friend does not have a wishlist."
            );

            return;
        }

        List<WishListItem> items =
                wishListItemDAO.getItemsByWishlistId(
                        wishlist.getId()
                );

        wishlistView
                .getItems()
                .addAll(items);

        if (items.isEmpty()) {

            statusLabel.setText(
                    "This wishlist is empty."
            );
        }
    }

    private void handleContribute() {

        WishListItem selectedItem =
                wishlistView
                        .getSelectionModel()
                        .getSelectedItem();

        if (selectedItem == null) {

            statusLabel.setText(
                    "Select an item first."
            );

            return;
        }

        if ("COMPLETED".equals(
                selectedItem.getStatus()
        )) {

            statusLabel.setText(
                    "This item has already been completed."
            );

            return;
        }

        double remaining =
                selectedItem.getTargetPrice()
                        - selectedItem.getCollectedAmount();

        if (remaining <= 0) {

            statusLabel.setText(
                    "This item has already been completed."
            );

            return;
        }

        TextInputDialog dialog =
                new TextInputDialog();

        dialog.setTitle("Contribute");

        dialog.setHeaderText(
                "Contribute to " +
                        selectedItem.getItemName()
        );

        dialog.setContentText(
                "Remaining amount: " +
                        String.format("%.2f", remaining) +
                        "\nEnter amount:"
        );

        Optional<String> result =
                dialog.showAndWait();

        if (result.isEmpty()) {
            return;
        }

        double amount;

        try {

            amount =
                    Double.parseDouble(
                            result.get().trim()
                    );

        } catch (NumberFormatException e) {

            showAlert(
                    "Invalid Amount",
                    "Please enter a valid number."
            );

            return;
        }

        if (amount <= 0) {

            showAlert(
                    "Invalid Amount",
                    "Amount must be greater than zero."
            );

            return;
        }

        if (amount > remaining) {

            showAlert(
                    "Invalid Amount",
                    "Amount cannot exceed the remaining amount."
            );

            return;
        }

        boolean success =
                contributionDAO.contribute(
                        selectedItem.getId(),
                        currentUserId,
                        amount
                );

        if (success) {

            statusLabel.setText(
                    "Contribution added successfully."
            );

            loadWishlist();

        } else {

            showAlert(
                    "Contribution Failed",
                    "Could not add the contribution."
            );
        }
    }

    private void showAlert(
            String title,
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class WishListItemCell
            extends ListCell<WishListItem> {

        @Override
        protected void updateItem(
                WishListItem item,
                boolean empty
        ) {

            super.updateItem(item, empty);

            if (empty || item == null) {

                setText(null);

                return;
            }

            double target =
                    item.getTargetPrice();

            double collected =
                    item.getCollectedAmount();

            double remaining =
                    target - collected;

            String text =
                    String.format(
                            "%s | Target: %.2f | Collected: %.2f | Remaining: %.2f | Status: %s",
                            item.getItemName(),
                            target,
                            collected,
                            remaining,
                            item.getStatus()
                    );

            setText(text);
        }
    }
}