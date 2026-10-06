package persistence;

import java.nio.file.Path;

/**
 * System property setter (simulator) for AppDataPath, CtrlAnalyze,
 *  CtrlAnswerQuestion, CtrlUser, CtrlFormQuestion
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class PropertySetter {
    private String originalOSName;
    private String originalAppData;
    private String originalXdgData;
    private String originalUserHome;

    public void set(Path rootDir) {
        originalOSName = System.getProperty("os.name");
        originalAppData = System.getProperty("APPDATA");
        originalXdgData = System.getProperty("XDG_DATA_HOME");
        originalUserHome = System.getProperty("user.home");

        System.setProperty("os.name", "Linux");
        System.setProperty("APPDATA", rootDir.toString());
        System.setProperty("XDG_DATA_HOME", rootDir.toString());
        System.setProperty("user.home", rootDir.toString());
    }

    public void restore() {
        // Reset system properties
        if (originalOSName != null) System.setProperty("os.name", originalOSName);
        else System.clearProperty("os.name");

        if (originalAppData != null) System.setProperty("APPDATA", originalAppData);
        else System.clearProperty("APPDATA");

        if (originalXdgData != null) System.setProperty("XDG_DATA_HOME", originalXdgData);
        else System.clearProperty("XDG_DATA_HOME");

        if (originalUserHome != null) System.setProperty("user.home", originalUserHome);
        else System.clearProperty("user.home");
    } 
}
