package com.JobSwipe.webApp.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class BackoffUtilsTest {

    private BackoffUtils backoffUtils;

    @BeforeEach
    void setUp() {
        backoffUtils = new BackoffUtils();
    }

    @Test
    void sleepQuietly_doesNotThrow() {
        assertThatCode(() -> backoffUtils.sleepQuietly(10)).doesNotThrowAnyException();
    }

    @Test
    void sleepWithJitter_doesNotThrow() {
        assertThatCode(() -> backoffUtils.sleepWithJitter(10)).doesNotThrowAnyException();
    }

    @Test
    void sleepWithJitter_completesWithinReasonableTime() {
        long baseMs = 50;
        long start = System.currentTimeMillis();
        backoffUtils.sleepWithJitter(baseMs);
        long elapsed = System.currentTimeMillis() - start;

        // jitter range is 0.5x–1.5x, so elapsed should be between 25ms and ~200ms
        assertThat(elapsed).isGreaterThanOrEqualTo(25).isLessThan(500);
    }

    @Test
    void sleepQuietly_zeroMs_doesNotThrow() {
        assertThatCode(() -> backoffUtils.sleepQuietly(0)).doesNotThrowAnyException();
    }
}
