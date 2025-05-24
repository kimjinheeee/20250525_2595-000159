package com.example.notification.client;

import com.example.notification.domain.client.request.KakaoRequest;
import com.example.notification.domain.client.response.KakaoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "kakaoNotificationClient", url = "${notification.api.kakao.url}")
public interface KakaoNotificationClient {

    @PostMapping
    KakaoResponse sendKakao(@RequestBody KakaoRequest request);
}