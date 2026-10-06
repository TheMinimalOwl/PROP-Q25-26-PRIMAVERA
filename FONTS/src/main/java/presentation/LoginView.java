package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Path;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * User login view for the Form Builder application.
 * Allows registered users to log in with their ID and password,
 * or guests to enter anonymously.
 */
public class LoginView {
    private JFrame frame;
    private CtrlPresentation presentationController;
    private JTextField idField;
    private JPasswordField passwordField;

    /**
     * Constructor for LoginView.
     * Initializes the window and components.
     */
    public LoginView(CtrlPresentation presentationController) {
        frame = new JFrame("Login");
        this.presentationController = presentationController;
        configureWindow();
        createComponents();
    }

    /**
     * Configures the main window properties.
     */
    private void configureWindow() {
        frame.setSize(650, 450);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        mainPanel.setBackground(new Color(245, 247, 250));

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("Welcome", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(30, 40, 70));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(title, gbc);

        JLabel subtitle = new JLabel("Access your account", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(120, 130, 150));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 40, 0);
        mainPanel.add(subtitle, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 0, 5, 15);
        gbc.anchor = GridBagConstraints.EAST;
        JLabel idLabel = new JLabel("User ID:");
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        idLabel.setForeground(new Color(60, 70, 90));
        mainPanel.add(idLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 0);
        idField = new JTextField();
        idField.setPreferredSize(new Dimension(280, 42));
        idField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        idField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        idField.setToolTipText("Enter your user ID (number)");
        mainPanel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 0, 5, 15);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(60, 70, 90));
        mainPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 0);
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(280, 42));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        mainPanel.add(passwordField, gbc);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2, 25, 0));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 0, 50));
        buttonsPanel.setBackground(new Color(245, 247, 250));

        JButton loginButton = createStyledButton("Log In",
                new Color(70, 130, 220),
                new Color(50, 110, 200),
                Color.WHITE);

        JButton signUpButton = createStyledButton("Sign Up",
                new Color(245, 247, 250),
                new Color(230, 235, 240),
                new Color(70, 130, 220));

        signUpButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 220), 1),
                BorderFactory.createEmptyBorder(12, 25, 12, 25)));

        buttonsPanel.add(loginButton);
        buttonsPanel.add(signUpButton);

        signUpButton.addActionListener(e -> {
            SignUpView signUpView = new SignUpView(presentationController);
            signUpView.makeVisible();
            frame.dispose();
        });

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonsPanel, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(30, 50, 20, 50);
        JSeparator bottomSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        bottomSeparator.setForeground(new Color(220, 225, 230));
        mainPanel.add(bottomSeparator, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel guestText = new JLabel(
                "<html><div style='text-align: center;'>Don't have an account? <b style='color:#4682DC;'>Enter as Guest</b></div></html>",
                SwingConstants.CENTER);
        guestText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        guestText.setForeground(new Color(120, 130, 150));
        guestText.setCursor(new Cursor(Cursor.HAND_CURSOR));

        guestText.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                guestText.setText(
                        "<html><div style='text-align: center;'>Don't have an account? <b style='color:#2a6bc8;'>Enter as Guest</b></div></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                guestText.setText(
                        "<html><div style='text-align: center;'>Don't have an account? <b style='color:#4682DC;'>Enter as Guest</b></div></html>");
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                createAnonymousUser();
            }
        });

        loginButton.addActionListener(e -> {
            performLogin();
        });

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        mainPanel.add(guestText, gbc);

        frame.setContentPane(mainPanel);
    }

    /**
     * Performs user login with provided ID and password.
     * Validates input and communicates with the presentation controller.
     */
    private void performLogin() {
        String idStr = idField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (idStr.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Please complete all fields.",
                    "Empty fields",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer userId;
        try {
            userId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "User ID must be a valid number.",
                    "Invalid ID",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String username = presentationController.getUsername(userId);

            if (username.equals("Unknown")) {
                JOptionPane.showMessageDialog(frame,
                        "User with ID '" + idStr + "' is not registered.\n" +
                                "Please register first.",
                        "User not found",
                        JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
                return;
            }

            if (username.startsWith("Guest_")) {
                JOptionPane.showMessageDialog(frame,
                        "Guest users cannot log in with password.\n" +
                                "Use the 'Enter as Guest' option to enter as guest.",
                        "Guest User",
                        JOptionPane.WARNING_MESSAGE);
                idField.setText("");
                passwordField.setText("");
                return;
            }

            presentationController.loginUser(userId, password);

            JOptionPane.showMessageDialog(frame,
                    "Login successful!\n\n" +
                            "Welcome: " + username + " (ID: " + idStr + ")",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            frame.dispose();
            MainMenuView mainMenuView = new MainMenuView(userId, username, presentationController);
            mainMenuView.makeVisible();

        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            String errorMsg = ex.getMessage();

            if (errorMsg != null && errorMsg.contains("Given password") && errorMsg.contains("is incorrect")) {
                JOptionPane.showMessageDialog(frame,
                        "Incorrect password.\n" +
                                "Please verify your password and try again.",
                        "Authentication error",
                        JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            } else if (errorMsg != null && errorMsg.contains("is not a RegisteredUser")) {
                JOptionPane.showMessageDialog(frame,
                        "User with ID '" + idStr + "' is not a registered user.\n" +
                                "Please register first.",
                        "User not registered",
                        JOptionPane.ERROR_MESSAGE);
                idField.setText("");
                passwordField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Login error: " + errorMsg,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "Unexpected error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            passwordField.setText("");
        }
    }

    /**
     * Creates an anonymous guest user.
     * Allows users to enter the application without registration.
     */
    private void createAnonymousUser() {
        try {
            Integer userId = presentationController.createAnonymousUser();
            String username = presentationController.getUsername(userId);

            if (!username.startsWith("Guest_")) {
                throw new Exception("Error: Created user is not a guest");
            }

            JOptionPane.showMessageDialog(frame,
                    "Guest mode activated!\n\n" +
                            "Guest user: " + username + "\n" +
                            "Note: Guest users have limited functionality.",
                    "Entering as guest",
                    JOptionPane.INFORMATION_MESSAGE);

            frame.dispose();
            MainMenuView mainMenuView = new MainMenuView(userId, username, presentationController);
            mainMenuView.makeVisible();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "Error creating anonymous user: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates a styled button with hover effects.
     * 
     * @param text       The button text
     * @param bgColor    The background color
     * @param hoverColor The background color on hover
     * @param textColor  The text color
     * @return The configured button
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
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * Makes the login window visible.
     */
    public void makeVisible() {
        frame.setVisible(true);
    }
}
