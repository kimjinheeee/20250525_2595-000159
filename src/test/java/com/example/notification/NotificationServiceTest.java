package com.example.notification;

import com.example.notification.common.exception.NotificationException;
import com.example.notification.domain.NotificationStatus;
import com.example.notification.domain.SendType;
import com.example.notification.domain.NotificationType;
import com.example.notification.domain.request.NotificationRegisterRequest;
import com.example.notification.domain.response.NotificationResponse;
import com.example.notification.entity.Notification;
import com.example.notification.event.NotificationSendEvent;
import com.example.notification.repository.NotificationRepository;
import com.example.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private NotificationService notificationService;


    // 즉시 발송(IMMEDIATE) 요청 시, 저장 후 이벤트가 발행되는지 검증
    @Test
    void registerNotification_immediatePublishesEvent() {
        // given
        NotificationRegisterRequest request = new NotificationRegisterRequest(
                "cust1",
                NotificationType.SMS,
                SendType.IMMEDIATE,
                null,
                "제목",
                "내용",
                "01012345678"
        );
        Notification saved = Notification.builder()
                .id(1L)
                .customerId("cust1")
                .notificationType(NotificationType.SMS)
                .sendType(SendType.IMMEDIATE)
                .scheduledTime(null)
                .title("제목")
                .contents("내용")
                .recipient("01012345678")
                .status(NotificationStatus.PENDING)
                .build();
        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        // when
        NotificationResponse response = notificationService.registerNotification(request);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(notificationRepository).save(any(Notification.class));
        verify(eventPublisher).publishEvent(any(NotificationSendEvent.class));
    }

    // 예약 발송(SCHEDULED) 시, scheduledTime 누락인 경우 예외 발생 검증
    @Test
    void registerNotification_scheduledMissingTime_throwsException() {
        // given
        NotificationRegisterRequest request = new NotificationRegisterRequest(
                "cust2",
                NotificationType.EMAIL,
                SendType.SCHEDULED,
                null,
                "제목",
                "내용",
                "test@example.com"
        );

        // when & then
        NotificationException ex = assertThrows(NotificationException.class,
                () -> notificationService.registerNotification(request));
        assertEquals("예약 발송 시각은 필수입니다.", ex.getMessage());
        verify(notificationRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    // 예약 발송(SCHEDULED) 시, scheduledTime 형식 오류인 경우 예외 발생 검증
    @Test
    void registerNotification_scheduledInvalidFormat_throwsException() {
        // given
        NotificationRegisterRequest request = new NotificationRegisterRequest(
                "cust3",
                NotificationType.KAKAO,
                SendType.SCHEDULED,
                "invalid-format",
                "제목",
                "내용",
                "01012345678"
        );

        // when & then
        NotificationException ex = assertThrows(NotificationException.class,
                () -> notificationService.registerNotification(request));
        assertEquals("예약 발송 시각 형식이 올바르지 않습니다.", ex.getMessage());
        verify(notificationRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    // 예약 발송(SCHEDULED) 시, 올바른 형식을 파싱하고 저장만 되며 이벤트 미발행 검증
    @Test
    void registerNotification_scheduledParsesAndSavesWithoutEvent() {
        // given
        String dt = LocalDateTime.now().plusHours(1)
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        NotificationRegisterRequest request = new NotificationRegisterRequest(
                "cust4",
                NotificationType.SMS,
                SendType.SCHEDULED,
                dt,
                "제목",
                "내용",
                "01012345678"
        );
        Notification saved = Notification.builder()
                .id(2L)
                .customerId("cust4")
                .notificationType(NotificationType.SMS)
                .sendType(SendType.SCHEDULED)
                .scheduledTime(LocalDateTime.parse(dt, DateTimeFormatter.ofPattern("yyyyMMddHHmm")))
                .title("제목")
                .contents("내용")
                .recipient("01012345678")
                .status(NotificationStatus.PENDING)
                .build();
        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        // when
        NotificationResponse response = notificationService.registerNotification(request);

        // then
        assertNotNull(response);
        assertEquals(2L, response.getId());
        verify(notificationRepository).save(any(Notification.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    // sendScheduledNotifications 호출 시, PENDING 예약 알림과 FAILED 알림에 대해 IN_PROGRESS 상태 변경 및 이벤트 발행 검증
    @Test
    void sendScheduledNotifications_sendsPendingAndFailedEvents() {
        // given
        Notification pending = Notification.builder()
                .id(3L)
                .sendType(SendType.SCHEDULED)
                .status(NotificationStatus.PENDING)
                .scheduledTime(LocalDateTime.now().minusMinutes(5))
                .build();
        Notification failed = Notification.builder()
                .id(4L)
                .status(NotificationStatus.FAILED)
                .build();
        when(notificationRepository.findBySendTypeAndScheduledTimeLessThanEqualAndStatus(
                eq(SendType.SCHEDULED), any(LocalDateTime.class), eq(NotificationStatus.PENDING)
        )).thenReturn(List.of(pending));
        when(notificationRepository.findByStatus(NotificationStatus.FAILED))
                .thenReturn(List.of(failed));

        // when
        notificationService.sendScheduledNotifications();

        // then
        // 각각 IN_PROGRESS 로 상태 변경 후 저장되고, 이벤트 발행
        verify(notificationRepository, times(2)).save(argThat(n -> n.getStatus() == NotificationStatus.IN_PROGRESS));
        verify(eventPublisher, times(2)).publishEvent(any(NotificationSendEvent.class));
    }

    @Test
    // getNotifications 호출 시, 최근 3개월 이내 SENT 알림 페이징 조회 후 NotificationResponse 매핑 검증
    void getNotifications_returnsPageOfResponses() {
        // given
        Pageable pageable = PageRequest.of(0, 5);
        Notification sent = Notification.builder()
                .id(5L)
                .customerId("cust5")
                .status(NotificationStatus.SENT)
                .sentAt(LocalDateTime.now().minusDays(1))
                .build();
        Page<Notification> page = new PageImpl<>(List.of(sent), pageable, 1);
        when(notificationRepository.findByCustomerIdAndSentAtAfterAndStatus(
                eq("cust5"), any(LocalDateTime.class), eq(NotificationStatus.SENT), eq(pageable)
        )).thenReturn(page);

        // when
        Page<NotificationResponse> result = notificationService.getNotifications("cust5", pageable);

        // then
        assertEquals(1, result.getTotalElements());
        assertEquals(5L, result.getContent().get(0).getId());
    }
}