package persistence;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;

/**
 * Unit test for the class AppDataPath.
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class AppDataPathTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    public EnvironmentVariables environmentVariables = new EnvironmentVariables();

    private String originalOsName;
    private String originalUserHome;
    private String originalAppDataProperty;
    private String originalXdgDataHomeProperty;

    private Path tempDirPath;
    private String appName = "form-builder";

    @Before
    public void setUp() throws Exception {
        // SetUp: Set all properties/env to null

        originalOsName = System.getProperty("os.name");
        System.clearProperty("os.name");

        originalUserHome = System.getProperty("user.home");
        System.clearProperty("user.home");

        originalAppDataProperty = System.getProperty("APPDATA");
        System.clearProperty("APPDATA");

        environmentVariables.set("APPDATA", null);

        originalXdgDataHomeProperty = System.getProperty("XDG_DATA_HOME");
        System.clearProperty("XDG_DATA_HOME");

        environmentVariables.set("XDG_DATA_HOME", null);

        environmentVariables.setup();

        tempDirPath = tempDir.getRoot().toPath();
    }

    @After
    public void tearDown() throws Exception {
        if (originalOsName != null) System.setProperty("os.name", originalOsName);
        else System.clearProperty("os.name");

        if (originalUserHome != null) System.setProperty("user.home", originalUserHome);
        else System.clearProperty("user.home");
        
        if (originalAppDataProperty != null) System.setProperty("APPDATA", originalAppDataProperty);
        else System.clearProperty("APPDATA");
        
        if (originalXdgDataHomeProperty != null) System.setProperty("XDG_DATA_HOME", originalXdgDataHomeProperty);
        else System.clearProperty("XDG_DATA_HOME");

        environmentVariables.teardown();
    }

    @Test
    public void testGetAppDataPathWithHome_Windows() {
        System.setProperty("os.name", "Windows 10");
        System.clearProperty("APPDATA"); 
        environmentVariables.set("APPDATA", null);
        // Clear Property + env => fallback to user.home
        System.setProperty("user.home", tempDirPath.toString());

        Path result = AppDataPath.getAppDataPath();

        Path expected = tempDirPath.resolve("AppData").resolve("Roaming").resolve(appName);
        assertEquals(expected, result);
    }

    @Test
    public void testGetAppDataPathWithProperty_Windows() throws IOException {
        Path appDataDir = tempDirPath.resolve("AppData").resolve("Roaming");

        System.setProperty("os.name", "Windows 10");
        System.setProperty("APPDATA", appDataDir.toString());

        Path result = AppDataPath.getAppDataPath();

        assertEquals(appDataDir.resolve(appName), result);
    }

    @Test
    public void testGetAppDataPathWithEnv_Windows() throws IOException {
        Path appDataDir = tempDirPath.resolve("AppData").resolve("Roaming");

        System.setProperty("os.name", "Windows 10");
        // Clear Property => fallback to env
        System.clearProperty("APPDATA");
        environmentVariables.set("APPDATA", appDataDir.toString());

        Path result = AppDataPath.getAppDataPath();

        assertEquals(appDataDir.resolve(appName), result);
    }

    @Test
    public void testGetAppDataPath_MacOS() {
        System.setProperty("os.name", "Mac OS X");
        System.setProperty("user.home", tempDir.getRoot().getAbsolutePath());

        Path result = AppDataPath.getAppDataPath();

        Path expected = tempDirPath.resolve("Library").resolve("Application Support").resolve(appName);
        assertEquals(expected, result);
    }

    @Test
    public void testGetAppDataPathWithHome_Linux() {
        System.setProperty("os.name", "Linux");
        // Clear Property + env => fallback to user.home
        System.clearProperty("XDG_DATA_HOME");
        environmentVariables.set("XDG_DATA_HOME", null);
        System.setProperty("user.home", tempDirPath.toString());

        Path result = AppDataPath.getAppDataPath();

        Path expected = tempDirPath.resolve(".local").resolve("share").resolve(appName);
        assertEquals(expected, result);
    }

    @Test
    public void testGetAppDataPathWithProperty_Linux() throws IOException {
        Path xdgDataDir = tempDirPath.resolve(".local").resolve("share");

        System.setProperty("os.name", "Linux");
        System.setProperty("XDG_DATA_HOME", xdgDataDir.toString());

        Path result = AppDataPath.getAppDataPath();

        assertEquals(xdgDataDir.resolve(appName), result);
    }

    @Test
    public void testGetAppDataPathWithEnv_Linux() throws Exception {
        Path xdgDataDir = tempDirPath.resolve(".local").resolve("share");

        System.setProperty("os.name", "Linux");
        // Clear Property => fallback to env
        System.clearProperty("XDG_DATA_HOME");
        environmentVariables.set("XDG_DATA_HOME", xdgDataDir.toString());

        Path result = AppDataPath.getAppDataPath();

        assertEquals(xdgDataDir.resolve(appName), result);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetAppDataPath_UnknownOSThrowsException() {
        System.setProperty("os.name", "Solaris");
        AppDataPath.getAppDataPath();
    }
}
