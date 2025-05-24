package com.example.notification.domain.client.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class KakaoRequest {
    private String talkId;
    private String title;
    private String contents;
}
