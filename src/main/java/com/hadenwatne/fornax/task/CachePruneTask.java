package com.hadenwatne.fornax.task;

import com.hadenwatne.fornax.service.caching.CacheService;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class CachePruneTask extends TimerTask {
    private CacheService cacheService;

    public CachePruneTask(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public CachePruneTask() {
        Calendar c = Calendar.getInstance();
        Timer t = new Timer();

        c.setTime(new Date());

        // Run this now, and then again every 30 minutes
        t.schedule(this, c.getTime(), 30 * 60 * 1000);
    }

    public void run() {
        cacheService.PruneCache();
    }
}
