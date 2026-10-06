package persistence;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import utils.PersistenceException;

/**
 *
 * Implementation of a simple Cache 'WriteAllocate' with operations put, get, delete and exists.
 * It is also implemented as a variant of a 'Copy Back' cache, with the difference
 *    it always writes an object to disk when it is to be replaced, as it does not
 *    track if the object has been modified.
 *    
 * It implements persistCache(), which needs to be called **before** an instance of
 *    this class is to be destructed.
 *
 * The cache:
 *  - Writes to disk when an object is to be replaced.
 *  - Writes to disk when an object is deleted.
 *  - Writes to disk at most 'capacity' times when calling persistCache().
 *  - Loads from disk when an object is to be getted or checked its existence, only when it's not previously in cache.
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class Cache <V> {

    private static final String COUNTER_FILE_NAME = "counter.json";

    /**
     * Given a directory, returns the filepath of the counter stored there.
     *
     * @param pathDir
     * @return
     */
    private static Path getCounterPathFromDir(Path pathDir) {
        Path counterFile = Paths.get(COUNTER_FILE_NAME);
        return pathDir.resolve(counterFile);
    }

    private final Map<Path, V> _map;
    private final Integer capacity = 50;
    private final Map<Path, Integer> _counter;
    private final Integer capacityCounter = 50;

    private final FileManager fileManager;
    private final Type type;

    /**
     * @param type The type of the Objects which will be used by this Cache
     */
    protected Cache(Type type) {
        this._map = initMap(this.capacity);
        this.fileManager = FileManager.getInstance();
        this.type = type;
        this._counter = initCounter(this.capacityCounter);
    }

    protected void resetForTests() {
        fileManager.resetDataPathForTests();
    }

    /**
     * Stores the value in the exact given path (relative to the program root).
     *
     * @param path the filepath on where to save this value
     * @param value the value to be saved
     * @throws IllegalArgumentException if given value is null
     */
    protected void put(Path path, V value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException(type.toString() + " cannot be null");
        }

        _map.put(path, value);
    }

    /**
     * Generates a correct and unique ID for the value. And stores the value in the exact
     *    given directory (relative to the program root), as the filepath
     *    path/id.json
     *
     * @param path directory on where to save this value
     * @param value the value to be saved
     * @return the unique ID generated for the value
     * @throws IOException
     */
    protected Integer putAndReturnId(Path path, V value) throws IOException {
        Integer id = getNextId(getCounterPathFromDir(path));

        put(path.resolve(id.toString() + ".json"), value);
    
        return id;
    }

     /**
     * Given a filepath, loads and returns the object saved there.
     *
     * @param path the filepath from which to load the object
     * @return the loaded object
     * @throws NoSuchFileException if no such path exists
     * @throws IOException
     */
    protected V get(Path path) throws NoSuchFileException, IOException {
        V value = _map.get(path);

        if (value == null) {
            value = fileManager.load(path, type);
            if (value == null) {
                throw new NoSuchFileException(type.toString() + " with path " + path + " not found.");
            }
            else {
                put(path, value); // add to cache
            }
        }
        return value;
    }

    /**
     * Given a filepath, deletes the object saved there, as well as the filepath.
     *
     * @param path the filepath to delete the object
     * @throws NoSuchFileException if no such path exists
     * @throws IOException
     */
    protected void remove(Path path) throws NoSuchFileException, IOException {
        if (!_map.containsKey(path)) {
            if (!fileManager.exists(path)) {
                throw new NoSuchFileException(type.toString() + " with path " + path + " not found.");
            }
        }

        if (_map.containsKey(path)) {
            _map.remove(path);
        }
        fileManager.delete(path);
    }

    /**
     * Given a path, checks if it exists (in cache or in disk)
     *
     * @param path the path to check if exists
     * @return
     */
    protected Boolean containsKey(Path path) {
        return (_map.containsKey(path) || fileManager.exists(path));
    }

    /**
     * Saves current cache state into disk.
     *
     * @throws IOException
     */
    protected void persistCache() throws IOException {
        for (Map.Entry<Path, V> entry : _map.entrySet()) {
            fileManager.save(entry.getKey(), entry.getValue(), type);
        }
        saveAllCounters();
    }

    /**
     * Flushes (deletes) cache, without saving to disk not doing anything similar.
     */
    protected void flushCache() {
        _map.clear();
        _counter.clear();
    }

    /**
     * Given a counter path, loads the counter and returns the next ID.
     *
     * @param counterPath
     * @return
     * @throws IOException
     */
    private Integer getNextId(Path counterPath) throws IOException {
        Integer count = loadCounter(counterPath);
        count++;
        _counter.put(counterPath, count);
        return count;
    }

    /**
     * Loads the given counter path to the cache, and returns the current count.
     *
     * @param counterPath
     * @return
     * @throws IOException
     */
    private Integer loadCounter(Path counterPath) throws IOException {
        Integer count = _counter.get(counterPath);

        if (count == null) {
            try {
                count = fileManager.load(counterPath, Integer.class);
            }
            // Això és si no troba el path, no si no hi ha cap fitxer (no hi ha cap id encara)
            catch (NoSuchFileException | FileNotFoundException e) {
                count = 0;

            _counter.put(counterPath, count);
            }
        }
        return count;
    }

    /**
     * Saves the given counter path to disk.
     *
     * @param counterPath
     * @throws IOException
     */
    private void saveCounter(Path counterPath) throws IOException {
        fileManager.save(counterPath, _counter.get(counterPath));
    }

    /**
     * Saves all present counters in cache, to disk.
     *
     * @throws IOException
     */
    private void saveAllCounters() throws IOException {
        for (Path counterPath : _counter.keySet()) {
            saveCounter(counterPath);
        }
    }

    /**
     * Initializes the _map Map with correct replace behavior, to simulate a
     *    CopyBack cache flavour.
     *
     * @param capacity
     * @return
     */
    private Map<Path, V> initMap(int capacity) {
        // cache with specified capacity, default load factor (0.75) and eldestEntry is based on access (doing a get).
        return new LinkedHashMap<>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<Path, V> eldest) {
                boolean b = (size() > capacity);
                if (b == true) {
                    try {
                        fileManager.save(eldest.getKey(), eldest.getValue(), type);
                    } catch (IOException e) {
                        throw new PersistenceException("Failed to save eldest Cache entry to disk", e);
                    }
                }
                return b;
            }
        };
    }

    /**
     * Initializes the _counter Map with correct replace behavior, to simulate a
     *    CopyBack cache flavour.
     *
     * @param counterCapacity
     * @return
     */
    private Map<Path, Integer> initCounter(int counterCapacity) {
        // cache with specified capacity, default load factor (0.75) and eldestEntry is based on insertion (doing a put).
        return new LinkedHashMap<>(counterCapacity, 0.75f, false) {
            protected boolean removeEldestEntry(Map.Entry<Path, Integer> eldest) {
                boolean b = (size() > counterCapacity);
                if (b == true) {
                    try {
                        fileManager.save(eldest.getKey(), eldest.getValue(), type);
                    } catch (IOException e) {
                        throw new PersistenceException("Failed to save eldest counter Cache entry to disk", e);
                    }
                }
                return b;
            }
        };
    }
}
