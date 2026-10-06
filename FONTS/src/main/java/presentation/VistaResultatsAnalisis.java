package presentation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Analysis Results View.
 * <p>
 * This class is responsible for displaying the results of a clustering analysis graphically.
 * It projects N-dimensional data (represented by a distance matrix) onto a 2D plane using
 * Multidimensional Scaling (MDS) to visualize the clusters.
 * </p>
 * <p>
 * It creates a custom Swing component to render the scatter plot and displays metrics
 * such as the number of clusters and the quality (Silhouette coefficient) of the solution.
 * </p>
 */
public class VistaResultatsAnalisis {

    private JFrame frame;
    private CtrlPresentation presentationController;

    private Integer idForm;
    private String formName;
    private Integer idUser;
    private int numClusters;
    private int nAnswers;
    private double quality;

    // Llista de llistes: cada element és la llista de punts d'un cluster un cop reduides les dimensions
    private ArrayList<ArrayList<Point2D.Double>> puntsPerCluster;

    // Paleta que sera diferent segons quantitat de clusters
    private Color[] CLUSTER_COLORS;

    /**
     * Constructs the Analysis Results View.
     * <p>
     * Upon initialization, this constructor calculates the 2D coordinates for all answers
     * using the MDS algorithm based on the provided distance matrix. It then groups these
     * points according to the {@code assignacions} array.
     * </p>
     *
     * @param idForm       The unique identifier of the form being analyzed.
     * @param formName     The name of the form (for display purposes).
     * @param numClusters  The number of clusters (k) resulting from the algorithm.
     * @param quality      The quality score of the clustering (e.g., Silhouette coefficient).
     * @param distances    The distance matrix between answers (N x N).
     * @param nAnswers     The total number of answers/responses processed.
     * @param assignacions An array where index {@code i} contains the cluster ID assigned to answer {@code i}.
     * @param idUser       The unique identifier of the user viewing the results.
     */
    public VistaResultatsAnalisis(Integer idForm, String formName, int numClusters, double quality,
                                  double[][] distances, int nAnswers, int[] assignacions, Integer idUser) {
        this.idForm = idForm;
        this.formName = formName;
        this.numClusters = numClusters;
        this.quality = quality;
        this.nAnswers = nAnswers;
        // Calculem les coordenades en 2d de totes les respostes
        ArrayList<Point2D.Double> answers2d = performMDS(distances);
        ArrayList<ArrayList<Point2D.Double>> puntsPerClusterAux = new ArrayList<>();
        for (int i = 0; i < numClusters; i++) {
            puntsPerClusterAux.add(new ArrayList<>());
        }
        for (int i = 0; i < nAnswers; i++) {
            puntsPerClusterAux.get(assignacions[i]).add(answers2d.get(i));
        }
        this.puntsPerCluster = puntsPerClusterAux;
        this.idUser = idUser;

        float increment = 1.f / (float) numClusters;
        CLUSTER_COLORS = new Color[numClusters];
        for (int i = 0; i < numClusters; i++) {
            CLUSTER_COLORS[i] = Color.getHSBColor(increment * (float)i, (float)0.8, (float)0.8);
        }

        frame = new JFrame("Analysis Results - " + formName);
        presentationController = CtrlPresentation.getInstance();
        
        configureWindow();
        createComponents();
    }

    /**
     * Configures the main JFrame properties such as size, location, default close operation, and icon.
     */
    private void configureWindow() {
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        try {
            ImageIcon icon = new ImageIcon("FONTS/src/main/resources/icons/IconoForms2.png");
            frame.setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Could not load icon: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initializes and organizes the UI components within the frame.
     * Sets up the info panel (left), the chart panel (center), and the action buttons (bottom).
     */
    private void createComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(new Color(245, 247, 250));

        // --- LEFT SIDE: INFO PANEL ---
        mainPanel.add(createInfoPanel(), BorderLayout.WEST);

        // --- CENTER: CHART PANEL (Implemented as Anonymous Class) ---
        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        chartContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel chartTitle = new JLabel("Cluster Visualization", SwingConstants.CENTER);
        chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        chartTitle.setForeground(new Color(60, 70, 90));
        chartTitle.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibuixarGrafic((Graphics2D) g, getWidth(), getHeight());
            }
        };
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        chartContainer.add(chartTitle, BorderLayout.NORTH);
        chartContainer.add(chartPanel, BorderLayout.CENTER);

        mainPanel.add(chartContainer, BorderLayout.CENTER);

        // --- BOTTOM: ACTIONS ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(245, 247, 250));
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton closeButton = createStyledButton("Back",
                new Color(70, 130, 220),
                new Color(50, 110, 200),
                Color.WHITE);
        closeButton.addActionListener(e -> {
            frame.dispose();
            // Tornem al menu d'analisis
            new VistaAnalitzar(idForm, formName, idUser).hacerVisible();
        });

        bottomPanel.add(closeButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
    }

    /**
     * Custom painting logic for the cluster chart.
     * Draws the grid, axes, and plots the points for each cluster using the calculated 2D coordinates.
     *
     * @param g2 The Graphics2D context used for drawing.
     * @param w  The width of the drawing area.
     * @param h  The height of the drawing area.
     */
    private void dibuixarGrafic(Graphics2D g2, int w, int h) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int padding = 40;

        // Dibuixar graella i eixos
        g2.setColor(new Color(240, 242, 245));
        for (int i = padding; i < w; i += 50) g2.drawLine(i, padding, i, h - padding);
        for (int i = padding; i < h; i += 50) g2.drawLine(padding, i, w - padding, i);

        g2.setColor(new Color(200, 210, 220));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(padding, h - padding, w - padding, h - padding); // Eix X
        g2.drawLine(padding, padding, padding, h - padding);         // Eix Y


        // 1. Calcular Min/Max
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        
        // Revisar punts
        for (List<Point2D.Double> cluster : puntsPerCluster) {
            for (Point2D.Double p : cluster) {
                if (p.getX() < minX) minX = p.getX();
                if (p.getX() > maxX) maxX = p.getX();
                if (p.getY() < minY) minY = p.getY();
                if (p.getY() > maxY) maxY = p.getY();
            }
        }

        double rangeX = (maxX - minX == 0) ? 1 : maxX - minX;
        double rangeY = (maxY - minY == 0) ? 1 : maxY - minY;

        // 2. Pintar Punts
        for (int i = 0; i < numClusters; i++) {
            // Assegurem que no sortim de rang si les llistes no coincideixen perfectament
            if (i >= puntsPerCluster.size()) break;

            Color baseColor = CLUSTER_COLORS[i % CLUSTER_COLORS.length];
            List<Point2D.Double> punts = puntsPerCluster.get(i);

            // Pintar Punts del Cluster
            g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 120));
            for (Point2D.Double p : punts) {
                int px = mapX(p.getX(), minX, rangeX, w, padding);
                int py = mapY(p.getY(), minY, rangeY, h, padding);
                g2.fill(new Ellipse2D.Double(px - 4, py - 4, 8, 8));
            }
        }
    }

    /**
     * Maps a logical X coordinate to the screen pixel X coordinate.
     *
     * @param val   The logical value to map.
     * @param min   The minimum logical value in the dataset.
     * @param range The range (max - min) of the logical values.
     * @param w     The width of the drawing area.
     * @param pad   The padding/margin of the chart.
     * @return The screen pixel X coordinate.
     */
    private int mapX(double val, double min, double range, int w, int pad) {
        return pad + (int) ((val - min) / range * (w - 2 * pad));
    }

    /**
     * Maps a logical Y coordinate to the screen pixel Y coordinate.
     * Note: Screen Y coordinates grow downwards, so this inverts the value.
     *
     * @param val   The logical value to map.
     * @param min   The minimum logical value in the dataset.
     * @param range The range (max - min) of the logical values.
     * @param h     The height of the drawing area.
     * @param pad   The padding/margin of the chart.
     * @return The screen pixel Y coordinate.
     */
    private int mapY(double val, double min, double range, int h, int pad) {
        return (h - pad) - (int) ((val - min) / range * (h - 2 * pad));
    }

    // --- PANEL LATERAL I HELPERS ---

    /**
     * Creates the information panel displaying the form name, number of clusters, and quality score.
     * @return A constructed JPanel.
     */
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(new Color(245, 247, 250));
        infoPanel.setPreferredSize(new Dimension(280, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 20, 0);

        // Títol
        JLabel title = new JLabel("<html>Results for:<br>" + formName + "</html>");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(30, 40, 70));
        infoPanel.add(title, gbc);

        gbc.gridy++;
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(200, 210, 220));
        infoPanel.add(sep, gbc);

        // Mètrica: Clusters
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 5, 0);
        JLabel lblK = new JLabel("Total Clusters");
        lblK.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblK.setForeground(new Color(120, 130, 150));
        infoPanel.add(lblK, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 20, 0);
        JLabel valK = new JLabel(String.valueOf(numClusters));
        valK.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valK.setForeground(new Color(70, 130, 220));
        infoPanel.add(valK, gbc);

        // Mètrica: Qualitat
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        JLabel lblQ = new JLabel("Quality Score");
        lblQ.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblQ.setForeground(new Color(120, 130, 150));
        infoPanel.add(lblQ, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 20, 0);
        String qStr = String.format("%.2f", quality);
        JLabel valQ = new JLabel(qStr);
        valQ.setFont(new Font("Segoe UI", Font.BOLD, 36));
        
        if (quality > 0.7) valQ.setForeground(new Color(46, 204, 113));
        else if (quality > 0.4) valQ.setForeground(new Color(243, 156, 18));
        else valQ.setForeground(new Color(231, 76, 60));
        
        infoPanel.add(valQ, gbc);

        // Llegenda
        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        infoPanel.add(createLegendPanel(), gbc);

        return infoPanel;
    }

    /**
     * Creates a legend panel explaining the symbols used in the chart.
     * @return A constructed JPanel with the legend.
     */
    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220)), 
                "Legend"));
        ((javax.swing.border.TitledBorder)panel.getBorder()).setTitleFont(new Font("Segoe UI", Font.PLAIN, 12));
        ((javax.swing.border.TitledBorder)panel.getBorder()).setTitleColor(new Color(120, 130, 150));

        JLabel l1 = new JLabel("●  Response Point");
        l1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l1.setForeground(new Color(60, 70, 90));
        


        panel.add(l1);

        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(245, 247, 250));
        wrapper.add(panel, BorderLayout.NORTH);

        return wrapper;
    }

    /**
     * Helper method to create a standardized button with hover effects.
     *
     * @param text       Button label.
     * @param bgColor    Normal background color.
     * @param hoverColor Background color on mouse hover.
     * @param textColor  Color of the text.
     * @return The configured JButton.
     */
    private JButton createStyledButton(String text, Color bgColor, Color hoverColor, Color textColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(160, 45));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(hoverColor); }
            public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
        });
        return button;
    }

    /**
     * Performs Metric Multidimensional Scaling (MDS) using gradient descent.
     * <p>
     * This method projects N-dimensional data points (defined by the distances between them)
     * onto a 2D plane. It iteratively adjusts the 2D positions to minimize the error
     * between the 2D Euclidean distances and the original target distances.
     * </p>
     *
     * @param targetDistances A square matrix (N x N) representing the distances between answers.
     * @return An ArrayList containing the calculated 2D coordinates (Point2D.Double) for each answer.
     */
    public ArrayList<Point2D.Double> performMDS(double[][] targetDistances) {

        // Inicialitzar punts 2D aleatòriament
        ArrayList<Point2D.Double> points2D = new ArrayList<>(nAnswers);
        Random rand = new Random();
        for (int i = 0; i < nAnswers; i++) {
            points2D.add(new Point2D.Double(rand.nextDouble(), rand.nextDouble()));
        }

        // Paràmetres de l'algorisme (ajustables segons necessitat)
        double learningRate = 0.3;
        int iterations = 300;

        // Optimització iterativa (Minimització de l'estrès)
        for (int iter = 0; iter < iterations; iter++) {
            double totalError = 0;

            // Creem un array temporal per guardar els gradients (moviments)
            double[][] gradients = new double[nAnswers][2];

            for (int i = 0; i < nAnswers; i++) {
                for (int j = 0; j < nAnswers; j++) {
                    if (i == j) continue;

                    Point2D.Double p1 = points2D.get(i);
                    Point2D.Double p2 = points2D.get(j);

                    // Distància actual en 2D
                    double dist2D = p1.distance(p2);
                    double distTarget = targetDistances[i][j];

                    // Evitem divisió per zero
                    if (dist2D < 1e-9) dist2D = 1e-9;

                    // Calculem l'error (diferència relativa)
                    // Volem que dist2D s'acosti a distTarget
                    double errorFactor = (dist2D - distTarget) / dist2D;

                    // Acumulem el gradient per moure el punt i
                    double dx = p1.x - p2.x;
                    double dy = p1.y - p2.y;

                    gradients[i][0] -= learningRate * errorFactor * dx;
                    gradients[i][1] -= learningRate * errorFactor * dy;
                }
            }

            // Actualitzem les posicions
            for (int i = 0; i < nAnswers; i++) {
                Point2D.Double p = points2D.get(i);
                double newX = p.x + gradients[i][0] / nAnswers; // Normalitzem pel nombre de punts
                double newY = p.y + gradients[i][1] / nAnswers;
                p.setLocation(newX, newY);
            }
            // learningRate *= 0.99;
        }
        return points2D;
    }

    /**
     * Makes the view visible to the user.
     */
    public void makeVisible() {
        frame.setVisible(true);
    }
}
