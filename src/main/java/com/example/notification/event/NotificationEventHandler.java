package com.example.notification.event;

import com.example.notification.repository.NotificationRepository;
import com.example.notification.service.sender.EmailNotificationSenderService;
import com.example.notification.service.sender.KakaoNotificationSenderService;
import com.example.notification.service.sender.SmsNotificationSenderService;
import com.example.notification.domain.NotificationStatus;
import com.example.notification.domain.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationEventHandler {

    private final NotificationRepository notificationRepository;
    private final SmsNotificationSenderService smsSenderService;
    private final KakaoNotificationSenderService kakaoSenderService;
    private final EmailNotificationSenderService emailSenderService;

    @Async
    @EventListener
    public void handleNotificationSendEvent(NotificationSendEvent event) {
        var notification = event.notification();

        try {
            CompletableFuture<Boolean> futureResult = null;

            if (notification.getNotificationType() == NotificationType.SMS) {
                log.info("SMS 발송 시작: {}", notification);
                futureResult = smsSenderService.send(notification);
            } else if (notification.getNotificationType() == NotificationType.KAKAO) {
                log.info("카카오톡 발송 시작: {}", notification);
                futureResult = kakaoSenderService.send(notification);
            } else if (notification.getNotificationType() == NotificationType.EMAIL) {
                log.info("이메일 발송 시작: {}", notification);
                futureResult = emailSenderService.send(notification);
            }

            if (futureResult != null) {
                futureResult.thenAccept(result -> {
                    if (result) {
                        notification.setStatus(NotificationStatus.SENT);
                        notification.setSentAt(LocalDateTime.now());
                    } else {
                        notification.setStatus(NotificationStatus.FAILED);
                    }
                    notificationRepository.save(notification);
                }).exceptionally(ex -> {
                    log.error("알림 발송 실패: {}", ex.getMessage());
                    notification.setStatus(NotificationStatus.FAILED);
                    notificationRepository.save(notification);
                    return null;
                });
            }

        } catch (Exception e) {
            log.error("알림 발송 중 오류 발생: {}", e.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);
        }
    }
}

