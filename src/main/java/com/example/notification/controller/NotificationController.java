package com.example.notification.controller;

import com.example.notification.common.response.ApiErrorResponse;
import com.example.notification.domain.request.NotificationPagingRequest;
import com.example.notification.domain.request.NotificationRegisterRequest;
import com.example.notification.domain.response.NotificationResponse;
import com.example.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "notifications", description = "notifications API")
@RestController
@RequestMapping("/notifications")
@Validated
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "503", description = "서버 에러",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
})
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 알림을 등록하고, 발송 타입(IMMEDIATE, SCHEDULED)에 따라 즉시 또는 예약 발송시킵니다.
     *
     * @param request 알림 등록 요청 정보
     * @return 등록된 알림의 응답 정보
     */
    @Operation(summary = "알림 발송 등록")
    @PostMapping
    public NotificationResponse registerNotification(
        @Valid @RequestBody NotificationRegisterRequest request) {
        return notificationService.registerNotification(request);
    }

    /**
     * 고객의 알림 내역을 페이지 단위로 조회합니다.
     *
     * @param request 고객 ID 및 페이지 정보를 담은 조회 요청 객체
     * @return 알림 응답 정보를 담은 페이지
     */
    @Operation(summary = "알림 내역 조회")
    @GetMapping
    public Page<NotificationResponse> getNotifications(
            @ParameterObject NotificationPagingRequest request) {
        return notificationService.getNotifications(
                request.customerId(), request.toPageable());
    }
}
