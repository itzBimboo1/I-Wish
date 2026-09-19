package com.iwish.database;

import com.iwish.model.Notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public boolean addNotification(
            int userId,
            String type,
            String message
    ) {

        String sql = """
                INSERT INTO notifications
                (user_id, type, message)
                VALUES (?, ?, ?)
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);
            statement.setString(2, type);
            statement.setString(3, message);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Notification> getNotifications(int userId) {

        List<Notification> notifications =
                new ArrayList<>();

        String sql = """
                SELECT
                    id,
                    user_id,
                    type,
                    message,
                    is_read,
                    created_at
                FROM notifications
                WHERE user_id = ?
                ORDER BY created_at DESC
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    Timestamp timestamp =
                            resultSet.getTimestamp("created_at");

                    Notification notification =
                            new Notification(
                                    resultSet.getInt("id"),
                                    resultSet.getInt("user_id"),
                                    resultSet.getString("type"),
                                    resultSet.getString("message"),
                                    resultSet.getBoolean("is_read"),
                                    timestamp != null
                                            ? timestamp.toLocalDateTime()
                                            : null
                            );

                    notifications.add(notification);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return notifications;
    }

    public boolean markAsRead(int notificationId) {

        String sql = """
                UPDATE notifications
                SET is_read = TRUE
                WHERE id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, notificationId);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean markAllAsRead(int userId) {

        String sql = """
                UPDATE notifications
                SET is_read = TRUE
                WHERE user_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}