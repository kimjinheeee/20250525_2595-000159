package com.example.notification.config;

import com.google.common.base.Charsets;
import feign.Logger;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

@Slf4j
@EnableFeignClients("com.example.notification")
@Configuration
public class OpenFeignConfig {

    @Bean
    public OkHttpClient feignClient() {
        return new OkHttpClient();
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            String responseBody;

            try {
                responseBody = IOUtils.toString(response.body().asInputStream(), Charsets.UTF_8);
            } catch (IOException e) {
                log.error("response body exception. {}", e.toString());
                responseBody = "{}";
            }

            if (response.status() >= HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                throw new RetryableException(
                        response.status(),
                        String.format(
                                "API 호출에 실패했습니다. 재시도 합니다. \nstatus: %s,\nheaders: %s,\n body: %s",
                                response.status(),
                                response.headers(),
                                responseBody),
                        response.request().httpMethod(),
                        Date.from(Instant.now()),
                        response.request());
            }

            log.error("API 호출에 실패했습니다. \nstatus: {},\nheaders: {},\nbody: {}",
                    response.status(), response.headers(), responseBody);
            return new IllegalStateException("API 호출에 실패했습니다.");
        };
    }
}
