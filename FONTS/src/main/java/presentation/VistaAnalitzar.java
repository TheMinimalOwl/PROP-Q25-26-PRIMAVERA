package presentation;

import utils.DomainException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Analysis History View.
 * <p>
 * This class represents the window that displays the history of clustering analyses performed
 * on a specific form (survey). It allows the user to:
 * <ul>
 * <li>View a list of past analyses with their date, number of clusters, and quality score.</li>
 * <li>Run a new analysis using K-Means or K-Medoids algorithms.</li>
 * <li>Navigate to the detailed result view of a specific analysis.</li>
 * <li>Return to the main menu.</li>
 * </ul>
 * </p>
 */
public class VistaAnalitzar {

    // Components visuals
    private JFrame frameVista;
    private JPanel panelContenido;
    private JPanel panelLlista; // Panel on s'afegiran les targetes

    private CtrlPresentation _ctrlPresentation;

    // Dades de l'enquesta actual
    private Integer formId;
    private String nomEnquesta;
    private Integer idUser;

    // Variables per FontAwesome (Copiades del teu estil)
    private Font fontAwesome;
    private final Map<String, Character> iconMap = new HashMap<>();

    /**
     * Constructs the Analysis History View.
     * <p>
     * Initializes the UI, loads necessary resources (fonts/icons), and fetches
     * previous analyses associated with the given form ID from the controller.
     * </p>
     *
     * @param formId      The unique identifier of the form (survey) to analyze.
     * @param nomEnquesta The name of the form, used for display in the title.
     * @param idUser      The unique identifier of the current user.
     */
    public VistaAnalitzar(Integer formId, String nomEnquesta, Integer idUser) {
        this.formId = formId;
        this.nomEnquesta = nomEnquesta;
        this._ctrlPresentation = CtrlPresentation.getInstance();
        this.idUser = idUser;

        frameVista = new JFrame("Analysis History - " + nomEnquesta);

        // 1. Càrrega d'estils i icones
        inicializarIconos();
        cargarFontAwesome();

        // 2. Configuració bàsica
        configurarVentana();

        // 3. Creació de la UI
        crearComponentes();

        // 4. Carregar analisis anteriors
        carregarAnalisisPrevis();
    }

    /**
     * Maps internal string keys to FontAwesome unicode characters.
     */
    private void inicializarIconos() {
        iconMap.put("back", '\uf060');      // Fletxa enrere
        iconMap.put("chart", '\uf080');     // Gràfic (dashboard)
        iconMap.put("calendar", '\uf073');  // Calendari
        iconMap.put("list", '\uf0ca');      // Llista
        iconMap.put("check", '\uf00c');     // Check
        iconMap.put("plus", '\uf067');      // Més (Nou anàlisi)
        iconMap.put("refresh", '\uf021');   // Recarregar
        iconMap.put("eye", '\uf06e');       // Veure
    }

    /**
     * Loads the FontAwesome font from the resources.
     * Falls back gracefully if the font file is not found.
     */
    private void cargarFontAwesome() {
        try (InputStream is = getClass().getResourceAsStream("/fonts/fontawesome.otf")) {
            if (is == null) {
                throw new RuntimeException("Font fontawesome.otf not found in JAR");
            }

            fontAwesome = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(fontAwesome);

        } catch (Exception e) {
            System.err.println("Error loading FontAwesome: " + e.getMessage());
            e.printStackTrace();
            fontAwesome = null;
        }
    }

    /**
     * Generates an HTML string to render an icon using the loaded FontAwesome font.
     *
     * @param iconName The key name of the icon (defined in {@code inicializarIconos}).
     * @param size     The font size in pixels.
     * @return HTML string for the JLabel, or "?" if the icon is not found.
     */
    private String getIcon(String iconName, float size) {
        Character iconChar = iconMap.get(iconName);
        if (iconChar != null && fontAwesome != null) {
            return "<html><span style='font-family: \"" +
                    fontAwesome.getFontName() + "\"; font-size: " +
                    size + "px;'>" + iconChar + "</span></html>";
        }
        return "?"; // Fallback simple
    }

    /**
     * Configures the main JFrame properties (size, location, icon).
     */
    private void configurarVentana() {
        frameVista.setSize(1000, 700);
        frameVista.setLocationRelativeTo(null);
        frameVista.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            // Intentar carregar la mateixa icona
            ImageIcon icono = new ImageIcon("FONTS\\src\\main\\resources\\icons\\IconoForms2.png");
            frameVista.setIconImage(icono.getImage());
        } catch (Exception e) {
            e.printStackTrace();
            // Ignorar si no troba la imatge
        }
    }

    /**
     * Builds the main layout of the window, including the top bar, the header,
     * and the scrollable list of analyses.
     */
    private void crearComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(245, 247, 250)); // gris-suau

        // Barra superior
        panelPrincipal.add(crearBarraSuperior(), BorderLayout.NORTH);

        // Contingut Central (Llista + Botó)
        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(new Color(245, 247, 250));
        panelContenido.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Títol de la secció i botó d'acció
        JPanel panelCapçaleraContingut = new JPanel(new BorderLayout());
        panelCapçaleraContingut.setOpaque(false);
        panelCapçaleraContingut.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel lblTitolSeccio = new JLabel("Analysis History");
        lblTitolSeccio.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitolSeccio.setForeground(new Color(30, 40, 70));

        JButton btnFerAnalisi = crearBotonAccion("Run New Analysis", "plus", new Color(70, 130, 220));
        btnFerAnalisi.addActionListener(e -> accioFerAnalisi());

        panelCapçaleraContingut.add(lblTitolSeccio, BorderLayout.WEST);
        panelCapçaleraContingut.add(btnFerAnalisi, BorderLayout.EAST);

        panelContenido.add(panelCapçaleraContingut, BorderLayout.NORTH);

        // Llista d'anàlisis (Scroll)
        panelLlista = new JPanel();
        panelLlista.setLayout(new BoxLayout(panelLlista, BoxLayout.Y_AXIS));
        panelLlista.setBackground(new Color(245, 247, 250)); // Que sembli transparent sobre el fons

        JScrollPane scrollPane = new JScrollPane(panelLlista);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(245, 247, 250));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panelContenido.add(scrollPane, BorderLayout.CENTER);

        panelPrincipal.add(panelContenido, BorderLayout.CENTER);
        frameVista.setContentPane(panelPrincipal);
    }

    /**
     * Creates the top navigation bar containing the "Back" button and the form name.
     * @return The constructed JPanel for the top bar.
     */
    private JPanel crearBarraSuperior() {
        JPanel barraPanel = new JPanel(new BorderLayout());
        barraPanel.setBackground(Color.WHITE);
        barraPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 230)),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)));

        // Botó Tornar
        String text = getIcon("back", 18) + " Back to Menu";
        JButton btnBack = new JButton(text);
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnBack.setForeground(new Color(100, 110, 130));
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> {
            frameVista.dispose();
            // Tornem al menu principal
            String username = _ctrlPresentation.getUsername(idUser);
            new MainMenuView(idUser, username, _ctrlPresentation).makeVisible();
        });

        // Títol de l'Enquesta al centre/dreta
        JLabel lblNomEnquesta = new JLabel(getIcon("chart", 18) + " " + nomEnquesta);
        lblNomEnquesta.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNomEnquesta.setForeground(new Color(30, 40, 70));

        barraPanel.add(btnBack, BorderLayout.WEST);
        barraPanel.add(lblNomEnquesta, BorderLayout.EAST);

        return barraPanel;
    }

    /**
     * Creates and configures a stylized action button with an associated icon and text.
     *
     * @param texto    The text label to be displayed on the button.
     * @param iconoKey The key identifier used to retrieve the corresponding icon representation.
     * @param bg       The primary background Color of the button.
     * @return A fully configured JButton instance ready for display.
     */
    private JButton crearBotonAccion(String texto, String iconoKey, Color bg) {
        JButton btn = new JButton(getIcon(iconoKey, 14) + "  " + texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect simple
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    // --- MÈTODES DE LÒGICA VISUAL ---

    /**
     * Adds a visual "card" representing a past analysis to the list panel.
     * Each card displays the date, number of clusters, and quality score, and is clickable.
     *
     * @param data        The date and time when the analysis was performed.
     * @param numClusters The number of clusters (k) used in the analysis.
     * @param qualitat    The quality score (Silhouette coefficient).
     * @param idAnalisi   The unique identifier of the analysis.
     * @param nomAnalisis The name given to the analysis.
     */
    private void afegirTargetaAnalisi(LocalDateTime data, int numClusters, double qualitat, int idAnalisi, String nomAnalisis) {
        JPanel targeta = new JPanel(new BorderLayout());
        targeta.setBackground(Color.WHITE);
        targeta.setMaximumSize(new Dimension(Short.MAX_VALUE, 100)); // Alçada fixa
        targeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(15, 20, 15, 20)
        ));
        targeta.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Part Esquerra: Data i Icona
        JPanel panelInfo = new JPanel(new GridLayout(2, 1, 0, 5));
        panelInfo.setBackground(Color.WHITE);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        JLabel lblData = new JLabel(nomAnalisis);
        lblData.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblData.setForeground(new Color(30, 40, 70));

        JLabel lblSubtitol = new JLabel(data.format(fmt));
        lblSubtitol.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitol.setForeground(Color.GRAY);

        panelInfo.add(lblData);
        panelInfo.add(lblSubtitol);

        // Part Central: Mètriques (Clusters i Qualitat)
        JPanel panelStats = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        panelStats.setBackground(Color.WHITE);

        JLabel lblClusters = crearStatLabel(numClusters + "", "Clusters");

        // Mostrem qualitat en percentatge
        String qualitatStr = String.format("%.1f%%", qualitat * 100);
        JLabel lblQualitat = crearStatLabel(qualitatStr, "Quality");

        // Color de la qualitat segons valor
        if (qualitat > 0.75) lblQualitat.setForeground(new Color(46, 204, 113)); // Verd
        else if (qualitat > 0.5) lblQualitat.setForeground(new Color(243, 156, 18)); // Taronja
        else lblQualitat.setForeground(new Color(231, 76, 60)); // Vermell

        panelStats.add(lblClusters);
        panelStats.add(lblQualitat);

        // Part Dreta: Botó "Open" (Visual)
        JLabel lblOpen = new JLabel(getIcon("eye", 16));
        lblOpen.setForeground(new Color(70, 130, 220));

        targeta.add(panelInfo, BorderLayout.WEST);
        targeta.add(panelStats, BorderLayout.CENTER);
        targeta.add(lblOpen, BorderLayout.EAST);

        // Lògica al clicar la targeta
        targeta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                obrirDetallAnalisi(idAnalisi);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                targeta.setBackground(new Color(250, 252, 255)); // Highlight lleuger
            }
            @Override
            public void mouseExited(MouseEvent e) {
                targeta.setBackground(Color.WHITE);
            }
        });

        panelLlista.add(targeta);
        panelLlista.add(Box.createRigidArea(new Dimension(0, 10))); // Espai entre targetes
    }

    /**
     * Creates a centered, multi-line label designed to display a statistic or metric.
     *
     * @param valor The main value or statistic to display
     * @param label The descriptive text or subtitle associated with the value.
     * @return A JLabel containing the formatted HTML text ready for display.
     */
    private JLabel crearStatLabel(String valor, String label) {
        JLabel l = new JLabel("<html><div style='text-align:center;'>" +
                "<span style='font-size:16px; font-weight:bold;'>" + valor + "</span><br>" +
                "<span style='font-size:10px; color:#999;'>" + label + "</span></div></html>");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return l;
    }

    // --- ACCIONS ---

    /**
     * Opens a popup dialog to configure and run a new analysis.
     * The user can input the analysis name, number of clusters, and select the algorithm (K-Means/K-Medoids).
     */
    private void accioFerAnalisi() {
        JFrame frameNouAnalisis = new JFrame();
        frameNouAnalisis.setLocationRelativeTo(null);
        frameNouAnalisis.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameNouAnalisis.setBackground(Color.white);
        frameNouAnalisis.setLayout(new FlowLayout());

        JTextField nomAnalisis = new JTextField();
        // Pot comportar problemes si alta ressolucio
        nomAnalisis.setPreferredSize(new Dimension(150, 40));
        nomAnalisis.setText("Analysis name");
        JTextField numClusters = new JTextField();
        numClusters.setPreferredSize(new Dimension(50, 40));
        numClusters.setText("nª");
        numClusters.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();

                // Sols permetem escriure digits i esborrar-los
                if (Character.isDigit(c) ||
                        c == KeyEvent.VK_BACK_SPACE ||
                        c == KeyEvent.VK_DELETE) {
                } else {
                    e.consume(); // Bloquejem els altres
                }
            }
        });

        JRadioButton botoKmedoids = new JRadioButton("k-medoids");
        JRadioButton botoKmeans = new JRadioButton("k-means");
        ButtonGroup grup = new ButtonGroup();
        grup.add(botoKmedoids);
        grup.add(botoKmeans);

        JButton botoCrear = new JButton("New analysis");
        botoCrear.addActionListener(a -> accioNouAnalisi(nomAnalisis.getText(), numClusters.getText(),
                botoKmeans.isSelected(), botoKmedoids.isSelected(), frameNouAnalisis));

        frameNouAnalisis.add(nomAnalisis);
        frameNouAnalisis.add(numClusters);
        frameNouAnalisis.add(botoKmedoids);
        frameNouAnalisis.add(botoKmeans);
        frameNouAnalisis.add(botoCrear);
        frameNouAnalisis.pack();
        frameNouAnalisis.setTitle("Create new analysis");
        frameNouAnalisis.setVisible(true);
    }

    /**
     * Executes the logic to create a new analysis based on user input.
     * Validates input, calls the controller to perform the clustering, and updates the UI.
     *
     * @param nomAnalisis      The name for the new analysis.
     * @param numClusters      The string representation of the number of clusters.
     * @param kmeansSelected   True if K-Means algorithm is selected.
     * @param kmedoidsSelected True if K-Medoids algorithm is selected.
     * @param frameParametres  The parameter dialog frame (to be closed or used for alerts).
     */
    public void accioNouAnalisi(String nomAnalisis, String numClusters, boolean kmeansSelected,
                                boolean kmedoidsSelected, JFrame frameParametres) {
        try {
            int nClusters = Integer.valueOf(numClusters);
            if (nClusters < 1) {
                throw new NumberFormatException("nclusters massa baix");
            }
            if (nomAnalisis.length() > 15 || nomAnalisis.isEmpty()) {
                throw new IllegalArgumentException("nom massa llarg");
            }
            if (!kmeansSelected && !kmedoidsSelected) {
                throw new IOException("no algorisme");
            }
            frameParametres.setVisible(false);
            Integer idAnalysis;
            if (kmeansSelected) {
                JOptionPane.showMessageDialog(frameParametres,
                        "Running k-means algorithm for '" + nomEnquesta + " with " +
                                nClusters + " clusters ...\nPlease wait.",
                        "Processing",
                        JOptionPane.INFORMATION_MESSAGE);
                idAnalysis = _ctrlPresentation.doAnalysis(formId, nomAnalisis, nClusters, "KMeans");
            } else {
                JOptionPane.showMessageDialog(frameParametres,
                        "Running k-medoids algorithm for '" + nomEnquesta + " with " +
                                nClusters + " clusters ...\nPlease wait.",
                        "Processing",
                        JOptionPane.INFORMATION_MESSAGE);
                idAnalysis = _ctrlPresentation.doAnalysis(formId, nomAnalisis, nClusters, "KMedoids");
            }
            double qualitat = _ctrlPresentation.getAnalysisQuality(idAnalysis, formId);
            afegirTargetaAnalisi(LocalDateTime.now(), nClusters, qualitat, idAnalysis, nomAnalisis);
            // Refrescar UI
            panelLlista.revalidate();
            panelLlista.repaint();
            obrirDetallAnalisi(idAnalysis);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frameParametres,
                    "Number of clusters must be a natural number greater than 0",
                    "Invalid number of clusters",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frameParametres,
                    "The name shouldn't be 15 characters or longer nor empty",
                    "Invalid name",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            System.err.println("Error message creating analysis: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(frameParametres,
                    "Please select an algorithm to do the clustering",
                    "Select algorithm",
                    JOptionPane.ERROR_MESSAGE);
        } catch (DomainException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frameVista,
                    "Unable to create analysis due to lack of answers (no answers or more clusters than answers)",
                    "Analysis fail",
                    JOptionPane.ERROR_MESSAGE);
            frameParametres.dispose();
        }
    }

    /**
     * Opens the detailed results view (`VistaResultatsAnalisis`) for a selected analysis.
     * Fetches all necessary data (clusters, quality, distance matrix) before switching views.
     *
     * @param idAnalisi The ID of the analysis to view.
     */
    private void obrirDetallAnalisi(int idAnalisi) {
        int nClusters = _ctrlPresentation.getAnalysisNumClusters(idAnalisi, formId);
        double qualitat = _ctrlPresentation.getAnalysisQuality(idAnalisi, formId);
        double[][] distances = _ctrlPresentation.getAnswersDistances(formId);
        Map<String, Object> infoForm = _ctrlPresentation.getFormBasicInfo(formId);
        Integer nAnswers = (Integer)infoForm.get("answerCount");
        int[] assignacions = _ctrlPresentation.getAnalysisAssignations(idAnalisi, formId);
        VistaResultatsAnalisis detall = new VistaResultatsAnalisis(formId, nomEnquesta, nClusters, qualitat,
                                                                   distances, nAnswers, assignacions, idUser);
        detall.makeVisible();
        frameVista.dispose();
    }

    // --- CÀRREGA DE DADES  ---

    /**
     * Stubs for loading dummy analysis data (for testing purposes).
     */
    private void carregarAnalisisPrevisStub() {
        afegirTargetaAnalisi(LocalDateTime.now().minusDays(1), 3, 0.85, 101, "nom1");
        afegirTargetaAnalisi(LocalDateTime.now().minusDays(5), 4, 0.72, 98, "nom2");
        afegirTargetaAnalisi(LocalDateTime.now().minusDays(12), 2, 0.65, 85, "nom3");
        afegirTargetaAnalisi(LocalDateTime.now().minusMonths(1), 5, 0.55, 42, "nom4");
    }

    /**
     * Fetches the list of previous analyses from the controller and populates the UI list.
     * If no analyses exist, displays a message.
     */
    private void carregarAnalisisPrevis() {
        ArrayList<Integer> idAnalysis = _ctrlPresentation.getAnalysisIds(formId);
        int nAnalysis = idAnalysis.size();
        for (int i = 0; i < nAnalysis; i++) {
            LocalDateTime data = _ctrlPresentation.getAnalysisDate(idAnalysis.get(i), formId);
            Integer nClusters = _ctrlPresentation.getAnalysisNumClusters(idAnalysis.get(i), formId);
            Double qualitat = _ctrlPresentation.getAnalysisQuality (idAnalysis.get(i), formId);
            String nom = _ctrlPresentation.getAnalysisName (idAnalysis.get(i), formId);
            afegirTargetaAnalisi(data, nClusters, qualitat, idAnalysis.get(i), nom);
        }
        if (nAnalysis == 0) {
            JLabel missatge = new JLabel();
            missatge.setText("There are no analysis yet");
            panelLlista.add(missatge);
        }
    }

    public void hacerVisible() {
        frameVista.setVisible(true);
    }
}
