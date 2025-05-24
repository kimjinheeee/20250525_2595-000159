package com.example.notification.entity;

import com.example.notification.domain.NotificationStatus;
import com.example.notification.domain.NotificationType;
import com.example.notification.domain.SendType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerId;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;  // SMS, KAKAO, EMAIL

    @Enumerated(EnumType.STRING)
    private SendType sendType;  // IMMEDIATE, SCHEDULED

    private LocalDateTime scheduledTime;  // 예약 발송 시간

    private String title;
    private String contents;
    private String recipient;  // 전화번호, 카카오톡 ID, 이메일 주소

    private LocalDateTime sentAt; // 실 발송 시간

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;  // PENDING, SENT, FAILED
}