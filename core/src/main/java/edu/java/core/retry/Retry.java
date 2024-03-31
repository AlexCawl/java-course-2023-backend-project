package edu.java.core.retry;

import java.time.Duration;
import java.util.Set;

public record Retry(Integer maxAttempts, Set<Integer> codes, RetryType type, Delay delay) {
    public enum RetryType {
        FIXED, LINEAR, EXPONENTIAL
    }

    public record Delay(Fixed fixed, Linear linear, Exponential exponential) {
        public record Fixed(Duration intervalDuration) {
        }

        public record Linear(Duration initialIntervalDuration, Duration maxIntervalDuration) {
        }

        public record Exponential(Duration initialIntervalDuration, Duration maxIntervalDuration) {
        }
    }
}