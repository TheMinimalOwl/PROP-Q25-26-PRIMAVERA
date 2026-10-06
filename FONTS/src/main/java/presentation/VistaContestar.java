package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * View for answering forms in the Form Builder application.
 * Provides a comprehensive interface for users to complete forms
 * with various question types and validation.
 */
public class VistaContestar {
    private JFrame frameVista;
    private CardLayout cardLayout;
    private JPanel panelContenido;
    private JLabel lblProgreso;
    private JProgressBar progressBar;
    private JLabel lblTituloFormulario;

    // REAL data
    private Integer formId;
    private Integer userId;
    private CtrlPresentation ctrlPresentation;

    // Form data obtained as map
    private Map<String, Object> formData;
    private List<Map<String, Object>> questionsData;
    private int preguntaActual = 0;
    private int totalPreguntas;

    private Map<Integer, Integer> preguntaIdMap;

    // TEMPORARY answer storage (only in memory)
    private Map<Integer, Object> respuestasTemporales;

    private MainMenuView mainMenuView;
    private boolean returningToMainMenu = false;

    /**
     * Constructor for creating a form answering view.
     * 
     * @param formId            The unique identifier of the form to answer
     * @param userId            The unique identifier of the user answering
     * @param ctrlPresentation  The presentation controller for business logic
     * @param mainMenuView      Reference to the main menu view for navigation
     */
    public VistaContestar(Integer formId, Integer userId, CtrlPresentation ctrlPresentation,
            MainMenuView mainMenuView) {
        this.formId = formId;
        this.userId = userId;
        this.ctrlPresentation = ctrlPresentation;
        this.mainMenuView = mainMenuView;

        this.preguntaIdMap = new HashMap<>();
        this.respuestasTemporales = new HashMap<>();

        // 1. Get REAL form data
        this.formData = ctrlPresentation.getFormForAnswering(formId);

        // 2. Check for errors
        if (formData == null || formData.containsKey("error")) {
            String errorMsg = formData != null ? (String) formData.get("error") : "Could not load form data";
            JOptionPane.showMessageDialog(null,
                    "Error: " + errorMsg,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Extract data with null validation
        String formName = (String) formData.get("name");
        this.questionsData = (List<Map<String, Object>>) formData.get("questions");

        // Validate that totalQuestions exists and is not null
        Object totalQuestionsObj = formData.get("totalQuestions");
        if (totalQuestionsObj == null) {
            // If totalQuestions is null, calculate based on questionsData
            this.totalPreguntas = (questionsData != null) ? questionsData.size() : 0;
        } else {
            this.totalPreguntas = ((Number) totalQuestionsObj).intValue();
        }

        // 4. VERIFY IF THE FORM HAS QUESTIONS
        if (questionsData == null || questionsData.isEmpty() || totalPreguntas == 0) {
            JOptionPane.showMessageDialog(null,
                    "This form has no questions. Cannot answer an empty form.",
                    "Empty Form",
                    JOptionPane.WARNING_MESSAGE);

            // Return to MainMenuView
            if (mainMenuView != null) {
                mainMenuView.makeVisible();
            }
            return;
        }

        // 5. Map indices to real question IDs
        for (int i = 0; i < questionsData.size(); i++) {
            Integer questionId = (Integer) questionsData.get(i).get("id");
            if (questionId != null) {
                preguntaIdMap.put(i, questionId);
            }
        }

        // 6. Configure window
        frameVista = new JFrame("Form Builder - " + formName);
        configurarVentana();
        crearComponentes();
        frameVista.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Configures the main window properties and behavior.
     */
    private void configurarVentana() {
        frameVista.setMinimumSize(new Dimension(800, 560));
        frameVista.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frameVista.setLocationRelativeTo(null);

        frameVista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalir();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (!returningToMainMenu && mainMenuView != null) {
                    mainMenuView.makeVisible();
                }
            }
        });
    }

    /**
     * Creates and arranges all UI components in the window.
     */
    private void crearComponentes() {
        // Main panel with BorderLayout
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(245, 247, 250));

        // Create top bar
        panelPrincipal.add(crearBarraSuperior(), BorderLayout.NORTH);

        // Create main content area
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(Color.WHITE);

        // Add REAL questions
        for (int i = 0; i < totalPreguntas; i++) {
            Map<String, Object> questionData = questionsData.get(i);
            panelContenido.add(crearPanelPregunta(questionData, i), "pregunta" + i);
        }

        panelPrincipal.add(panelContenido, BorderLayout.CENTER);

        // Create bottom panel with navigation
        panelPrincipal.add(crearPanelNavegacion(), BorderLayout.SOUTH);

        frameVista.setContentPane(panelPrincipal);
    }

    /**
     * Creates the top navigation bar with form title and exit button.
     * 
     * @return The configured top bar panel
     */
    private JPanel crearBarraSuperior() {
        JPanel barraPanel = new JPanel(new BorderLayout());
        barraPanel.setBackground(Color.WHITE);
        barraPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 230)),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)));

        // Form title 
        String formName = (String) formData.get("name");
        lblTituloFormulario = new JLabel(formName);
        lblTituloFormulario.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTituloFormulario.setForeground(new Color(30, 40, 70));

        // Form information
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        panelInfo.setBackground(Color.WHITE);

        panelInfo.add(lblTituloFormulario);
        panelInfo.add(Box.createRigidArea(new Dimension(30, 0)));

        // Actions panel
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelAcciones.setBackground(Color.WHITE);

        JButton btnExit = createStyledButton("Exit",
                new Color(245, 247, 250),
                new Color(230, 235, 240),
                new Color(70, 130, 220));
        btnExit.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        btnExit.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnExit.addActionListener(e -> {
            int respuesta = JOptionPane.showConfirmDialog(frameVista,
                    "Do you want to exit? Your answers will NOT be saved.",
                    "Exit",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (respuesta == JOptionPane.YES_OPTION) {
                returningToMainMenu = true;
                frameVista.dispose();
                if (mainMenuView != null) {
                    mainMenuView.makeVisible();
                }
            }
        });

        panelAcciones.add(btnExit);

        barraPanel.add(panelInfo, BorderLayout.WEST);
        barraPanel.add(panelAcciones, BorderLayout.EAST);

        return barraPanel;
    }

    /**
     * Creates a question panel based on the question type.
     * 
     * @param questionData The question data
     * @param numero       The question number
     * @return The configured question panel
     */
    private JPanel crearPanelPregunta(Map<String, Object> questionData, int numero) {
        String type = (String) questionData.get("type");
        String statement = (String) questionData.get("statement");
        boolean optional = (Boolean) questionData.get("optional");

        switch (type) {
            case "FREE":
                return crearPreguntaTexto(statement, numero, optional, questionData);
            case "NUMERICAL":
                return crearPreguntaNumerica(statement, numero, optional, questionData);
            case "MULTIPLE_CHOICE":
                return crearPreguntaOpcionMultiple(statement, numero, optional, questionData);
            case "ORDERED_SINGLE":
                return crearPreguntaOrdenada(statement, numero, optional, questionData);
            default:
                throw new IllegalArgumentException("Unsupported question type: " + type);
        }
    }

    /**
     * Creates a panel for a numeric question.
     * 
     * @param statement    The question statement
     * @param numero       The question number
     * @param optional     Whether the question is optional
     * @param questionData The complete question data
     * @return The configured numeric question panel
     */
    private JPanel crearPreguntaNumerica(String statement, int numero, boolean optional,
            Map<String, Object> questionData) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Question header
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(Color.WHITE);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));

        JLabel lblNumero = new JLabel("Q" + (numero + 1));
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNumero.setForeground(new Color(70, 130, 220));
        lblNumero.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JLabel lblPregunta = new JLabel(statement);
        lblPregunta.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblPregunta.setForeground(new Color(30, 40, 70));

        // Instruction based on range
        Integer minValue = (Integer) questionData.get("minValue");
        Integer maxValue = (Integer) questionData.get("maxValue");
        String instruccion = "Enter a number";
        if (minValue != null && maxValue != null) {
            instruccion = "Enter a number between " + minValue + " and " + maxValue;
        } else if (minValue != null) {
            instruccion = "Minimum value: " + minValue;
        } else if (maxValue != null) {
            instruccion = "Maximum value: " + maxValue;
        }

        // Mark if optional
        if (optional) {
            instruccion += " (Optional)";
        }

        JLabel lblInstruccion = new JLabel(instruccion);
        lblInstruccion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInstruccion.setForeground(new Color(120, 130, 150));

        panelHeader.add(lblNumero, BorderLayout.NORTH);
        panelHeader.add(lblPregunta, BorderLayout.CENTER);
        panelHeader.add(lblInstruccion, BorderLayout.SOUTH);

        // Numeric text field
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        textField.setPreferredSize(new Dimension(200, 50));
        textField.setHorizontalAlignment(JTextField.CENTER);

        // Validation to only accept numbers
        textField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();

                // Allow: digits, negative sign, backspace, delete
                if (Character.isDigit(c) ||
                        c == '-' ||
                        c == KeyEvent.VK_BACK_SPACE ||
                        c == KeyEvent.VK_DELETE) {
                    // Do nothing, allow the character
                } else {
                    e.consume(); // Block other characters
                }
            }
        });

        // Store reference to text field
        panel.putClientProperty("textField", textField);

        JPanel panelCentro = new JPanel();
        panelCentro.setBackground(Color.WHITE);
        panelCentro.add(textField);

        panel.add(panelHeader, BorderLayout.NORTH);
        panel.add(panelCentro, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates a panel for a free text question.
     * 
     * @param statement    The question statement
     * @param numero       The question number
     * @param optional     Whether the question is optional
     * @param questionData The complete question data
     * @return The configured free text question panel
     */
    private JPanel crearPreguntaTexto(String statement, int numero, boolean optional,
            Map<String, Object> questionData) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Question header
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(Color.WHITE);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));

        JLabel lblNumero = new JLabel("Q" + (numero + 1));
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNumero.setForeground(new Color(70, 130, 220));
        lblNumero.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JLabel lblPregunta = new JLabel(statement);
        lblPregunta.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblPregunta.setForeground(new Color(30, 40, 70));

        String instruccion = "";
        if (optional)
            instruccion += " (Optional)";

        JLabel lblInstruccion = new JLabel(instruccion);
        lblInstruccion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInstruccion.setForeground(new Color(120, 130, 150));

        panelHeader.add(lblNumero, BorderLayout.NORTH);
        panelHeader.add(lblPregunta, BorderLayout.CENTER);
        panelHeader.add(lblInstruccion, BorderLayout.SOUTH);

        // Text area
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        textArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        // Store reference to text area
        panel.putClientProperty("textArea", textArea);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(800, 200));

        // Get maximum length if exists
        Integer maxLength = (Integer) questionData.get("maxLength");
        String maxLengthText = "Max. " + (maxLength != null ? maxLength : "unlimited") + " characters";
        JLabel lblContador = new JLabel(maxLengthText);
        lblContador.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblContador.setForeground(new Color(150, 160, 170));
        lblContador.setHorizontalAlignment(SwingConstants.RIGHT);
        lblContador.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        panel.add(panelHeader, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(lblContador, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates a panel for a multiple choice question.
     * 
     * @param statement    The question statement
     * @param numero       The question number
     * @param optional     Whether the question is optional
     * @param questionData The complete question data
     * @return The configured multiple choice question panel
     */
    private JPanel crearPreguntaOpcionMultiple(String statement, int numero, boolean optional,
            Map<String, Object> questionData) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Question header
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(Color.WHITE);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));

        JLabel lblNumero = new JLabel("Q" + (numero + 1));
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNumero.setForeground(new Color(70, 130, 220));
        lblNumero.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JLabel lblPregunta = new JLabel(statement);
        lblPregunta.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblPregunta.setForeground(new Color(30, 40, 70));

        // Get selection limits
        int minSelections = (int) questionData.get("minSelections");
        int maxSelections = (int) questionData.get("maxSelections");
        String selectionText = "";
        if (minSelections == 1 && maxSelections == 1) {
            selectionText = "Select one option";
        } else if (minSelections == maxSelections) {
            selectionText = "Select exactly " + minSelections + " options";
        } else {
            selectionText = "Select between " + minSelections + " and " + maxSelections + " options";
        }
        if (optional)
            selectionText += " (Optional)";

        JLabel lblInstruccion = new JLabel(selectionText);
        lblInstruccion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInstruccion.setForeground(new Color(120, 130, 150));

        panelHeader.add(lblNumero, BorderLayout.NORTH);
        panelHeader.add(lblPregunta, BorderLayout.CENTER);
        panelHeader.add(lblInstruccion, BorderLayout.SOUTH);

        // Options (obtained from map)
        List<String> opciones = (List<String>) questionData.get("options");

        JPanel panelOpciones = new JPanel();
        panelOpciones.setLayout(new BoxLayout(panelOpciones, BoxLayout.Y_AXIS));
        panelOpciones.setBackground(Color.WHITE);
        panelOpciones.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // List to store references to selection components
        List<AbstractButton> selectionButtons = new ArrayList<>();

        // Button group for radio buttons
        ButtonGroup radioButtonGroup = null;
        if (maxSelections == 1) {
            radioButtonGroup = new ButtonGroup();
        }

        for (int i = 0; i < opciones.size(); i++) {
            String opcion = opciones.get(i);
            JPanel panelOpcion = new JPanel(new BorderLayout());
            panelOpcion.setBackground(Color.WHITE);
            panelOpcion.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
            panelOpcion.setCursor(new Cursor(Cursor.HAND_CURSOR));

            AbstractButton selectionButton;

            // Create JRadioButton if only one can be selected, JCheckBox if multiple
            if (maxSelections == 1) {
                JRadioButton radioButton = new JRadioButton(opcion);
                radioButtonGroup.add(radioButton);
                selectionButton = radioButton;
            } else {
                JCheckBox checkBox = new JCheckBox(opcion);
                selectionButton = checkBox;
            }

            selectionButton.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            selectionButton.setBackground(Color.WHITE);
            selectionButton.setForeground(new Color(60, 70, 90));
            selectionButton.setFocusPainted(false);

            // Save limits in the component
            selectionButton.putClientProperty("minSelections", minSelections);
            selectionButton.putClientProperty("maxSelections", maxSelections);

            // Listener for validation only for checkboxes (radio buttons don't need it)
            if (maxSelections > 1) {
                selectionButton.addActionListener(e -> {
                    // Count how many are selected
                    int selectedCount = 0;
                    for (AbstractButton btn : selectionButtons) {
                        if (btn.isSelected())
                            selectedCount++;
                    }

                    // Get limits
                    int maxSel = (Integer) selectionButton.getClientProperty("maxSelections");

                    // Deselect if maximum exceeded
                    if (selectionButton.isSelected() && selectedCount > maxSel) {
                        ((JCheckBox) selectionButton).setSelected(false);
                    }
                });
            }

            selectionButtons.add(selectionButton);

            panelOpcion.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    panelOpcion.setBackground(new Color(245, 247, 250));
                }

                public void mouseExited(MouseEvent e) {
                    panelOpcion.setBackground(Color.WHITE);
                }

                public void mouseClicked(MouseEvent e) {
                    selectionButton.setSelected(!selectionButton.isSelected());
                    // Trigger action listener if exists
                    for (ActionListener al : selectionButton.getActionListeners()) {
                        al.actionPerformed(new ActionEvent(selectionButton, ActionEvent.ACTION_PERFORMED, "click"));
                    }
                }
            });

            panelOpcion.add(selectionButton, BorderLayout.WEST);
            panelOpciones.add(panelOpcion);
        }

        // Save list of selection components in main panel
        panel.putClientProperty("selectionButtons", selectionButtons);

        JScrollPane scrollPane = new JScrollPane(panelOpciones);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 240), 1));
        scrollPane.setPreferredSize(new Dimension(800, 300));

        panel.add(panelHeader, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates a panel for an ordered single choice question.
     * 
     * @param statement    The question statement
     * @param numero       The question number
     * @param optional     Whether the question is optional
     * @param questionData The complete question data
     * @return The configured ordered single choice question panel
     */
    private JPanel crearPreguntaOrdenada(String statement, int numero, boolean optional,
            Map<String, Object> questionData) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Question header
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(Color.WHITE);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));

        JLabel lblNumero = new JLabel("Q" + (numero + 1));
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNumero.setForeground(new Color(70, 130, 220));
        lblNumero.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JLabel lblPregunta = new JLabel(statement);
        lblPregunta.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblPregunta.setForeground(new Color(30, 40, 70));

        String instruccion = "Select one option";
        if (optional)
            instruccion += " (Optional)";

        JLabel lblInstruccion = new JLabel(instruccion);
        lblInstruccion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInstruccion.setForeground(new Color(120, 130, 150));

        panelHeader.add(lblNumero, BorderLayout.NORTH);
        panelHeader.add(lblPregunta, BorderLayout.CENTER);
        panelHeader.add(lblInstruccion, BorderLayout.SOUTH);

        // Options
        List<String> opciones = (List<String>) questionData.get("options");

        JPanel panelOpciones = new JPanel();
        panelOpciones.setLayout(new BoxLayout(panelOpciones, BoxLayout.Y_AXIS));
        panelOpciones.setBackground(Color.WHITE);
        panelOpciones.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        ButtonGroup buttonGroup = new ButtonGroup();
        List<JRadioButton> radioButtons = new ArrayList<>();

        for (int i = 0; i < opciones.size(); i++) {
            String opcion = opciones.get(i);
            JPanel panelOpcion = new JPanel(new BorderLayout());
            panelOpcion.setBackground(Color.WHITE);
            panelOpcion.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
            panelOpcion.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JRadioButton radioButton = new JRadioButton(opcion);
            radioButton.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            radioButton.setBackground(Color.WHITE);
            radioButton.setForeground(new Color(60, 70, 90));
            radioButton.setFocusPainted(false);

            radioButtons.add(radioButton);
            buttonGroup.add(radioButton);

            // Hover effect
            panelOpcion.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    panelOpcion.setBackground(new Color(245, 247, 250));
                }

                public void mouseExited(MouseEvent e) {
                    panelOpcion.setBackground(Color.WHITE);
                }

                public void mouseClicked(MouseEvent e) {
                    radioButton.setSelected(true);
                }
            });

            panelOpcion.add(radioButton, BorderLayout.WEST);
            panelOpciones.add(panelOpcion);
        }

        // Save list of radio buttons in main panel
        panel.putClientProperty("radioButtons", radioButtons);

        JScrollPane scrollPane = new JScrollPane(panelOpciones);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 240), 1));
        scrollPane.setPreferredSize(new Dimension(800, 300));

        panel.add(panelHeader, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Collects all answers from the form.
     */
    private void recolectarRespuestas() {
        respuestasTemporales.clear();

        for (int i = 0; i < totalPreguntas; i++) {
            Component preguntaPanel = panelContenido.getComponent(i);
            Map<String, Object> questionData = questionsData.get(i);
            String type = (String) questionData.get("type");

            switch (type) {
                case "FREE":
                    recolectarRespuestaTexto(preguntaPanel, i);
                    break;
                case "NUMERICAL":
                    recolectarRespuestaNumerica(preguntaPanel, i);
                    break;
                case "MULTIPLE_CHOICE":
                    recolectarRespuestaMultipleChoice(preguntaPanel, i);
                    break;
                case "ORDERED_SINGLE":
                    recolectarRespuestaOrdenada(preguntaPanel, i);
                    break;
            }
        }
    }

    /**
     * Collects free text answer from a question panel.
     * 
     * @param preguntaPanel The question panel
     * @param questionIndex The question index
     */
    private void recolectarRespuestaTexto(Component preguntaPanel, int questionIndex) {
        JTextArea textArea = (JTextArea) ((JPanel) preguntaPanel).getClientProperty("textArea");
        if (textArea != null) {
            String texto = textArea.getText().trim();
            if (!texto.isEmpty()) {
                respuestasTemporales.put(questionIndex, texto);
            }
        }
    }

    /**
     * Collects numeric answer from a question panel.
     * 
     * @param preguntaPanel The question panel
     * @param questionIndex The question index
     */
    private void recolectarRespuestaNumerica(Component preguntaPanel, int questionIndex) {
        JTextField textField = (JTextField) ((JPanel) preguntaPanel).getClientProperty("textField");
        if (textField != null) {
            String texto = textField.getText().trim();
            if (!texto.isEmpty()) {
                try {
                    Integer value = Integer.parseInt(texto);

                    // Get minimum and maximum values from the question
                    Map<String, Object> questionData = questionsData.get(questionIndex);
                    Integer minValue = (Integer) questionData.get("minValue");
                    Integer maxValue = (Integer) questionData.get("maxValue");

                    // Validate that it's within range
                    boolean dentroDelRango = true;
                    if (minValue != null && value < minValue) {
                        dentroDelRango = false;
                    }
                    if (maxValue != null && value > maxValue) {
                        dentroDelRango = false;
                    }

                    if (dentroDelRango) {
                        respuestasTemporales.put(questionIndex, value);
                    } else {
                        // Show error message if out of range
                        JOptionPane.showMessageDialog(frameVista,
                                "The value must be " +
                                        (minValue != null ? "at least " + minValue : "") +
                                        (minValue != null && maxValue != null ? " and " : "") +
                                        (maxValue != null ? "at most " + maxValue : "") + ".",
                                "Invalid Range",
                                JOptionPane.WARNING_MESSAGE);
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    // Ignore non-numeric values
                }
            }
        }
    }

    /**
     * Collects multiple choice answer from a question panel.
     * 
     * @param preguntaPanel The question panel
     * @param questionIndex The question index
     */
    @SuppressWarnings("unchecked")
    private void recolectarRespuestaMultipleChoice(Component preguntaPanel, int questionIndex) {
        List<JCheckBox> checkBoxes = (List<JCheckBox>) ((JPanel) preguntaPanel).getClientProperty("checkBoxes");
        if (checkBoxes != null) {
            List<Integer> seleccionados = new ArrayList<>();
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    seleccionados.add(i);
                }
            }
            if (!seleccionados.isEmpty()) {
                respuestasTemporales.put(questionIndex, seleccionados);
            }
        }
    }

    /**
     * Collects ordered single choice answer from a question panel.
     * 
     * @param preguntaPanel The question panel
     * @param questionIndex The question index
     */
    @SuppressWarnings("unchecked")
    private void recolectarRespuestaOrdenada(Component preguntaPanel, int questionIndex) {
        List<JRadioButton> radioButtons = (List<JRadioButton>) ((JPanel) preguntaPanel).getClientProperty("radioButtons");
        if (radioButtons != null) {
            for (int i = 0; i < radioButtons.size(); i++) {
                if (radioButtons.get(i).isSelected()) {
                    respuestasTemporales.put(questionIndex, i);
                    break;
                }
            }
        }
    }

    /**
     * Saves all answers to the database.
     */
    private void guardarTodasLasRespuestas() {
        for (Map.Entry<Integer, Object> entry : respuestasTemporales.entrySet()) {
            int questionIndex = entry.getKey();
            Object respuesta = entry.getValue();
            Integer questionId = preguntaIdMap.get(questionIndex);
            Map<String, Object> questionData = questionsData.get(questionIndex);
            String type = (String) questionData.get("type");

            if (questionId != null) {
                switch (type) {
                    case "FREE":
                        ctrlPresentation.addFreeAnswer(userId, formId, questionId, (String) respuesta);
                        break;
                    case "NUMERICAL":
                        ctrlPresentation.addNumericalAnswer(userId, formId, questionId, (Integer) respuesta);
                        break;
                    case "MULTIPLE_CHOICE":
                        ctrlPresentation.addMultipleChoiceAnswer(userId, formId, questionId,
                                new ArrayList<>((List<Integer>) respuesta));
                        break;
                    case "ORDERED_SINGLE":
                        ctrlPresentation.addOrderedSingleAnswer(userId, formId, questionId, (Integer) respuesta);
                        break;
                }
            }
        }
    }

    /**
     * Finds a component of a specific type within a container.
     * 
     * @param <T>       The type of component to find
     * @param container The container to search in
     * @param clazz     The class of the component to find
     * @return The found component, or null if not found
     */
    @SuppressWarnings("unchecked")
    private <T extends Component> T findComponent(Container container, Class<T> clazz) {
        for (Component comp : container.getComponents()) {
            if (clazz.isInstance(comp)) {
                return (T) comp;
            }
            if (comp instanceof Container) {
                T found = findComponent((Container) comp, clazz);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * Creates the navigation panel with progress bar and buttons.
     * 
     * @return The configured navigation panel
     */
    private JPanel crearPanelNavegacion() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)));

        // Progress bar
        JPanel panelProgreso = new JPanel(new BorderLayout());
        panelProgreso.setBackground(Color.WHITE);

        lblProgreso = new JLabel("Progress: " + (preguntaActual + 1) + " of " + totalPreguntas);
        lblProgreso.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblProgreso.setForeground(new Color(120, 130, 150));

        progressBar = new JProgressBar(0, totalPreguntas);
        progressBar.setValue(preguntaActual + 1);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        progressBar.setForeground(new Color(70, 130, 220));
        progressBar.setBackground(new Color(230, 235, 240));
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        progressBar.setPreferredSize(new Dimension(300, 10));

        panelProgreso.add(lblProgreso, BorderLayout.NORTH);
        panelProgreso.add(progressBar, BorderLayout.CENTER);

        // Navigation buttons
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelBotones.setBackground(Color.WHITE);

        JButton btnPrevious = createStyledButton("Previous",
                new Color(245, 247, 250),
                new Color(230, 235, 240),
                new Color(70, 130, 220));
        btnPrevious.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1),
                BorderFactory.createEmptyBorder(12, 25, 12, 25)));
        btnPrevious.setEnabled(preguntaActual > 0);

        JButton btnNext = createStyledButton((preguntaActual == totalPreguntas - 1) ? "Submit" : "Next",
                new Color(70, 130, 220),
                new Color(50, 110, 200),
                Color.WHITE);

        btnPrevious.addActionListener(e -> {
            if (preguntaActual > 0) {
                preguntaActual--;
                cardLayout.show(panelContenido, "pregunta" + preguntaActual);
                btnPrevious.setEnabled(preguntaActual > 0);
                btnNext.setText((preguntaActual == totalPreguntas - 1) ? "Submit" : "Next");

                // Update UI
                lblProgreso.setText("Progress: " + (preguntaActual + 1) + " of " + totalPreguntas);
                progressBar.setValue(preguntaActual + 1);
            }
        });

        btnNext.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

        btnNext.addActionListener(e -> {
            if (preguntaActual < totalPreguntas - 1) {
                // Validate current question before advancing
                Map<String, Object> currentQuestion = questionsData.get(preguntaActual);
                boolean optional = (Boolean) currentQuestion.get("optional");

                // If the question is NOT optional, validate that it has an answer
                if (!optional) {
                    boolean tieneRespuesta = validarPreguntaEsObligatoria(currentQuestion, preguntaActual);
                    if (!tieneRespuesta) {
                        return; // Don't advance if no answer
                    }
                }

                preguntaActual++;
                cardLayout.show(panelContenido, "pregunta" + preguntaActual);
                btnPrevious.setEnabled(preguntaActual > 0);
                btnNext.setText((preguntaActual == totalPreguntas - 1) ? "Submit" : "Next");

                // Update UI
                lblProgreso.setText("Progress: " + (preguntaActual + 1) + " of " + totalPreguntas);
                progressBar.setValue(preguntaActual + 1);
            } else {
                // Validate the last question before submit
                Map<String, Object> lastQuestion = questionsData.get(preguntaActual);
                boolean optional = (Boolean) lastQuestion.get("optional");

                // If the question is NOT optional, validate that it has an answer
                if (!optional) {
                    boolean tieneRespuesta = validarPreguntaEsObligatoria(lastQuestion, preguntaActual);
                    if (!tieneRespuesta) {
                        return;
                    }
                }

                // Final submit
                int respuesta = JOptionPane.showConfirmDialog(frameVista,
                        "Are you sure you want to submit your responses?",
                        "Submit Form",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (respuesta == JOptionPane.YES_OPTION) {
                    // 1. Collect all answers
                    recolectarRespuestas();

                    // 2. Save all answers to the database
                    guardarTodasLasRespuestas();

                    JOptionPane.showMessageDialog(frameVista,
                            "Thank you! Your responses have been submitted successfully.",
                            "Submission Complete",
                            JOptionPane.INFORMATION_MESSAGE);
                    returningToMainMenu = true;
                    frameVista.dispose();
                    if (mainMenuView != null) {
                        mainMenuView.makeVisible();
                    }
                }
            }
        });
        panelBotones.add(btnPrevious);
        panelBotones.add(btnNext);

        panel.add(panelProgreso, BorderLayout.WEST);
        panel.add(panelBotones, BorderLayout.EAST);

        return panel;
    }

    /**
     * Creates a styled button with hover effects.
     * 
     * @param text      The button text
     * @param bgColor   The background color
     * @param hoverColor The hover background color
     * @param textColor The text color
     * @return The configured styled button
     */
    private JButton createStyledButton(String text, Color bgColor, Color hoverColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * Validates that a required question has been answered.
     * 
     * @param questionData  The question data
     * @param questionIndex The question index
     * @return true if the question has a valid answer, false otherwise
     */
    private boolean validarPreguntaEsObligatoria(Map<String, Object> questionData, int questionIndex) {
        String type = (String) questionData.get("type");
        Component currentPanel = panelContenido.getComponent(questionIndex);

        boolean tieneRespuesta = false;
        String mensajeError = "This question is required. Please provide a response.";

        switch (type) {
            case "FREE":
                JTextArea textArea = (JTextArea) ((JPanel) currentPanel).getClientProperty("textArea");
                if (textArea != null) {
                    String respuesta = textArea.getText().trim();
                    tieneRespuesta = !respuesta.isEmpty();
                    
                    // Get maximum character limit from question data
                    Integer maxLength = (Integer) questionData.get("maxLength");
                    
                    // Validate character limit if response exists and limit is set
                    if (tieneRespuesta && maxLength != null && maxLength > 0) {
                        int respuestaLength = respuesta.length();
                        
                        if (respuestaLength > maxLength) {
                            tieneRespuesta = false;
                            mensajeError = "Response exceeds maximum length of " + maxLength + 
                                          " characters.\n" +
                                          "Current length: " + respuestaLength + " characters.\n" +
                                          "Please shorten your response by " + 
                                          (respuestaLength - maxLength) + " characters.";
                            
                            JOptionPane.showMessageDialog(frameVista,
                                    mensajeError,
                                    "Length Exceeded",
                                    JOptionPane.WARNING_MESSAGE);
                            
                            // Optional: Highlight the text area and focus it
                            textArea.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(Color.RED, 2),
                                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
                            textArea.requestFocusInWindow();
                            
                            return false;
                        }
                    }
                }
                break;
            case "NUMERICAL":
                JTextField textField = (JTextField) ((JPanel) currentPanel).getClientProperty("textField");
                if (textField != null) {
                    String texto = textField.getText().trim();
                    if (!texto.isEmpty()) {
                        try {
                            Integer valor = Integer.parseInt(texto);

                            // Get minimum and maximum values from the questionnaire
                            Integer minValue = (Integer) questionData.get("minValue");
                            Integer maxValue = (Integer) questionData.get("maxValue");

                            // Validate range
                            boolean dentroDelRango = true;

                            if (minValue != null && valor < minValue) {
                                mensajeError = "Value must be at least " + minValue + ".";
                                dentroDelRango = false;
                            }

                            if (maxValue != null && valor > maxValue) {
                                mensajeError = "Value cannot exceed " + maxValue + ".";
                                dentroDelRango = false;
                            }

                            if (minValue != null && maxValue != null && (valor < minValue || valor > maxValue)) {
                                mensajeError = "Value must be between " + minValue + " and " + maxValue + ".";
                                dentroDelRango = false;
                            }

                            tieneRespuesta = dentroDelRango;

                            if (!dentroDelRango) {
                                JOptionPane.showMessageDialog(frameVista,
                                        mensajeError,
                                        "Invalid Range",
                                        JOptionPane.WARNING_MESSAGE);
                                return false;
                            }

                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(frameVista,
                                    "Please enter a valid number.",
                                    "Invalid Input",
                                    JOptionPane.WARNING_MESSAGE);
                            return false;
                        }
                    }
                }
                break;

            case "MULTIPLE_CHOICE":
                List<AbstractButton> selectionButtons = (List<AbstractButton>) ((JPanel) currentPanel)
                        .getClientProperty("selectionButtons");
                if (selectionButtons != null) {
                    for (AbstractButton button : selectionButtons) {
                        if (button.isSelected()) {
                            tieneRespuesta = true;
                            break;
                        }
                    }
                }
                break;

            case "ORDERED_SINGLE":
                List<JRadioButton> radioButtons = (List<JRadioButton>) ((JPanel) currentPanel)
                        .getClientProperty("radioButtons");
                if (radioButtons != null) {
                    for (JRadioButton radioButton : radioButtons) {
                        if (radioButton.isSelected()) {
                            tieneRespuesta = true;
                            break;
                        }
                    }
                }
                break;
        }

        if (!tieneRespuesta) {
            JOptionPane.showMessageDialog(frameVista,
                    mensajeError,
                    "Required Field",
                    JOptionPane.WARNING_MESSAGE);
        }

        return tieneRespuesta;
    }

    /**
     * Confirms exit and checks for unsaved answers.
     */
    private void confirmarSalir() {
        int respuesta = JOptionPane.showConfirmDialog(frameVista,
                "Are you sure you want to close and exit?\nYour answers will NOT be saved.",
                "Confirm exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (respuesta == JOptionPane.YES_OPTION) {
            returningToMainMenu = true;
            frameVista.dispose();
            if (mainMenuView != null) {
                mainMenuView.makeVisible();
            }
        }
    }

    /**
     * Makes the form answering window visible and maximizes it.
     */
    public void hacerVisible() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frameVista.setSize(screenSize);
        frameVista.setLocationRelativeTo(null);
        frameVista.validate();
        frameVista.repaint();
        frameVista.setVisible(true);
        frameVista.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}
