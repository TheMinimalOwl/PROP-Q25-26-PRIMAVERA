package persistence;

import java.nio.file.NoSuchFileException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import domain.User;
import utils.EntityNotFoundException;

/**
 * The CtrlUser class is responsible for controlling persistence-related
 *  operations on the class User.
 * It's intended to be called by the controller of the persistence.
 *
 * Stores Users in memory and disk through the Cache. IDs are auto-generated
 *  if not provided.
 * This class follows the singleton pattern.
 *
 * The CtrlUser::close() method needs to be run manually **before** this class
 *    is destructed.
 * 
 * @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
 */
public class CtrlUser {
    /** Inner static class for lazy-loaded singleton. */
    private static class CtrlUserSingleton {
        private static final CtrlUser instance = new CtrlUser();
    }

    private static final String USERS_DIR = "Users";

    // ==============
    // Static Getters
    // ==============

    /**
     * Returns the singleton instance of this class.
     *
     * @return the singleton {@link CtrlUser}
     */
    protected static CtrlUser getInstance() {
        return CtrlUserSingleton.instance;
    }

    /**
     * Returns the file path for a specific user.
     *
     * @return the path to the user's file
     */
    protected static Path getUsersDir() {
        return Paths.get(USERS_DIR);
    }

    /**
     * Returns the directory path where users are stored.
     *
     * @param idUser the external user ID
     * @return the path to the users directory
     * @throws IOException if an I/O error occurs during cache persistence
     */
    protected static Path getUserPath(Integer idUser) {
        return getUsersDir().resolve(idUser.toString() + ".json");
    }

    /**
     * Resets the cache
     */
    protected void resetForTests() {
        usersCache.resetForTests();
    }

    // =========================
    // Variables And Constructor
    // =========================

    private final Cache<User> usersCache;

    /** Private constructor for singleton. Initializes all in-memory storage. */
    private CtrlUser() {
        this.usersCache = new Cache<User>(User.class);
    }

    // =======================
    // User-related operations
    // =======================

    /**
     * Stores a new User and returns its external ID.
     *
     * @param user the User to store
     * @return idUser, the external user ID assigned to the new User
     * @throws IllegalArgumentException if the user is null
     * @throws IOException if an I/O error occurs
     */
    protected Integer addUser(User user) throws IllegalArgumentException, IOException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.hasAssignedId()) {
            throw new IllegalArgumentException("User ID must be unassigned");
        }

        Integer idUser = usersCache.putAndReturnId(getUsersDir(), user);
        user.setIdUser(idUser);

        return user.getIdUser();
    }

    /**
     * Returns the User associated with the given external ID.
     *
     * @param idUser the external user ID
     * @return the User associated with idUser
     * @throws EntityNotFoundException if no User exists with the given ID
     * @throws IOException if an I/O error occurs
     */
    protected User getUser(Integer idUser) throws EntityNotFoundException, IOException {
        try {
            return usersCache.get(getUserPath(idUser));
        }

        catch (NoSuchFileException e) {
            throw new NoSuchFileException("User with ID" + idUser + " not found.");
        }
    }

    /**
     * Deletes the User associated with the given external ID.
     *
     * @param idUser the external user ID
     * @throws EntityNotFoundException if no User exists with the given ID
     * @throws IOException if an I/O error occurs
     */
    protected void deleteUser(Integer idUser) throws EntityNotFoundException, IOException {
        try {
            usersCache.remove(getUserPath(idUser));
        }

        catch (NoSuchFileException e) {
            throw new NoSuchFileException("User with ID" + idUser + " not found.");
        }
    }

    /**
     * Checks if a User exists with the given external ID.
     *
     * @param idUser the external user ID
     * @return true if the User exists, false otherwise
     */
    protected Boolean existsUser(Integer idUser) {
        return usersCache.containsKey(getUserPath(idUser));
    }

    // ==================
    // General operations
    // ==================

    /**
     * Operation needed to be run before this class is destructed
     *
     * @throws IOException
     */
    protected void close() throws IOException {
        usersCache.persistCache();
        usersCache.flushCache();
    }

    /**
     * Clears all in-memory data (users and counter).
     */
    protected void clear() throws IOException {
        usersCache.flushCache();
    }
}
