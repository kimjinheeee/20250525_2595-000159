package com.example.notification.scheduler;

import com.example.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class NotificationDeleteScheduler {

    private final NotificationRepository notificationRepository;

    // 매일 새벽 2시에 실행
    @Scheduled(cron = "0 0 2 * * *")
    public void deleteOldNotifications() {
        LocalDateTime cutoff = LocalDateTime.now().minusMonths(3);
        int deleted = notificationRepository.deleteBySentAtBefore(cutoff);
        log.info("3개월 지난 알림 {}건 삭제됨 ({} 이전)", deleted, cutoff);
    }
}
