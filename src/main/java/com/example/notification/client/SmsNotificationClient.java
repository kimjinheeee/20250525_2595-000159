package com.example.notification.client;

import com.example.notification.domain.client.request.SmsRequest;
import com.example.notification.domain.client.response.SmsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "smsNotificationClient", url = "${notification.api.sms.url}")
public interface SmsNotificationClient {

    @PostMapping
    SmsResponse sendSms(@RequestBody SmsRequest request);
}