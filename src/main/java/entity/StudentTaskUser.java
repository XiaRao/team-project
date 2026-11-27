package entity;

import java.util.List;

/**
 * Represents a student who owns academic tasks and has a country preference
 * for public-holiday warnings.
 *
 * <p>Downstream packages should create or restore instances through
 * {@link StudentUserFactory}. Each instance owns a task factory bound to its
 * immutable user ID. Task IDs are unique within a user's task list; different
 * users may have tasks with the same task ID.
 *
 * <p>Creating or restoring a task through {@link #studentTaskFactory} does not
 * add it to this user's list. Call {@link #addTask(StudentTask)} separately.
 *
 * <p>This class manages state in memory. Downstream packages are responsible
 * for authentication, persistent storage, and saving the task factory's next
 * task ID alongside the user's task data.
 */
public class StudentTaskUser extends TaskUser<StudentTask> {
    /**
     * Initial country before a factory applies the selected or restored country.
     */
    public static final Country DEFAULT_COUNTRY_CODE = Country.CA;

    /** Country used when determining applicable public holidays for this user. */
    private Country countryCode = DEFAULT_COUNTRY_CODE;

    /**
     * Factory for creating or restoring tasks owned by this user.
     *
     * <p>The factory reference cannot be replaced, but its next-task-ID counter
     * changes as new tasks are created or higher-ID tasks are added.
     *
     * <p>Use {@link StudentTaskFactory#getNextTaskUniqueId()} to obtain the
     * counter for persistence. Preserve this counter after task deletion so
     * previously allocated task IDs are not reused.
     */
    public final StudentTaskFactory studentTaskFactory;

    /**
     * Constructs a new user with an empty task list and a task factory whose
     * first task ID is 1.
     *
     * <p>Package access allows {@link StudentUserFactory} to control creation.
     * The factory applies the selected country after this constructor returns.
     *
     * @param username the username to store
     * @param password the password to store
     * @param uniqueUserId the user ID assigned by the user factory
     */
    StudentTaskUser(String username, String password, int uniqueUserId) {
        super(username, password, uniqueUserId);

        this.studentTaskFactory =
            new StudentTaskFactory(1, getUniqueUserId());
    }

    /**
     * Constructs a user with existing tasks and a saved next-task-ID counter.
     *
     * <p>The supplied list is copied, but its task objects are shared with the
     * caller. A null list is treated as empty. Every supplied task must belong
     * to this user, and task IDs must be unique within the supplied list.
     *
     * <p>The counter must be positive and greater than every supplied task ID.
     * It is restored directly; reconstructing the user does not consume task
     * IDs. The factory applies the saved country after construction.
     *
     * @param username the saved username
     * @param password the saved password
     * @param uniqueUserId the saved user ID
     * @param tasks the reconstructed tasks, or null for an empty task list
     * @param nextTaskUniqueId the saved next task ID for this user
     * @throws IllegalArgumentException if a task is null, belongs to another
     *         user, or duplicates another task ID; or if the counter is
     *         nonpositive or not greater than every supplied task ID
     */
    StudentTaskUser(String username, String password, int uniqueUserId,
                    List<StudentTask> tasks, int nextTaskUniqueId) {
        super(username, password, uniqueUserId, tasks);

        if (nextTaskUniqueId <= 0) {
            throw new IllegalArgumentException(
                "The next task unique ID must be positive.");
        }

        for (StudentTask task : getTasks()) {
            if (nextTaskUniqueId <= task.getUniqueTaskId()) {
                throw new IllegalArgumentException(
                    "The next task unique ID must be greater than "
                        + "every existing task's unique ID.");
            }
        }

        this.studentTaskFactory =
            new StudentTaskFactory(nextTaskUniqueId, getUniqueUserId());
    }

    /**
     * Adds an existing task to this user's list and updates the task factory's
     * counter to remain greater than the added task's ID.
     *
     * <p>The task must belong to this user and must not duplicate an ID already
     * in the list. This method does not create a task or allocate its ID.
     * The counter never decreases.
     *
     * <p>Changes occur only in memory; callers are responsible for saving the
     * updated task list and counter.
     *
     * @param task the task to add
     * @throws IllegalArgumentException if the task is null, belongs to another
     *         user, or has an ID already present in this user's task list
     * @throws IllegalStateException if the task ID is {@link Integer#MAX_VALUE},
     *         because no subsequent task ID can be represented
     */
    @Override
    public void addTask(StudentTask task) {
        if (task != null && task.getUniqueTaskId() == Integer.MAX_VALUE) {
            throw new IllegalStateException(
                "No task ID is available after this ID.");
        }

        super.addTask(task);
        studentTaskFactory.advancePast(task.getUniqueTaskId());
    }

    /**
     * Returns the country used for this user's public-holiday warnings.
     *
     * @return the current, nonnull country
     */
    public Country getCountryCode() {
        return countryCode;
    }

    /**
     * Updates the country used for this user's public-holiday warnings.
     *
     * <p>A null value is rejected before the current country is changed.
     * This method updates the preference in memory; it does not retrieve
     * holiday data or save the preference to persistent storage.
     *
     * @param countryCode the new, nonnull country
     * @throws IllegalArgumentException if countryCode is null
     */
    public void setCountryCode(Country countryCode) {
        if (countryCode == null) {
            throw new IllegalArgumentException("Country code cannot be null.");
        }

        this.countryCode = countryCode;
    }
}