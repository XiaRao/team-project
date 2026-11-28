package entity;

/**
 * Provides sequential task ID allocation for concrete task factories.
 *
 * <p>Each instance maintains an independent counter containing the next ID
 * to allocate. For student tasks, this counter belongs to one user's task
 * factory, since task IDs are unique within that user's task list.
 *
 * <p>This class manages the counter in memory. The persistence layer must
 * supply the saved counter when restoring a factory and save its updated
 * value whenever it changes.
 *
 * <p>{@link Integer#MAX_VALUE} represents an exhausted counter and is never
 * allocated as a task ID.
 */
public abstract class TaskFactory {
    /** Next task ID to allocate, or Integer.MAX_VALUE if allocation is exhausted. */
    private int nextTaskUniqueId;

    /**
     * Initializes the factory with the next task ID counter.
     *
     * <p>Use 1 for a new user's task factory. When restoring an existing
     * user's factory, supply the saved next available task ID.
     * This constructor checks that the counter is positive; it does not
     * compare it against an existing task list.
     *
     * @param nextTaskUniqueId the positive next task ID counter
     * @throws IllegalArgumentException if the supplied counter is zero or negative
     */
    protected TaskFactory(int nextTaskUniqueId) {
        if (nextTaskUniqueId <= 0) {
            throw new IllegalArgumentException("The next task unique ID must be positive.");
        }

        this.nextTaskUniqueId = nextTaskUniqueId;
    }

    /**
     * Allocates the current task ID and increments the counter.
     *
     * <p>The allocated ID is consumed immediately. If subsequent task
     * construction or validation fails, the counter is not automatically
     * rolled back, so gaps between allocated task IDs are possible.
     *
     * <p>If allocation is exhausted, this method throws without changing
     * the counter.
     *
     * @return the allocated task ID
     * @throws IllegalStateException if the counter has reached Integer.MAX_VALUE
     */
    protected int generateNextTaskUniqueId() {
        if (nextTaskUniqueId == Integer.MAX_VALUE) {
            throw new IllegalStateException("No more task IDs can be generated.");
        }

        return nextTaskUniqueId++;
    }

    /**
     * Returns the current counter without allocating an ID or changing state.
     *
     * <p>The persistence layer can save this value and supply it to the
     * constructor when restoring the factory.
     *
     * @return the next task ID counter, or Integer.MAX_VALUE if exhausted
     */
    public int getNextTaskUniqueId() {
        return nextTaskUniqueId;
    }

    /**
     * Advances the counter past an existing task ID when necessary.
     *
     * <p>This supports adding an existing task to a user's list while keeping
     * future allocations above that task's ID. The resulting counter is the
     * greater of its current value and {@code usedTaskId + 1}; it never decreases.
     * This method does not allocate an ID or save the counter to storage.
     *
     * <p>If the supplied ID is Integer.MAX_VALUE, this method throws without
     * changing the counter.
     *
     * @param usedTaskId the existing task ID that future allocations must exceed
     * @throws IllegalStateException if usedTaskId is Integer.MAX_VALUE
     */
    void advancePast(int usedTaskId) {
        if (usedTaskId == Integer.MAX_VALUE) {
            throw new IllegalStateException(
                "No task ID is available after this ID.");
        }

        nextTaskUniqueId = Math.max(nextTaskUniqueId, usedTaskId + 1);
    }
}