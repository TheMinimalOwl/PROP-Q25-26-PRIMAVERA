package persistence;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit test for the class FileManager
 * @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
 */
public class FileManagerTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    private PropertySetter p = new PropertySetter();

    private FileManager fileManager;
    private static final String TEST_DIR = "test_filemanager";

    // Simple class for testing serialization
    private static class TestObject {
        String name;
        int value;

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestObject that = (TestObject) o;
            return value == that.value && name.equals(that.name);
        }
    }

    @Before
    public void setUp() {
        p.set(tempDir.getRoot().toPath());

        fileManager = FileManager.getInstance();
        fileManager.resetDataPathForTests();
    }

    @After
    public void tearDown() {
        p.restore();
        fileManager.resetDataPathForTests();
    }

    @Test
    public void testSaveAndLoad() throws IOException {
        Path fileName = Paths.get(TEST_DIR, "test_obj.json");
        TestObject original = new TestObject("test", 123);

        fileManager.save(fileName, original);

        TestObject loaded = fileManager.load(fileName, TestObject.class);

        assertNotNull("Loaded object should not be null", loaded);
        assertEquals("Loaded object should match original", original, loaded);
    }

    @Test
    public void testExists() throws IOException {
        Path fileName = Paths.get(TEST_DIR, "exists_check.json");
        TestObject obj = new TestObject("exists", 1);

        assertFalse("File should not exist before save", fileManager.exists(fileName));

        fileManager.save(fileName, obj);

        assertTrue("File should exist after save", fileManager.exists(fileName));
    }

    @Test
    public void testDelete() throws IOException {
        Path fileName = Paths.get(TEST_DIR, "delete_check.json");
        TestObject obj = new TestObject("delete", 1);

        fileManager.save(fileName, obj);
        assertTrue(fileManager.exists(fileName));

        boolean deleted = fileManager.delete(fileName);
        assertTrue("Delete should return true", deleted);
        assertFalse("File should not exist after delete", fileManager.exists(fileName));
    }

    @Test
    public void testListFiles() throws IOException {
        Path dirName = Paths.get(TEST_DIR, "list_dir");
        Path file1 = dirName.resolve("file1.json");
        Path file2 = dirName.resolve("file2.json");

        fileManager.save(file1, new TestObject("1", 1));
        fileManager.save(file2, new TestObject("2", 2));

        List<Path> files = fileManager.listFiles(dirName);
        
        assertNotNull("List files should not return null", files);
        assertEquals("Should find 2 files", 2, files.size());
        
        // Verify filenames
        boolean found1 = false;
        boolean found2 = false;
        for (Path f : files) {
            if (f.getFileName().toString().equals("file1.json")) found1 = true;
            if (f.getFileName().toString().equals("file2.json")) found2 = true;
        }
        assertTrue("file1.json should be found", found1);
        assertTrue("file2.json should be found", found2);
    }
    
    @Test(expected = FileNotFoundException.class)
    public void testLoadNonExistent() throws IOException {
        Path fileName = Paths.get(TEST_DIR, "non_existent.json");
        fileManager.load(fileName, TestObject.class);
    }
}
