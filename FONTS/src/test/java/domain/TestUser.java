package domain;

import org.junit.*;
import static org.junit.Assert.*;

public class TestUser {

    private User user;

    @Before
    public void setUp() {
        // Create a User with idUser=42
        user = new User(42);
    }

    /**
     * Test object: Test the constructor of User
     * Operation: Create a User and check that the idUser field
     * is initialized correctly.
     */
    @Test
    public void testConstructorInitializesId() {
        assertEquals(Integer.valueOf(42), user.getIdUser());
    }

    /**
     * Test object: Test the getter getIdUser()
     * Operation: Create a User with a known ID and check that getIdUser()
     * returns it.
     */
    @Test
    public void testGetIdUser() {
        User anotherUser = new User(99);
        assertEquals(Integer.valueOf(99), anotherUser.getIdUser());
    }

    /**
     * Test object: Test with ID=0
     * Operation: Create a User with ID=0 and check that it is returned correctly.
     */
    @Test
    public void testIdZero() {
        User zeroUser = new User(0);
        assertEquals(Integer.valueOf(0), zeroUser.getIdUser());
    }

    /**
     * Test object: Test with a large ID
     * Operation: Create a User with a very large ID and check that it is
     * returned correctly.
     */
    @Test
    public void testLargeId() {
        User largeUser = new User(Integer.MAX_VALUE);
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), largeUser.getIdUser());
    }

    /**
     * Test object: Test with ID=null
     * Operation: Create a User with ID=null and check that getIdUser()
     * throws a NullPointerException.
     */
    @Test(expected = NullPointerException.class)
    public void testNullIdThrowsException() {
        User nullUser = new User(null);
        nullUser.getIdUser(); // should throw NullPointerException
    }
}
