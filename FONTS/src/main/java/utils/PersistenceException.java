package utils;

/**
 *  Exception found when an error found in persistence layer is to be propagated
 *    to domain layer.
 *
 *  This is an unchecked exception (extends {@link RuntimeException}) and is
 *    typically used by persistence to propagate an exception to the domain
 *    layer.
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class PersistenceException extends RuntimeException {

    /**
     * Constructs a new {@code PersistenceException} with the specified detail message.
     *
     * @param message the detail message explaining which entity was not found
     */
    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
