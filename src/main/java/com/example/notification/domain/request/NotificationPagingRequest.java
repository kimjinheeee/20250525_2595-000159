package com.example.notification.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Schema(description = "알림 내역 조회 요청")
public record NotificationPagingRequest(
        @NotBlank @Schema(description = "고객 ID", example = "12345") String customerId,
        @Min(0) @Schema(description = "페이지 번호", example = "0") Integer page,
        @Min(1) @Schema(description = "페이지 크기", example = "10") Integer size,
        @Schema(description = "정렬 조건", example = "sentAt") String sort
) {
    public Pageable toPageable() {
        int pageVal = (page != null) ? page : 0;
        int sizeVal = (size != null) ? size : 10;
        Sort sortVal = (sort != null && !sort.isEmpty()) ? Sort.by(sort) : Sort.unsorted();
        return PageRequest.of(pageVal, sizeVal, sortVal);
    }
}
