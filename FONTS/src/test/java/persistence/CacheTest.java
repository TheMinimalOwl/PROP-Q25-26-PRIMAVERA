package persistence;

import com.google.gson.reflect.TypeToken;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;

import static org.junit.Assert.*;

/**
 * Unit test for the class Cache.
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class CacheTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    private Cache<TestData> cache;
    private Type type;

    private PropertySetter p = new PropertySetter();
    
    @Before
    public void setUp() {
        p.set(tempDir.getRoot().toPath());
        
        type = new TypeToken<TestData>() {}.getType();
        cache = new Cache<>(type);
        cache.resetForTests();
    }

    @After
    public void tearDown() {
        p.restore();

        cache.resetForTests();
    }

    @Test
    public void testPutAndGet() throws IOException {
        Path filePath = Path.of("test.json");
        TestData data = new TestData("Alice", 25);

        cache.put(filePath, data);
        TestData result = cache.get(filePath);

        assertEquals("Alice", result.name);
        assertEquals(25, result.age);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullThrowsException() {
        Path filePath = Path.of("null.json");
        cache.put(filePath, null);
    }

    @Test
    public void testPutAndReturnIdCreatesSequentialIds() throws IOException {
        Path dirPath = Path.of("dir");
        TestData data = new TestData("Bob", 30);

        Integer id1 = cache.putAndReturnId(dirPath, data);
        Integer id2 = cache.putAndReturnId(dirPath, new TestData("Charlie", 40));

        assertEquals(Integer.valueOf(1), id1);
        assertEquals(Integer.valueOf(2), id2);
    }

    @Test
    public void testGetLoadsFromDiskIfNotInCache() throws IOException {
        Path filePath = Path.of("disk.json");
        TestData data = new TestData("Disk", 99);

        // Save directly via FileManager
        FileManager.getInstance().save(filePath, data, type);

        // Cache should load from disk
        TestData loaded = cache.get(filePath);
        assertEquals("Disk", loaded.name);
        assertEquals(99, loaded.age);
    }

    @Test(expected = java.io.FileNotFoundException.class)
    public void testGetNonExistentThrowsException() throws IOException {
        Path filePath = Path.of("missing.json");
        cache.get(filePath);
    }

    @Test
    public void testRemoveDeletesFile() throws IOException {
        Path filePath = Path.of("delete.json");
        TestData data = new TestData("Del", 50);

        cache.put(filePath, data);
        cache.persistCache(); // ensure file exists

        assertTrue(FileManager.getInstance().exists(filePath));

        cache.remove(filePath);
        assertFalse(FileManager.getInstance().exists(filePath));
    }

    @Test
    public void testContainsKeyChecksCacheAndDisk() throws IOException {
        Path filePath = Path.of("exists.json");
        TestData data = new TestData("Exists", 60);

        // Save directly to disk
        FileManager.getInstance().save(filePath, data, type);

        assertTrue(cache.containsKey(filePath));
    }

    @Test
    public void testPersistCacheWritesAllEntries() throws IOException {
        Path filePath = Path.of("persist.json");
        TestData data = new TestData("Persist", 70);

        cache.put(filePath, data);
        cache.persistCache();

        assertTrue(FileManager.getInstance().exists(filePath));
    }

    @Test
    public void testFlushCacheClearsEntries() throws IOException {
        Path filePath = Path.of("flush.json");
        TestData data = new TestData("Flush", 80);

        cache.put(filePath, data);
        cache.flushCache();

        assertFalse(cache.containsKey(filePath));
    }

    @Test
    public void testCounterStartsAtZeroAndIncrements() throws IOException {
        Path dirPath = Path.of("counterDir");

        // First time: should start at 0
        Integer first = cache.putAndReturnId(dirPath, new TestData("First", 1));
        assertEquals(Integer.valueOf(1), first);

        Integer second = cache.putAndReturnId(dirPath, new TestData("Second", 2));
        assertEquals(Integer.valueOf(2), second);
    }

    @Test
    public void testPersistCacheAlsoSavesCounters() throws IOException {
        Path dirPath = Path.of("counterDir");

        // Add something so the counter increments
        cache.putAndReturnId(dirPath, new TestData("X", 1));

        // Persist cache (this calls saveAllCounters internally)
        cache.persistCache();

        Path counterPath = dirPath.resolve("counter.json");
        assertTrue(FileManager.getInstance().exists(counterPath));
    }

    // Helper class for test data
    static class TestData {
        String name;
        int age;

        TestData(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
}


