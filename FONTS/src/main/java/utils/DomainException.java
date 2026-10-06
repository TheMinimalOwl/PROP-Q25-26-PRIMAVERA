package utils;

/**
 *  Exception found when an error found in domain layer is to be propagated
 *    to presentation layer.
 *
 *  This is an unchecked exception (extends {@link RuntimeException}) and is
 *    typically used by the domain to propagate an exception to the presentation
 *    layer.
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class DomainException extends RuntimeException {

    /**
     * Constructs a new {@code DomainException} with the specified detail message.
     *
     * @param message the detail message explaining which entity was not found
     */
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
