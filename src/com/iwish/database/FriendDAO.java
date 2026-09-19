package com.iwish.database;

import com.iwish.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FriendDAO {

    public List<User> getFriends(int userId) {

        List<User> friends = new ArrayList<>();

        String sql = """
                SELECT
                    u.id,
                    u.username,
                    u.email,
                    u.password
                FROM friendships f
                JOIN users u
                    ON f.friend_id = u.id
                WHERE f.user_id = ?
                ORDER BY u.username
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    User friend = new User(
                            resultSet.getInt("id"),
                            resultSet.getString("username"),
                            resultSet.getString("email"),
                            resultSet.getString("password")
                    );

                    friends.add(friend);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return friends;
    }
}