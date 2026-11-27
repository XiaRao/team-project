package entity;

import java.time.LocalDate;

/**
 * Represents a student's task.
 *
 * <p>Instances are created through {@link StudentTaskFactory}. The task ID and
 * owner ID are inherited from {@link Task} and cannot be changed after creation.
 *
 * <p>{@link #fieldsValidation()} defensively checks the current object state
 * against this class's invariants, including checks for invalid or corrupted
 * data. {@link #inputValidation(LocalDate, double, byte)} checks only the
 * supplied deadline, estimated hours, and completion progress.
 *
 * <p>Setters reject changes if the current object state is already invalid.
 * After assigning a new value, they validate the resulting state and restore
 * the previous field value if validation fails.
 */
public class StudentTask extends Task {
    /** Default estimated total workload, in hours. */
    public static final double DEFAULT_ESTIMATED_HOURS = 10.0;

    /** Default difficulty when no difficulty is specified. */
    public static final DifficultyLevel DEFAULT_DIFFICULTY_LEVEL = DifficultyLevel.AVERAGE;

    /** Task title; may be empty but cannot be null. */
    private String title;

    /** Task description; may be empty but cannot be null. */
    private String description;

    /** Due date; past dates are permitted, but null is not. */
    private LocalDate deadline;

    /** Estimated total hours needed to complete the task; finite and positive. */
    private double estimatedHoursToCompleteTask;

    /** Integer completion percentage, from 0 through 100 inclusive. */
    private byte completionProgress;

    /** Task difficulty; cannot be null. */
    private DifficultyLevel difficultyLevel;

    /**
     * Constructs a task with 10 estimated hours, 0% completion, and Average
     * difficulty.
     *
     * <p>Package access allows the entity factory to control task creation.
     *
     * @param uniqueTaskId the task identifier within the owning user's task list
     * @param uniqueUserId the nonzero identifier of the owning user
     * @param title the task title; may be empty but cannot be null
     * @param description the task description; may be empty but cannot be null
     * @param deadline the nonnull due date; past dates are permitted
     * @throws IllegalArgumentException if the supplied fields fail validation
     */
    StudentTask(int uniqueTaskId, int uniqueUserId, String title,
                String description, LocalDate deadline) {
        this(uniqueTaskId, uniqueUserId, title, description, deadline,
            DEFAULT_ESTIMATED_HOURS, (byte) 0, DEFAULT_DIFFICULTY_LEVEL);
    }

    /**
     * Constructs a task with explicitly supplied field values.
     *
     * <p>The resulting object is checked using {@link #fieldsValidation()}
     * before construction completes.
     *
     * @param uniqueTaskId the task identifier within the owning user's task list
     * @param uniqueUserId the nonzero identifier of the owning user
     * @param title the task title; may be empty but cannot be null
     * @param description the task description; may be empty but cannot be null
     * @param deadline the nonnull due date; past dates are permitted
     * @param estimatedHoursToCompleteTask the finite, positive estimated total hours
     * @param completionProgress the completion percentage, from 0 through 100
     * @param difficultyLevel the nonnull difficulty
     * @throws IllegalArgumentException if the supplied fields fail validation
     */
    StudentTask(int uniqueTaskId, int uniqueUserId, String title,
                String description, LocalDate deadline,
                double estimatedHoursToCompleteTask, byte completionProgress,
                DifficultyLevel difficultyLevel) {
        super(uniqueTaskId, uniqueUserId);
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.estimatedHoursToCompleteTask = estimatedHoursToCompleteTask;
        this.completionProgress = completionProgress;
        this.difficultyLevel = difficultyLevel;

        if (!fieldsValidation()) {
            throw new IllegalArgumentException("The StudentTask has invalid fields.");
        }
    }

    /**
     * Checks the current object state against this class's invariants.
     *
     * <p>The owner ID must be nonzero. The title, description, and difficulty
     * must be nonnull. The deadline, estimated hours, and completion progress
     * must satisfy {@link #inputValidation(LocalDate, double, byte)}.
     * Empty titles and descriptions are permitted.
     *
     * <p>This defensive check helps detect invalid or corrupted state. It is
     * also used during construction and after edits to reject invalid values.
     * It does not modify or repair the object, and it does not itself throw
     * a validation exception.
     *
     * @return true if all checked fields are valid; otherwise false
     */
    public boolean fieldsValidation() {
        return getUniqueUserId() != 0
            && title != null
            && description != null
            && difficultyLevel != null
            && inputValidation(deadline, estimatedHoursToCompleteTask, completionProgress);
    }

    /**
     * Checks proposed deadline, estimated hours, and completion progress values
     * without modifying this task.
     *
     * <p>Downstream code may use this method to validate parsed user input
     * before applying changes. Only the supplied arguments are checked;
     * the task's owner ID, title, description, and difficulty are not checked
     * by this method.
     *
     * @param deadline the proposed due date; must be nonnull but may be in the past
     * @param estimatedHours the proposed estimated total hours; must be finite
     *                       and greater than zero
     * @param progress the proposed integer completion percentage, from 0 through 100
     * @return true if all three supplied values are valid; otherwise false
     */
    public boolean inputValidation(LocalDate deadline, double estimatedHours, byte progress) {
        return deadline != null
            && Double.isFinite(estimatedHours)
            && estimatedHours > 0
            && progress >= 0
            && progress <= 100;
    }

    /**
     * Updates the task title.
     *
     * <p>An empty title is permitted. A rejected update restores the previous title.
     *
     * @param newTitle the new nonnull title
     * @throws IllegalStateException if the task is invalid before the update
     * @throws IllegalArgumentException if the new title is null
     */
    public void setTitle(String newTitle) {
        if (!fieldsValidation()) {
            throw new IllegalStateException("The task has invalid fields.");
        }
        String previousTitle = this.title;
        this.title = newTitle;

        if (!fieldsValidation()) {
            this.title = previousTitle;
            throw new IllegalArgumentException("The new title cannot be null.");
        }
    }

    /**
     * Updates the task description.
     *
     * <p>An empty description is permitted. A rejected update restores the
     * previous description.
     *
     * @param newDescription the new nonnull description
     * @throws IllegalStateException if the task is invalid before the update
     * @throws IllegalArgumentException if the new description is null
     */
    public void setDescription(String newDescription) {
        if (!fieldsValidation()) {
            throw new IllegalStateException("The task has invalid fields.");
        }
        String previousDescription = this.description;
        this.description = newDescription;

        if (!fieldsValidation()) {
            this.description = previousDescription;
            throw new IllegalArgumentException("The new description cannot be null.");
        }
    }

    /**
     * Past deadlines are allowed, but null deadlines are not.
     *
     * <p>The proposed deadline is validated before assignment. The resulting
     * object state is checked afterward, with the previous deadline restored
     * if that check fails.
     *
     * @param newDeadline the new nonnull due date
     * @throws IllegalStateException if the task is invalid before the update
     * @throws IllegalArgumentException if the proposed deadline or resulting
     *                                  object state fails validation
     */
    public void setDeadline(LocalDate newDeadline) {
        if (!fieldsValidation()) {
            throw new IllegalStateException("The task has invalid fields.");
        }
        if (!inputValidation(newDeadline, estimatedHoursToCompleteTask, completionProgress)) {
            throw new IllegalArgumentException("The new deadline is not valid.");
        }

        LocalDate previousDeadline = this.deadline;
        this.deadline = newDeadline;

        if (!fieldsValidation()) {
            this.deadline = previousDeadline;
            throw new IllegalArgumentException("The new deadline is not valid.");
        }
    }

    /**
     * Updates the estimated total hours needed to complete the task.
     *
     * <p>The proposed value is validated before assignment. The resulting
     * object state is checked afterward, with the previous estimate restored
     * if that check fails.
     *
     * @param newEstimatedHours the new finite estimate, greater than zero
     * @throws IllegalStateException if the task is invalid before the update
     * @throws IllegalArgumentException if the proposed estimate or resulting
     *                                  object state fails validation
     */
    public void setEstimatedHoursToCompleteTask(double newEstimatedHours) {
        if (!fieldsValidation()) {
            throw new IllegalStateException("The task has invalid fields.");
        }
        if (!inputValidation(deadline, newEstimatedHours, completionProgress)) {
            throw new IllegalArgumentException("Estimated hours must be finite and greater than 0.");
        }

        double previousEstimatedHours = this.estimatedHoursToCompleteTask;
        this.estimatedHoursToCompleteTask = newEstimatedHours;

        if (!fieldsValidation()) {
            this.estimatedHoursToCompleteTask = previousEstimatedHours;
            throw new IllegalArgumentException("Estimated hours must be finite and greater than 0.");
        }
    }

    /**
     * Updates the integer completion percentage.
     *
     * <p>Both 0 and 100 are permitted; 100 represents a completed task.
     * The proposed value is validated before assignment. The resulting object
     * state is checked afterward, with the previous progress restored if that
     * check fails.
     *
     * @param newCompletionProgress the new completion percentage, from 0 through 100
     * @throws IllegalStateException if the task is invalid before the update
     * @throws IllegalArgumentException if the proposed progress or resulting
     *                                  object state fails validation
     */
    public void setCompletionProgress(byte newCompletionProgress) {
        if (!fieldsValidation()) {
            throw new IllegalStateException("The task has invalid fields.");
        }
        if (!inputValidation(deadline, estimatedHoursToCompleteTask, newCompletionProgress)) {
            throw new IllegalArgumentException("Completion progress must be between 0 and 100.");
        }

        byte previousCompletionProgress = this.completionProgress;
        this.completionProgress = newCompletionProgress;

        if (!fieldsValidation()) {
            this.completionProgress = previousCompletionProgress;
            throw new IllegalArgumentException("Completion progress must be between 0 and 100.");
        }
    }

    /**
     * Updates the task difficulty.
     *
     * <p>A rejected update restores the previous difficulty.
     *
     * @param newDifficultyLevel the new nonnull difficulty
     * @throws IllegalStateException if the task is invalid before the update
     * @throws IllegalArgumentException if the new difficulty is null
     */
    public void setDifficultyLevel(DifficultyLevel newDifficultyLevel) {
        if (!fieldsValidation()) {
            throw new IllegalStateException("The task has invalid fields.");
        }
        DifficultyLevel previousDifficultyLevel = this.difficultyLevel;
        this.difficultyLevel = newDifficultyLevel;

        if (!fieldsValidation()) {
            this.difficultyLevel = previousDifficultyLevel;
            throw new IllegalArgumentException("The difficulty level cannot be null.");
        }
    }

    /**
     * Returns the task title.
     *
     * @return the current title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the task description.
     *
     * @return the current description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the task deadline.
     *
     * @return the current due date
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * Returns the estimated total workload.
     *
     * @return the current estimated total hours needed to complete the task
     */
    public double getEstimatedHoursToCompleteTask() {
        return estimatedHoursToCompleteTask;
    }

    /**
     * Returns the task's completion percentage.
     *
     * @return the current integer completion percentage
     */
    public byte getCompletionProgress() {
        return completionProgress;
    }

    /**
     * Returns the task difficulty.
     *
     * @return the current difficulty
     */
    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }
}