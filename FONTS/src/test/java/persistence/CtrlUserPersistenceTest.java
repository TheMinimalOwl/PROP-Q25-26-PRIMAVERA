package persistence;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import domain.AnonymousUser;
import domain.RegisteredUser;
import domain.User;
import utils.EntityNotFoundException;

/**
 * Unit test for the class CtrlUserPersistence
 * @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
 */
public class CtrlUserPersistenceTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    private PropertySetter p = new PropertySetter();

    private CtrlUser ctrlUser;


    @Before
    public void setUp() throws IOException {
        p.set(tempDir.getRoot().toPath());

        ctrlUser = CtrlUser.getInstance();
        // reset CtrlUser Singleton
        ctrlUser.resetForTests();
        ctrlUser.clear();
    }

    @After
    public void tearDown() throws IOException {
        p.restore();

        // reset CtrlUser Singleton
        ctrlUser.resetForTests();
    }

    @Test
    public void testSaveAndLoadSingleRegisteredUser() throws IOException, EntityNotFoundException {
        // Create and add a RegisteredUser
        RegisteredUser user = new RegisteredUser("testuser", "password123", "test@example.com");
        user.setAnswers(new ArrayList<>(Arrays.asList(10, 20, 30)));
        Integer id = ctrlUser.addUser(user);

        // Clear in-memory data
        ctrlUser.close();

        // Load single user from file
        User loadedUser = ctrlUser.getUser(id);

        // Verify user is restored
        assertNotNull(loadedUser);
        assertTrue(loadedUser instanceof RegisteredUser);
        
        RegisteredUser loadedRegistered = (RegisteredUser) loadedUser;
        assertEquals("testuser", loadedRegistered.getUsername());
        assertEquals("password123", loadedRegistered.getPassword());
        assertEquals("test@example.com", loadedRegistered.getEmail());
        assertEquals(Arrays.asList(10, 20, 30), loadedRegistered.getAnswers());
    }

    @Test
    public void testSaveAndLoadAnonymousUser() throws IOException, EntityNotFoundException {
        // Create and add an AnonymousUser
        AnonymousUser user = new AnonymousUser();
        Integer id = ctrlUser.addUser(user);

        // Clear in-memory data
        ctrlUser.close();

        // Load from file
        User loadedUser = ctrlUser.getUser(id);

        // Verify user is restored
        assertNotNull(loadedUser);
        assertTrue(loadedUser instanceof AnonymousUser);
        assertEquals(id, loadedUser.getIdUser());
    }

    @Test
    public void testSaveAndLoadAllUsers() throws IOException, EntityNotFoundException {
        // Create multiple users
        RegisteredUser user1 = new RegisteredUser("user1", "password123", "user1@example.com");
        RegisteredUser user2 = new RegisteredUser("user2", "password456", "user2@example.com");
        AnonymousUser user3 = new AnonymousUser();

        Integer id1 = ctrlUser.addUser(user1);
        Integer id2 = ctrlUser.addUser(user2);
        Integer id3 = ctrlUser.addUser(user3);

        // Clear and reload
        ctrlUser.close();

        // Verify all users are restored
        RegisteredUser loaded1 = (RegisteredUser) ctrlUser.getUser(id1);
        assertEquals("user1", loaded1.getUsername());
        
        RegisteredUser loaded2 = (RegisteredUser) ctrlUser.getUser(id2);
        assertEquals("user2", loaded2.getUsername());
        
        User loaded3 = ctrlUser.getUser(id3);
        assertTrue(loaded3 instanceof AnonymousUser);
    }

    @Test
    public void testIdCounterPersistence() throws IOException {
        // Add some users
        ctrlUser.addUser(new RegisteredUser("user1", "password123", "user1@example.com"));
        ctrlUser.addUser(new RegisteredUser("user2", "password456", "user2@example.com"));

        // Clear and reload
        ctrlUser.close();

        // Add a new user - should get ID 3, not 1
        RegisteredUser newUser = new RegisteredUser("user3", "password789", "user3@example.com");
        Integer newId = ctrlUser.addUser(newUser);

        assertEquals(Integer.valueOf(3), newId);
    }

    @Test(expected = FileNotFoundException.class)
    public void testLoadNonExistentUser() throws IOException, FileNotFoundException {
        // Load should throw exception for non-existent user
        ctrlUser.getUser(999);
    }

    @Test
    public void testDeleteUserFile() throws IOException, EntityNotFoundException {
        // Create and save a user
        RegisteredUser user = new RegisteredUser("testuser", "password123", "test@example.com");
        Integer id = ctrlUser.addUser(user);

        // Verify file exists
        assertTrue(ctrlUser.existsUser(id));

        // Delete the file
        ctrlUser.deleteUser(id);

        // Verify file no longer exists
        assertFalse(ctrlUser.existsUser(id));
    }

    @Test
    public void testModificationPersistence() throws IOException, EntityNotFoundException {
        // 1. Add a user
        RegisteredUser user = new RegisteredUser("originalName", "originalPass123", "original@example.com");
        Integer id = ctrlUser.addUser(user);

        // 2. Get the user (should be the same instance from cache)
        RegisteredUser retrievedUser = (RegisteredUser) ctrlUser.getUser(id);
        
        // 3. Modify the user object
        retrievedUser.setUsername("modifiedName");
        retrievedUser.setPassword("modifiedPass123");

        // 4. Clear the cache (should trigger save of modified object)
        ctrlUser.close();

        // 5. Load the user again from disk
        RegisteredUser loadedUser = (RegisteredUser) ctrlUser.getUser(id);

        // 6. Verify modifications are present
        assertEquals("modifiedName", loadedUser.getUsername());
        assertEquals("modifiedPass123", loadedUser.getPassword());
        assertEquals("original@example.com", loadedUser.getEmail());
    }
}
