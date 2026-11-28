package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a user who owns a collection of tasks.
 *
 * Each task must belong to this user, and task IDs must be unique within
 * this user's task list. Different users may have tasks with the same task ID.
 *
 * This class manages tasks in memory; it does not load or save persistent data.
 *
 * @param <T> the type of task owned by this user
 */
public abstract class TaskUser<T extends Task> extends User {
    /** The user's task list, modified through this class's methods. */
    private final List<T> tasks;

    /**
     * Creates a user with an empty task list.
     *
     * @param username the user's username
     * @param password the user's password
     * @param uniqueUserId the user's identifier, used to verify task ownership
     */
    TaskUser(String username, String password, int uniqueUserId) {
        super(username, password, uniqueUserId);
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a user with an existing collection of tasks, such as tasks
     * reconstructed from persistent storage.
     *
     * The supplied list is copied into a new internal list. The task objects
     * themselves are not copied. A null list is treated as an empty list.
     *
     * @param username the user's username
     * @param password the user's password
     * @param uniqueUserId the user's identifier, used to verify task ownership
     * @param tasks the initial tasks, or null for an empty task list
     * @throws IllegalArgumentException if any task is null, belongs to another
     *         user, or has the same task ID as another task in the supplied list
     */
    TaskUser(String username, String password, int uniqueUserId,
             List<T> tasks) {
        this(username, password, uniqueUserId);

        if (tasks != null) {
            for (T task : tasks) {
                validateTask(task);
                this.tasks.add(task);
            }
        }
    }

    /**
     * Returns an unmodifiable live view of this user's task list.
     *
     * Tasks cannot be added or removed through the returned list. Later changes
     * to the internal list are reflected in this view. The task objects
     * themselves are not copied or made immutable.
     *
     * @return an unmodifiable view of the tasks in their current list order
     */
    public List<T> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Finds a task by its task ID within this user's list.
     *
     * @param uniqueTaskId the ID of the task to find
     * @return the matching task, or null if no task has that ID
     */
    public T getTask(int uniqueTaskId) {
        for (T task : tasks) {
            if (task.getUniqueTaskId() == uniqueTaskId) {
                return task;
            }
        }
        return null;
    }

    /**
     * Appends an existing task to this user's list after checking its ownership
     * and ensuring that its task ID is not already present.
     *
     * This method does not create a task or allocate its identifier.
     *
     * @param task the task to add
     * @throws IllegalArgumentException if the task is null, belongs to another
     *         user, or has a task ID already present in this user's list
     */
    public void addTask(T task) {
        validateTask(task);
        this.tasks.add(task);
    }

    /**
     * Removes the first task equal to the supplied task, provided the supplied
     * task belongs to this user.
     *
     * Matching follows {@link List#remove(Object)} equality rules; this method
     * does not search by task ID. The list remains unchanged if the task is
     * null, belongs to another user, or has no matching entry.
     *
     * @param task the task to remove
     */
    public void removeTask(T task) {
        if (task != null && task.getUniqueUserId() == getUniqueUserId()) {
            this.tasks.remove(task);
        }
    }

    /**
     * Checks whether a task can be added to the current task list.
     *
     * This checks list membership constraints: a non-null task, matching owner
     * ID, and a task ID not already present. It does not validate the task's
     * other internal fields or modify the list.
     *
     * @param task the candidate task
     * @throws IllegalArgumentException if the task is null, belongs to another
     *         user, or has a task ID already present in this user's list
     */
    private void validateTask(T task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }

        if (task.getUniqueUserId() != super.getUniqueUserId()) {
            throw new IllegalArgumentException(
                "Task must belong to the current user.");
        }

        for (T existingTask : tasks) {
            if (existingTask.getUniqueTaskId() == task.getUniqueTaskId()) {
                throw new IllegalArgumentException(
                    "A task with the same uniqueTaskId already exists.");
            }
        }
    }
}