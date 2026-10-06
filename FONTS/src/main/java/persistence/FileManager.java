package persistence;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import com.google.gson.Gson;

import utils.PersistenceException;

/**
 * A generic class for reading and writing JSON files using Gson.
 *
 * @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
 */
public class FileManager {

    private final Gson gson;
    private Path dataPath;
    private static FileManager instance;

    /**
     * Creates a new FileManager with default Gson settings (pretty printing enabled).
     */
    private FileManager() {
        this.gson = GsonFactory.getGson();
        this.dataPath = AppDataPath.getAppDataPath();
    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return the singleton {@link FileManager}
     */
    protected static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        
        return instance;
    }
    
    /**
     * Resets the singleton instance. Used for testing purposes only.
     */
    protected void resetDataPathForTests() {
        this.dataPath = AppDataPath.getAppDataPath();
    }

    /**
     * Saves an object to a JSON file at the specified path.
     * Creates parent directories if they don't exist.
     *
     * @param filePath the path where the JSON file will be saved
     * @param data     the object to serialize and save
     * @throws IOException if an I/O error occurs while writing the file
     */
    protected void save(Path filePath, Object data) throws IOException {
        Path path = dataPath.resolve(filePath);

        try {
            // Create parent directories if they don't exist
            Path parent = path.getParent();
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new PersistenceException("Failed to create app data directory: " + parent, e);
            }

            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                gson.toJson(data, writer);
            }

        } catch (IOException e) {
            throw new IOException("Failed to save JSON to: " + path, e);
        }
    }

    /**
     * Saves an object to a JSON file at the specified path using a specific Type.
     * This is useful when you want to serialize using a base class or interface type
     * to ensure polymorphic serialization works correctly (e.g. with RuntimeTypeAdapterFactory).
     *
     * @param filePath the path where the JSON file will be saved
     * @param data     the object to serialize and save
     * @param typeOfSrc the specific type to use for serialization
     * @throws IOException if an I/O error occurs while writing the file
     */
    protected void save(Path filePath, Object data, Type typeOfSrc) throws IOException {
        Path path = dataPath.resolve(filePath);

        try {
            // Create parent directories if they don't exist
            Path parent = path.getParent();
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new PersistenceException("Failed to create app data directory: " + parent, e);
            }

            // try (Writer writer = new Files.newBufferedWriter(path)) {
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                gson.toJson(data, typeOfSrc, writer);
            }

        } catch (IOException e) {
            throw new IOException("Failed to save JSON to: " + path, e);
        }
    }

    /**
     * Loads an object from a JSON file at the specified path using a Type reference.
     *
     * @param <T>      the type of object to deserialize
     * @param filePath the path to the JSON file
     * @param type     the Type of the object to deserialize (use TypeToken for generics)
     * @return the deserialized object, or null if the file doesn't exist
     * @throws IOException if an I/O error occurs while reading the file
     * @throws FileNotFoundException if the given filePath is not found
     */
    protected <T> T load(Path filePath, Type type) throws IOException {
        Path path = dataPath.resolve(filePath);

        if (!Files.exists(path)) {
             throw new FileNotFoundException("File not found: " + path);
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, type);
        }
    }

    /**
     * Checks if a file exists at the specified path.
     *
     * @param filePath the path to check
     * @return true if the file exists, false otherwise
     */
    protected boolean exists(Path filePath) {
        Path path = dataPath.resolve(filePath);
        return Files.exists(path);
    }

    /**
     * Deletes a file at the specified path if it exists.
     *
     * @param filePath the path to the file to delete
     * @return true if the file was deleted, false if it didn't exist
     * @throws IOException if an I/O error occurs while deleting the file
     */
    protected boolean delete(Path filePath) throws IOException {
        Path path = dataPath.resolve(filePath);
        return Files.deleteIfExists(path);
    }

    /**
     * Deletes a directory and all its contents recursively.
     *
     * @param dirPath the path to the directory to delete
     * @throws IOException if an I/O error occurs
     */
    protected void deleteDirectoryRecursively(Path dirPath) throws IOException {
        Path path = dataPath.resolve(dirPath);
        if (!Files.exists(path)) {
            return;
        }

        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(java.util.Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(java.io.File::delete);
        }
    }

    /**
     * Lists files in a directory relative to the data path.
     * 
     * @param dirPath the directory path relative to data path
     * @return array of Files in the directory, or null if not a directory or doesn't exist
     */
    protected List<Path> listFiles(Path dirPath) throws IOException {
        Path path = dataPath.resolve(dirPath);

        if (!Files.isDirectory(path)) {
            return List.of();
        }

        try (Stream<Path> stream = Files.list(path)) {
            return stream.toList();
        }
    }
}
