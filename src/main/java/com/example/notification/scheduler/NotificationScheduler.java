package com.example.notification.scheduler;

import com.example.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@EnableScheduling
@Component
public class NotificationScheduler {

    private final NotificationService notificationService;

    @Scheduled(fixedRate = 1000)
    public void scheduleNotificationTask() {
        log.info("스케줄된 알림 발송 작업 실행");
        notificationService.sendScheduledNotifications();
    }
}