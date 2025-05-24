package com.example.notification.service.sender;

import com.example.notification.entity.Notification;
import com.example.notification.domain.NotificationType;
import com.example.notification.client.EmailNotificationClient;
import com.example.notification.domain.client.request.EmailRequest;
import com.example.notification.domain.client.response.EmailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@Qualifier("emailSenderService")
@RequiredArgsConstructor
public class EmailNotificationSenderService {

    private final EmailNotificationClient emailNotificationClient;

    @Async
    public CompletableFuture<Boolean> send(Notification notification) {
        if (notification.getNotificationType() != NotificationType.EMAIL) {
            return CompletableFuture.completedFuture(false);
        }
        try {
            EmailRequest request = EmailRequest.builder()
                    .emailAddress(notification.getRecipient())
                    .title(notification.getTitle())
                    .contents(notification.getContents())
                    .build();
            EmailResponse response = emailNotificationClient.sendEmail(request);
            boolean result = response != null && "SUCCESS".equalsIgnoreCase(response.getResultCode());
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("이메일 발송 실패: {}", e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }
}