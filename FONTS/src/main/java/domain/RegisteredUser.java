package domain;

import java.util.ArrayList;

/**
 * @author Joel Forcadell (joel.forcadell@estudiantat.upc.edu)
 * 
 * Represents a registered user in the system.
 * Stores the user's ID, username, password, and email address.
 * Provides validation for username, password, and email format.
 */
public class RegisteredUser extends User {
    private String username;
    private String password;
    private String email;
    private ArrayList<Integer> forms; // forms which the RegisteredUser is propietary

    // ========= CONSTRUCTORS ==========

    /**
     * Constructs a new RegisteredUser with the given parameters.
     * Validates the username, password, and email before assignment.
     *
     * @param username the username of the registered user
     * @param password the password of the registered user
     * @param email    the email address of the registered user
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public RegisteredUser(String username, String password, String email) {
        super();
        setUsername(username);
        setPassword(password);
        setEmail(email);
        this.forms = new ArrayList<>();
    }

    /**
     * Constructs a new RegisteredUser with the given parameters.
     * Validates the username, password, and email before assignment.
     *
     * @param idUser   the identifier of the user
     * @param username the username of the registered user
     * @param password the password of the registered user
     * @param email    the email address of the registered user
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public RegisteredUser(Integer idUser, String username, String password, String email) {
        super(idUser);
        setUsername(username);
        setPassword(password);
        setEmail(email);
        this.forms = new ArrayList<>();
    }

    // ========= GETTERS AND SETTERS ===========

    /**
     * Returns the username of the user.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     * The username must not be null or empty.
     *
     * @param username the new username
     * @throws IllegalArgumentException if the username is null or empty
     */
    public void setUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        this.username = username;
    }

    /**
     * Returns the password of the user.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     * The password must be at least 8 characters long and contain both letters and numbers.
     *
     * @param password the new password
     * @throws IllegalArgumentException if the password is invalid
     */
    public void setPassword(String password) {
        if (password == null || !isValidPassword(password)) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters long and contain letters and numbers");
        }
        this.password = password;
    }

    /**
     * Returns the email address of the user.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     * The email must follow a valid format (e.g., abcd@mail.com).
     *
     * @param email the new email address
     * @throws IllegalArgumentException if the email format is invalid
     */
    public void setEmail(String email) {
        if (email == null || !isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }

    /**
     * @return all forms associated with RegisteredUser
     */
    public ArrayList<Integer> getForms() {
        return forms;
    }

    /**
     * @param forms
     */
    public void setForms(ArrayList<Integer> forms) {
        this.forms = forms;
    }

    /**
     * Associates Form idForm with RegisteredUser
     *
     * @param idForm form to be associated with RegisteredUser
     */
    public void addForm(Integer idForm) {
        this.forms.add(idForm);
    }

    /**
     * Disassociates Form idForm from RegisteredUser
     *
     * @param idForm form to be disassociated from RegisteredUser
     */
    public void removeForm(Integer idForm) {
        forms.remove(idForm);
    }


    // ========= HELPER METHODS ===========

    /**
     * Validates the format of an email address.
     *
     * @param email the email to validate
     * @return true if the email matches the expected format, false otherwise
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * Validates the format of a password.
     * The password must be at least 8 characters long,
     * contain at least one letter and at least one number.
     *
     * @param password the password to validate
     * @return true if the password is valid, false otherwise
     */
    private boolean isValidPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Za-z].*") &&
                password.matches(".*[0-9].*");
    }
}
