package com.iwish.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ContributionDAO {

    public boolean contribute(
            int wishlistItemId,
            int buyerId,
            double amount
    ) {

        if (amount <= 0) {
            return false;
        }

        String checkSql = """
                SELECT
                    wi.target_price,
                    wi.collected_amount,
                    wi.status,
                    wi.wishlist_id,
                    w.user_id AS owner_id,
                    i.name AS item_name,
                    buyer.username AS buyer_username,
                    owner.username AS owner_username
                FROM wishlist_items wi
                JOIN wishlists w
                    ON wi.wishlist_id = w.id
                JOIN items i
                    ON wi.item_id = i.id
                JOIN users owner
                    ON w.user_id = owner.id
                JOIN users buyer
                    ON buyer.id = ?
                WHERE wi.id = ?
                """;

        String insertContributionSql = """
                INSERT INTO contributions
                (wishlist_item_id, buyer_id, amount)
                VALUES (?, ?, ?)
                """;

        String updateWishlistItemSql = """
                UPDATE wishlist_items
                SET
                    collected_amount = collected_amount + ?,
                    status = CASE
                        WHEN collected_amount + ? >= target_price
                        THEN 'COMPLETED'
                        ELSE 'AVAILABLE'
                    END
                WHERE id = ?
                  AND status = 'AVAILABLE'
                """;

        String buyerNotificationSql = """
                INSERT INTO notifications
                (user_id, type, message)
                VALUES (?, 'GIFT_COMPLETED', ?)
                """;

        String ownerNotificationSql = """
                INSERT INTO notifications
                (user_id, type, message)
                VALUES (?, 'ITEM_BOUGHT', ?)
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection()
        ) {

            connection.setAutoCommit(false);

            try (
                    PreparedStatement checkStatement =
                            connection.prepareStatement(checkSql);

                    PreparedStatement contributionStatement =
                            connection.prepareStatement(
                                    insertContributionSql
                            );

                    PreparedStatement updateStatement =
                            connection.prepareStatement(
                                    updateWishlistItemSql
                            );

                    PreparedStatement buyerNotificationStatement =
                            connection.prepareStatement(
                                    buyerNotificationSql
                            );

                    PreparedStatement ownerNotificationStatement =
                            connection.prepareStatement(
                                    ownerNotificationSql
                            )
            ) {

                checkStatement.setInt(1, buyerId);
                checkStatement.setInt(2, wishlistItemId);

                double targetPrice;
                double collectedAmount;
                int ownerId;
                String status;
                String itemName;
                String buyerUsername;
                String ownerUsername;

                try (
                        ResultSet resultSet =
                                checkStatement.executeQuery()
                ) {

                    if (!resultSet.next()) {
                        connection.rollback();
                        return false;
                    }

                    targetPrice =
                            resultSet.getDouble("target_price");

                    collectedAmount =
                            resultSet.getDouble(
                                    "collected_amount"
                            );

                    status =
                            resultSet.getString("status");

                    ownerId =
                            resultSet.getInt("owner_id");

                    itemName =
                            resultSet.getString("item_name");

                    buyerUsername =
                            resultSet.getString(
                                    "buyer_username"
                            );

                    ownerUsername =
                            resultSet.getString(
                                    "owner_username"
                            );
                }

                if (buyerId == ownerId) {
                    connection.rollback();
                    return false;
                }

                if ("COMPLETED".equals(status)) {
                    connection.rollback();
                    return false;
                }

                double remaining =
                        targetPrice - collectedAmount;

                if (amount > remaining) {
                    connection.rollback();
                    return false;
                }

                contributionStatement.setInt(
                        1,
                        wishlistItemId
                );

                contributionStatement.setInt(
                        2,
                        buyerId
                );

                contributionStatement.setDouble(
                        3,
                        amount
                );

                int contributionRows =
                        contributionStatement.executeUpdate();

                if (contributionRows == 0) {
                    connection.rollback();
                    return false;
                }

                updateStatement.setDouble(
                        1,
                        amount
                );

                updateStatement.setDouble(
                        2,
                        amount
                );

                updateStatement.setInt(
                        3,
                        wishlistItemId
                );

                int updatedRows =
                        updateStatement.executeUpdate();

                if (updatedRows == 0) {
                    connection.rollback();
                    return false;
                }

                double newCollectedAmount =
                        collectedAmount + amount;

                boolean completed =
                        newCollectedAmount >= targetPrice;

                if (completed) {

                    String buyerMessage =
                            "Your contribution completed the gift item '"
                                    + itemName
                                    + "' for "
                                    + ownerUsername
                                    + ".";

                    buyerNotificationStatement.setInt(
                            1,
                            buyerId
                    );

                    buyerNotificationStatement.setString(
                            2,
                            buyerMessage
                    );

                    buyerNotificationStatement.executeUpdate();

                    String ownerMessage =
                            buyerUsername
                                    + " completed the purchase of '"
                                    + itemName
                                    + "' from your wishlist.";

                    ownerNotificationStatement.setInt(
                            1,
                            ownerId
                    );

                    ownerNotificationStatement.setString(
                            2,
                            ownerMessage
                    );

                    ownerNotificationStatement.executeUpdate();
                }

                connection.commit();

                return true;

            } catch (Exception e) {

                connection.rollback();

                e.printStackTrace();

                return false;
            }

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }
}