package domain;

import org.junit.*;
import static org.junit.Assert.*;

public class TestRegisteredUser {

    private RegisteredUser user;

    @Before
    public void setUp() {
        // Create a RegisteredUser with valid data
        user = new RegisteredUser(1, "joel", "abc12345", "joel@mail.com");
    }

    /**
     * Test object: Test the constructor
     * Operation: Create a RegisteredUser with valid data and check that the fields are initialized correctly.
     */
    @Test
    public void testConstructorInitializesFields() {
        assertEquals(Integer.valueOf(1), user.getIdUser());
        assertEquals("joel", user.getUsername());
        assertEquals("abc12345", user.getPassword());
        assertEquals("joel@mail.com", user.getEmail());
    }

    /**
     * Test object: Test the setter of username with a valid value
     */
    @Test
    public void testSetUsernameValid() {
        user.setUsername("nuevoNombre");
        assertEquals("nuevoNombre", user.getUsername());
    }

    /**
     * Test object: Test the setter of username with null or empty value
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetUsernameInvalidEmpty() {
        user.setUsername("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetUsernameInvalidNull() {
        user.setUsername(null);
    }

    /**
     * Test object: Test the setter of password with a valid value
     */
    @Test
    public void testSetPasswordValid() {
        user.setPassword("pass1234");
        assertEquals("pass1234", user.getPassword());
    }

    /**
     * Test object: Test the setter of password with a value that is too short
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetPasswordTooShort() {
        user.setPassword("abc1");
    }

    /**
     * Test object: Test the setter of password without numbers
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetPasswordNoNumbers() {
        user.setPassword("abcdefgh");
    }

    /**
     * Test object: Test the setter of password without letters
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetPasswordNoLetters() {
        user.setPassword("12345678");
    }

    /**
     * Test object: Test the setter of email with a valid value
     */
    @Test
    public void testSetEmailValid() {
        user.setEmail("nuevo@mail.com");
        assertEquals("nuevo@mail.com", user.getEmail());
    }

    /**
     * Test object: Test the setter of email with an invalid format
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetEmailInvalidFormat() {
        user.setEmail("correoSinArroba.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetEmailNull() {
        user.setEmail(null);
    }
}
