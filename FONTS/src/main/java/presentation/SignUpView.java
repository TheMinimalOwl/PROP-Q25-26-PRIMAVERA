package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Path;

/**
 * User registration view for the Form Builder application.
 * Allows new users to create an account with email, username, and password.
 */
public class SignUpView {
    private JFrame frame;
    private CtrlPresentation presentationController;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;

    /**
     * Constructor for SignUpView.
     * Initializes the window and components.
     */
    public SignUpView(CtrlPresentation presentationController) {
        frame = new JFrame("Sign Up");
        this.presentationController = presentationController;
        configureWindow();
        createComponents();
    }

    /**
     * Configures the main window properties.
     */
    private void configureWindow() {
        frame.setSize(650, 525);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        centerPanel.setBackground(new Color(245, 247, 250));

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(30, 40, 70));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(title, gbc);

        JLabel subtitle = new JLabel("Join our community", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(120, 130, 150));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 25, 0);
        centerPanel.add(subtitle, gbc);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(new Color(245, 247, 250));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        GridBagConstraints gbcFields = new GridBagConstraints();
        gbcFields.fill = GridBagConstraints.HORIZONTAL;
        gbcFields.weightx = 1.0;
        gbcFields.anchor = GridBagConstraints.CENTER;

        gbcFields.gridx = 0;
        gbcFields.gridy = 0;
        gbcFields.insets = new Insets(0, 0, 5, 5);
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setForeground(new Color(60, 70, 90));
        fieldsPanel.add(emailLabel, gbcFields);

        gbcFields.gridx = 1;
        gbcFields.insets = new Insets(0, 0, 5, 0);
        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(280, 42));
        emailField.setMinimumSize(new Dimension(280, 42));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        fieldsPanel.add(emailField, gbcFields);

        gbcFields.gridx = 0;
        gbcFields.gridy = 1;
        gbcFields.insets = new Insets(5, 0, 5, 5);
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(60, 70, 90));
        fieldsPanel.add(usernameLabel, gbcFields);

        gbcFields.gridx = 1;
        gbcFields.insets = new Insets(5, 0, 5, 0);
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(280, 42));
        usernameField.setMinimumSize(new Dimension(280, 42));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        usernameField.setToolTipText("Display username");
        fieldsPanel.add(usernameField, gbcFields);

        gbcFields.gridx = 0;
        gbcFields.gridy = 2;
        gbcFields.insets = new Insets(5, 0, 5, 5);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(60, 70, 90));
        fieldsPanel.add(passwordLabel, gbcFields);

        gbcFields.gridx = 1;
        gbcFields.insets = new Insets(5, 0, 5, 0);
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(280, 42));
        passwordField.setMinimumSize(new Dimension(280, 42));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        fieldsPanel.add(passwordField, gbcFields);

        gbcFields.gridx = 0;
        gbcFields.gridy = 3;
        gbcFields.insets = new Insets(5, 0, 0, 5);
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordLabel.setForeground(new Color(60, 70, 90));
        fieldsPanel.add(confirmPasswordLabel, gbcFields);

        gbcFields.gridx = 1;
        gbcFields.insets = new Insets(5, 0, 0, 0);
        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setPreferredSize(new Dimension(280, 42));
        confirmPasswordField.setMinimumSize(new Dimension(280, 42));
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        fieldsPanel.add(confirmPasswordField, gbcFields);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(fieldsPanel, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(15, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JButton signUpButton = createStyledButton("Sign Up",
                new Color(70, 130, 220),
                new Color(50, 110, 200),
                Color.WHITE);

        signUpButton.setPreferredSize(new Dimension(300, 50));
        signUpButton.setMinimumSize(new Dimension(300, 50));
        signUpButton.setMaximumSize(new Dimension(300, 50));
        signUpButton.setFont(new Font("Segoe UI", Font.BOLD, 16));

        signUpButton.addActionListener(e -> {
            registerUser(confirmPasswordField);
        });

        buttonPanel.add(signUpButton);
        centerPanel.add(buttonPanel, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(20, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(220, 225, 230));
        centerPanel.add(separator, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel loginText = new JLabel(
                "<html><div style='text-align: center;'>Already have an account? <b style='color:#4682DC;'>Log In</b></div></html>",
                SwingConstants.CENTER);
        loginText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginText.setForeground(new Color(120, 130, 150));
        loginText.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginText.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginText.setText(
                        "<html><div style='text-align: center;'>Already have an account? <b style='color:#2a6bc8;'>Log In</b></div></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginText.setText(
                        "<html><div style='text-align: center;'>Already have an account? <b style='color:#4682DC;'>Log In</b></div></html>");
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
                // Pasar la misma instancia de CtrlPresentation
                LoginView loginView = new LoginView(presentationController);
                loginView.makeVisible();
            }
        });

        centerPanel.add(loginText, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        frame.setContentPane(mainPanel);
    }

    /**
     * Registers a new user with the provided information.
     * Validates input fields and communicates with the presentation controller.
     * 
     * @param confirmPasswordField The password confirmation field
     */
    private void registerUser(JPasswordField confirmPasswordField) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        if (username.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "All fields are required.",
                    "Incomplete fields",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(frame,
                    "Passwords do not match.",
                    "Password error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(frame,
                    "Please enter a valid email.",
                    "Invalid email",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Integer createdId = presentationController.createUser(username, password, confirmPassword, email);

            JOptionPane.showMessageDialog(frame,
                    "User created successfully!\n" +
                            "Your user ID is: " + createdId + "\n" +
                            "Username: " + username + "\n\n" +
                            "Save your ID, you will need it to log in.",
                    "Registration successful",
                    JOptionPane.INFORMATION_MESSAGE);

            frame.dispose();
            LoginView loginView = new LoginView(presentationController);
            loginView.makeVisible();

        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "Error creating user: " + ex.getMessage(),
                    "Registration error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "Unexpected error: " + ex.getMessage(),
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
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));

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
     * Makes the sign-up window visible.
     */
    public void makeVisible() {
        frame.setVisible(true);
    }
}
