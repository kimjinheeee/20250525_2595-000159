package com.example.notification.service;

import com.example.notification.entity.Notification;
import com.example.notification.domain.NotificationStatus;
import com.example.notification.domain.SendType;
import com.example.notification.domain.request.NotificationRegisterRequest;
import com.example.notification.domain.response.NotificationResponse;
import com.example.notification.common.exception.NotificationException;
import com.example.notification.event.NotificationSendEvent;
import com.example.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 알림을 등록합니다.
     * 예약 발송인 경우 예약 시각을 파싱하며, 발송 타입이 즉시일 경우 발송 이벤트를 발생시킵니다.
     *
     * @param request 알림 등록 요청 정보
     * @return 등록된 알림의 응답 정보
     * @throws NotificationException 예약 발송 시각이 없거나 형식이 올바르지 않은 경우 발생합니다.
     */
    public NotificationResponse registerNotification(NotificationRegisterRequest request) {
        LocalDateTime scheduledTime = null;
        if (request.sendType() == SendType.SCHEDULED) {
            if (request.scheduledTime() == null) {
                throw new NotificationException("예약 발송 시각은 필수입니다.");
            }
            try {
                scheduledTime = LocalDateTime.parse(
                        request.scheduledTime(), DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            } catch (DateTimeParseException e) {
                throw new NotificationException("예약 발송 시각 형식이 올바르지 않습니다.");
            }
        }
        Notification notification = Notification.builder()
                .customerId(request.customerId())
                .notificationType(request.notificationType())
                .sendType(request.sendType())
                .scheduledTime(scheduledTime)
                .title(request.title())
                .contents(request.contents())
                .recipient(request.recipient())
                .status(NotificationStatus.PENDING)
                .build();

        notification = notificationRepository.save(notification);

        if (request.sendType() == SendType.IMMEDIATE) {
            eventPublisher.publishEvent(new NotificationSendEvent(notification));
        }
        return NotificationResponse.of(notification);
    }

    /**
     * 예약 발송 및 발송 실패 알림에 대해 발송 이벤트를 발생시킵니다.
     * 현재 시각보다 이전인 예약 알림과 실패한 알림을 조회하여 발송 이벤트를 발생시킵니다.
     */
    public void sendScheduledNotifications() {

        // 현재 시각보다 같거나 이전인 예약 알림
        LocalDateTime now = LocalDateTime.now();
        List<Notification> scheduledNotifications =
                notificationRepository.findBySendTypeAndScheduledTimeLessThanEqualAndStatus(
                                SendType.SCHEDULED, now, NotificationStatus.PENDING)
                        .stream()
                        .toList();
        List<Notification> notificationsToSend = new ArrayList<>(scheduledNotifications);

        // 실패한 알림
        List<Notification> failedNotifications =
                notificationRepository.findByStatus(NotificationStatus.FAILED);
        notificationsToSend.addAll(failedNotifications);

        // 요청받은 순서대로 발송 정렬
        notificationsToSend.sort(
                Comparator
                        .comparing(Notification::getScheduledTime,
                                Comparator.nullsLast(LocalDateTime::compareTo))
                        .thenComparing(Notification::getId)
        );

        for (Notification notification : notificationsToSend) {
            // 중복 발송 방지를 위해 먼저 IN_PROGRESS로 상태 설정 후 저장
            notification.setStatus(NotificationStatus.IN_PROGRESS);
            notificationRepository.save(notification);

            eventPublisher.publishEvent(new NotificationSendEvent(notification));
        }
    }

    /**
     * 고객의 알림 내역을 페이지 단위로 조회합니다.
     * 최근 3개월 이내에 전송된 알림만 조회합니다.
     *
     * @param customerId 고객 식별자
     * @param pageable 페이지 정보
     * @return 알림 응답 정보를 담은 페이지
     */
    @Cacheable(cacheNames = "notifications", key = "#customerId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<NotificationResponse> getNotifications(String customerId, Pageable pageable) {

        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);

        Page<Notification> page = notificationRepository.findByCustomerIdAndSentAtAfterAndStatus(
                customerId, threeMonthsAgo, NotificationStatus.SENT, pageable);

        return page.map(NotificationResponse::of);
    }
}