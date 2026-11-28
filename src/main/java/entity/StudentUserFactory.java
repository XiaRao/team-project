package entity;

import java.util.List;

/**
 * Creates student users with sequential unique user IDs.
 *
 * <p>Use {@link #createStudentUser(String, String, Country)} for a new account.
 * Use {@code restoreStudentUser(...)} to recreate an existing account while
 * preserving its saved user ID.
 *
 * <p>The user ID counter is inherited from {@link UserFactory}. It represents
 * the next available user ID across accounts, separate from the task ID
 * counter maintained by each user's {@link StudentTaskFactory}.
 * Each factory instance maintains its own user ID counter; callers are
 * responsible for coordinating instances with the application's saved counter.
 *
 * <p>When loading existing application data, initialize this factory with the
 * saved next user ID. After creating a new account, persist the account together
 * with the updated counter returned by {@link #getNextUniqueId()}.
 * Restoring an existing account does not advance that counter.
 *
 * <p>This factory constructs objects in memory. The calling layers are
 * responsible for reading and writing persistent data, checking username
 * uniqueness, and verifying login credentials. Usernames and passwords are
 * stored as supplied, including null values; this factory does not validate them.
 */
public class StudentUserFactory extends UserFactory {

    /**
     * Creates a factory whose first user ID is 1.
     *
     * <p>Use this constructor when starting a new user ID sequence.
     * When continuing an existing sequence from persistent storage, use
     * {@link #StudentUserFactory(int)} instead.
     */
    public StudentUserFactory() {
        super();
    }

    /**
     * Creates a factory using the next available user ID restored from storage.
     *
     * <p>The supplied value is the next ID to allocate, not the ID of the most
     * recently created user. It must be greater than every existing user ID
     * in the application's saved data.
     *
     * <p>This constructor accepts the counter value; the calling layer is
     * responsible for loading that value from persistent storage.
     *
     * @param nextUniqueId the positive next available user ID
     * @throws IllegalArgumentException if nextUniqueId is zero or negative
     */
    public StudentUserFactory(int nextUniqueId) {
        super(nextUniqueId);
    }

    /**
     * Creates a student user with the selected country and an empty task list.
     * The user's task factory starts at task ID 1.
     *
     * <p>The new user receives this factory's next available user ID.
     * Successful creation advances the user ID counter by one.
     * The user's task factory is bound to that new user's ID.
     *
     * <p>A null country is rejected before a user ID is allocated.
     * Username and password values are passed through without validation.
     *
     * <p>The caller is responsible for saving the returned user together with
     * the updated value from {@link #getNextUniqueId()}.
     *
     * @param username the username to store unchanged; may be null
     * @param password the password to store unchanged; may be null
     * @param countryCode the user's nonnull country
     * @return a new user with the selected country, an empty task list,
     *         and a task factory whose next task ID is 1
     * @throws IllegalArgumentException if countryCode is null
     * @throws IllegalStateException if the next user ID has reached
     *         {@link Integer#MAX_VALUE} and no further ID can be allocated
     */
    public StudentTaskUser createStudentUser(
        String username,
        String password,
        Country countryCode) {

        // Validate before allocating a user ID.
        if (countryCode == null) {
            throw new IllegalArgumentException("Country code cannot be null.");
        }

        StudentTaskUser user = new StudentTaskUser(
            username, password, generateNextUniqueId());
        user.setCountryCode(countryCode);

        return user;
    }

    /**
     * Restores an existing user without advancing the global user ID counter.
     * This factory must be initialized with the saved nextUniqueId.
     * The supplied tasks must already be reconstructed from storage.
     *
     * <p>The saved user ID must be positive and less than this factory's
     * current next available user ID. The restored user retains the supplied
     * ID, username, password, and country.
     *
     * <p>The restored user's task factory is initialized with
     * {@code nextTaskUniqueId} and bound to {@code uniqueUserId}.
     * The next task ID must be positive and greater than every task ID
     * in the supplied list. Restoration does not allocate user or task IDs.
     *
     * <p>A null task list is treated as an empty list. Otherwise, the list
     * must contain only nonnull tasks belonging to this user, with no
     * duplicate task IDs. Its membership is copied into a new internal list;
     * the task objects themselves are shared, not copied.
     *
     * <p>Callers may also restore the user before reconstructing its tasks:
     *
     * <pre>{@code
     * StudentUserFactory factory = new StudentUserFactory(savedNextUserId);
     * StudentTaskUser user = factory.restoreStudentUser(
     *     savedUserId, savedUsername, savedPassword, savedCountry,
     *     java.util.Collections.emptyList(), savedNextTaskId);
     * }</pre>
     *
     * <p>Then, for each saved task, call
     * {@code user.studentTaskFactory.restoreStudentTask(...)} followed by
     * {@code user.addTask(task)}. With consistent saved IDs and counters,
     * these operations leave the saved next task ID unchanged.
     *
     * <p>The caller supplies values already loaded from storage and performs
     * any required credential verification. This method recreates the user;
     * it does not authenticate a login or write persistent data.
     *
     * @param uniqueUserId the positive saved user ID; must be less than
     *                     this factory's current next available user ID
     * @param username the saved username to store unchanged; may be null
     * @param password the saved password to store unchanged; may be null
     * @param countryCode the user's nonnull saved country
     * @param tasks the reconstructed tasks, or null for an empty task list
     * @param nextTaskUniqueId the positive saved next task ID; must exceed
     *                         every task ID in the supplied list
     * @return a recreated user preserving the supplied account information,
     *         task membership, and next task ID
     * @throws IllegalArgumentException if uniqueUserId is not positive or
     *         is not less than this factory's next available user ID;
     *         if countryCode is null; if nextTaskUniqueId is not positive
     *         or does not exceed every supplied task ID; or if the list
     *         contains a null task, a task belonging to another user,
     *         or duplicate task IDs
     */
    public StudentTaskUser restoreStudentUser(
        int uniqueUserId,
        String username,
        String password,
        Country countryCode,
        List<StudentTask> tasks,
        int nextTaskUniqueId) {

        if (uniqueUserId <= 0) {
            throw new IllegalArgumentException(
                "The restored user unique ID must be positive.");
        }

        if (uniqueUserId >= getNextUniqueId()) {
            throw new IllegalArgumentException(
                "The restored user unique ID must be less than "
                    + "the next user unique ID.");
        }

        if (countryCode == null) {
            throw new IllegalArgumentException(
                "Country code cannot be null.");
        }

        StudentTaskUser user = new StudentTaskUser(
            username,
            password,
            uniqueUserId,
            tasks,
            nextTaskUniqueId);

        user.setCountryCode(countryCode);
        return user;
    }

}