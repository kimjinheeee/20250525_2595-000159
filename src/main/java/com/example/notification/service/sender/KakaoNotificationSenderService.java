package com.example.notification.service.sender;

import com.example.notification.entity.Notification;
import com.example.notification.domain.NotificationType;
import com.example.notification.client.KakaoNotificationClient;
import com.example.notification.domain.client.request.KakaoRequest;
import com.example.notification.domain.client.response.KakaoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@Qualifier("kakaoSenderService")
@RequiredArgsConstructor
public class KakaoNotificationSenderService {

    private final KakaoNotificationClient kakaoNotificationClient;

    @Async
    public CompletableFuture<Boolean> send(Notification notification) {
        if (notification.getNotificationType() != NotificationType.KAKAO) {
            return CompletableFuture.completedFuture(false);
        }

        try {
            KakaoRequest request = KakaoRequest.builder()
                    .talkId(notification.getRecipient())
                    .title(notification.getTitle())
                    .contents(notification.getContents())
                    .build();
            KakaoResponse response = kakaoNotificationClient.sendKakao(request);
            boolean result = response != null && "SUCCESS".equals(response.getResultCode());
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("카카오톡 발송 실패: {}", e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }
}