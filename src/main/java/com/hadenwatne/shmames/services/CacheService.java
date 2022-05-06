package com.hadenwatne.shmames.services;

import com.hadenwatne.shmames.models.CacheItem;
import com.hadenwatne.shmames.tasks.CachePruneTask;

import java.util.Date;
import java.util.HashMap;

public class CacheService {
    private static HashMap<String, CacheItem> cache;

    public static void Init() {
        cache = new HashMap<>();

        new CachePruneTask();
    }

    public static <T> T RetrieveItem(String cacheKey, Class<T> type) {
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

    public static void StoreItem(String cacheKey, Object object) {
        cache.put(cacheKey, new CacheItem(object));
    }

    public static String GenerateCacheKey(long serverID, long channelID, long userID, String ...criteria) {
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

    /**
     * Remove any expired cache items from the store.
     */
    public static void PruneCache() {
        for(String cacheKey : new HashMap<>(cache).keySet()) {
            CacheItem cacheItem = cache.get(cacheKey);

            if(cacheItem.getExpires().before(new Date())) {
                cache.remove(cacheKey);
            }
        }
    }
}
