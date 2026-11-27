package entity;

public abstract class TaskFactory {
    private int nextTaskUniqueId;

    protected TaskFactory(int nextTaskUniqueId) {
        if (nextTaskUniqueId <= 0) {
            throw new IllegalArgumentException("The next task unique ID must be positive.");
        }

        this.nextTaskUniqueId = nextTaskUniqueId;
    }

    protected int generateNextTaskUniqueId() {
        if (nextTaskUniqueId == Integer.MAX_VALUE) {
            throw new IllegalStateException("No more task IDs can be generated.");
        }

        return nextTaskUniqueId++;
    }

    public int getNextTaskUniqueId() {
        return nextTaskUniqueId;
    }

    void advancePast(int usedTaskId) {
        if (usedTaskId == Integer.MAX_VALUE) {
            throw new IllegalStateException(
                "No task ID is available after this ID.");
        }

        nextTaskUniqueId = Math.max(nextTaskUniqueId, usedTaskId + 1);
    }
}