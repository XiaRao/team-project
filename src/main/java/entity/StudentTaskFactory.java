package entity;

import java.time.LocalDate;

public class StudentTaskFactory extends TaskFactory {
    private final int uniqueUserId;

    public StudentTaskFactory(int nextTaskUniqueId, int uniqueUserId) {
        super(nextTaskUniqueId);
        this.uniqueUserId = uniqueUserId;
    }

    public StudentTask createStudentTask(String title,
                                         String description,
                                         LocalDate deadline,
                                         double estimatedHoursToCompleteTask,
                                         byte completionProgress,
                                         DifficultyLevel difficultyLevel) {
        int taskId = generateNextTaskUniqueId();

        return new StudentTask(taskId, uniqueUserId, title, description,
                deadline, estimatedHoursToCompleteTask,
                completionProgress, difficultyLevel);
    }

    /**
     * Creates a task with 10 estimated hours, 0% completion,
     * and Average difficulty.
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