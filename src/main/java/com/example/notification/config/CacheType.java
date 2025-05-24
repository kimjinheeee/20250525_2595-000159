package com.example.notification.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CacheType {
    NOTIFICATION("notifications", 10, 20);

    private final String cacheName;
    private final int expireAfterWrite;
    private final int maximumSize;
}
