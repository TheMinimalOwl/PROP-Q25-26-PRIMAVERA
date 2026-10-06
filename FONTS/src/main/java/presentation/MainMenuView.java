package presentation;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Main menu view for the Form Builder application.
 * Provides navigation to different sections like dashboard, my forms,
 * forms to answer, and settings.
 */
public class MainMenuView {
    private JFrame frame;
    private JPanel sideMenuPanel;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private String username;
    private Integer userId;
    private CtrlPresentation presentationController;

    private Font fontAwesome;
    private final Map<String, Character> iconMap = new HashMap<>();

    /**
     * Constructor for MainMenuView with user ID and username.
     * 
     * @param userId           The unique identifier of the user
     * @param username         The display name of the user
     * @param ctrlPresentation The presentation controller
     */
    public MainMenuView(Integer userId, String username, CtrlPresentation ctrlPresentation) {
        this.userId = userId;
        this.username = username;
        presentationController = ctrlPresentation;

        frame = new JFrame("Form Builder - Main Menu");

        initializeIcons();
        loadFontAwesome();
        configureWindow();
        createComponents();
    }

    /**
     * Initializes the icon map with FontAwesome character codes.
     */
    private void initializeIcons() {
        iconMap.put("dashboard", '\uf080');
        iconMap.put("forms", '\uf15c');
        iconMap.put("contestables", '\uf14a');
        iconMap.put("settings", '\uf085');
        iconMap.put("help", '\uf059');
        iconMap.put("logout", '\uf08b');
        iconMap.put("user", '\uf007');
        iconMap.put("guest", '\uf007');
        iconMap.put("create", '\uf067');
        iconMap.put("view", '\uf06e');
        iconMap.put("edit", '\uf044');
        iconMap.put("answer", '\uf14a');
        iconMap.put("analyze", '\uf080');
        iconMap.put("eye", '\uf06e');
        iconMap.put("eye-slash", '\uf070');
        iconMap.put("delete", '\uf1f8');
    }

    /**
     * Loads the FontAwesome font from the resources folder.
     */
    private void loadFontAwesome() {
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
     * Gets the HTML-formatted icon with specified size.
     * 
     * @param iconName The name of the icon to retrieve
     * @param size     The font size for the icon
     * @return HTML string containing the icon
     */
    private String getIcon(String iconName, float size) {
        Character iconChar = iconMap.get(iconName);
        if (iconChar != null && fontAwesome != null) {
            return "<html><span style='font-family: \"" +
                    fontAwesome.getFontName() + "\"; font-size: " +
                    size + "px;'>" + iconChar + "</span></html>";
        }
        return getFallbackIcon(iconName);
    }

    /**
     * Provides fallback text icons when FontAwesome is not available.
     * 
     * @param iconName The name of the icon to get fallback for
     * @return Text representation of the icon
     */
    private String getFallbackIcon(String iconName) {
        switch (iconName) {
            case "dashboard":
                return "📊";
            case "forms":
                return "";
            case "contestables":
                return "";
            case "settings":
                return "⚙";
            case "help":
                return "?";
            case "logout":
                return "←";
            case "user":
                return "👤";
            case "guest":
                return "👤";
            case "create":
                return "+";
            case "view":
                return "👁";
            case "edit":
                return "";
            case "answer":
                return "";
            case "analyze":
                return "📊";
            case "eye":
                return "👁";
            case "eye-slash":
                return "🚫";
            case "delete":
                return "🗑";
            default:
                return "•";
        }
    }

    /**
     * Configures the main window properties.
     */
    private void configureWindow() {
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1024, 768));

        try {
            Path iconPath = Path.of("src")
                    .resolve("main")
                    .resolve("resources")
                    .resolve("icons")
                    .resolve("IconoForms2.png");

            File iconFile = iconPath.toFile();
            ImageIcon icon = new ImageIcon(iconFile.getAbsolutePath());
            frame.setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Could not load icon: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates and arranges all UI components.
     */
    private void createComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        sideMenuPanel = createSideMenu();
        mainPanel.add(sideMenuPanel, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(createDashboardPanel(), "dashboard");
        contentPanel.add(createMyFormsPanel(), "myforms");
        contentPanel.add(createContestablesPanel(), "contestables");
        contentPanel.add(createSettingsPanel(), "settings");
        contentPanel.add(createHelpPanel(), "help");

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(createTopBar(), BorderLayout.NORTH);

        frame.setContentPane(mainPanel);
    }

    /**
     * Creates the side navigation menu.
     * 
     * @return The configured side menu panel
     */
    private JPanel createSideMenu() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(30, 40, 70));
        menuPanel.setPreferredSize(new Dimension(280, 0));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = new JLabel("FORM BUILDER");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        menuPanel.add(titleLabel);

        JPanel userInfoSidePanel = createUserInfoSidePanel();
        menuPanel.add(userInfoSidePanel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(70, 80, 100));
        separator.setMaximumSize(new Dimension(250, 1));
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(separator);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        String[] menuItems = { " Dashboard", " My Forms", " Forms to Answer", " Settings", " Help" };
        String[] iconNames = { "dashboard", "forms", "contestables", "settings", "help" };

        for (int i = 0; i < menuItems.length; i++) {
            JPanel menuItem = createMenuItem(iconNames[i], menuItems[i], i == 0);
            menuPanel.add(menuItem);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        menuPanel.add(Box.createVerticalGlue());

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        logoutPanel.setBackground(new Color(30, 40, 70));
        logoutPanel.setMaximumSize(new Dimension(280, 60));

        JButton logoutButton = new JButton();
        logoutButton.setText(getIcon("logout", 14) + " Logout");

        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutButton.setForeground(new Color(200, 210, 220));
        logoutButton.setBackground(new Color(50, 60, 90));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setFocusPainted(false);

        logoutButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);

            if (response == JOptionPane.YES_OPTION) {
                presentationController.logout();

                frame.dispose();
                LoginView loginView = new LoginView(presentationController);
                loginView.makeVisible();
            }
        });

        logoutPanel.add(logoutButton);
        menuPanel.add(logoutPanel);

        return menuPanel;
    }

    /**
     * Creates the user information panel for the side menu.
     * 
     * @return The configured user info panel
     */
    private JPanel createUserInfoSidePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(40, 50, 90));
        panel.setMaximumSize(new Dimension(260, 100));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 70, 110), 1),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boolean isGuest = username.startsWith("Guest_");
        String iconName = isGuest ? "guest" : "user";
        JLabel iconLabel = new JLabel(getIcon(iconName, 24));
        iconLabel.setForeground(Color.WHITE);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(new Color(40, 50, 90));

        JLabel nameLabel = new JLabel(username);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);

        JLabel typeLabel = new JLabel(isGuest ? "Guest User" : "Registered User");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        typeLabel.setForeground(new Color(180, 190, 220));

        if (!isGuest) {
            JLabel idLabel = new JLabel("ID: " + userId);
            idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            idLabel.setForeground(new Color(160, 170, 200));
            textPanel.add(idLabel);
        }

        textPanel.add(nameLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(typeLabel);

        panel.add(iconLabel);
        panel.add(textPanel);

        panel.setToolTipText("Logged in as: " + username + (isGuest ? "" : " (ID: " + userId + ")"));

        return panel;
    }

    /**
     * Creates a single menu item for the side navigation.
     * 
     * @param iconName The name of the icon to display
     * @param text     The text label for the menu item
     * @param active   Whether this item is currently active
     * @return The configured menu item panel
     */
    private JPanel createMenuItem(String iconName, String text, boolean active) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBackground(active ? new Color(50, 110, 200) : new Color(30, 40, 70));
        itemPanel.setPreferredSize(new Dimension(280, 50));
        itemPanel.setMaximumSize(new Dimension(280, 50));
        itemPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        itemPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel iconLabel = new JLabel(getIcon(iconName, 16));
        iconLabel.setForeground(Color.WHITE);

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textLabel.setForeground(Color.WHITE);

        itemPanel.add(iconLabel, BorderLayout.WEST);
        itemPanel.add(textLabel, BorderLayout.CENTER);

        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!active)
                    itemPanel.setBackground(new Color(40, 50, 80));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!active)
                    itemPanel.setBackground(new Color(30, 40, 70));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                String cardName = text.toLowerCase().replace(" ", "").trim();
                if (cardName.equals("myforms")) {
                    cardName = "myforms";
                } else if (cardName.equals("formstoanswer")) {
                    cardName = "contestables";
                }
                cardLayout.show(contentPanel, cardName);
                updateActiveMenu(text);
                updateTopBarTitle(getPageTitleForCard(cardName));

                if (cardName.equals("dashboard")) {
                    refreshDashboard();
                } else if (cardName.equals("myforms")) {
                    refreshMyFormsPanel();
                } else if (cardName.equals("contestables")) {
                    refreshContestablesPanel();
                }
            }
        });

        return itemPanel;
    }

    /**
     * Gets the page title for a specific card.
     * 
     * @param cardName The card name
     * @return The page title
     */
    private String getPageTitleForCard(String cardName) {
        switch (cardName) {
            case "dashboard":
                return "Dashboard";
            case "myforms":
                return "My Forms";
            case "contestables":
                return "Forms to Answer";
            case "settings":
                return "Settings";
            case "help":
                return "Help & Documentation";
            default:
                return "Form Builder";
        }
    }

    /**
     * Updates the active state of menu items in the side navigation.
     * 
     * @param activeText The text of the active menu item
     */
    private void updateActiveMenu(String activeText) {
        Component[] components = sideMenuPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getComponentCount() > 0) {
                    Component[] children = panel.getComponents();
                    for (Component child : children) {
                        if (child instanceof JLabel) {
                            JLabel label = (JLabel) child;
                            if (label.getText().equals(activeText) ||
                                    label.getText().contains(activeText.trim())) {
                                panel.setBackground(new Color(50, 110, 200));
                            } else if (panel.getBackground().equals(new Color(50, 110, 200))) {
                                panel.setBackground(new Color(30, 40, 70));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates the top navigation bar.
     * 
     * @return The configured top bar panel
     */
    private JPanel createTopBar() {
        JPanel topBarPanel = new JPanel(new BorderLayout());
        topBarPanel.setBackground(Color.WHITE);
        topBarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 230)),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)));

        JLabel pageTitleLabel = new JLabel("Dashboard");
        pageTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        pageTitleLabel.setForeground(new Color(30, 40, 70));
        topBarPanel.add(pageTitleLabel, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setBackground(Color.WHITE);

        JPanel userInfoPanel = createUserInfoTopPanel();

        userPanel.add(userInfoPanel);

        topBarPanel.add(userPanel, BorderLayout.EAST);

        return topBarPanel;
    }

    /**
     * Creates the user information panel for the top bar.
     * 
     * @return The configured user info panel for the top bar
     */
    private JPanel createUserInfoTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(Color.WHITE);

        boolean isGuest = username.startsWith("Guest_");
        String iconName = isGuest ? "guest" : "user";
        JLabel avatarLabel = new JLabel(getIcon(iconName, 22));
        avatarLabel.setToolTipText(isGuest ? "Guest User" : "Registered User");

        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(60, 70, 90));

        if (!isGuest) {
            usernameLabel.setToolTipText("User ID: " + userId);
        } else {
            usernameLabel.setToolTipText("Guest Mode - Limited Functionality");
        }

        panel.add(avatarLabel);
        panel.add(usernameLabel);

        return panel;
    }

    /**
     * Creates a styled action button with hover effects.
     * 
     * @param text   The button text
     * @param action The action name for the icon
     * @param color  The background color of the button
     * @return The configured action button
     */
    private JButton createActionButton(String text, String action, Color color) {
        JButton button = new JButton();

        String icon = getIcon(action, 16);
        String fullText = icon + " " + text;
        button.setText(fullText);

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    /**
     * Creates a small styled action button for secondary actions.
     * 
     * @param text   The button text
     * @param action The action name for the icon
     * @param color  The background color of the button
     * @return The configured small action button
     */
    private JButton createSmallActionButton(String text, String action, Color color) {
        JButton button = new JButton();

        String iconHtml = getIcon(action, 14);

        if (iconHtml != null && iconHtml.contains("<html>")) {
            String combinedHtml = "<html>" +
                    iconHtml.replace("<html>", "").replace("</html>", "").trim() +
                    "&nbsp;&nbsp;" +
                    text +
                    "</html>";
            button.setText(combinedHtml);
        } else {
            button.setText(text);
        }

        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.CENTER);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    /**
     * Creates the dashboard panel with statistics and quick actions.
     * 
     * @return The configured dashboard panel
     */
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("Dashboard Overview");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(30, 40, 70));
        panel.add(titleLabel, BorderLayout.NORTH);

        boolean isGuest = username.startsWith("Guest_");
        String welcomeMessage = isGuest ? "Welcome, Guest! You have limited functionality."
                : "Welcome back, " + username + "!";

        JLabel welcomeLabel = new JLabel(welcomeMessage);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeLabel.setForeground(new Color(100, 110, 130));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(welcomeLabel, BorderLayout.CENTER);

        final int[] stats = calculateDashboardStats();
        final int totalForms = stats[0];

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        String[] statsTitles = { "My Forms", "Published Forms", "Forms to Answer" };
        String[] statsValues = {
                String.valueOf(totalForms),
                String.valueOf(getPublishedFormsCount()),
                String.valueOf(getContestableFormsCount())
        };
        Color[] colors = {
                new Color(70, 130, 220),
                new Color(60, 180, 160),
                new Color(220, 120, 70)
        };

        for (int i = 0; i < 3; i++) {
            statsPanel.add(createStatCard(statsTitles[i], statsValues[i], colors[i]));
        }

        panel.add(statsPanel, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton newFormButton = createActionButton("Create New Form", "create", new Color(70, 130, 220));

        newFormButton.addActionListener(e -> {
            createNewForm();
        });

        if (isGuest) {
            newFormButton.setEnabled(false);
            newFormButton.setToolTipText("Guest users cannot create forms");
        }

        actionsPanel.add(newFormButton);

        panel.add(actionsPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Gets the number of published forms for the user.
     * 
     * @return Number of published forms
     */
    private int getPublishedFormsCount() {
        try {
            ArrayList<Integer> userForms = presentationController.getForms(userId);
            if (userForms == null || userForms.isEmpty()) {
                return 0;
            }

            int publishedCount = 0;
            for (Integer formId : userForms) {
                if (presentationController.isFormPublished(formId)) {
                    publishedCount++;
                }
            }
            return publishedCount;
        } catch (Exception e) {
            System.err.println("Error getting published forms count: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Gets the number of contestable forms for the user.
     * 
     * @return Number of forms the user can answer
     */
    private int getContestableFormsCount() {
        try {
            ArrayList<Integer> contestableForms = presentationController.getContestableForms(userId);
            return contestableForms != null ? contestableForms.size() : 0;
        } catch (Exception e) {
            System.err.println("Error getting contestable forms count: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Calculates the statistics for the dashboard.
     * 
     * @return Array with [totalForms, activeForms, totalResponses]
     */
    private int[] calculateDashboardStats() {
        int totalForms = 0;
        int activeForms = 0;
        int totalResponses = 0;

        boolean isGuest = username.startsWith("Guest_");

        if (!isGuest) {
            try {
                ArrayList<Integer> userForms = presentationController.getForms(userId);
                totalForms = userForms != null ? userForms.size() : 0;
                activeForms = getPublishedFormsCount();

                if (userForms != null && !userForms.isEmpty()) {
                    for (Integer formId : userForms) {
                        try {
                            int formCompletions = presentationController.getFormCompletionCount(formId);
                            totalResponses += formCompletions;
                        } catch (Exception e) {
                            System.err.println("[ERROR] Error getting completion count for form " + formId + ": "
                                    + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("[ERROR] Error calculating dashboard stats: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return new int[] { totalForms, activeForms, totalResponses };
    }

    /**
     * Creates a statistics card for the dashboard.
     * 
     * @param title The title of the statistic
     * @param value The numerical value to display
     * @param color The color for the value text
     * @return The configured statistics card
     */
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 235, 240), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(color);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(120, 130, 150));

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Creates the "My Forms" management panel.
     * 
     * @return The configured my forms panel
     */
    private JPanel createMyFormsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("My Forms");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(30, 40, 70));

        headerPanel.add(titleLabel, BorderLayout.WEST);

        boolean isGuest = username.startsWith("Guest_");

        if (!isGuest) {
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            buttonPanel.setBackground(Color.WHITE);

            JButton createFormButton = createActionButton("Create New Form", "create", new Color(70, 130, 220));
            createFormButton.addActionListener(e -> {
                createNewForm();
            });

            buttonPanel.add(createFormButton);
            headerPanel.add(buttonPanel, BorderLayout.EAST);
        }

        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel formsListPanel = new JPanel();
        formsListPanel.setLayout(new BoxLayout(formsListPanel, BoxLayout.Y_AXIS));
        formsListPanel.setBackground(Color.WHITE);

        loadMyForms(formsListPanel);

        JScrollPane scrollPane = new JScrollPane(formsListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the "Forms to Answer" panel for forms the user can answer.
     * 
     * @return The configured contestables panel
     */
    private JPanel createContestablesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("Forms to Answer");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(30, 40, 70));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel formsListPanel = new JPanel();
        formsListPanel.setLayout(new BoxLayout(formsListPanel, BoxLayout.Y_AXIS));
        formsListPanel.setBackground(Color.WHITE);

        loadContestableForms(formsListPanel);

        JScrollPane scrollPane = new JScrollPane(formsListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Loads user's own forms from the presentation controller.
     * 
     * @param formsListPanel The panel to add form cards to
     */
    private void loadMyForms(JPanel formsListPanel) {
        boolean isGuest = username.startsWith("Guest_");

        if (isGuest) {
            addNoFormsMessage(formsListPanel, "Guest users cannot create or view forms", "myforms");
            return;
        }

        try {
            ArrayList<Integer> userForms = presentationController.getForms(userId);

            if (userForms == null || userForms.isEmpty()) {
                addNoFormsMessage(formsListPanel, "You haven't created any forms yet", "myforms");
            } else {
                for (Integer formId : userForms) {
                    try {
                        Map<String, Object> formInfo = presentationController.getFormInfoWithStatus(formId);

                        String name = (String) formInfo.getOrDefault("name", "Form " + formId);
                        String date = (String) formInfo.getOrDefault("createdDate", "Created recently");
                        Boolean isPublished = (Boolean) formInfo.getOrDefault("isPublished", false);

                        int completions = presentationController.getFormCompletionCount(formId);

                        formsListPanel.add(createFormCardForMyForms(name, date, completions, formId, isPublished));
                        formsListPanel.add(Box.createRigidArea(new Dimension(0, 15)));

                    } catch (Exception e) {
                        System.err.println("Error loading form " + formId + ": " + e.getMessage());
                        e.printStackTrace();
                        formsListPanel.add(
                                createFormCardForMyForms("Form " + formId, "Error loading form", 0, formId, false));
                        formsListPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting user forms: " + e.getMessage());
            e.printStackTrace();
            addNoFormsMessage(formsListPanel, "Error loading forms: " + e.getMessage(), "myforms");
        }
    }

    /**
     * Loads contestable forms that the user can answer.
     * 
     * @param formsListPanel The panel to add form cards to
     */
    private void loadContestableForms(JPanel formsListPanel) {
        try {
            ArrayList<Integer> contestableForms = presentationController.getContestableForms(userId);

            if (contestableForms == null || contestableForms.isEmpty()) {
                addNoFormsMessage(formsListPanel, "No forms available to answer at the moment", "contestables");
            } else {
                for (Integer formId : contestableForms) {
                    try {
                        Map<String, Object> formInfo = presentationController.getFormInfoWithStatus(formId);

                        String name = (String) formInfo.getOrDefault("name", "Form " + formId);
                        String date = (String) formInfo.getOrDefault("createdDate", "Created recently");
                        String owner = (String) formInfo.getOrDefault("owner", "Unknown");
                        Boolean isPublished = (Boolean) formInfo.getOrDefault("isPublished", false);

                        boolean alreadyAnswered = presentationController.hasUserAnsweredForm(userId, formId);

                        if (isPublished) {
                            formsListPanel.add(createContestableFormCard(name, date, owner, alreadyAnswered, formId));
                            formsListPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                        }

                    } catch (Exception e) {
                        System.err.println("Error loading contestable form " + formId + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting contestable forms: " + e.getMessage());
            e.printStackTrace();
            addNoFormsMessage(formsListPanel, "Error loading forms to answer: " + e.getMessage(), "contestables");
        }
    }

    /**
     * Adds a message panel when there are no forms to display.
     * 
     * @param formsListPanel The panel to add the message to
     * @param message        The message to display
     * @param panelType      The type of panel ("myforms" or "contestables")
     */
    private void addNoFormsMessage(JPanel formsListPanel, String message, String panelType) {
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(150, 0, 150, 0));

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        messageLabel.setForeground(new Color(120, 130, 150));

        messagePanel.add(messageLabel, BorderLayout.CENTER);

        if (panelType.equals("myforms") && !username.startsWith("Guest_")) {
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

            JButton createFirstFormButton = new JButton("Create Your First Form");
            createFirstFormButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            createFirstFormButton.setBackground(new Color(70, 130, 220));
            createFirstFormButton.setForeground(Color.WHITE);
            createFirstFormButton.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
            createFirstFormButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            createFirstFormButton.setFocusPainted(false);

            createFirstFormButton.addActionListener(e -> {
                createNewForm();
            });

            createFirstFormButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    createFirstFormButton.setBackground(new Color(50, 110, 200));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    createFirstFormButton.setBackground(new Color(70, 130, 220));
                }
            });

            buttonPanel.add(createFirstFormButton);
            messagePanel.add(buttonPanel, BorderLayout.SOUTH);
        }

        formsListPanel.add(messagePanel);
    }

    /**
     * Toggles the publish status of a form.
     * 
     * @param formId        The form ID
     * @param formName      The form name
     * @param currentStatus The current publish status
     */
    private void togglePublishStatus(Integer formId, String formName, boolean currentStatus) {
        try {
            boolean success;
            String action = currentStatus ? "unpublish" : "publish";

            if (currentStatus) {
                success = presentationController.unpublishForm(formId);
            } else {
                success = presentationController.publishForm(formId);
            }

            if (success) {
                JOptionPane.showMessageDialog(frame,
                        "Form '" + formName + "' has been " + action + "ed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                refreshContestablesPanel();
                refreshDashboard();
                refreshMyFormsPanel();
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Failed to " + action + " form '" + formName + "'.\n" +
                                "It may already be in the desired state.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "Error changing publish status: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates a card to display contestable form information.
     * 
     * @param name            The name of the form
     * @param date            The creation or modification date
     * @param owner           The owner/creator of the form
     * @param alreadyAnswered Whether the user has already answered this form
     * @param formId          The unique identifier of the form
     * @return The configured contestable form card
     */
    private JPanel createContestableFormCard(String name, String date, String owner, boolean alreadyAnswered,
            Integer formId) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(alreadyAnswered ? new Color(200, 200, 200) : new Color(230, 235, 240),
                        1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 120));

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(alreadyAnswered ? new Color(150, 150, 150) : new Color(30, 40, 70));

        JLabel detailsLabel = new JLabel("Created by: " + owner + " | " + date);
        detailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsLabel.setForeground(new Color(120, 130, 150));

        JLabel statusLabel = new JLabel(alreadyAnswered ? "Already answered" : "Available to answer");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(alreadyAnswered ? new Color(100, 180, 100) : new Color(220, 120, 70));

        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(detailsLabel, BorderLayout.CENTER);
        infoPanel.add(statusLabel, BorderLayout.SOUTH);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);

        JButton answerButton = createSmallActionButton("Answer", "answer",
                alreadyAnswered ? new Color(180, 180, 180) : new Color(70, 130, 220));

        if (alreadyAnswered) {
            answerButton.setEnabled(false);
            answerButton.setToolTipText("You have already answered this form");
        }

        answerButton.addActionListener(e -> {
            openAnswerView(formId);
        });

        buttonsPanel.add(answerButton);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonsPanel, BorderLayout.EAST);

        return card;
    }

    /**
     * Creates the settings panel with minimalist options.
     * 
     * @return The configured settings panel
     */
    /**
     * Creates the settings panel with minimalist options.
     * 
     * @return The configured settings panel
     */
    /**
     * Creates the settings panel with minimalist options.
     * 
     * @return The configured settings panel
     */
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(30, 40, 70));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel userInfoTitle = new JLabel("Account Information");
        userInfoTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userInfoTitle.setForeground(new Color(50, 60, 80));
        userInfoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(userInfoTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        String userEmail = "";
        if (!username.startsWith("Guest_")) {
            try {
                userEmail = presentationController.getUserEmail(userId);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error getting user email: " + e.getMessage());
                userEmail = "Not available";
            }
        }

        JPanel userPanel = createSettingItem("Username", username, false);
        panel.add(userPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        if (!username.startsWith("Guest_")) {
            JPanel emailPanel = createSettingItem("Email", userEmail, false);
            panel.add(emailPanel);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));

            JPanel idPanel = createSettingItem("User ID", userId.toString(), false);
            panel.add(idPanel);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JPanel typePanel = createSettingItem("Account Type",
                username.startsWith("Guest_") ? "Guest" : "Registered", false);
        panel.add(typePanel);

        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        if (!username.startsWith("Guest_")) {
            JLabel accountActionsTitle = new JLabel("Account Actions");
            accountActionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
            accountActionsTitle.setForeground(new Color(50, 60, 80));
            accountActionsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(accountActionsTitle);
            panel.add(Box.createRigidArea(new Dimension(0, 15)));

            JButton changeUsernameBtn = new JButton("Change Username");
            changeUsernameBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            changeUsernameBtn.setBackground(new Color(70, 130, 220));
            changeUsernameBtn.setForeground(Color.WHITE);
            changeUsernameBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 110, 200), 1),
                    BorderFactory.createEmptyBorder(12, 30, 12, 30)));
            changeUsernameBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            changeUsernameBtn.setFocusPainted(false);
            changeUsernameBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

            changeUsernameBtn.addActionListener(e -> {
                changeUsername();
            });

            changeUsernameBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    changeUsernameBtn.setBackground(new Color(50, 110, 200));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    changeUsernameBtn.setBackground(new Color(70, 130, 220));
                }
            });

            panel.add(changeUsernameBtn);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));

            JButton changeEmailBtn = new JButton("Change Email        ");
            changeEmailBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            changeEmailBtn.setBackground(new Color(70, 130, 220));
            changeEmailBtn.setForeground(Color.WHITE);
            changeEmailBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 110, 200), 1),
                    BorderFactory.createEmptyBorder(12, 30, 12, 30)));
            changeEmailBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            changeEmailBtn.setFocusPainted(false);
            changeEmailBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

            changeEmailBtn.addActionListener(e -> {
                changeEmail();
            });

            changeEmailBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    changeEmailBtn.setBackground(new Color(50, 110, 200));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    changeEmailBtn.setBackground(new Color(70, 130, 220));
                }
            });

            panel.add(changeEmailBtn);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));

            JButton changePasswordBtn = new JButton("Change Password ");
            changePasswordBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            changePasswordBtn.setBackground(new Color(70, 130, 220));
            changePasswordBtn.setForeground(Color.WHITE);
            changePasswordBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 110, 200), 1),
                    BorderFactory.createEmptyBorder(12, 30, 12, 30)));
            changePasswordBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            changePasswordBtn.setFocusPainted(false);
            changePasswordBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

            changePasswordBtn.addActionListener(e -> {
                changePassword();
            });

            changePasswordBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    changePasswordBtn.setBackground(new Color(50, 110, 200));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    changePasswordBtn.setBackground(new Color(70, 130, 220));
                }
            });

            panel.add(changePasswordBtn);
            panel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JLabel appInfoTitle = new JLabel("Application Information");
        appInfoTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appInfoTitle.setForeground(new Color(50, 60, 80));
        appInfoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(appInfoTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton aboutBtn = new JButton("About Form Builder");
        aboutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        aboutBtn.setBackground(new Color(240, 240, 240));
        aboutBtn.setForeground(new Color(80, 90, 110));
        aboutBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        aboutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        aboutBtn.setFocusPainted(false);
        aboutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        aboutBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame,
                    "Form Builder v1\n\n"
                            + "A simple yet powerful form creation tool\n"
                            + "Designed for easy form management and data collection\n\n"
                            + "2025 Subgrup 13-3 PROP",
                    "About",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        panel.add(aboutBtn);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    /**
     * Changes the user's username.
     */
    private void changeUsername() {
        JTextField newUsernameField = new JTextField(20);

        JPanel usernamePanel = new JPanel(new GridLayout(0, 1, 5, 5));
        usernamePanel.add(new JLabel("New Username:"));
        usernamePanel.add(newUsernameField);

        int result = JOptionPane.showConfirmDialog(frame,
                usernamePanel,
                "Change Username",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newUsername = newUsernameField.getText().trim();

            if (newUsername.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Username field cannot be empty.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newUsername.length() < 3) {
                JOptionPane.showMessageDialog(frame,
                        "Username must be at least 3 characters long.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newUsername.equals(username)) {
                JOptionPane.showMessageDialog(frame,
                        "New username must be different from the current username.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                presentationController.changeUsername(userId, newUsername);

                this.username = newUsername;

                updateUsernameInAllComponents();
                refreshSettingsPanel();

                JOptionPane.showMessageDialog(frame,
                        "Username changed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                        "Error changing username: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Updates the username in all UI components that display it.
     */
    private void updateUsernameInAllComponents() {
        updateUserInfoSidePanel();
        updateUserInfoTopPanel();
        refreshDashboard();
        updateUsernameTooltips();
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Updates the user info panel in the side menu.
     */
    private void updateUserInfoSidePanel() {
        for (Component comp : sideMenuPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getBackground().equals(new Color(40, 50, 90))) {
                    for (Component child : panel.getComponents()) {
                        if (child instanceof JPanel) {
                            JPanel innerPanel = (JPanel) child;
                            for (Component innerChild : innerPanel.getComponents()) {
                                if (innerChild instanceof JLabel) {
                                    JLabel label = (JLabel) innerChild;
                                    if (label.getFont().getSize() == 16 && label.getFont().getStyle() == Font.BOLD) {
                                        label.setText(username);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    panel.setToolTipText("Logged in as: " + username +
                            (username.startsWith("Guest_") ? "" : " (ID: " + userId + ")"));
                    break;
                }
            }
        }
    }

    /**
     * Updates the user info panel in the top bar.
     */
    private void updateUserInfoTopPanel() {
        Component topBar = ((BorderLayout) frame.getContentPane().getLayout()).getLayoutComponent(BorderLayout.NORTH);
        if (topBar instanceof JPanel) {
            JPanel topBarPanel = (JPanel) topBar;

            for (Component comp : topBarPanel.getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel rightPanel = (JPanel) comp;
                    for (Component rightChild : rightPanel.getComponents()) {
                        if (rightChild instanceof JPanel) {
                            JPanel userInfoPanel = (JPanel) rightChild;
                            for (Component infoComp : userInfoPanel.getComponents()) {
                                if (infoComp instanceof JLabel) {
                                    JLabel label = (JLabel) infoComp;
                                    if (!label.getText().contains("<html>") && label.getFont().getSize() == 14) {
                                        label.setText(username);
                                        if (username.startsWith("Guest_")) {
                                            label.setToolTipText("Guest Mode - Limited Functionality");
                                        } else {
                                            label.setToolTipText("User ID: " + userId);
                                        }
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * Updates tooltips that contain the username.
     */
    private void updateUsernameTooltips() {
        for (Component comp : sideMenuPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                String tooltip = panel.getToolTipText();
                if (tooltip != null && tooltip.contains("Logged in as:")) {
                    panel.setToolTipText("Logged in as: " + username +
                            (username.startsWith("Guest_") ? "" : " (ID: " + userId + ")"));
                }
            }
        }
    }

    /**
     * Changes the user's email.
     */
    private void changeEmail() {
        JTextField newEmailField = new JTextField(20);

        JPanel emailPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        emailPanel.add(new JLabel("New Email:"));
        emailPanel.add(newEmailField);

        int result = JOptionPane.showConfirmDialog(frame,
                emailPanel,
                "Change Email",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newEmail = newEmailField.getText().trim();

            if (newEmail.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Email field cannot be empty.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newEmail.contains("@") || !newEmail.contains(".")) {
                JOptionPane.showMessageDialog(frame,
                        "Please enter a valid email address.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                presentationController.changeUserEmail(userId, newEmail);

                JOptionPane.showMessageDialog(frame,
                        "Email changed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                refreshSettingsPanel();

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                        "Error changing email: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Refreshes the settings panel to show updated information.
     */
    private void refreshSettingsPanel() {
        contentPanel.remove(3);
        contentPanel.add(createSettingsPanel(), "settings", 3);

        cardLayout.show(contentPanel, "settings");
        updateActiveMenu(" Settings");
        updateTopBarTitle("Settings");

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Changes the user's password.
     */
    private void changePassword() {
        JPasswordField currentPasswordField = new JPasswordField(20);
        JPasswordField newPasswordField = new JPasswordField(20);
        JPasswordField confirmPasswordField = new JPasswordField(20);

        JPanel passwordPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        passwordPanel.add(new JLabel("Current Password:"));
        passwordPanel.add(currentPasswordField);
        passwordPanel.add(new JLabel("New Password:"));
        passwordPanel.add(newPasswordField);
        passwordPanel.add(new JLabel("Confirm New Password:"));
        passwordPanel.add(confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(frame,
                passwordPanel,
                "Change Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "All fields are required.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(frame,
                        "New passwords do not match.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newPassword.equals(currentPassword)) {
                JOptionPane.showMessageDialog(frame,
                        "New password must be different from the current password.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                presentationController.changePassword(userId, currentPassword, newPassword);

                JOptionPane.showMessageDialog(frame,
                        "Password changed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (SecurityException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                        "Current password is incorrect.",
                        "Authentication Error",
                        JOptionPane.ERROR_MESSAGE);

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                String errorMessage = e.getMessage();
                if (errorMessage.contains("at least 8 characters") ||
                        errorMessage.contains("contain at least one number")) {
                    JOptionPane.showMessageDialog(frame,
                            errorMessage,
                            "Password Requirements",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Error: " + errorMessage,
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                        "Error changing password: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Creates a simple setting item display.
     * 
     * @param label    The setting label
     * @param value    The setting value
     * @param editable Whether the setting is editable
     * @return The configured setting item panel
     */
    private JPanel createSettingItem(String label, String value, boolean editable) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        labelComp.setForeground(new Color(30, 40, 70));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        valueLabel.setForeground(new Color(100, 110, 130));

        panel.add(labelComp, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Creates the help panel with documentation and information.
     * 
     * @return The configured help panel
     */
    private JPanel createHelpPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Help & Documentation");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(30, 40, 70));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(Color.WHITE);

        JPanel sectionsPanel = new JPanel();
        sectionsPanel.setLayout(new BoxLayout(sectionsPanel, BoxLayout.Y_AXIS));
        sectionsPanel.setBackground(Color.WHITE);
        sectionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JPanel helpSection = createHelpSection();
        helpSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionsPanel.add(helpSection);
        sectionsPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel docsSection = createDocumentationSection();
        docsSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionsPanel.add(docsSection);
        sectionsPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel contactSection = createContactSection();
        contactSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionsPanel.add(contactSection);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.WHITE);
        wrapperPanel.add(sectionsPanel, BorderLayout.NORTH);

        mainContentPanel.add(wrapperPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(mainContentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the help section with FAQ and guidance.
     * 
     * @return The configured help section panel
     */
    private JPanel createHelpSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 235, 240), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        JLabel sectionTitle = new JLabel("Quick Help");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(new Color(30, 40, 70));
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sectionTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        String[] questions = {
                "How do I create a new form?",
                "How do I publish/unpublish a form?",
                "Can I edit a form after publishing?",
                "How do I share my forms with others?",
                "What types of questions can I add?",
                "How do I answer forms from other users?"
        };

        String[] answers = {
                "Go to Dashboard and click 'Create New Form', or go to My Forms and click 'Create Your First Form' if you have no forms.",
                "In 'My Forms', click the Publish/Unpublish button next to each form to control its availability.",
                "Forms cannot be edited after being published, and also cannot be modified after someone answers them.",
                "Publish your forms to make them available in the 'Forms to Answer' section for other registered users.",
                "You can add numerical, multiple choice, single choice, free text, and ordered single choice questions.",
                "Go to 'Forms to Answer' section to see all published forms available for you to answer from other users."
        };

        for (int i = 0; i < questions.length; i++) {
            JPanel faqItem = new JPanel(new BorderLayout());
            faqItem.setBackground(Color.WHITE);
            faqItem.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel questionLabel = new JLabel("<html><b>" + questions[i] + "</b></html>");
            questionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            questionLabel.setForeground(new Color(50, 60, 80));

            JLabel answerLabel = new JLabel("<html>" + answers[i] + "</html>");
            answerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            answerLabel.setForeground(new Color(100, 110, 130));
            answerLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

            faqItem.add(questionLabel, BorderLayout.NORTH);
            faqItem.add(answerLabel, BorderLayout.CENTER);
            panel.add(faqItem);

            if (i < questions.length - 1) {
                panel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        return panel;
    }

    /**
     * Creates the documentation section with user guide.
     * 
     * @return The configured documentation section panel
     */
    private JPanel createDocumentationSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 235, 240), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        JLabel sectionTitle = new JLabel("User Guide");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(new Color(30, 40, 70));
        panel.add(sectionTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        String[] steps = {
                "1. <b>Create Forms</b>: Start by creating a form from the Dashboard or My Forms page.",
                "2. <b>Add Questions</b>: Use the edit function to add different types of questions to your form.",
                "3. <b>Publish Forms</b>: Click 'Publish' on your forms to make them available to other users.",
                "4. <b>Share Forms</b>: Published forms appear in the 'Forms to Answer' section for all users.",
                "5. <b>Answer Forms</b>: Go to 'Forms to Answer' to complete forms from other users.",
                "6. <b>Analyze Data</b>: Use the Analyze feature to gain insights from collected responses.",
                "7. <b>Manage Forms</b>: Edit, publish, or unpublish forms as needed to control their availability."
        };

        for (String step : steps) {
            JLabel stepLabel = new JLabel("<html>" + step + "</html>");
            stepLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            stepLabel.setForeground(new Color(80, 90, 110));
            stepLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
            panel.add(stepLabel);
        }

        return panel;
    }

    /**
     * Creates the contact section with support information.
     * 
     * @return The configured contact section panel
     */
    private JPanel createContactSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 235, 240), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        JLabel sectionTitle = new JLabel("Support & Contact");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(new Color(30, 40, 70));
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(sectionTitle, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel supportInfo = new JLabel("<html>" +
                "<p>If you need additional help or have found a bug, please contact our support team:</p>" +
                "<br>" +
                "<p><b>Upstream project is on </b> https://repo.fib.upc.edu/grau-prop/subgrup-prop13.3/</p>" +
                "<p><b>Version 1.0</b></p>" +
                "<br>" +
                "<p>We're continuously working to improve Form Builder. Your feedback is valuable!</p>" +
                "</html>");
        supportInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        supportInfo.setForeground(new Color(80, 90, 110));
        supportInfo.setVerticalAlignment(SwingConstants.TOP);
        supportInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(supportInfo);

        panel.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton contactButton = new JButton("Contact Support");
        contactButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        contactButton.setBackground(new Color(70, 130, 220));
        contactButton.setForeground(Color.WHITE);
        contactButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        contactButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        contactButton.setFocusPainted(false);

        contactButton.addActionListener(e -> {
            openBrowser("https://repo.fib.upc.edu/grau-prop/subgrup-prop13.3/");
        });

        contactButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                contactButton.setBackground(new Color(50, 110, 200));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                contactButton.setBackground(new Color(70, 130, 220));
            }
        });

        buttonPanel.add(contactButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates a new form and updates the forms panel.
     */
    private void createNewForm() {
        String formName = JOptionPane.showInputDialog(frame,
                "Enter form name:",
                "Create New Form",
                JOptionPane.PLAIN_MESSAGE);

        if (formName != null && !formName.trim().isEmpty()) {
            try {
                Integer newFormId = presentationController.createForm(userId, formName.trim());

                JOptionPane.showMessageDialog(frame,
                        "Form '" + formName + "' created successfully!\nForm ID: " + newFormId,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                refreshDashboard();
                refreshMyFormsPanel();

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                        "Error creating form: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Refreshes the "My Forms" panel to show updated forms list.
     */
    private void refreshMyFormsPanel() {
        contentPanel.remove(1);
        contentPanel.add(createMyFormsPanel(), "myforms", 1);

        cardLayout.show(contentPanel, "myforms");
        updateActiveMenu(" My Forms");
        updateTopBarTitle("My Forms");

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Refreshes the "Forms to Answer" panel to show updated contestable forms list.
     */
    private void refreshContestablesPanel() {
        contentPanel.remove(2);
        contentPanel.add(createContestablesPanel(), "contestables", 2);

        cardLayout.show(contentPanel, "contestables");
        updateActiveMenu(" Forms to Answer");
        updateTopBarTitle("Forms to Answer");

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Refreshes the dashboard panel with updated statistics.
     */
    private void refreshDashboard() {
        contentPanel.remove(0);
        contentPanel.add(createDashboardPanel(), "dashboard", 0);

        cardLayout.show(contentPanel, "dashboard");
        updateActiveMenu(" Dashboard");
        updateTopBarTitle("Dashboard");

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Updates the title in the top bar.
     * 
     * @param title The new title
     */
    private void updateTopBarTitle(String title) {
        Component topBar = ((BorderLayout) frame.getContentPane().getLayout()).getLayoutComponent(BorderLayout.NORTH);
        if (topBar instanceof JPanel) {
            JPanel topBarPanel = (JPanel) topBar;
            for (Component comp : topBarPanel.getComponents()) {
                if (comp instanceof JLabel) {
                    JLabel label = (JLabel) comp;
                    label.setText(title);
                    break;
                }
            }
        }
    }

    /**
     * Opens the form edit view for a specific form.
     * 
     * @param formId The unique identifier of the form to edit
     */
    private void openEditView(Integer formId) {
        try {
            frame.dispose();
            VistaCrearEditarForm editFormView = new VistaCrearEditarForm(formId, presentationController, this);
            editFormView.makeVisible();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "Error opening edit view: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            frame.setVisible(true);
        }
    }

    /**
     * Opens the form answer view for a specific form.
     * 
     * @param formId The unique identifier of the form to answer
     */
    private void openAnswerView(Integer formId) {
        frame.dispose();
        VistaContestar answerFormView = new VistaContestar(formId, userId, presentationController, this);
        answerFormView.hacerVisible();
    }

    /**
     * Makes the main menu window visible.
     */
    public void makeVisible() {
        refreshDashboard();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    /**
     * Confirms and deletes a form from the user.
     * 
     * @param formId   The form ID to delete
     * @param formName The form name to display in the message
     */
    private void confirmAndDeleteForm(Integer formId, String formName) {
        int response = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete the form:\n\"" + formName + "\"?\n\n" +
                        "This action cannot be undone. All associated data will be permanently deleted.",
                "Confirm Delete Form",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            try {
                presentationController.deleteForm(formId);

                JOptionPane.showMessageDialog(frame,
                        "Form \"" + formName + "\" has been deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                refreshDashboard();
                refreshMyFormsPanel();
                refreshContestablesPanel();

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                        "Cannot delete form: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                        "Error deleting form: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Creates a card for forms in "My Forms" section with delete button.
     * 
     * @param name        The name of the form
     * @param date        The creation or modification date
     * @param responses   The number of completions
     * @param formId      The unique identifier of the form
     * @param isPublished Whether the form is published or not
     * @return The configured form card with delete button
     */
    private JPanel createFormCardForMyForms(String name, String date, int responses, Integer formId,
            boolean isPublished) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 235, 240), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 140));

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(new Color(30, 40, 70));

        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(120, 130, 150));

        JLabel statusLabel = new JLabel(isPublished ? "Published" : "Not Published");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(isPublished ? new Color(60, 180, 100) : new Color(220, 100, 100));

        JLabel responsesLabel = new JLabel(responses + " completions");
        responsesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        responsesLabel.setForeground(new Color(70, 130, 220));

        JPanel infoDetails = new JPanel(new GridLayout(2, 2, 5, 5));
        infoDetails.setBackground(Color.WHITE);
        infoDetails.add(dateLabel);
        infoDetails.add(statusLabel);
        infoDetails.add(new JLabel(""));
        infoDetails.add(responsesLabel);

        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(infoDetails, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);

        JButton publishButton = createSmallActionButton(
                isPublished ? "Unpublish" : "Publish",
                isPublished ? "eye-slash" : "eye",
                isPublished ? new Color(220, 120, 70) : new Color(60, 180, 160));

        publishButton.addActionListener(e -> {
            togglePublishStatus(formId, name, isPublished);
        });

        JButton editButton = createSmallActionButton("Edit", "edit", new Color(70, 130, 220));
        editButton.addActionListener(e -> {
            openEditView(formId);
        });

        JButton answerButton = createSmallActionButton("Answer", "answer", new Color(60, 180, 160));
        answerButton.addActionListener(e -> {
            openAnswerView(formId);
        });

        JButton analyzeButton = createSmallActionButton("Analyze", "analyze", new Color(220, 120, 70));
        analyzeButton.addActionListener(e -> {
            frame.dispose();
            VistaAnalitzar analyzeView = new VistaAnalitzar(formId, name, userId);
            analyzeView.hacerVisible();
        });

        JButton deleteButton = createSmallActionButton("Delete", "delete", new Color(220, 80, 70));
        deleteButton.addActionListener(e -> {
            confirmAndDeleteForm(formId, name);
        });

        if (!isPublished) {
            analyzeButton.setEnabled(false);
            analyzeButton.setBackground(new Color(180, 180, 180));
            analyzeButton.setToolTipText("Form must be published to analyze");
        }

        buttonsPanel.add(publishButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(answerButton);
        buttonsPanel.add(analyzeButton);
        buttonsPanel.add(deleteButton);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonsPanel, BorderLayout.EAST);

        return card;
    }

    /**
     * Opens the default browser with the specified URL.
     * 
     * @param url The URL to open
     */
    public static void openBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
                return;
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                Runtime.getRuntime().exec(
                        new String[] { "rundll32", "url.dll,FileProtocolHandler", url });
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[] { "open", url });
            } else {
                Runtime.getRuntime().exec(new String[] { "xdg-open", url });
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to open browser", e);
        }
    }
}
