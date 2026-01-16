package scheduler;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EventDrivenTaskScheduler {
    private final Clock clock;
    private final TaskDispatcher dispatcher;
    private final ConcurrentMap<UUID, ScheduledTask> tasks = new ConcurrentHashMap<>();

    public EventDrivenTaskScheduler(Clock clock, TaskDispatcher dispatcher) {
        this.clock = Objects.requireNonNull(clock, "clock");
        this.dispatcher = Objects.requireNonNull(dispatcher, "dispatcher");
    }

    public UUID schedule(Runnable task, Instant runAt) {
        if (task == null) {
            throw new IllegalArgumentException("task must not be null");
        }
        if (runAt == null) {
            throw new IllegalArgumentException("runAt must not be null");
        }
        Instant now = clock.instant();
        if (runAt.isBefore(now)) {
            throw new IllegalArgumentException("runAt must not be in the past");
        }
        UUID id = UUID.randomUUID();
        tasks.put(id, new ScheduledTask(id, runAt, task));
        return id;
    }

    public boolean cancel(UUID id) {
        if (id == null) {
            return false;
        }
        return tasks.remove(id) != null;
    }

    public void dispatchDueTasks(Instant now) {
        Objects.requireNonNull(now, "now");
        tasks.forEach((id, scheduled) -> {
            if (!scheduled.runAt.isAfter(now)) {
                if (tasks.remove(id, scheduled)) {
                    dispatcher.dispatch(scheduled.task);
                }
            }
        });
    }

    public int pendingCount() {
        return tasks.size();
    }

    private static final class ScheduledTask {
        private final UUID id;
        private final Instant runAt;
        private final Runnable task;

        private ScheduledTask(UUID id, Instant runAt, Runnable task) {
            this.id = id;
            this.runAt = runAt;
            this.task = task;
        }
    }
}