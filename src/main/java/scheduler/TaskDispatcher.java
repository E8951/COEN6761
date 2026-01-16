package scheduler;

public interface TaskDispatcher {
    void dispatch(Runnable task);
}