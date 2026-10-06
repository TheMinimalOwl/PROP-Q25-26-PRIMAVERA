package domain;

import java.util.ArrayList;

/**
 * @author Joel Forcadell (joel.forcadell@estudiantat.upc.edu)
 * 
 *         Represents a generic user in the system.
 *         <p>
 *         The {@code User} class serves as the base class for different types
 *         of users,
 *         such as {@link RegisteredUser} and {@link AnonymousUser}.
 *         It stores only the unique identifier of the user, which can be
 *         extended
 *         with additional attributes in subclasses.
 *         </p>
 */
public class User {

    public static final Integer UNASSIGNED_ID = -1;
    /** Unique identifier of the user. */
    private Integer idUser = UNASSIGNED_ID;
    private ArrayList<Integer> answers;

    /**
     * Constructs a new User without assigning and id.
     *
    */
    public User() {
        this.idUser = UNASSIGNED_ID;
        this.answers = new ArrayList<>();
    }

    /**
     * Constructs a new User with the given identifier.
     *
     * @param idUser the identifier of the user
     * @throws NullPointerException if the idUser is null
     */
    public User(Integer idUser) {
        if (idUser == null) {
            throw new NullPointerException("idUser cannot be null");
        }
        this.idUser = idUser;
        this.answers = new ArrayList<>();
    }

    /**
     * Returns true if the instance has been assigned an id.
     *
     */
    public Boolean hasAssignedId() {
        return (idUser != UNASSIGNED_ID);
    }

    /**
     * Returns the user identifier.
     *
     * @return the user ID
     */
    public Integer getIdUser() {
        return idUser;
    }

    /**
     * Sets the unique identifier of the user.
     *
     * @param idUser the new user ID to assign
     * @throws NullPointerException if the idUser is null
     */
    public void setIdUser(Integer idUser) {
        if (idUser == null) {
            throw new NullPointerException("idUser cannot be null");
        }
        this.idUser = idUser;
    }
   
    /**
     * @return all annswers associated with User
     */
    public ArrayList<Integer> getAnswers() {
        return answers;
    }

    /**
     * @param answers 
     */
    public void setAnswers(ArrayList<Integer> answers) {
        this.answers = answers;
    }

    /**
     * Associates Answer with external key idUser+idForm with User
     *
     * @param idForm answer to be associated with User
     */
    public void addAnswer(Integer idForm) {
        this.answers.add(idForm);
    }

    /**
     * Disassociates Answer with external key idUser+idForm from User
     *
     * @param idForm answer to be disassociated from User
     */
    public void removeAnswer(Integer idForm) {
        answers.remove(idForm);
    }
}
