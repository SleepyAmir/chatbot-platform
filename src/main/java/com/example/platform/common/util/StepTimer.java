package com.example.platform.common.util;

import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * ابزار سبک برای اندازه‌گیری مدت‌زمان هر مرحله از یک فرآیند چندمرحله‌ای (مثل orchestration چت)
 * و تولید یک لاگ خلاصه در پایان.
 *
 * استفاده:
 * <pre>
 *   StepTimer timer = new StepTimer(log, "chat-orchestration");
 *   String x = timer.time("cache_lookup", () -> cacheService.find(question));
 *   ...
 *   timer.logSummary();
 * </pre>
 *
 * هر صدا‌زدن {@link #time} بلافاصله یک خط لاگ مجزا برای همان مرحله می‌نویسد (DEBUG)
 * + در پایان {@link #logSummary()} یک خط جمع‌بندی با همه‌ی مراحل و زمان کل می‌نویسد (INFO).
 *
 * Thread-safety: این کلاس thread-safe نیست؛ باید برای هر درخواست یک نمونه‌ی جدید ساخته شود
 * (نه یک bean مشترک singleton).
 */
public class StepTimer {

    private final Logger log;
    private final String operationName;
    private final Map<String, Long> stepDurationsMs = new LinkedHashMap<>();
    private final long startNanos;

    public StepTimer(Logger log, String operationName) {
        this.log = log;
        this.operationName = operationName;
        this.startNanos = System.nanoTime();
    }

    /** اجرای یک مرحله که مقدار برمی‌گرداند، با اندازه‌گیری و لاگ خودکار. */
    public <T> T time(String stepName, Supplier<T> action) {
        long start = System.nanoTime();
        try {
            return action.get();
        } finally {
            recordStep(stepName, start);
        }
    }

    /** اجرای یک مرحله بدون مقدار برگشتی. */
    public void timeVoid(String stepName, Runnable action) {
        long start = System.nanoTime();
        try {
            action.run();
        } finally {
            recordStep(stepName, start);
        }
    }

    private void recordStep(String stepName, long startNanos) {
        long ms = (System.nanoTime() - startNanos) / 1_000_000;
        stepDurationsMs.put(stepName, ms);
        log.debug("⏱ [{}] step='{}' took={}ms", operationName, stepName, ms);
    }

    public long totalMs() {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }

    public Map<String, Long> steps() {
        return stepDurationsMs;
    }

    /** یک خط جمع‌بندی شامل تمام مراحل + زمان کل، برای لاگ نهایی هر درخواست. */
    public void logSummary() {
        StringBuilder sb = new StringBuilder();
        stepDurationsMs.forEach((k, v) -> sb.append(k).append('=').append(v).append("ms, "));
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }
        log.info("⏱ [{}] SUMMARY total={}ms steps=({})", operationName, totalMs(), sb);
    }
}