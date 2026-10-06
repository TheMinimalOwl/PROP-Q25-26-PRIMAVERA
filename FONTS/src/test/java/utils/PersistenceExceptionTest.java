package utils;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;

/**
 * Unit test for the class PersistenceException.
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class PersistenceExceptionTest {

    @Test
    public void testConstructorWithMessageAndCause() {
        String expectedMessage = "Persistence layer failed";
        Throwable cause = new IOException("Disk error");

        PersistenceException exception = new PersistenceException(expectedMessage, cause);

        assertEquals("The exception message should match the expected message",
                expectedMessage, exception.getMessage());
        assertEquals("The cause should match the provided throwable",
                cause, exception.getCause());
    }

    @Test
    public void testExceptionType() {
        PersistenceException exception = new PersistenceException("Persistence error", new RuntimeException());

        assertTrue("Exception should be an instance of PersistenceException",
                exception instanceof PersistenceException);
        assertTrue("Exception should be an instance of RuntimeException",
                exception instanceof RuntimeException);
    }

    @Test(expected = PersistenceException.class)
    public void testThrowException() {
        throw new PersistenceException("Persistence error", new RuntimeException("Cause"));
    }
}
