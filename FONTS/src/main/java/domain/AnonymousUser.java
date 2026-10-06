package domain;

/**
 * @author Joel Forcadell (joel.forcadell@estudiantat.upc.edu)
 * 
 * Represents an anonymous user in the system.
 * <p>
 * Unlike a {@link RegisteredUser}, an AnonymousUser only stores
 * the user identifier inherited from the {@link User} class.
 * This class is useful for cases where a user interacts with the system
 * without providing registration details such as username, password, or email.
 * </p>
 */
public class AnonymousUser extends User {
    /**
     * Constructs a new AnonymousUser.
     */
    public AnonymousUser() {
        super();
    }

    /**
     * Constructs a new AnonymousUser with the given user identifier.
     *
     * @param idUser the identifier of the anonymous user
     */
    public AnonymousUser(Integer idUser) {
        super(idUser);
    }
}
