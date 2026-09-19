package com.iwish.database;

import com.iwish.model.WishListItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class WishListItemDAO {

    public List<WishListItem> getItemsByWishlistId(int wishlistId) {

        List<WishListItem> items = new ArrayList<>();

        String sql = """
                SELECT
                    wi.id,
                    wi.wishlist_id,
                    wi.item_id,
                    i.name,
                    i.description,
                    wi.target_price,
                    wi.collected_amount,
                    wi.status
                FROM wishlist_items wi
                JOIN items i ON wi.item_id = i.id
                WHERE wi.wishlist_id = ?
                ORDER BY i.name
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, wishlistId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    WishListItem item = new WishListItem(
                            resultSet.getInt("id"),
                            resultSet.getInt("wishlist_id"),
                            resultSet.getInt("item_id"),
                            resultSet.getString("name"),
                            resultSet.getString("description"),
                            resultSet.getDouble("target_price"),
                            resultSet.getDouble("collected_amount"),
                            resultSet.getString("status")
                    );

                    items.add(item);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public boolean addItem(
            int wishlistId,
            int itemId,
            double targetPrice
    ) {

        String sql = """
                INSERT INTO wishlist_items
                (wishlist_id, item_id, target_price)
                VALUES (?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, wishlistId);
            statement.setInt(2, itemId);
            statement.setDouble(3, targetPrice);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeItem(int wishlistItemId) {

        String sql = """
                DELETE FROM wishlist_items
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, wishlistItemId);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}