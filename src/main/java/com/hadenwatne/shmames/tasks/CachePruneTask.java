package com.hadenwatne.shmames.tasks;

import com.hadenwatne.shmames.services.CacheService;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Clears out any cached data from the CacheService. Cached items will clear themselves out if requested manually, but
 * this task ensures that stale data is cleared from memory automatically if it is no longer needed.
 */
public class CachePruneTask extends TimerTask {
    public CachePruneTask() {
        Calendar c = Calendar.getInstance();
        Timer t = new Timer();

        c.setTime(new Date());

        // Run this now, and then again every 15 minutes
        t.schedule(this, c.getTime(), 900000);
    }

    public void run() {
        CacheService.PruneCache();
    }
}
