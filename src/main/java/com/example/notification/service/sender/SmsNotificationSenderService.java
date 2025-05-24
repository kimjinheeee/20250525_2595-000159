package com.example.notification.service.sender;

import com.example.notification.entity.Notification;
import com.example.notification.domain.NotificationType;
import com.example.notification.client.SmsNotificationClient;
import com.example.notification.domain.client.request.SmsRequest;
import com.example.notification.domain.client.response.SmsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@Qualifier("smsSenderService")
@RequiredArgsConstructor
public class SmsNotificationSenderService {

    private final SmsNotificationClient smsNotificationClient;

    @Async
    public CompletableFuture<Boolean> send(Notification notification) {
        if (notification.getNotificationType() != NotificationType.SMS) {
            return CompletableFuture.completedFuture(false);
        }
        try {
            SmsRequest smsRequest = SmsRequest.builder()
                    .phoneNumber(notification.getRecipient())
                    .title(notification.getTitle())
                    .contents(notification.getContents())
                    .build();

            SmsResponse response = smsNotificationClient.sendSms(smsRequest);

            boolean result = response != null && "SUCCESS".equals(response.getResultCode());
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("SMS 발송 실패: {}", e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }
}