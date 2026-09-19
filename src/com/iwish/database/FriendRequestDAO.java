package com.iwish.database;

import com.iwish.model.FriendRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestDAO {

    public boolean sendRequest(int senderId, int receiverId) {

        if (senderId == receiverId) {
            return false;
        }

        String sql = """
                INSERT INTO friend_requests
                (sender_id, receiver_id)
                VALUES (?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, senderId);
            statement.setInt(2, receiverId);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<FriendRequest> getPendingRequests(int userId) {

        List<FriendRequest> requests = new ArrayList<>();

        String sql = """
                SELECT
                    fr.id,
                    fr.sender_id,
                    fr.receiver_id,
                    sender.username AS sender_username,
                    receiver.username AS receiver_username,
                    fr.status
                FROM friend_requests fr
                JOIN users sender
                    ON fr.sender_id = sender.id
                JOIN users receiver
                    ON fr.receiver_id = receiver.id
                WHERE fr.receiver_id = ?
                  AND fr.status = 'PENDING'
                ORDER BY fr.created_at DESC
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    FriendRequest request = new FriendRequest(
                            resultSet.getInt("id"),
                            resultSet.getInt("sender_id"),
                            resultSet.getInt("receiver_id"),
                            resultSet.getString("sender_username"),
                            resultSet.getString("receiver_username"),
                            resultSet.getString("status")
                    );

                    requests.add(request);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return requests;
    }

    public boolean acceptRequest(int requestId) {

        String sql = """
                UPDATE friend_requests
                SET status = 'ACCEPTED'
                WHERE id = ?
                  AND status = 'PENDING'
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, requestId);

            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return createFriendship(requestId);
    }

    public boolean declineRequest(int requestId) {

        String sql = """
                UPDATE friend_requests
                SET status = 'DECLINED'
                WHERE id = ?
                  AND status = 'PENDING'
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, requestId);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean createFriendship(int requestId) {

        String sql = """
                INSERT INTO friendships (user_id, friend_id)
                SELECT sender_id, receiver_id
                FROM friend_requests
                WHERE id = ?
                """;

        String reverseSql = """
                INSERT INTO friendships (user_id, friend_id)
                SELECT receiver_id, sender_id
                FROM friend_requests
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                PreparedStatement reverseStatement =
                        connection.prepareStatement(reverseSql)
        ) {

            statement.setInt(1, requestId);
            reverseStatement.setInt(1, requestId);

            statement.executeUpdate();
            reverseStatement.executeUpdate();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}