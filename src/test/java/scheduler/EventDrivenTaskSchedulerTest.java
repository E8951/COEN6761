package scheduler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventDrivenTaskSchedulerTest {
    @Mock
    private TaskDispatcher dispatcher;

    private ExecutorService executor;

    @AfterEach
    void tearDown() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    @Test
    void delayedExecutionHonorsDelayBoundary() {
        Instant now = Instant.parse("2024-01-01T00:00:00Z");
        Clock clock = Clock.fixed(now, ZoneOffset.UTC);

        EventDrivenTaskScheduler scheduler = new EventDrivenTaskScheduler(clock, dispatcher);
        Runnable task = () -> {}; // ✅ real runnable, not mocked
        Instant runAt = now.plusSeconds(5);

        scheduler.schedule(task, runAt);

        scheduler.dispatchDueTasks(now);
        verify(dispatcher, never()).dispatch(task);

        scheduler.dispatchDueTasks(runAt);
        verify(dispatcher, times(1)).dispatch(task);
    }

    @Test
    void invalidSchedulingTimesRejectPastAndNulls() {
        Instant now = Instant.parse("2024-01-01T00:00:00Z");
        Clock clock = Clock.fixed(now, ZoneOffset.UTC);

        EventDrivenTaskScheduler scheduler = new EventDrivenTaskScheduler(clock, dispatcher);

        Runnable task = () -> {}; // ✅ real runnable

        assertThrows(IllegalArgumentException.class, () -> scheduler.schedule(null, now.plusSeconds(1)));
        assertThrows(IllegalArgumentException.class, () -> scheduler.schedule(task, null));
        assertThrows(IllegalArgumentException.class, () -> scheduler.schedule(task, now.minusSeconds(1)));
    }

    @Test
    void concurrentDispatchDoesNotDoubleDispatchTasks() throws InterruptedException {
        Instant now = Instant.parse("2024-01-01T00:00:00Z");
        Clock clock = Clock.fixed(now, ZoneOffset.UTC);

        AtomicInteger dispatchCount = new AtomicInteger();
        doAnswer(invocation -> {
            dispatchCount.incrementAndGet();
            return null;
        }).when(dispatcher).dispatch(any());

        EventDrivenTaskScheduler scheduler = new EventDrivenTaskScheduler(clock, dispatcher);

        int taskCount = 100;
        for (int i = 0; i < taskCount; i++) {
            scheduler.schedule(() -> {}, now); // ✅ real runnable, not mocked
        }

        executor = Executors.newFixedThreadPool(4);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(4);

        for (int i = 0; i < 4; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await(5, TimeUnit.SECONDS);
                    scheduler.dispatchDueTasks(now);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(5, TimeUnit.SECONDS), "Dispatch threads did not finish in time"); // ✅ assert

        assertEquals(taskCount, dispatchCount.get(),
                "Dispatch should occur once per task even under concurrency");
        assertEquals(0, scheduler.pendingCount(),
                "All tasks should be drained after concurrent dispatch");
    }

    @Test
    void cancellationPreventsDispatchAndIsIdempotent() {
        Instant now = Instant.parse("2024-01-01T00:00:00Z");
        Clock clock = Clock.fixed(now, ZoneOffset.UTC);

        EventDrivenTaskScheduler scheduler = new EventDrivenTaskScheduler(clock, dispatcher);
        Runnable task = () -> {}; // ✅ real runnable

        UUID taskId = scheduler.schedule(task, now.plusSeconds(1));
        assertTrue(scheduler.cancel(taskId));
        assertFalse(scheduler.cancel(taskId), "Second cancel should be a no-op");

        scheduler.dispatchDueTasks(now.plusSeconds(5));
        verify(dispatcher, never()).dispatch(task);

        UUID dispatchedId = scheduler.schedule(task, now);
        scheduler.dispatchDueTasks(now);

        assertFalse(scheduler.cancel(dispatchedId), "Cancel after dispatch should return false");
        verify(dispatcher, times(1)).dispatch(task);
    }
}
