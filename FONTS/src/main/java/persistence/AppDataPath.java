package persistence;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * This class is meant only to call getAppDataPath(). It retrieves and returns the
 *    correct path where to save data, depending on the OS being used (Windows,
 *    Unix, BSDs, or a Unix-like variant such as Linux)
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class AppDataPath {
    /**
     * @return the correct appDataPath to save this program's data in this os.
     */
    protected static Path getAppDataPath() {
        final Path appname = Paths.get("form-builder");
        final String os = System.getProperty("os.name").toLowerCase();
        Path appDataPath = null;

        if (os.contains("win")) {
            // Windows: %APPDATA% or fallback to user.home if APPDATA is missing
            // Check System property first for testing purposes
            String appDataEnv = System.getProperty("APPDATA");
            if (appDataEnv == null) {
                appDataEnv = System.getenv("APPDATA");
            }
            
            if (appDataEnv != null) {
                appDataPath = Paths.get(appDataEnv);

            } else {
                Path userHome = Paths.get(System.getProperty("user.home"));
                appDataPath = userHome.resolve("AppData").resolve("Roaming");
            }

        } else if (os.contains("mac")) {
            // macOS convention: ~/Library/Application Support
            Path userHome = Paths.get(System.getProperty("user.home"));
            appDataPath = userHome.resolve("Library").resolve("Application Support");

        } else if (os.contains("nix") || os.contains("nux")) {
            // Linux
            // Check System property first for testing purposes
            String xdgDataEnv = System.getProperty("XDG_DATA_HOME");
            if (xdgDataEnv == null) {
                xdgDataEnv = System.getenv("XDG_DATA_HOME");
            }
            
            if (xdgDataEnv != null)
                appDataPath = Paths.get(xdgDataEnv);

            else {
                // Default to ~/.local/share for Linux
                Path userHome = Paths.get(System.getProperty("user.home"));
                appDataPath = userHome.resolve(".local").resolve("share");
            }
        }

        if (appDataPath == null) {
             throw new IllegalStateException("AppData path could not be determined for this OS");
        }

        return appDataPath.resolve(appname);
    }
}
