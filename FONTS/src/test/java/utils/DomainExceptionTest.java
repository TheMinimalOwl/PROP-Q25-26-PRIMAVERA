package utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for the class DomainException.
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class DomainExceptionTest {

    @Test
    public void testConstructorWithMessageAndCause() {
        String expectedMessage = "Domain layer failed";
        Throwable cause = new IllegalArgumentException("Invalid input");

        DomainException exception = new DomainException(expectedMessage, cause);

        assertEquals("The exception message should match the expected message",
                expectedMessage, exception.getMessage());
        assertEquals("The cause should match the provided throwable",
                cause, exception.getCause());
    }

    @Test
    public void testExceptionType() {
        DomainException exception = new DomainException("Domain error", new RuntimeException());

        assertTrue("Exception should be an instance of DomainException",
                exception instanceof DomainException);
        assertTrue("Exception should be an instance of RuntimeException",
                exception instanceof RuntimeException);
    }

    @Test(expected = DomainException.class)
    public void testThrowException() {
        throw new DomainException("Domain error", new RuntimeException("Cause"));
    }
}
