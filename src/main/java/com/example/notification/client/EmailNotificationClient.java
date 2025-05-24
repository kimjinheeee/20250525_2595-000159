package com.example.notification.client;

import com.example.notification.domain.client.request.EmailRequest;
import com.example.notification.domain.client.response.EmailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "emailNotificationClient", url = "${notification.api.email.url}")
public interface EmailNotificationClient {

    @PostMapping
    EmailResponse sendEmail(@RequestBody EmailRequest request);
}