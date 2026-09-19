package com.iwish.client;

import com.iwish.database.NotificationDAO;
import com.iwish.model.Notification;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class NotificationsApp {

    private final int userId;

    private final NotificationDAO notificationDAO =
            new NotificationDAO();

    private final ListView<Notification> notificationsView =
            new ListView<>();

    private final Label statusLabel =
            new Label();

    public NotificationsApp(int userId) {
        this.userId = userId;
    }

    public void show(Stage stage) {

        Label title =
                new Label("Notifications");

        title.setStyle(
                "-fx-font-size: 28px; -fx-font-weight: bold;"
        );

        notificationsView.setCellFactory(
                list -> new NotificationCell()
        );

        Button markAllButton =
                new Button("Mark All as Read");

        markAllButton.setOnAction(
                e -> markAllAsRead()
        );

        Button backButton =
                new Button("Back");

        backButton.setOnAction(e -> {

            DashboardApp dashboard =
                    new DashboardApp(
                            userId,
                            "User"
                    );

            dashboard.show(stage);
        });

        loadNotifications();

        VBox layout = new VBox(
                15,
                title,
                notificationsView,
                markAllButton,
                statusLabel,
                backButton
        );

        layout.setPadding(
                new Insets(25)
        );

        Scene scene =
                new Scene(layout, 800, 600);

        stage.setTitle(
                "I-Wish - Notifications"
        );

        stage.setScene(scene);
        stage.show();
    }

    private void loadNotifications() {

        List<Notification> notifications =
                notificationDAO.getNotifications(userId);

        notificationsView.getItems().clear();

        notificationsView
                .getItems()
                .addAll(notifications);

        if (notifications.isEmpty()) {

            statusLabel.setText(
                    "No notifications."
            );

        } else {

            statusLabel.setText(
                    notifications.size()
                            + " notification(s)"
            );
        }
    }

    private void markAllAsRead() {

        boolean updated =
                notificationDAO.markAllAsRead(userId);

        if (updated) {

            statusLabel.setText(
                    "All notifications marked as read."
            );

        } else {

            statusLabel.setText(
                    "No unread notifications."
            );
        }

        loadNotifications();
    }

    private class NotificationCell
            extends ListCell<Notification> {

        @Override
        protected void updateItem(
                Notification notification,
                boolean empty
        ) {

            super.updateItem(
                    notification,
                    empty
            );

            if (empty || notification == null) {

                setText(null);

                return;
            }

            String status =
                    notification.isRead()
                            ? "READ"
                            : "NEW";

            setText(
                    "[" + status + "] "
                            + notification.getMessage()
            );
        }
    }
}