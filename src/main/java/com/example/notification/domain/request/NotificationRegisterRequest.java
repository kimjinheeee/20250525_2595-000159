package com.example.notification.domain.request;

import com.example.notification.domain.NotificationType;
import com.example.notification.domain.SendType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "알림 발송 등록 요청")
public record NotificationRegisterRequest(
        @NotBlank @Schema(description = "고객 ID", example = "12345") String customerId,
        @NotNull @Schema(description = "알림 타입 (예: SMS, KAKAO, EMAIL)", example = "SMS") NotificationType notificationType,
        @NotNull @Schema(description = "발송 타입 (IMMEDIATE, SCHEDULED)", example = "IMMEDIATE") SendType sendType,
        @Pattern(regexp = "\\d{12}", message = "yyyyMMddHHmm 형식이어야 합니다")
        @Schema(description = "예약 발송 시각 (sendType이 SCHEDULED인 경우 필수)", example = "202305231230")
        String scheduledTime,
        @NotBlank @Schema(description = "알림 제목", example = "알림 제목") String title,
        @NotBlank @Schema(description = "알림 내용", example = "알림 내용") String contents,
        @NotBlank @Schema(description = "수신자 정보", example = "01012345678") String recipient
) {

    @AssertTrue(message = "예약 발송 시 scheduledTime을 yyyyMMddHHmm 포맷으로 입력하세요")
    public boolean isScheduledTimeValid() {
        return sendType() != SendType.SCHEDULED || (scheduledTime() != null && scheduledTime().matches("\\d{12}"));
    }
}