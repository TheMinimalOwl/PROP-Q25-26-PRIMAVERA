package domain;

import org.junit.*;
import static org.junit.Assert.*;

public class TestAnonymousUser {

    private AnonymousUser anonUser;

    @Before
    public void setUp() {
        // Create an AnonymousUser with idUser=123
        anonUser = new AnonymousUser(123);
    }

    /**
     * Test object: Test the constructor of AnonymousUser
     * Operation: Create an AnonymousUser and check that the idUser field
     * is initialized correctly.
     */
    @Test
    public void testConstructorInitializesId() {
        assertEquals(Integer.valueOf(123), anonUser.getIdUser());
    }

    /**
     * Test object: Test with ID=0
     * Operation: Create an AnonymousUser with ID=0 and check that it is
     * returned correctly.
     */
    @Test
    public void testIdZero() {
        AnonymousUser zeroUser = new AnonymousUser(0);
        assertEquals(Integer.valueOf(0), zeroUser.getIdUser());
    }

    /**
     * Test object: Test with a large ID
     * Operation: Create an AnonymousUser with a very large ID and check that it is
     * returned correctly.
     */
    @Test
    public void testLargeId() {
        AnonymousUser largeUser = new AnonymousUser(Integer.MAX_VALUE);
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), largeUser.getIdUser());
    }

    /**
     * Test object: Test with ID=null
     * Operation: Create an AnonymousUser with ID=null and check that getIdUser()
     * throws a NullPointerException.
     */
    @Test(expected = NullPointerException.class)
    public void testNullIdThrowsException() {
        AnonymousUser nullUser = new AnonymousUser(null);
        nullUser.getIdUser(); // should throw NullPointerException
    }
}
