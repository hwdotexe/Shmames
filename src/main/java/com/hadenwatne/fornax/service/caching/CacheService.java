package com.hadenwatne.fornax.service.caching;

import java.util.Date;
import java.util.HashMap;

public class CacheService {
    private final HashMap<String, CacheItem> cache;

    public CacheService() {
        cache = new HashMap<>();
    }

    public <T> T RetrieveItem(String cacheKey, Class<T> type) {
        for(String key : cache.keySet()) {
            if(key.equals(cacheKey)) {
                CacheItem item = cache.get(cacheKey);

                if(item.getExpires().before(new Date())) {
                    cache.remove(key);
                    return null;
                }

                return item.getCachedObject(type);
            }
        }

        return null;
    }

    public void StoreItem(String cacheKey, Object object) {
        cache.put(cacheKey, new CacheItem(object));
    }

    public void RemoveItem(String cacheKey) {
        cache.remove(cacheKey);
    }

    public String GenerateCacheKey(long serverID, long channelID, long userID, String... criteria) {
        StringBuilder key = new StringBuilder();

        key.append(serverID);
        key.append("@");
        key.append(channelID);
        key.append("@");
        key.append(userID);

        for(String string: criteria) {
            key.append("#");
            key.append(string);
        }

        return key.toString();
    }

    public void PruneCache() {
        Date now = new Date();

        for(String cacheKey : new HashMap<>(cache).keySet()) {
            CacheItem cacheItem = cache.get(cacheKey);

            if(cacheItem.getExpires().before(now)) {
                cache.remove(cacheKey);
            }
        }
    }
}
