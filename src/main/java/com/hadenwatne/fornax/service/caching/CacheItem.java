package com.hadenwatne.fornax.service.caching;

import com.hadenwatne.fornax.App;

import java.util.Calendar;
import java.util.Date;

public class CacheItem {
    private final Object cachedObject;
    private final Date expires;

    CacheItem(Object cachedObject) {
        this.cachedObject = cachedObject;

        Calendar calendar = Calendar.getInstance();

        // Cached items are only valid for 5 minutes.
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 5);

        this.expires = calendar.getTime();
    }

    <T> T getCachedObject(Class<T> type) {
        try {
            return type.cast(this.cachedObject);
        } catch (Exception e) {
            App.getLogger().LogException(e);

            return null;
        }
    }

    Date getExpires() {
        return this.expires;
    }
}
