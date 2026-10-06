package persistence;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import domain.Analyze;
import utils.EntityNotFoundException;

/**
 *
 * The CtrlAnalyze class is responsible for controlling persistence-related
 *  operations on the class Analyze. 
 * It's intended to be called by the controller of the persistence.
 *
 * Stores Analyzes in memory and disk through the Cache. IDs are auto-generated
 *  if not provided.
 * This class follows the singleton pattern.
 *
 * The CtrlAnalyze::close() method needs to be run manually **before** this class
 *    is destructed.
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class CtrlAnalyze {

    /** Inner static class for lazy-loaded singleton. */
    private static class CtrlAnalyzeSingleton {
        private static final CtrlAnalyze instance = new CtrlAnalyze();
    }

    private static final String ANALYZES_DIR = "Analyzes"; // FORMS_DIR/formId/Analyzes
    private static final CtrlFormQuestion _ctrlFormQuestion = CtrlFormQuestion.getInstance();

    // ==============
    // Static Getters
    // ==============

    /**
     * Returns the singleton instance of this class.
     *
     * @return the singleton {@link CtrlAnalyze}
     */
    protected static CtrlAnalyze getInstance() {
        return CtrlAnalyzeSingleton.instance;
    }
    /**
     * @param idForm the external form ID
     * @return the correct directory to save analyzes with the given idForm.
     */
    protected static Path getAnalyzesDir(Integer idForm) {
        return CtrlFormQuestion.getFormsDir().resolve(idForm.toString()).resolve(ANALYZES_DIR);
    }

    /**
     * @param idForm the external form ID
     * @param idAnalyze the external analyze ID
     * @return the path where Analyze with IDs idForm and idAnalyze is saved.
     */
    protected static Path getAnalyzePath(Integer idForm, Integer idAnalyze) {
        return getAnalyzesDir(idForm).resolve(idAnalyze.toString() + ".json");
    }

    protected void resetForTests() {
        analyzeCache.resetForTests();
    }

    // =========================
    // Variables And Constructor
    // =========================

    private final Cache<Analyze> analyzeCache;

    /** Private constructor for singleton. Initializes all in-memory storage. */
    private CtrlAnalyze() {
        this.analyzeCache = new Cache<Analyze>(Analyze.class);
    }

    // ==========================
    // Analyze-related operations
    // ==========================

    /**
     * Stores a new Analyze.
     *
     * @param analyze the Analyze to store
     * @throws IllegalArgumentException if the analyze is null
     * @throws EntityNotFoundException if no User exists related with the given Analyze
     * @throws IOException 
     * @return the corresponding ID to the given Analyze
     */
    protected Integer addAnalyze(Analyze analyze) throws IllegalArgumentException, EntityNotFoundException, IOException {
        if (analyze == null) {
            throw new IllegalArgumentException("Analyze cannot be null");
        }

        if (!_ctrlFormQuestion.existsForm(analyze.getIdForm())) {
            throw new EntityNotFoundException(
                "Form with ID " + analyze.getIdForm() + " does not exist");
        }

        if (analyze.hasAssignedId()) {
            throw new IllegalArgumentException("Analyze id must be unassigned");
        }

        Integer idForm = analyze.getIdForm();
        Integer idAnalyze = analyzeCache.putAndReturnId(getAnalyzesDir(idForm), analyze);
        analyze.setIdAnalyze(idAnalyze);

        return analyze.getIdAnalyze();
    }

    /**
     * Returns the Analyze for the given form ID and analyze ID.
     *
     * @param idForm the external form ID
     * @param idAnalyze the external analyze ID
     * @return the Analyze associated with the given form ID and analyze ID
     * @throws EntityNotFoundException if no Analyze exists for the given form ID and analyze ID
     * @throws IOException 
     */
    protected Analyze getAnalyze(Integer idForm, Integer idAnalyze) throws EntityNotFoundException, IOException {
        try {
            return analyzeCache.get(getAnalyzePath(idForm, idAnalyze));
        }

        catch (NoSuchFileException e) {
            throw new EntityNotFoundException(
                "Analyze for Form" + idForm + ", with ID " + idAnalyze + " not found.");
        }
    }

    /**
     * Deletes the Analyze for the given form ID and analyze ID.
     *
     * @param idForm the external form ID
     * @param idAnalyze the external analyze ID
     * @throws EntityNotFoundException if no Analyze exists for the given form ID and analyze ID
     * @throws IOException 
     */
    protected void deleteAnalyze(Integer idForm, Integer idAnalyze) throws EntityNotFoundException, IOException {
        try {
            analyzeCache.remove(getAnalyzePath(idForm, idAnalyze));
        }

        catch (NoSuchFileException e) {
            throw new EntityNotFoundException(
                "Analyze for Form" + idForm + ", with ID " + idAnalyze + " not found.");
        }
    }

    /**
     * Checks if the Analyze for the given form ID and analyze ID exists.
     *
     * @param idForm the external form ID
     * @param idAnalyze the external analyze ID
     * @return True if the Analyze associated with the given form ID and analyze ID exists
     */
    protected Boolean existsAnalyze(Integer idForm, Integer idAnalyze) {
        return _ctrlFormQuestion.existsForm(idForm) && analyzeCache.containsKey(getAnalyzePath(idForm, idAnalyze)); 
    }

    // ==================
    // General operations
    // ==================

    /**
     * Operation needed to be run before this class is destructed
     *
     * @throws IOException
     */
    protected void close() throws IOException {
        analyzeCache.persistCache();
        analyzeCache.flushCache();
    }

    /**
     * Clears all in-memory data (analyzes and counter).
     */
    protected void clear() {
        analyzeCache.flushCache();
    }
}
