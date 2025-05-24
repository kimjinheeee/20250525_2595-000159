package com.example.notification.domain.client.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SmsRequest {
    private String phoneNumber;
    private String title;
    private String contents;
}
