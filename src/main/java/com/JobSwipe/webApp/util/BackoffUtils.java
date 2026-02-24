package com.JobSwipe.webApp.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public final class BackoffUtils {

    public void sleepWithJitter(long baseMs) {
        long low = (long) (baseMs * 0.5);
        long high = (long) (baseMs * 1.5);
        long delay = ThreadLocalRandom.current().nextLong(Math.max(1, low), Math.max(2, high));
        sleepQuietly(delay);
    }

    public void sleepQuietly(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignore) {}
    }
}