package com.example.notification.domain.response;

import com.example.notification.domain.NotificationStatus;
import com.example.notification.domain.NotificationType;
import com.example.notification.domain.SendType;
import com.example.notification.entity.Notification;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class NotificationResponse {
    private final Long id;
    private final String customerId;
    private final NotificationType notificationType;
    private final SendType sendType;
    private final String scheduledTime;
    private final String title;
    private final String contents;
    private final String recipient;
    private final LocalDateTime sentAt;
    private final NotificationStatus status;

    public static NotificationResponse of(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .customerId(notification.getCustomerId())
                .notificationType(notification.getNotificationType())
                .sendType(notification.getSendType())
                .scheduledTime(notification.getScheduledTime() != null
                        ? notification.getScheduledTime().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
                        : null)
                .title(notification.getTitle())
                .contents(notification.getContents())
                .recipient(notification.getRecipient())
                .sentAt(notification.getSentAt())
                .status(notification.getStatus())
                .build();
    }
}

