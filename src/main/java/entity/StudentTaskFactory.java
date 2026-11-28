package entity;

import java.time.LocalDate;

/**
 * Creates new student tasks and reconstructs saved tasks for one user.
 *
 * <p>Every returned task receives this factory's immutable owner ID. Task IDs
 * are unique within a user's task list, rather than across all users.
 *
 * <p>For normal task creation, use the factory exposed by
 * {@link StudentTaskUser#studentTaskFactory}. Creation allocates a new task ID;
 * restoration preserves a saved task ID without advancing the counter.
 *
 * <p>This factory constructs tasks but does not add them to a user's task list
 * or perform persistence operations. Callers add returned tasks through
 * {@link StudentTaskUser#addTask(StudentTask)} and save the resulting state.
 * The inherited {@link #getNextTaskUniqueId()} returns the next-ID counter
 * for persistence without consuming an ID.
 *
 * <p>Creation allocates an ID before validating the task fields. Consequently,
 * a creation attempt rejected during task validation still consumes an ID.
 */
public class StudentTaskFactory extends TaskFactory {
    /** Immutable owner ID assigned to every task created or restored here. */
    private final int uniqueUserId;

    /**
     * Creates a task factory with an initial counter and a fixed owner ID.
     *
     * <p>Use 1 as the next task ID for a new user. When reconstructing an
     * existing user's state, supply the saved next-ID counter. That counter
     * must be greater than every task ID to be restored.
     *
     * <p>This constructor stores the owner ID without validating it.
     * {@link StudentTask} rejects an owner ID of zero when a task is
     * subsequently created or restored.
     *
     * @param nextTaskUniqueId the positive next unused task ID for this user
     * @param uniqueUserId the owning user's ID; must be nonzero for task
     *                     construction to succeed
     * @throws IllegalArgumentException if nextTaskUniqueId is not positive
     */
    public StudentTaskFactory(int nextTaskUniqueId, int uniqueUserId) {
        super(nextTaskUniqueId);
        this.uniqueUserId = uniqueUserId;
    }

    /**
     * Creates a task with explicitly supplied field values and a new task ID.
     *
     * <p>The task constructor validates the supplied fields before returning
     * the object. A rejected construction still consumes the allocated ID.
     * The returned task has not been added to a user's task list.
     *
     * @param title the nonnull task title; empty strings are permitted
     * @param description the nonnull description; empty strings are permitted
     * @param deadline the nonnull due date; past dates are permitted
     * @param estimatedHoursToCompleteTask the finite, positive estimated total
     *                                    workload in hours
     * @param completionProgress the integer completion percentage, from 0
     *                           through 100 inclusive
     * @param difficultyLevel the nonnull task difficulty
     * @return a new validated task with an allocated ID and this factory's
     *         owner ID
     * @throws IllegalArgumentException if any task field is invalid or this
     *                                  factory's owner ID is zero
     * @throws IllegalStateException if the task ID counter is exhausted
     */
    public StudentTask createStudentTask(String title,
                                         String description,
                                         LocalDate deadline,
                                         double estimatedHoursToCompleteTask,
                                         byte completionProgress,
                                         DifficultyLevel difficultyLevel) {
        // The counter advances before the task constructor validates its fields.
        int taskId = generateNextTaskUniqueId();

        return new StudentTask(taskId, uniqueUserId, title, description,
            deadline, estimatedHoursToCompleteTask,
            completionProgress, difficultyLevel);
    }

    /**
     * Creates a task with 10 estimated hours, 0% completion,
     * and Average difficulty.
     *
     * <p>A new task ID is allocated before the task fields are validated.
     * A rejected construction still consumes that ID. The returned task has
     * not been added to a user's task list.
     *
     * @param title the nonnull task title; empty strings are permitted
     * @param description the nonnull description; empty strings are permitted
     * @param deadline the nonnull due date; past dates are permitted
     * @return a new validated task with the default values, an allocated ID,
     *         and this factory's owner ID
     * @throws IllegalArgumentException if title, description, or deadline is
     *                                  null, or this factory's owner ID is zero
     * @throws IllegalStateException if the task ID counter is exhausted
     */
    public StudentTask createStudentTask(
        String title,
        String description,
        LocalDate deadline) {

        return new StudentTask(
            generateNextTaskUniqueId(),
            uniqueUserId,
            title,
            description,
            deadline);
    }

    /**
     * Restores a saved task using its original ID and this factory's owner ID.
     * Does not advance the next task ID counter.
     *
     * <p>Use this method when reconstructing saved tasks. Initialize this
     * factory with the saved next-ID counter and the saved owner's ID first.
     * The restored task ID must be positive and strictly below that counter.
     *
     * <p>The task constructor applies the same field validation used for new
     * tasks. A rejected restoration leaves the counter unchanged.
     *
     * <p>This method does not check for another task with the same ID and does
     * not add the returned task to a user's list. Adding the task through
     * {@link StudentTaskUser#addTask(StudentTask)} checks ownership and rejects
     * duplicate IDs already present in that user's list.
     *
     * @param uniqueTaskId the original positive task ID, strictly below this
     *                     factory's next-ID counter
     * @param title the saved nonnull title; empty strings are permitted
     * @param description the saved nonnull description; empty strings are
     *                    permitted
     * @param deadline the saved nonnull due date; past dates are permitted
     * @param estimatedHoursToCompleteTask the saved finite, positive estimated
     *                                    total workload in hours
     * @param completionProgress the saved integer completion percentage, from
     *                           0 through 100 inclusive
     * @param difficultyLevel the saved nonnull task difficulty
     * @return a newly constructed task preserving the supplied ID and field
     *         values, with this factory's owner ID
     * @throws IllegalArgumentException if the task ID is not positive, is not
     *                                  below the next-ID counter, any task
     *                                  field is invalid, or this factory's
     *                                  owner ID is zero
     */
    public StudentTask restoreStudentTask(
        int uniqueTaskId,
        String title,
        String description,
        LocalDate deadline,
        double estimatedHoursToCompleteTask,
        byte completionProgress,
        DifficultyLevel difficultyLevel) {

        if (uniqueTaskId <= 0) {
            throw new IllegalArgumentException(
                "The restored task unique ID must be positive.");
        }

        if (uniqueTaskId >= getNextTaskUniqueId()) {
            throw new IllegalArgumentException(
                "The restored task unique ID must be less than "
                    + "the next task unique ID.");
        }

        return new StudentTask(
            uniqueTaskId,
            uniqueUserId,
            title,
            description,
            deadline,
            estimatedHoursToCompleteTask,
            completionProgress,
            difficultyLevel);
    }

}