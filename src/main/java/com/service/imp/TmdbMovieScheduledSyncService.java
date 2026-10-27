package com.service.imp;

import com.service.ITmdbMovieSyncService;
import com.service.dto.TmdbSyncResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class TmdbMovieScheduledSyncService {

    @Resource
    private ITmdbMovieSyncService movieSyncService;

    @Value("${tmdb.schedule.enabled:false}")
    private boolean scheduleEnabled;

    @Value("${tmdb.schedule.interval-ms:86400000}")
    private long scheduleIntervalMs;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private volatile TmdbSyncResult lastResult;
    private volatile String lastError;
    private volatile long lastRunTime;

    @Scheduled(fixedDelayString = "${tmdb.schedule.interval-ms:86400000}")
    public void syncScheduledCinemaMovies() {
        if (scheduleEnabled) {
            runNow();
        }
    }

    public boolean runNow() {
        if (!running.compareAndSet(false, true)) {
            return false;
        }

        try {
            lastResult = movieSyncService.syncNowPlayingAndUpcoming();
            lastError = null;
            lastRunTime = System.currentTimeMillis();
        } catch (Exception exception) {
            lastError = exception.getMessage();
            lastResult = null;
        } finally {
            running.set(false);
        }
        return true;
    }

    public synchronized void setScheduleEnabled(boolean enabled) {
        scheduleEnabled = enabled;
    }

    public boolean isScheduleEnabled() {
        return scheduleEnabled;
    }

    public long getScheduleIntervalMs() {
        return scheduleIntervalMs;
    }

    public boolean isRunning() {
        return running.get();
    }

    public TmdbSyncResult getLastResult() {
        return lastResult;
    }

    public String getLastError() {
        return lastError;
    }

    public long getLastRunTime() {
        return lastRunTime;
    }
}
