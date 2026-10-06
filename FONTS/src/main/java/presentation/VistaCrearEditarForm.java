package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * View for creating and editing forms in the Form Builder application.
 * Provides a comprehensive interface for form creation, question management,
 * and form editing with support for various question types.
 */
public class VistaCrearEditarForm {
    private JFrame frame;
    private JPanel panelQuestions;
    private List<JPanel> questionPanels;
    private List<Map<String, Object>> questionsData;
    private JTextField txtFormTitle;
    private JButton btnAddQuestion;
    private JLabel lblQuestionCount;
    private JButton btnSave;
    private JButton btnReorderQuestions;

    // Form data
    private Integer formId;
    private CtrlPresentation ctrlPresentation;

    // Form state
    private boolean formHasAnswers = false;
    private boolean formIsPublished = false;
    private boolean unsavedChanges = false;

    private MainMenuView mainMenuView;
    private boolean returningToMainMenu = false;

    /**
     * Constructor for creating or editing a form.
     * 
     * @param formId            The unique identifier of the form to edit (null for new forms)
     * @param ctrlPresentation  The presentation controller for business logic
     * @param mainMenuView      Reference to the main menu view for navigation
     */
    public VistaCrearEditarForm(Integer formId, CtrlPresentation ctrlPresentation, MainMenuView mainMenuView) {
        // Verify that formId is valid
        if (formId == null || formId <= 0) {
            showError("Error", "Invalid form ID. Cannot edit form.");
            // Do not continue creating the window
            return;
        }

        this.formId = formId;
        this.ctrlPresentation = ctrlPresentation;
        this.mainMenuView = mainMenuView; // Save reference
        this.questionPanels = new ArrayList<>();
        this.questionsData = new ArrayList<>();

        frame = new JFrame("Form Builder - Edit Form");
        configureWindow();
        createComponents();
        loadExistingForm();

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Configures the main window properties and behavior.
     */
    private void configureWindow() {
        frame.setMinimumSize(new Dimension(1000, 700));
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (!returningToMainMenu && mainMenuView != null) {
                    mainMenuView.makeVisible();
                }
            }
        });

        try {
            ImageIcon icon = new ImageIcon("FONTS\\src\\main\\java\\presentation\\IconoForms2.png");
            frame.setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Could not load icon: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates and arranges all UI components in the window.
     */
    private void createComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        mainPanel.add(createTopBar(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
    }

    /**
     * Creates the top navigation bar with title and exit button.
     * 
     * @return The configured top bar panel
     */
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 230)),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)));

        JLabel lblWindowTitle = new JLabel("Edit Form");
        lblWindowTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblWindowTitle.setForeground(new Color(30, 40, 70));

        topBar.add(lblWindowTitle, BorderLayout.WEST);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelAcciones.setBackground(Color.WHITE);

        JButton btnCancelTop = createStyledButton("Exit",
                new Color(245, 247, 250),
                new Color(230, 235, 240),
                new Color(70, 130, 220));
        btnCancelTop.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        btnCancelTop.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnCancelTop.addActionListener(e -> {
            int respuesta = JOptionPane.showConfirmDialog(frame,
                    "Do you want to exit without saving? Your changes will NOT be saved.",
                    "Exit Without Saving",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (respuesta == JOptionPane.YES_OPTION) {
                returningToMainMenu = true;
                frame.dispose();
                if (mainMenuView != null) {
                    mainMenuView.makeVisible();
                }
            }
        });

        panelAcciones.add(btnCancelTop);
        topBar.add(panelAcciones, BorderLayout.EAST);

        return topBar;
    }

    /**
     * Creates the main content area with form title and questions.
     * 
     * @return The configured center panel
     */
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel lblTitle = new JLabel("Form Title");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(30, 40, 70));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        txtFormTitle = new JTextField();
        txtFormTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtFormTitle.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        // Listener to detect changes
        txtFormTitle.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                unsavedChanges = true;
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                unsavedChanges = true;
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                unsavedChanges = true;
            }
        });

        titlePanel.add(lblTitle, BorderLayout.NORTH);
        titlePanel.add(txtFormTitle, BorderLayout.CENTER);

        // Questions panel
        JPanel questionsContainer = new JPanel(new BorderLayout());
        questionsContainer.setBackground(Color.WHITE);

        JLabel lblQuestions = new JLabel("Questions");
        lblQuestions.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblQuestions.setForeground(new Color(30, 40, 70));
        lblQuestions.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        panelQuestions = new JPanel();
        panelQuestions.setLayout(new BoxLayout(panelQuestions, BoxLayout.Y_AXIS));
        panelQuestions.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(panelQuestions);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(Color.WHITE);

        questionsContainer.add(lblQuestions, BorderLayout.NORTH);
        questionsContainer.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(titlePanel, BorderLayout.NORTH);
        centerPanel.add(questionsContainer, BorderLayout.CENTER);

        return centerPanel;
    }

    /**
     * Creates the bottom panel with question count and action buttons.
     * 
     * @return The configured bottom panel
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)));

        lblQuestionCount = new JLabel("Questions: 0");
        lblQuestionCount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblQuestionCount.setForeground(new Color(120, 130, 150));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);

        btnReorderQuestions = createStyledButton("Reorder Questions",
                new Color(100, 180, 100),
                new Color(80, 160, 80),
                Color.WHITE);
        btnReorderQuestions.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReorderQuestions.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnReorderQuestions.addActionListener(e -> showReorderDialog());
        btnReorderQuestions.setVisible(false); // Only visible if no answers
        buttonPanel.add(btnReorderQuestions);

        btnAddQuestion = createStyledButton("+ Add Question",
                new Color(70, 130, 220),
                new Color(50, 110, 200),
                Color.WHITE);
        btnAddQuestion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAddQuestion.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnAddQuestion.addActionListener(e -> showQuestionTypeMenu());

        btnSave = createStyledButton("Save Changes",
                new Color(70, 130, 220),
                new Color(50, 110, 200),
                Color.WHITE);
        btnSave.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btnSave.addActionListener(e -> saveForm());

        buttonPanel.add(btnAddQuestion);
        buttonPanel.add(btnSave);

        panel.add(lblQuestionCount, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Loads an existing form from the presentation controller.
     * Populates the form title and questions from the database.
     */
    private void loadExistingForm() {
        try {
            // Use the presentation controller
            Map<String, Object> formData = ctrlPresentation.getFormForEditing(formId);

            if (formData.containsKey("error")) {
                showError("Error loading form", (String) formData.get("error"));
                frame.dispose();
                return;
            }

            txtFormTitle.setText((String) formData.get("name"));

            formHasAnswers = (Boolean) formData.get("hasAnswers");
            formIsPublished = ctrlPresentation.isFormPublished((Integer) formData.get("id"));

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> questions = (List<Map<String, Object>>) formData.get("questions");

            if (questions != null) {
                for (Map<String, Object> questionData : questions) {
                    // Ensure question ID is present
                    if (questionData.get("id") != null) {
                        questionData.put("questionId", questionData.get("id"));
                    }
                    addExistingQuestion(questionData);
                }
            }

            updateQuestionCount();

            if (formHasAnswers) {
                btnAddQuestion.setEnabled(false);
                btnAddQuestion.setToolTipText("Cannot add questions because form already has answers");
            } else {
                if (formIsPublished) {
                    btnAddQuestion.setEnabled(false);
                    btnAddQuestion.setToolTipText("Cannot add questions because form is published.");
                } else {
                    btnReorderQuestions.setVisible(true);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Could not load the form: " + e.getMessage());
            frame.dispose();
        }
    }

    /**
     * Displays a popup menu for selecting question types.
     */
    private void showQuestionTypeMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(Color.WHITE);
        menu.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 240), 1));

        String[] questionTypes = {
                "Free Text", "Multiple Choice", "Numeric", "Ordered Single Choice"
        };

        String[] typeCodes = { "FREE", "MULTIPLE_CHOICE", "NUMERICAL", "ORDERED_SINGLE" };

        for (int i = 0; i < questionTypes.length; i++) {
            JMenuItem item = createMenuItem(questionTypes[i], typeCodes[i]);
            menu.add(item);
        }

        menu.show(btnAddQuestion, 0, btnAddQuestion.getHeight());
    }

    /**
     * Creates a menu item for the question type selection menu.
     * 
     * @param text The display text for the menu item
     * @param type The internal type code for the question
     * @return The configured menu item
     */
    private JMenuItem createMenuItem(String text, String type) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        item.setBackground(Color.WHITE);
        item.setForeground(new Color(60, 70, 90));
        item.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        item.addActionListener(e -> addNewQuestion(type));

        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                item.setBackground(new Color(245, 247, 250));
            }

            public void mouseExited(MouseEvent e) {
                item.setBackground(Color.WHITE);
            }
        });

        return item;
    }

    /**
     * Adds a new question to the form based on the selected type.
     * 
     * @param type The type of question to add
     */
    private void addNewQuestion(String type) {
        Map<String, Object> questionData = showCreateQuestionDialog(type);

        if (questionData != null && !questionData.isEmpty()) {
            questionData.put("existing", false);
            questionData.put("status", "new");

            questionsData.add(questionData);

            // Calculate correct number (only visible questions)
            int visibleCount = 0;
            for (Map<String, Object> qData : questionsData) {
                if (!"delete".equals(qData.get("status"))) {
                    visibleCount++;
                }
            }

            // Create visual panel with correct number
            JPanel questionPanel = createQuestionPanel(questionData, visibleCount, false);
            questionPanels.add(questionPanel);
            panelQuestions.add(questionPanel);
            panelQuestions.add(Box.createRigidArea(new Dimension(0, 15)));

            panelQuestions.revalidate();
            panelQuestions.repaint();
            updateQuestionCount();
            unsavedChanges = true;

            SwingUtilities.invokeLater(() -> {
                JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class,
                        panelQuestions);
                if (scrollPane != null) {
                    JScrollBar vertical = scrollPane.getVerticalScrollBar();
                    vertical.setValue(vertical.getMaximum());
                }
            });
        }
    }

    /**
     * Adds an existing question loaded from the database.
     * 
     * @param questionData The question data from the database
     */
    private void addExistingQuestion(Map<String, Object> questionData) {
        // Mark as existing question with "keep" status
        questionData.put("status", "keep");
        questionData.put("existing", true);

        // Add to data list
        questionsData.add(questionData);

        // Create visual panel (existing questions are read-only if form has answers)
        JPanel questionPanel = createQuestionPanel(questionData, questionPanels.size() + 1, formHasAnswers || formIsPublished);
        questionPanels.add(questionPanel);
        panelQuestions.add(questionPanel);
        panelQuestions.add(Box.createRigidArea(new Dimension(0, 15)));

        panelQuestions.revalidate();
        panelQuestions.repaint();
    }

    /**
     * Shows the appropriate question creation dialog based on type.
     * 
     * @param type The type of question to create
     * @return Map containing the question data, or null if cancelled
     */
    private Map<String, Object> showCreateQuestionDialog(String type) {
        switch (type) {
            case "FREE":
                return showFreeQuestionDialog();
            case "NUMERICAL":
                return showNumericQuestionDialog();
            case "MULTIPLE_CHOICE":
                return showMultipleChoiceDialog();
            case "ORDERED_SINGLE":
                return showOrderedSingleDialog();
            default:
                return null;
        }
    }

    /**
     * Shows a dialog for creating a free text question.
     * 
     * @return Map containing the free text question data
     */
    private Map<String, Object> showFreeQuestionDialog() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Statement
        JLabel lblStatement = new JLabel("Question statement:");
        JTextArea txtStatement = new JTextArea(3, 30);
        txtStatement.setLineWrap(true);
        txtStatement.setWrapStyleWord(true);
        JScrollPane scrollStatement = new JScrollPane(txtStatement);

        // Max length
        JLabel lblMaxLength = new JLabel("Maximum length (characters):");
        JTextField txtMaxLength = new JTextField("500");

        // Optional
        JCheckBox chkOptional = new JCheckBox("Optional question");

        panel.add(lblStatement, BorderLayout.NORTH);
        panel.add(scrollStatement, BorderLayout.CENTER);

        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        optionsPanel.add(lblMaxLength);
        optionsPanel.add(txtMaxLength);
        optionsPanel.add(chkOptional);
        optionsPanel.add(new JLabel());

        panel.add(optionsPanel, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(frame, panel,
                "Create Free Text Question", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String statement = txtStatement.getText().trim();
            if (statement.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Question statement cannot be empty!");
                return null;
            }
            Map<String, Object> questionData = new java.util.HashMap<>();
            questionData.put("type", "FREE");
            questionData.put("statement", statement);
            questionData.put("optional", chkOptional.isSelected());

            try {
                int maxLength = Integer.parseInt(txtMaxLength.getText());
                questionData.put("maxLength", maxLength);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                questionData.put("maxLength", 500);
            }

            return questionData;
        }

        return null;
    }

    /**
     * Shows a dialog for creating a numeric question.
     * 
     * @return Map containing the numeric question data
     */
    private Map<String, Object> showNumericQuestionDialog() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblStatement = new JLabel("Question statement:");
        JTextArea txtStatement = new JTextArea(3, 30);
        txtStatement.setLineWrap(true);
        txtStatement.setWrapStyleWord(true);

        JLabel lblMin = new JLabel("Minimum value:");
        JTextField txtMin = new JTextField();

        JLabel lblMax = new JLabel("Maximum value:");
        JTextField txtMax = new JTextField();

        JCheckBox chkOptional = new JCheckBox("Optional question");

        // VALIDATION TO ONLY ACCEPT NUMBERS AND NEGATIVE SIGN
        txtMin.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String currentText = txtMin.getText();

                // Allow only: digits, backspace, delete
                // And negative sign only at the beginning
                if (c == '-') {
                    // Only allow negative sign if at the beginning and no other
                    if (!currentText.isEmpty() && txtMin.getCaretPosition() != 0) {
                        e.consume();
                    }
                } else if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE &&
                        c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });

        txtMax.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String currentText = txtMax.getText();

                if (c == '-') {
                    if (!currentText.isEmpty() && txtMax.getCaretPosition() != 0) {
                        e.consume();
                    }
                } else if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE &&
                        c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });

        panel.add(lblStatement, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtStatement), BorderLayout.CENTER);

        JPanel optionsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        optionsPanel.add(lblMin);
        optionsPanel.add(txtMin);
        optionsPanel.add(lblMax);
        optionsPanel.add(txtMax);
        optionsPanel.add(chkOptional);
        optionsPanel.add(new JLabel());

        panel.add(optionsPanel, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(frame, panel,
                "Create Numeric Question", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String statement = txtStatement.getText().trim();
            if (statement.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Question statement cannot be empty!");
                return null;
            }

            Map<String, Object> questionData = new java.util.HashMap<>();
            questionData.put("type", "NUMERICAL");
            questionData.put("statement", statement);
            questionData.put("optional", chkOptional.isSelected());

            try {
                Integer minValue = null;
                Integer maxValue = null;

                // Validate and process minimum value
                if (!txtMin.getText().trim().isEmpty()) {
                    String minText = txtMin.getText().trim();
                    // Accept negative and positive numbers
                    if (minText.matches("-?\\d+")) {
                        minValue = Integer.parseInt(minText);
                        questionData.put("minValue", minValue);
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Invalid minimum value. Please enter a valid integer.");
                        return null;
                    }
                }

                // Validate and process maximum value
                if (!txtMax.getText().trim().isEmpty()) {
                    String maxText = txtMax.getText().trim();
                    // Accept negative and positive numbers
                    if (maxText.matches("-?\\d+")) {
                        maxValue = Integer.parseInt(maxText);
                        questionData.put("maxValue", maxValue);
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Invalid maximum value. Please enter a valid integer.");
                        return null;
                    }
                }

                // minValue cannot be greater than maxValue
                if (minValue != null && maxValue != null && minValue > maxValue) {
                    JOptionPane.showMessageDialog(frame,
                            "Minimum value cannot be greater than maximum value.");
                    return null;
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                        "Invalid number format. Please enter valid integers.");
                return null;
            }

            return questionData;
        }

        return null;
    }

    /**
     * Shows a dialog for creating a multiple choice question.
     * 
     * @return Map containing the multiple choice question data
     */
    private Map<String, Object> showMultipleChoiceDialog() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel statementPanel = new JPanel(new BorderLayout(5, 5));
        JLabel lblStatement = new JLabel("Question statement:");
        JTextArea txtStatement = new JTextArea(3, 30);
        txtStatement.setLineWrap(true);
        txtStatement.setWrapStyleWord(true);
        JScrollPane scrollStatement = new JScrollPane(txtStatement);

        statementPanel.add(lblStatement, BorderLayout.NORTH);
        statementPanel.add(scrollStatement, BorderLayout.CENTER);

        JPanel optionsPanel = new JPanel(new BorderLayout(5, 5));
        JLabel lblOptions = new JLabel("Options (add at least 2):");

        DefaultListModel<String> optionsListModel = new DefaultListModel<>();
        JList<String> optionsList = new JList<>(optionsListModel);
        JScrollPane listScrollPane = new JScrollPane(optionsList);
        listScrollPane.setPreferredSize(new Dimension(300, 150));

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        JButton btnAddOption = new JButton("Add Option");
        JButton btnRemoveOption = new JButton("Remove Selected");

        buttonsPanel.add(btnAddOption);
        buttonsPanel.add(btnRemoveOption);

        optionsPanel.add(lblOptions, BorderLayout.NORTH);
        optionsPanel.add(listScrollPane, BorderLayout.CENTER);
        optionsPanel.add(buttonsPanel, BorderLayout.SOUTH);

        JPanel selectionsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JLabel lblMinSelections = new JLabel("Minimum selections:");
        JTextField txtMinSelections = new JTextField("1");
        JLabel lblMaxSelections = new JLabel("Maximum selections:");
        JTextField txtMaxSelections = new JTextField("1");

        selectionsPanel.add(lblMinSelections);
        selectionsPanel.add(txtMinSelections);
        selectionsPanel.add(lblMaxSelections);
        selectionsPanel.add(txtMaxSelections);

        JCheckBox chkOptional = new JCheckBox("Optional question");

        btnAddOption.addActionListener(e -> {
            String option = JOptionPane.showInputDialog(
                    panel,
                    "Enter option text:",
                    "Add Option",
                    JOptionPane.PLAIN_MESSAGE);
            if (option != null && !option.trim().isEmpty()) {
                optionsListModel.addElement(option.trim());
            }
        });

        btnRemoveOption.addActionListener(e -> {
            int selectedIndex = optionsList.getSelectedIndex();
            if (selectedIndex != -1) {
                optionsListModel.remove(selectedIndex);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        mainPanel.add(statementPanel, BorderLayout.NORTH);

        // Center panel with options and configurations
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(optionsPanel, BorderLayout.CENTER);
        centerPanel.add(selectionsPanel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(chkOptional, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(
                frame,
                mainPanel,
                "Create Multiple Choice Question",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String statement = txtStatement.getText().trim();
            boolean optional = chkOptional.isSelected();

            if (statement.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Statement cannot be empty!");
                return null;
            }

            if (optionsListModel.size() < 2) {
                JOptionPane.showMessageDialog(frame, "At least 2 options are required!");
                return null;
            }

            ArrayList<String> options = new ArrayList<>();
            for (int i = 0; i < optionsListModel.size(); i++) {
                options.add(optionsListModel.getElementAt(i));
            }

            int minSelections = 1;
            int maxSelections = 1;
            try {
                minSelections = Integer.parseInt(txtMinSelections.getText().trim());
                maxSelections = Integer.parseInt(txtMaxSelections.getText().trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Invalid number for selections. Using default (1).");

            }

            if (minSelections < 1) {
                minSelections = 1;
            }
            if (maxSelections > options.size()) {
                maxSelections = options.size();
            }
            if (minSelections > maxSelections) {
                minSelections = maxSelections;
            }

            Map<String, Object> questionData = new HashMap<>();
            questionData.put("type", "MULTIPLE_CHOICE");
            questionData.put("statement", statement);
            questionData.put("optional", optional);
            questionData.put("options", options);
            questionData.put("minSelections", minSelections);
            questionData.put("maxSelections", maxSelections);

            return questionData;
        }

        return null;
    }

    /**
     * Shows a dialog for creating an ordered single choice question.
     * 
     * @return Map containing the ordered single choice question data
     */
    private Map<String, Object> showOrderedSingleDialog() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel statementPanel = new JPanel(new BorderLayout(5, 5));
        JLabel lblStatement = new JLabel("Question statement:");
        JTextArea txtStatement = new JTextArea(3, 30);
        txtStatement.setLineWrap(true);
        txtStatement.setWrapStyleWord(true);
        JScrollPane scrollStatement = new JScrollPane(txtStatement);

        statementPanel.add(lblStatement, BorderLayout.NORTH);
        statementPanel.add(scrollStatement, BorderLayout.CENTER);

        JPanel optionsPanel = new JPanel(new BorderLayout(5, 5));
        JLabel lblOptions = new JLabel("Options (add at least 2, they will be shown in this order):");

        DefaultListModel<String> optionsListModel = new DefaultListModel<>();
        JList<String> optionsList = new JList<>(optionsListModel);
        optionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(optionsList);
        listScrollPane.setPreferredSize(new Dimension(300, 150));

        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JButton btnAddOption = new JButton("Add Option");
        JButton btnRemoveOption = new JButton("Remove");
        JButton btnMoveUp = new JButton("Move Up");
        JButton btnMoveDown = new JButton("Move Down");

        buttonsPanel.add(btnAddOption);
        buttonsPanel.add(btnRemoveOption);
        buttonsPanel.add(btnMoveUp);
        buttonsPanel.add(btnMoveDown);

        optionsPanel.add(lblOptions, BorderLayout.NORTH);
        optionsPanel.add(listScrollPane, BorderLayout.CENTER);
        optionsPanel.add(buttonsPanel, BorderLayout.SOUTH);

        JCheckBox chkOptional = new JCheckBox("Optional question");

        btnAddOption.addActionListener(e -> {
            String option = JOptionPane.showInputDialog(
                    panel,
                    "Enter option text:",
                    "Add Option",
                    JOptionPane.PLAIN_MESSAGE);
            if (option != null && !option.trim().isEmpty()) {
                optionsListModel.addElement(option.trim());
            }
        });

        btnRemoveOption.addActionListener(e -> {
            int selectedIndex = optionsList.getSelectedIndex();
            if (selectedIndex != -1) {
                optionsListModel.remove(selectedIndex);
            }
        });

        btnMoveUp.addActionListener(e -> {
            int selectedIndex = optionsList.getSelectedIndex();
            if (selectedIndex > 0) {
                String item = optionsListModel.getElementAt(selectedIndex);
                optionsListModel.remove(selectedIndex);
                optionsListModel.add(selectedIndex - 1, item);
                optionsList.setSelectedIndex(selectedIndex - 1);
            }
        });

        btnMoveDown.addActionListener(e -> {
            int selectedIndex = optionsList.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < optionsListModel.size() - 1) {
                String item = optionsListModel.getElementAt(selectedIndex);
                optionsListModel.remove(selectedIndex);
                optionsListModel.add(selectedIndex + 1, item);
                optionsList.setSelectedIndex(selectedIndex + 1);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(statementPanel, BorderLayout.NORTH);
        mainPanel.add(optionsPanel, BorderLayout.CENTER);
        mainPanel.add(chkOptional, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(
                frame,
                mainPanel,
                "Create Ordered Single Question",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String statement = txtStatement.getText().trim();
            boolean optional = chkOptional.isSelected();

            if (statement.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Statement cannot be empty!");
                return null;
            }

            if (optionsListModel.size() < 2) {
                JOptionPane.showMessageDialog(frame, "At least 2 options are required!");
                return null;
            }

            ArrayList<String> options = new ArrayList<>();
            for (int i = 0; i < optionsListModel.size(); i++) {
                options.add(optionsListModel.getElementAt(i));
            }

            Map<String, Object> questionData = new HashMap<>();
            questionData.put("type", "ORDERED_SINGLE");
            questionData.put("statement", statement);
            questionData.put("optional", optional);
            questionData.put("options", options);

            return questionData;
        }
        return null;
    }

    /**
     * Creates a visual panel for displaying a question.
     * 
     * @param questionData The question data to display
     * @param number       The question number
     * @param readOnly     Whether the question should be read-only
     * @return The configured question panel
     */
    private JPanel createQuestionPanel(Map<String, Object> questionData, int number, boolean readOnly) {
        // Calculate real number based on visible questions
        int visibleNumber = 1;
        for (int i = 0; i < questionsData.indexOf(questionData); i++) {
            if (!"delete".equals(questionsData.get(i).get("status"))) {
                visibleNumber++;
            }
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(readOnly ? new Color(230, 230, 230) : new Color(230, 235, 240), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        // Header
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(Color.WHITE);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        String type = (String) questionData.get("type");
        String typeDisplay = "";
        switch (type) {
            case "FREE":
                typeDisplay = "Free Text";
                break;
            case "NUMERICAL":
                typeDisplay = "Numeric";
                break;
            case "MULTIPLE_CHOICE":
                typeDisplay = "Multiple Choice";
                break;
            case "ORDERED_SINGLE":
                typeDisplay = "Ordered Single Choice";
                break;
        }

        JLabel lblQuestionNumber = new JLabel("Q" + visibleNumber + " - " + typeDisplay);
        lblQuestionNumber.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblQuestionNumber.setForeground(new Color(70, 130, 220));

        panelHeader.add(lblQuestionNumber, BorderLayout.WEST);

        // Action buttons panel
        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actionButtonsPanel.setBackground(Color.WHITE);

        // Edit button (only if not read-only)
        if (!readOnly) {
            JButton btnEdit = createActionButton("Edit", new Color(70, 130, 220));
            btnEdit.addActionListener(e -> editQuestion(panel, questionData));
            actionButtonsPanel.add(btnEdit);
        }

        // Delete button (only if not read-only)
        if (!readOnly) {
            JButton btnDelete = createActionButton("Delete", Color.RED);
            btnDelete.addActionListener(e -> deleteQuestion(panel));
            actionButtonsPanel.add(btnDelete);
        }

        panelHeader.add(actionButtonsPanel, BorderLayout.EAST);

        String statement = (String) questionData.get("statement");
        JLabel lblStatement = new JLabel("<html>" + statement + "</html>");
        lblStatement.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblStatement.setForeground(new Color(60, 70, 90));
        lblStatement.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelInfo.setBackground(Color.WHITE);

        if ((Boolean) questionData.get("optional")) {
            JLabel lblOptional = new JLabel("Optional");
            lblOptional.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblOptional.setForeground(Color.GRAY);
            panelInfo.add(lblOptional);
        }

        switch (type) {
            case "FREE":
                Integer maxLength = (Integer) questionData.get("maxLength");
                if (maxLength != null) {
                    JLabel lblMaxLength = new JLabel("Max length: " + maxLength + " chars");
                    lblMaxLength.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    lblMaxLength.setForeground(new Color(120, 130, 150));
                    panelInfo.add(lblMaxLength);
                }
                break;
            case "NUMERICAL":
                Integer minValue = (Integer) questionData.get("minValue");
                Integer maxValue = (Integer) questionData.get("maxValue");
                String rangeText = "Range: ";
                if (minValue != null && maxValue != null) {
                    rangeText += minValue + " to " + maxValue;
                } else if (minValue != null) {
                    rangeText += "min " + minValue;
                } else if (maxValue != null) {
                    rangeText += "max " + maxValue;
                } else {
                    rangeText += "any value";
                }
                JLabel lblRange = new JLabel(rangeText);
                lblRange.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lblRange.setForeground(new Color(120, 130, 150));
                panelInfo.add(lblRange);
                break;
            case "MULTIPLE_CHOICE":
                Integer minSelections = (Integer) questionData.get("minSelections");
                Integer maxSelections = (Integer) questionData.get("maxSelections");
                String selectionText = "Selections: " + minSelections + " to " + maxSelections;
                JLabel lblSelections = new JLabel(selectionText);
                lblSelections.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lblSelections.setForeground(new Color(120, 130, 150));
                panelInfo.add(lblSelections);
                break;
        }

        if (readOnly) {
            JLabel lblReadOnly = new JLabel("Read-only");
            lblReadOnly.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblReadOnly.setForeground(Color.ORANGE.darker());
            panelInfo.add(lblReadOnly);
        }

        panel.add(panelHeader, BorderLayout.NORTH);
        panel.add(lblStatement, BorderLayout.CENTER);
        panel.add(panelInfo, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Edits an existing question.
     * 
     * @param questionPanel The panel containing the question to edit
     * @param questionData  The question data to edit
     */
    private void editQuestion(JPanel questionPanel, Map<String, Object> questionData) {
        int index = questionPanels.indexOf(questionPanel);
        if (index >= 0) {
            // Show edit dialog based on question type
            Map<String, Object> editedData = showEditQuestionDialog(questionData);

            if (editedData != null && !editedData.isEmpty()) {
                // Update question data
                Map<String, Object> originalData = questionsData.get(index);

                // Preserve original ID and status if it's an existing question
                if (originalData.containsKey("questionId")) {
                    editedData.put("questionId", originalData.get("questionId"));
                }

                // Update status
                if (originalData.containsKey("existing") && (Boolean) originalData.get("existing")) {
                    editedData.put("existing", true);
                    editedData.put("status", "modified");
                } else {
                    editedData.put("existing", false);
                    editedData.put("status", "new");
                }

                // Replace in data list
                questionsData.set(index, editedData);

                // Calculate correct number
                int visibleNumber = 1;
                for (int i = 0; i < index; i++) {
                    if (!"delete".equals(questionsData.get(i).get("status"))) {
                        visibleNumber++;
                    }
                }

                // Update visual panel
                JPanel newQuestionPanel = createQuestionPanel(editedData, visibleNumber, false);
                questionPanels.set(index, newQuestionPanel);

                // Replace in questions panel
                int panelIndex = findPanelIndex(questionPanel);
                if (panelIndex >= 0) {
                    panelQuestions.remove(panelIndex);
                    panelQuestions.add(newQuestionPanel, panelIndex);
                    panelQuestions.add(Box.createRigidArea(new Dimension(0, 15)), panelIndex + 1);
                }

                panelQuestions.revalidate();
                panelQuestions.repaint();
                unsavedChanges = true;

                // Update numbers of all questions
                updateQuestionNumbersInPanels();
            }
        }
    }

    /**
     * Shows the appropriate question edit dialog based on type.
     * 
     * @param originalData The original question data
     * @return Map containing the edited question data
     */
    private Map<String, Object> showEditQuestionDialog(Map<String, Object> originalData) {
        String type = (String) originalData.get("type");

        switch (type) {
            case "FREE":
                return showEditFreeQuestionDialog(originalData);
            case "NUMERICAL":
                return showEditNumericQuestionDialog(originalData);
            case "MULTIPLE_CHOICE":
                return showEditMultipleChoiceDialog(originalData);
            case "ORDERED_SINGLE":
                return showEditOrderedSingleDialog(originalData);
            default:
                return null;
        }
    }

    /**
     * Shows a dialog for editing a free text question.
     * 
     * @param originalData The original question data
     * @return Map containing the edited free text question data
     */
    private Map<String, Object> showEditFreeQuestionDialog(Map<String, Object> originalData) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblStatement = new JLabel("Question statement:");
        JTextArea txtStatement = new JTextArea(3, 30);
        txtStatement.setLineWrap(true);
        txtStatement.setWrapStyleWord(true);
        txtStatement.setText((String) originalData.get("statement"));
        JScrollPane scrollStatement = new JScrollPane(txtStatement);

        JLabel lblMaxLength = new JLabel("Maximum length (characters):");
        JTextField txtMaxLength = new JTextField();
        txtMaxLength.setText(originalData.get("maxLength").toString());

        JCheckBox chkOptional = new JCheckBox("Optional question");
        chkOptional.setSelected((Boolean) originalData.get("optional"));

        panel.add(lblStatement, BorderLayout.NORTH);
        panel.add(scrollStatement, BorderLayout.CENTER);

        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        optionsPanel.add(lblMaxLength);
        optionsPanel.add(txtMaxLength);
        optionsPanel.add(chkOptional);
        optionsPanel.add(new JLabel());

        panel.add(optionsPanel, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(frame, panel,
                "Edit Free Text Question", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String statement = txtStatement.getText().trim();
            if (statement.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Question statement cannot be empty!");
                return null;
            }

            Map<String, Object> questionData = new java.util.HashMap<>();
            questionData.put("type", "FREE");
            questionData.put("statement", statement);
            questionData.put("optional", chkOptional.isSelected());

            try {
                int maxLength = Integer.parseInt(txtMaxLength.getText());
                questionData.put("maxLength", maxLength);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                questionData.put("maxLength", 500);
            }

            return questionData;
        }

        return null;
    }

    /**
     * Shows a dialog for editing a numeric question.
     * 
     * @param originalData The original question data
     * @return Map containing the edited numeric question data
     */
    private Map<String, Object> showEditNumericQuestionDialog(Map<String, Object> originalData) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblStatement = new JLabel("Question statement:");
        JTextArea txtStatement = new JTextArea(3, 30);
        txtStatement.setLineWrap(true);
        txtStatement.setWrapStyleWord(true);
        txtStatement.setText((String) originalData.get("statement"));

        JLabel lblMin = new JLabel("Minimum value:");
        JTextField txtMin = new JTextField();
        if (originalData.get("minValue") != null) {
            txtMin.setText(originalData.get("minValue").toString());
        }

        JLabel lblMax = new JLabel("Maximum value:");
        JTextField txtMax = new JTextField();
        if (originalData.get("maxValue") != null) {
            txtMax.setText(originalData.get("maxValue").toString());
        }

        JCheckBox chkOptional = new JCheckBox("Optional question");
        chkOptional.setSelected((Boolean) originalData.get("optional"));

        txtMin.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String currentText = txtMin.getText();

                if (c == '-') {
                    if (!currentText.isEmpty() && txtMin.getCaretPosition() != 0) {
                        e.consume();
                    }
                } else if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE &&
                        c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });

        txtMax.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String currentText = txtMax.getText();

                if (c == '-') {
                    if (!currentText.isEmpty() && txtMax.getCaretPosition() != 0) {
                        e.consume();
                    }
                } else if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE &&
                        c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });

        panel.add(lblStatement, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtStatement), BorderLayout.CENTER);

        JPanel optionsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        optionsPanel.add(lblMin);
        optionsPanel.add(txtMin);
        optionsPanel.add(lblMax);
        optionsPanel.add(txtMax);
        optionsPanel.add(chkOptional);
        optionsPanel.add(new JLabel());

        panel.add(optionsPanel, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(frame, panel,
                "Edit Numeric Question", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String statement = txtStatement.getText().trim();
            if (statement.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Question statement cannot be empty!");
                return null;
            }

            Map<String, Object> questionData = new java.util.HashMap<>();
            questionData.put("type", "NUMERICAL");
            questionData.put("statement", statement);
            questionData.put("optional", chkOptional.isSelected());

            try {
                Integer minValue = null;
                Integer maxValue = null;

                if (!txtMin.getText().trim().isEmpty()) {
                    String minText = txtMin.getText().trim();
                    if (minText.matches("-?\\d+")) {
                        minValue = Integer.parseInt(minText);
                        questionData.put("minValue", minValue);
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Invalid minimum value. Please enter a valid integer.");
                        return null;
                    }
                }

                if (!txtMax.getText().trim().isEmpty()) {
                    String maxText = txtMax.getText().trim();
                    if (maxText.matches("-?\\d+")) {
                        maxValue = Integer.parseInt(maxText);
                        questionData.put("maxValue", maxValue);
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Invalid maximum value. Please enter a valid integer.");
                        return null;
                    }
                }

                if (minValue != null && maxValue != null && minValue > maxValue) {
                    JOptionPane.showMessageDialog(frame,
                            "Minimum value cannot be greater than maximum value.");
                    return null;
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                        "Invalid number format. Please enter valid integers.");
                return null;
            }

            return questionData;
        }

        return null;
    }

    /**
     * Shows a dialog for editing a multiple choice question.
     * 
     * @param originalData The original question data
     * @return Map containing the edited multiple choice question data
     */
    private Map<String, Object> showEditMultipleChoiceDialog(Map<String, Object> originalData) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel statementPanel = new JPanel(new BorderLayout(5, 5));
        JLabel lblStatement = new JLabel("Question statement:");
        JTextArea txtStatement = new JTextArea(3, 30);
        txtStatement.setLineWrap(true);
        txtStatement.setWrapStyleWord(true);
        txtStatement.setText((String) originalData.get("statement"));
        JScrollPane scrollStatement = new JScrollPane(txtStatement);

        statementPanel.add(lblStatement, BorderLayout.NORTH);
        statementPanel.add(scrollStatement, BorderLayout.CENTER);

        JPanel optionsPanel = new JPanel(new BorderLayout(5, 5));
        JLabel lblOptions = new JLabel("Options (add at least 2):");

        DefaultListModel<String> optionsListModel = new DefaultListModel<>();
        @SuppressWarnings("unchecked")
        ArrayList<String> originalOptions = (ArrayList<String>) originalData.get("options");
        for (String option : originalOptions) {
            optionsListModel.addElement(option);
        }

        JList<String> optionsList = new JList<>(optionsListModel);
        JScrollPane listScrollPane = new JScrollPane(optionsList);
        listScrollPane.setPreferredSize(new Dimension(300, 150));

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        JButton btnAddOption = new JButton("Add Option");
        JButton btnRemoveOption = new JButton("Remove Selected");

        buttonsPanel.add(btnAddOption);
        buttonsPanel.add(btnRemoveOption);

        optionsPanel.add(lblOptions, BorderLayout.NORTH);
        optionsPanel.add(listScrollPane, BorderLayout.CENTER);
        optionsPanel.add(buttonsPanel, BorderLayout.SOUTH);

        JPanel selectionsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JLabel lblMinSelections = new JLabel("Minimum selections:");
        JTextField txtMinSelections = new JTextField(originalData.get("minSelections").toString());
        JLabel lblMaxSelections = new JLabel("Maximum selections:");
        JTextField txtMaxSelections = new JTextField(originalData.get("maxSelections").toString());

        selectionsPanel.add(lblMinSelections);
        selectionsPanel.add(txtMinSelections);
        selectionsPanel.add(lblMaxSelections);
        selectionsPanel.add(txtMaxSelections);

        JCheckBox chkOptional = new JCheckBox("Optional question");
        chkOptional.setSelected((Boolean) originalData.get("optional"));

        btnAddOption.addActionListener(e -> {
            String option = JOptionPane.showInputDialog(
                    panel,
                    "Enter option text:",
                    "Add Option",
                    JOptionPane.PLAIN_MESSAGE);
            if (option != null && !option.trim().isEmpty()) {
                optionsListModel.addElement(option.trim());
            }
        });

        btnRemoveOption.addActionListener(e -> {
            int selectedIndex = optionsList.getSelectedIndex();
            if (selectedIndex != -1) {
                optionsListModel.remove(selectedIndex);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        mainPanel.add(statementPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(optionsPanel, BorderLayout.CENTER);
        centerPanel.add(selectionsPanel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(chkOptional, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(
                frame,
                mainPanel,
                "Edit Multiple Choice Question",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String statement = txtStatement.getText().trim();
            boolean optional = chkOptional.isSelected();

            if (statement.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Statement cannot be empty!");
                return null;
            }

            if (optionsListModel.size() < 2) {
                JOptionPane.showMessageDialog(frame, "At least 2 options are required!");
                return null;
            }

            ArrayList<String> options = new ArrayList<>();
            for (int i = 0; i < optionsListModel.size(); i++) {
                options.add(optionsListModel.getElementAt(i));
            }

            int minSelections = 1;
            int maxSelections = 1;
            try {
                minSelections = Integer.parseInt(txtMinSelections.getText().trim());
                maxSelections = Integer.parseInt(txtMaxSelections.getText().trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Invalid number for selections. Using default (1).");
            }

            if (minSelections < 1) {
                minSelections = 1;
            }
            if (maxSelections > options.size()) {
                maxSelections = options.size();
            }
            if (minSelections > maxSelections) {
                minSelections = maxSelections;
            }

            Map<String, Object> questionData = new HashMap<>();
            questionData.put("type", "MULTIPLE_CHOICE");
            questionData.put("statement", statement);
            questionData.put("optional", optional);
            questionData.put("options", options);
            questionData.put("minSelections", minSelections);
            questionData.put("maxSelections", maxSelections);

            return questionData;
        }

        return null;
    }

    /**
     * Shows a dialog for editing an ordered single choice question.
     * 
     * @param originalData The original question data
     * @return Map containing the edited ordered single choice question data
     */
    private Map<String, Object> showEditOrderedSingleDialog(Map<String, Object> originalData) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel statementPanel = new JPanel(new BorderLayout(5, 5));
        JLabel lblStatement = new JLabel("Question statement:");
        JTextArea txtStatement = new JTextArea(3, 30);
        txtStatement.setLineWrap(true);
        txtStatement.setWrapStyleWord(true);
        txtStatement.setText((String) originalData.get("statement"));
        JScrollPane scrollStatement = new JScrollPane(txtStatement);

        statementPanel.add(lblStatement, BorderLayout.NORTH);
        statementPanel.add(scrollStatement, BorderLayout.CENTER);

        JPanel optionsPanel = new JPanel(new BorderLayout(5, 5));
        JLabel lblOptions = new JLabel("Options (add at least 2, they will be shown in this order):");

        DefaultListModel<String> optionsListModel = new DefaultListModel<>();
        @SuppressWarnings("unchecked")
        ArrayList<String> originalOptions = (ArrayList<String>) originalData.get("options");
        for (String option : originalOptions) {
            optionsListModel.addElement(option);
        }

        JList<String> optionsList = new JList<>(optionsListModel);
        optionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(optionsList);
        listScrollPane.setPreferredSize(new Dimension(300, 150));

        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JButton btnAddOption = new JButton("Add Option");
        JButton btnRemoveOption = new JButton("Remove");
        JButton btnMoveUp = new JButton("Move Up");
        JButton btnMoveDown = new JButton("Move Down");

        buttonsPanel.add(btnAddOption);
        buttonsPanel.add(btnRemoveOption);
        buttonsPanel.add(btnMoveUp);
        buttonsPanel.add(btnMoveDown);

        optionsPanel.add(lblOptions, BorderLayout.NORTH);
        optionsPanel.add(listScrollPane, BorderLayout.CENTER);
        optionsPanel.add(buttonsPanel, BorderLayout.SOUTH);

        JCheckBox chkOptional = new JCheckBox("Optional question");
        chkOptional.setSelected((Boolean) originalData.get("optional"));

        btnAddOption.addActionListener(e -> {
            String option = JOptionPane.showInputDialog(
                    panel,
                    "Enter option text:",
                    "Add Option",
                    JOptionPane.PLAIN_MESSAGE);
            if (option != null && !option.trim().isEmpty()) {
                optionsListModel.addElement(option.trim());
            }
        });

        btnRemoveOption.addActionListener(e -> {
            int selectedIndex = optionsList.getSelectedIndex();
            if (selectedIndex != -1) {
                optionsListModel.remove(selectedIndex);
            }
        });

        btnMoveUp.addActionListener(e -> {
            int selectedIndex = optionsList.getSelectedIndex();
            if (selectedIndex > 0) {
                String item = optionsListModel.getElementAt(selectedIndex);
                optionsListModel.remove(selectedIndex);
                optionsListModel.add(selectedIndex - 1, item);
                optionsList.setSelectedIndex(selectedIndex - 1);
            }
        });

        btnMoveDown.addActionListener(e -> {
            int selectedIndex = optionsList.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < optionsListModel.size() - 1) {
                String item = optionsListModel.getElementAt(selectedIndex);
                optionsListModel.remove(selectedIndex);
                optionsListModel.add(selectedIndex + 1, item);
                optionsList.setSelectedIndex(selectedIndex + 1);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(statementPanel, BorderLayout.NORTH);
        mainPanel.add(optionsPanel, BorderLayout.CENTER);
        mainPanel.add(chkOptional, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(
                frame,
                mainPanel,
                "Edit Ordered Single Question",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String statement = txtStatement.getText().trim();
            boolean optional = chkOptional.isSelected();

            if (statement.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Statement cannot be empty!");
                return null;
            }

            if (optionsListModel.size() < 2) {
                JOptionPane.showMessageDialog(frame, "At least 2 options are required!");
                return null;
            }

            ArrayList<String> options = new ArrayList<>();
            for (int i = 0; i < optionsListModel.size(); i++) {
                options.add(optionsListModel.getElementAt(i));
            }

            Map<String, Object> questionData = new HashMap<>();
            questionData.put("type", "ORDERED_SINGLE");
            questionData.put("statement", statement);
            questionData.put("optional", optional);
            questionData.put("options", options);

            return questionData;
        }
        return null;
    }

    /**
     * Shows a dialog for reordering questions in the form.
     */
    private void showReorderDialog() {
        // Filter NOT deleted questions to show in the dialog
        List<Map<String, Object>> visibleQuestions = new ArrayList<>();
        List<JPanel> visiblePanels = new ArrayList<>();

        for (int i = 0; i < questionsData.size(); i++) {
            if (!"delete".equals(questionsData.get(i).get("status"))) {
                visibleQuestions.add(questionsData.get(i));
                visiblePanels.add(questionPanels.get(i));
            }
        }

        if (visibleQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No questions to reorder.");
            return;
        }

        JPanel reorderPanel = new JPanel(new BorderLayout(10, 10));
        reorderPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblInstructions = new JLabel(
                "<html>Drag questions to reorder them. The order will be saved when you click 'Save Changes'.<br>Deleted questions are not shown and will remain at their current position.</html>");
        lblInstructions.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reorderPanel.add(lblInstructions, BorderLayout.NORTH);

        // Create list model only with visible questions (not deleted)
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (int i = 0; i < visibleQuestions.size(); i++) {
            Map<String, Object> questionData = visibleQuestions.get(i);
            String type = (String) questionData.get("type");
            String typeDisplay = "";
            switch (type) {
                case "FREE":
                    typeDisplay = "Free Text";
                    break;
                case "NUMERICAL":
                    typeDisplay = "Numeric";
                    break;
                case "MULTIPLE_CHOICE":
                    typeDisplay = "Multiple Choice";
                    break;
                case "ORDERED_SINGLE":
                    typeDisplay = "Ordered Single Choice";
                    break;
            }
            String statement = (String) questionData.get("statement");
            if (statement.length() > 50) {
                statement = statement.substring(0, 47) + "...";
            }
            listModel.addElement("Q" + (i + 1) + " - " + typeDisplay + ": " + statement);
        }

        JList<String> questionList = new JList<>(listModel);
        questionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(questionList);
        listScrollPane.setPreferredSize(new Dimension(500, 300));
        reorderPanel.add(listScrollPane, BorderLayout.CENTER);

        // Button panel for moving questions
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        JButton btnMoveUp = new JButton("↑ Move Up");
        JButton btnMoveDown = new JButton("↓ Move Down");
        JButton btnReset = new JButton("Reset Order");
        JButton btnApply = new JButton("Apply Changes");

        buttonPanel.add(btnMoveUp);
        buttonPanel.add(btnMoveDown);
        buttonPanel.add(btnReset);
        buttonPanel.add(btnApply);
        reorderPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        btnMoveUp.addActionListener(e -> {
            int selectedIndex = questionList.getSelectedIndex();
            if (selectedIndex > 0) {
                String item = listModel.getElementAt(selectedIndex);
                listModel.remove(selectedIndex);
                listModel.add(selectedIndex - 1, item);
                questionList.setSelectedIndex(selectedIndex - 1);
                // Update numbers in list items
                updateQuestionNumbersInList(listModel);
            }
        });

        btnMoveDown.addActionListener(e -> {
            int selectedIndex = questionList.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < listModel.size() - 1) {
                String item = listModel.getElementAt(selectedIndex);
                listModel.remove(selectedIndex);
                listModel.add(selectedIndex + 1, item);
                questionList.setSelectedIndex(selectedIndex + 1);
                // Update numbers in list items
                updateQuestionNumbersInList(listModel);
            }
        });

        btnReset.addActionListener(e -> {
            // Restore original order
            listModel.removeAllElements();
            for (int i = 0; i < visibleQuestions.size(); i++) {
                Map<String, Object> questionData = visibleQuestions.get(i);
                String type = (String) questionData.get("type");
                String typeDisplay = "";
                switch (type) {
                    case "FREE":
                        typeDisplay = "Free Text";
                        break;
                    case "NUMERICAL":
                        typeDisplay = "Numeric";
                        break;
                    case "MULTIPLE_CHOICE":
                        typeDisplay = "Multiple Choice";
                        break;
                    case "ORDERED_SINGLE":
                        typeDisplay = "Ordered Single Choice";
                        break;
                }
                String statement = (String) questionData.get("statement");
                if (statement.length() > 50) {
                    statement = statement.substring(0, 47) + "...";
                }
                listModel.addElement("Q" + (i + 1) + " - " + typeDisplay + ": " + statement);
            }
        });

        JDialog reorderDialog = new JDialog(frame, "Reorder Questions", true);
        reorderDialog.setLayout(new BorderLayout());
        reorderDialog.add(reorderPanel, BorderLayout.CENTER);
        reorderDialog.setSize(600, 450);
        reorderDialog.setLocationRelativeTo(frame);

        btnApply.addActionListener(e -> {
            List<Map<String, Object>> deletedQuestions = new ArrayList<>();
            List<JPanel> deletedPanels = new ArrayList<>();
            List<Map<String, Object>> nonDeletedQuestions = new ArrayList<>();
            List<JPanel> nonDeletedPanels = new ArrayList<>();

            for (int i = 0; i < questionsData.size(); i++) {
                Map<String, Object> questionData = questionsData.get(i);
                JPanel panel = questionPanels.get(i);

                if ("delete".equals(questionData.get("status"))) {
                    deletedQuestions.add(questionData);
                    deletedPanels.add(panel);
                } else {
                    nonDeletedQuestions.add(questionData);
                    nonDeletedPanels.add(panel);
                }
            }

            Map<String, Integer> questionMap = new HashMap<>();
            for (int i = 0; i < nonDeletedQuestions.size(); i++) {
                Map<String, Object> questionData = nonDeletedQuestions.get(i);
                String type = (String) questionData.get("type");
                String typeDisplay = "";
                switch (type) {
                    case "FREE":
                        typeDisplay = "Free Text";
                        break;
                    case "NUMERICAL":
                        typeDisplay = "Numeric";
                        break;
                    case "MULTIPLE_CHOICE":
                        typeDisplay = "Multiple Choice";
                        break;
                    case "ORDERED_SINGLE":
                        typeDisplay = "Ordered Single Choice";
                        break;
                }
                String statement = (String) questionData.get("statement");
                if (statement.length() > 50) {
                    statement = statement.substring(0, 47) + "...";
                }
                String key = typeDisplay + ": " + statement;
                questionMap.put(key, i);
            }

            List<Map<String, Object>> newNonDeletedQuestions = new ArrayList<>();
            List<JPanel> newNonDeletedPanels = new ArrayList<>();

            for (int i = 0; i < listModel.size(); i++) {
                String listItem = listModel.getElementAt(i);

                String description = listItem.substring(listItem.indexOf(" - ") + 3);

                // Find the original question using the map
                if (questionMap.containsKey(description)) {
                    int originalIndex = questionMap.get(description);
                    newNonDeletedQuestions.add(nonDeletedQuestions.get(originalIndex));
                    newNonDeletedPanels.add(nonDeletedPanels.get(originalIndex));
                }
            }

            List<Map<String, Object>> newQuestionsData = new ArrayList<>();
            List<JPanel> newQuestionPanels = new ArrayList<>();

            newQuestionsData.addAll(newNonDeletedQuestions);
            newQuestionPanels.addAll(newNonDeletedPanels);

            newQuestionsData.addAll(deletedQuestions);
            newQuestionPanels.addAll(deletedPanels);

            questionsData.clear();
            questionsData.addAll(newQuestionsData);

            questionPanels.clear();
            questionPanels.addAll(newQuestionPanels);

            updateQuestionNumbersInPanels();

            updateQuestionPanels();
            unsavedChanges = true;

            reorderDialog.dispose();
            JOptionPane.showMessageDialog(frame,
                    "Questions reordered successfully. Remember to save changes to apply the new order.");
        });

        reorderDialog.setVisible(true);
    }

    /**
     * Updates question numbers in all visible panels.
     */
    private void updateQuestionNumbersInPanels() {
        int visibleQuestionCount = 0;

        for (int i = 0; i < questionPanels.size(); i++) {
            JPanel panel = questionPanels.get(i);
            Map<String, Object> questionData = questionsData.get(i);

            // Skip questions marked for deletion
            if ("delete".equals(questionData.get("status"))) {
                continue;
            }

            visibleQuestionCount++;

            // Update question number label in panel
            Component[] components = panel.getComponents();
            if (components.length > 0 && components[0] instanceof JPanel) {
                JPanel headerPanel = (JPanel) components[0];
                Component[] headerComponents = headerPanel.getComponents();

                // Find the JLabel containing the question number
                for (Component comp : headerComponents) {
                    if (comp instanceof JLabel) {
                        JLabel label = (JLabel) comp;
                        String text = label.getText();

                        // Check if it's the question number label
                        if (text.startsWith("Q")) {
                            // Update the number while keeping the rest of the text
                            String type = (String) questionData.get("type");
                            String typeDisplay = "";
                            switch (type) {
                                case "FREE":
                                    typeDisplay = "Free Text";
                                    break;
                                case "NUMERICAL":
                                    typeDisplay = "Numeric";
                                    break;
                                case "MULTIPLE_CHOICE":
                                    typeDisplay = "Multiple Choice";
                                    break;
                                case "ORDERED_SINGLE":
                                    typeDisplay = "Ordered Single Choice";
                                    break;
                            }
                            label.setText("Q" + visibleQuestionCount + " - " + typeDisplay);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Updates question numbers in a list model.
     * 
     * @param listModel The list model to update
     */
    private void updateQuestionNumbersInList(DefaultListModel<String> listModel) {
        for (int i = 0; i < listModel.size(); i++) {
            String oldItem = listModel.getElementAt(i);
            // Extract description (everything after the first " - ")
            String description = oldItem.substring(oldItem.indexOf(" - ") + 3);
            // Create new item with updated number
            String newItem = "Q" + (i + 1) + " - " + description;
            listModel.set(i, newItem);
        }
    }

    /**
     * Deletes a question from the form.
     * 
     * @param questionPanel The panel containing the question to delete
     */
    private void deleteQuestion(JPanel questionPanel) {
        int index = questionPanels.indexOf(questionPanel);
        if (index >= 0) {
            int response = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to delete this question?",
                    "Delete Question",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                Map<String, Object> questionData = questionsData.get(index);
                Boolean isExisting = (Boolean) questionData.get("existing");

                if (isExisting != null && isExisting) {
                    questionData.put("status", "delete");
                    questionPanel.setVisible(false);
                } else {
                    questionsData.remove(index);
                    questionPanels.remove(index);
                }

                // Update the visual list
                updateQuestionPanels();
                updateQuestionCount();
                unsavedChanges = true;
            }
        }
    }

    /**
     * Updates the visual display of question panels.
     */
    private void updateQuestionPanels() {
        panelQuestions.removeAll();

        for (int i = 0; i < questionPanels.size(); i++) {
            JPanel panel = questionPanels.get(i);
            Map<String, Object> questionData = questionsData.get(i);

            if (!"delete".equals(questionData.get("status"))) {
                panelQuestions.add(panel);
                panelQuestions.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }

        panelQuestions.revalidate();
        panelQuestions.repaint();
    }

    /**
     * Finds the index of a panel in the questions panel.
     * 
     * @param panel The panel to find
     * @return The index of the panel, or -1 if not found
     */
    private int findPanelIndex(JPanel panel) {
        Component[] components = panelQuestions.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] == panel) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Updates the question count display.
     */
    private void updateQuestionCount() {
        int visibleCount = 0;
        for (Map<String, Object> questionData : questionsData) {
            if (!"delete".equals(questionData.get("status"))) {
                visibleCount++;
            }
        }
        lblQuestionCount.setText("Questions: " + visibleCount);
    }

    /**
     * Saves the form with all its questions to the database.
     */
    private void saveForm() {
        String formTitle = txtFormTitle.getText().trim();

        if (formTitle.isEmpty()) {
            showError("Error", "Form title cannot be empty.");
            return;
        }

        try {
            try {
                ctrlPresentation.modifyForm(formId, formTitle);

            } catch (Exception e) {
                e.printStackTrace();
                showError("Error updating form", e.getMessage());
                return;
            }

            // Counters for summary
            int newQuestionsAdded = 0;
            int questionsModified = 0;
            int questionsDeleted = 0;

            for (int i = questionsData.size() - 1; i >= 0; i--) {
                Map<String, Object> questionData = questionsData.get(i);
                if ("delete".equals(questionData.get("status"))) {
                    Integer questionId = (Integer) questionData.get("questionId");
                    if (questionId != null) {
                        try {
                            ctrlPresentation.deleteQuestion(formId, questionId);
                            questionsDeleted++;
                            // Remove from lists
                            questionsData.remove(i);
                            questionPanels.remove(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showError("Error deleting question",
                                    "Could not delete question ID " + questionId + ": " + e.getMessage());
                            return;
                        }
                    } else {
                        questionsData.remove(i);
                        questionPanels.remove(i);
                    }
                }
            }

            // Add new questions
            for (Map<String, Object> questionData : questionsData) {
                if ("new".equals(questionData.get("status"))) {
                    addQuestionToForm(formId, questionData);
                    newQuestionsAdded++;
                    questionData.put("status", "keep");
                }
            }

            // Modify existing questions
            for (Map<String, Object> questionData : questionsData) {
                if ("modified".equals(questionData.get("status"))) {
                    modifyQuestionInForm(formId, questionData);
                    questionsModified++;
                    // Change status after modifying
                    questionData.put("status", "keep");
                }
            }

            // Reorder questions
            List<Integer> currentOrderIds = new ArrayList<>();

            for (Map<String, Object> questionData : questionsData) {
                if (questionData.containsKey("questionId")) {
                    currentOrderIds.add((Integer) questionData.get("questionId"));
                } else {
                    showError("Error", "Cannot reorder: some questions don't have IDs.");
                    return;
                }
            }

            // Only reorder if there's more than 1 question
            if (currentOrderIds.size() > 1) {
                try {
                    ctrlPresentation.reorderFormQuestions(formId, currentOrderIds);
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Error reordering questions",
                            "Questions were saved but order might not be correct: " + e.getMessage());
                }
            }

            StringBuilder message = new StringBuilder("Form updated successfully!");

            if (newQuestionsAdded > 0) {
                message.append(" Added ").append(newQuestionsAdded).append(" new question(s).");
            }

            if (questionsModified > 0) {
                if (newQuestionsAdded > 0)
                    message.append(" ");
                message.append("Modified ").append(questionsModified).append(" question(s).");
            }

            if (questionsDeleted > 0) {
                if (newQuestionsAdded > 0 || questionsModified > 0)
                    message.append(" ");
                message.append("Deleted ").append(questionsDeleted).append(" question(s).");
            }

            JOptionPane.showMessageDialog(frame,
                    message.toString(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            unsavedChanges = false;
            returningToMainMenu = true;
            frame.dispose();

            if (mainMenuView != null) {
                mainMenuView.makeVisible();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error saving form", e.getMessage());
        }
    }

    /**
     * Modifies an existing question in the form.
     * 
     * @param formId       The ID of the form
     * @param questionData The updated question data
     */
    private void modifyQuestionInForm(Integer formId, Map<String, Object> questionData) {
        Integer questionId = (Integer) questionData.get("questionId");
        String type = (String) questionData.get("type");

        switch (type) {
            case "FREE":
                ctrlPresentation.modifyFreeQuestion(
                        formId,
                        questionId,
                        (String) questionData.get("statement"),
                        (Boolean) questionData.get("optional"),
                        (Integer) questionData.get("maxLength"));
                break;

            case "NUMERICAL":
                ctrlPresentation.modifyNumericalQuestion(
                        formId,
                        questionId,
                        (String) questionData.get("statement"),
                        (Boolean) questionData.get("optional"),
                        (Integer) questionData.get("minValue"),
                        (Integer) questionData.get("maxValue"));
                break;

            case "MULTIPLE_CHOICE":
                ArrayList<String> optionsMC = (ArrayList<String>) questionData.get("options");
                ctrlPresentation.modifyMultipleChoiceQuestion(
                        formId,
                        questionId,
                        (String) questionData.get("statement"),
                        (Boolean) questionData.get("optional"),
                        optionsMC,
                        (Integer) questionData.get("minSelections"),
                        (Integer) questionData.get("maxSelections"));
                break;

            case "ORDERED_SINGLE":
                ArrayList<String> optionsOS = (ArrayList<String>) questionData.get("options");
                ctrlPresentation.modifyOrderedSingleQuestion(
                        formId,
                        questionId,
                        (String) questionData.get("statement"),
                        (Boolean) questionData.get("optional"),
                        optionsOS);
                break;
        }
    }

    /**
     * Adds a new question to the form.
     * 
     * @param formId       The ID of the form
     * @param questionData The question data to add
     */
    private void addQuestionToForm(Integer formId, Map<String, Object> questionData) {
        String type = (String) questionData.get("type");
        Integer idQ = null;

        switch (type) {
            case "FREE":
                idQ = ctrlPresentation.addFreeQuestion(
                        formId,
                        (String) questionData.get("statement"),
                        (Boolean) questionData.get("optional"),
                        (Integer) questionData.get("maxLength"));
                break;

            case "NUMERICAL":
                idQ = ctrlPresentation.addNumericalQuestion(
                        formId,
                        (String) questionData.get("statement"),
                        (Boolean) questionData.get("optional"),
                        (Integer) questionData.get("minValue"),
                        (Integer) questionData.get("maxValue"));
                break;

            case "MULTIPLE_CHOICE":
                ArrayList<String> optionsMC = (ArrayList<String>) questionData.get("options");
                idQ = ctrlPresentation.addMultipleChoiceQuestion(
                        formId,
                        (String) questionData.get("statement"),
                        (Boolean) questionData.get("optional"),
                        optionsMC,
                        (Integer) questionData.get("minSelections"),
                        (Integer) questionData.get("maxSelections"));
                break;

            case "ORDERED_SINGLE":
                ArrayList<String> optionsOS = (ArrayList<String>) questionData.get("options");
                idQ = ctrlPresentation.addOrderedSingleQuestion(
                        formId,
                        (String) questionData.get("statement"),
                        (Boolean) questionData.get("optional"),
                        optionsOS);
                break;

            default:
                return;
        }

        // Ensure an ID was obtained
        if (idQ != null) {
            // Save the generated ID for future references
            questionData.put("questionId", idQ);
        } else {
            throw new RuntimeException("Failed to get ID for new question");
        }
    }

    /**
     * Confirms exit and checks for unsaved changes.
     */
    private void confirmExit() {
        if (unsavedChanges) {
            int response = JOptionPane.showConfirmDialog(frame,
                    "You have unsaved changes. Are you sure you want to exit?",
                    "Unsaved Changes",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                returningToMainMenu = true;
                frame.dispose();
                if (mainMenuView != null) {
                    mainMenuView.makeVisible();
                }
            }
        } else {
            returningToMainMenu = true;
            frame.dispose();
            if (mainMenuView != null) {
                mainMenuView.makeVisible();
            }
        }
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
     * Creates an action button for question operations.
     * 
     * @param text  The button text
     * @param color The button color
     * @return The configured action button
     */
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(Color.WHITE);
        button.setForeground(color);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(255, 240, 240));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });

        return button;
    }

    /**
     * Displays an error message dialog.
     * 
     * @param title   The error dialog title
     * @param message The error message
     */
    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Makes the form editing window visible and maximizes it.
     */
    public void makeVisible() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize);
        frame.setLocationRelativeTo(null);
        frame.validate();
        frame.repaint();
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}
