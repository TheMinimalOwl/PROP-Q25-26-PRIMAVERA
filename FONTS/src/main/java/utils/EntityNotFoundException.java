package utils;

/**
 * Exception thrown when an entity is requested (e.g. a User) but not found in
 *  the storage.
 *
 *  This is an unchecked exception (extends {@link RuntimeException}) and is
 *    typically used by persistence controllers when a lookup by key fails.
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class EntityNotFoundException extends RuntimeException {
    /**
     * Constructs a new {@code EntityNotFoundException} with the specified detail message.
     *
     * @param message the detail message explaining which entity was not found
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
}
