package utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for the class EntityNotFoundException.
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class EntityNotFoundExceptionTest {

    @Test
    public void testConstructorWithMessage() {
        String expectedMessage = "Entity not found with ID 123";

        EntityNotFoundException exception = new EntityNotFoundException(expectedMessage);

        assertEquals("The exception message should match the expected message", expectedMessage, exception.getMessage());
    }

    @Test
    public void testExceptionType() {
        String expectedMessage = "Entity not found with ID 123";

        EntityNotFoundException exception = new EntityNotFoundException(expectedMessage);

        assertTrue("Exception should be an instance of EntityNotFoundException", exception instanceof EntityNotFoundException);
        assertTrue("Exception should be an instance of RuntimeException", exception instanceof RuntimeException);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testThrowException() {
        String expectedMessage = "Entity not found with ID 123";

        throw new EntityNotFoundException(expectedMessage); // This should throw the exception
    }
}
