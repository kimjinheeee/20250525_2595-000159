package com.example.notification.repository;

import com.example.notification.domain.SendType;
import com.example.notification.entity.Notification;
import com.example.notification.domain.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByCustomerIdAndSentAtAfterAndStatus(String customerId, LocalDateTime date, NotificationStatus status, Pageable pageable);

    List<Notification> findBySendTypeAndScheduledTimeLessThanEqualAndStatus(SendType sendType, LocalDateTime now, NotificationStatus status);

    List<Notification> findByStatus(NotificationStatus status);

    int deleteBySentAtBefore(LocalDateTime cutoff);
}