package com.iwish.client;

import com.iwish.database.ItemDAO;
import com.iwish.database.WishListDAO;
import com.iwish.database.WishListItemDAO;
import com.iwish.model.Item;
import com.iwish.model.WishList;
import com.iwish.model.WishListItem;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class WishListApp {

    private final int userId;

    private final WishListDAO wishListDAO = new WishListDAO();
    private final WishListItemDAO wishListItemDAO = new WishListItemDAO();
    private final ItemDAO itemDAO = new ItemDAO();

    private final ListView<WishListItem> wishlistView = new ListView<>();
    private final ComboBox<Item> itemComboBox = new ComboBox<>();
    private final TextField priceField = new TextField();
    private final Label statusLabel = new Label();

    private int wishlistId;

    public WishListApp(int userId) {
        this.userId = userId;
    }

    public void show(Stage stage) {

        WishList wishlist = wishListDAO.getWishlistByUserId(userId);

        if (wishlist == null) {
            wishlistId = wishListDAO.createWishlist(userId);
        } else {
            wishlistId = wishlist.getId();
        }

        loadItems();
        loadWishlistItems();

        Label title = new Label("My Wishlist");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        itemComboBox.setPrefWidth(250);

        priceField.setPromptText("Target Price");
        priceField.setPrefWidth(150);

        Button addButton = new Button("Add Item");
        Button removeButton = new Button("Remove Item");
        Button backButton = new Button("Back");

        addButton.setOnAction(e -> addItem());

        removeButton.setOnAction(e -> removeItem());

        backButton.setOnAction(e -> {
            DashboardApp dashboard =
                    new DashboardApp(userId, getUsername());

            dashboard.show(stage);
        });

        HBox addBox = new HBox(
                10,
                itemComboBox,
                priceField,
                addButton
        );

        VBox layout = new VBox(
                15,
                title,
                addBox,
                wishlistView,
                removeButton,
                statusLabel,
                backButton
        );

        layout.setPadding(new Insets(25));

        Scene scene = new Scene(layout, 800, 600);

        stage.setTitle("I-Wish - My Wishlist");
        stage.setScene(scene);
        stage.show();
    }

    private void loadItems() {

        List<Item> items = itemDAO.getAllItems();

        itemComboBox.getItems().clear();
        itemComboBox.getItems().addAll(items);
    }

    private void loadWishlistItems() {

        List<WishListItem> items =
                wishListItemDAO.getItemsByWishlistId(wishlistId);

        wishlistView.getItems().clear();
        wishlistView.getItems().addAll(items);
    }

    private void addItem() {

        Item selectedItem = itemComboBox.getValue();

        if (selectedItem == null) {
            statusLabel.setText("Please select an item.");
            return;
        }

        double targetPrice;

        try {
            targetPrice = Double.parseDouble(priceField.getText());

        } catch (NumberFormatException e) {
            statusLabel.setText("Enter a valid price.");
            return;
        }

        if (targetPrice <= 0) {
            statusLabel.setText("Price must be greater than zero.");
            return;
        }

        boolean added = wishListItemDAO.addItem(
                wishlistId,
                selectedItem.getId(),
                targetPrice
        );

        if (added) {

            statusLabel.setText("Item added successfully.");

            priceField.clear();
            itemComboBox.getSelectionModel().clearSelection();

            loadWishlistItems();

        } else {
            statusLabel.setText("Could not add item.");
        }
    }

    private void removeItem() {

        WishListItem selectedItem =
                wishlistView.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            statusLabel.setText("Select an item to remove.");
            return;
        }

        boolean removed =
                wishListItemDAO.removeItem(selectedItem.getId());

        if (removed) {

            statusLabel.setText("Item removed successfully.");

            loadWishlistItems();

        } else {
            statusLabel.setText("Could not remove item.");
        }
    }

    private String getUsername() {
        return "User";
    }
}