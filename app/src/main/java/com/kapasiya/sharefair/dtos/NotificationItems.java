package com.kapasiya.sharefair.dtos;

public class NotificationItems {
    private String notificationTitle;
    private String notificationDateTime;

    public NotificationItems() {
    }

    public NotificationItems(String notificationTitle, String notificationDateTime) {
        this.notificationTitle = notificationTitle;
        this.notificationDateTime = notificationDateTime;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationDateTime() {
        return notificationDateTime;
    }

    public void setNotificationDateTime(String notificationDateTime) {
        this.notificationDateTime = notificationDateTime;
    }
}
