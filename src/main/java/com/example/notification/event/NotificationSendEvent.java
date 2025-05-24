package com.example.notification.event;

import com.example.notification.entity.Notification;

public record NotificationSendEvent(Notification notification) {
}