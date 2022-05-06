package com.hadenwatne.shmames.models;

import com.hadenwatne.shmames.services.LoggingService;

import java.util.Calendar;
import java.util.Date;

public class CacheItem {
    private final Object cachedObject;
    private final Date expires;

    public CacheItem(Object cachedObject) {
        this.cachedObject = cachedObject;

        Calendar calendar = Calendar.getInstance();

        // Cached items are only valid for 5 minutes.
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 5);

        this.expires = calendar.getTime();
    }

    public <T> T getCachedObject(Class<T> type) {
        try {
            return type.cast(this.cachedObject);
        } catch (Exception e) {
            LoggingService.LogException(e);

            return null;
        }
    }

    public Date getExpires() {
        return this.expires;
    }
}
