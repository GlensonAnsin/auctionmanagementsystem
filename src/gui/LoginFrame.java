package gui;

import models.Item;
import models.User;
import utils.ColorScheme;
import utils.FileHandler;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.io.File;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private static final String USERS_FILE = "users.dat";

    public LoginFrame() {
        setTitle("Auction System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 500); // Increased size to match RegisterDialog
        setLocationRelativeTo(null);

        // Modern styling
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Logo or icon (optional)
        ImageIcon logo = new ImageIcon("resources/auction-logo.png");
        JLabel logoLabel = new JLabel(logo);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(logoLabel, gbc);

        // Title
        JLabel titleLabel = new JLabel("Welcome to Auction System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        mainPanel.add(titleLabel, gbc);

        // Username field with styled border
        usernameField = createStyledField("Username");
        mainPanel.add(usernameField, gbc);

        // Password field with styled border
        passwordField = createStyledPasswordField("Password");
        mainPanel.add(passwordField, gbc);

        // Buttons panel with more spacing
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        JButton loginButton = createStyledButton("Login", true);
        JButton registerButton = createStyledButton("Register", false);

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.insets = new Insets(25, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);

        // Apply colors
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        titleLabel.setForeground(ColorScheme.TEXT_PRIMARY);

        // Add hover effects
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(ColorScheme.BUTTON_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(ColorScheme.BUTTON_DEFAULT);
            }
        });

        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        // Login action
        loginButton.addActionListener(e -> handleLogin());

        // Register action
        registerButton.addActionListener(e -> openRegisterDialog());

        add(mainPanel);
    }

    private JTextField createStyledField(String title) {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(0, 45));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(createStyledBorder(title));
        return field;
    }

    private JPasswordField createStyledPasswordField(String title) {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(0, 45));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(createStyledBorder(title));
        return field;
    }

    private Border createStyledBorder(String title) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ColorScheme.BORDER),
                title,
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.PLAIN, 12),
                ColorScheme.TEXT_SECONDARY);

        Border empty = BorderFactory.createEmptyBorder(2, 8, 2, 8);
        return BorderFactory.createCompoundBorder(titledBorder, empty);
    }

    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 40));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        if (isPrimary) {
            button.setBackground(ColorScheme.BUTTON_DEFAULT);
            button.setForeground(ColorScheme.BUTTON_TEXT);
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(ColorScheme.BUTTON_HOVER);
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(ColorScheme.BUTTON_DEFAULT);
                }
            });
        } else {
            button.setBackground(ColorScheme.BUTTON_SECONDARY);
            button.setForeground(ColorScheme.BUTTON_SECONDARY_TEXT);
        }

        return button;
    }

    private void handleLogin() {
        try {
            String username = usernameField.getText().trim(); // Added trim()
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter both username and password!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            @SuppressWarnings("unchecked")
            ArrayList<User> users = (ArrayList<User>) FileHandler.readFromFile(USERS_FILE); // Fixed file path

            if (users == null) {
                users = new ArrayList<>();
            }

            boolean found = false;
            for (User user : users) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    found = true;
                    openMainDashboard(user);
                    break;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error during login: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void openRegisterDialog() {
        RegisterDialog dialog = new RegisterDialog(this);
        dialog.setVisible(true);
    }

    private void openMainDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            new MainDashboard(user).setVisible(true);
            this.dispose();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize data directory and files
                FileHandler.initializeDataDirectory();

                // Create initial users file if it doesn't exist
                File usersFile = new File("data/users.dat");
                if (!usersFile.exists()) {
                    ArrayList<User> users = new ArrayList<>();
                    FileHandler.saveToFile(USERS_FILE, users);
                }

                // Create initial items file if it doesn't exist
                File itemsFile = new File("data/items.dat");
                if (!itemsFile.exists()) {
                    ArrayList<Item> items = new ArrayList<>();
                    FileHandler.saveToFile("items.dat", items);
                }

                new LoginFrame().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Error initializing application: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
