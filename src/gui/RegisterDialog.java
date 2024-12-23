package gui;

import models.User;
import utils.ColorScheme;
import utils.FileHandler;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

public class RegisterDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    @SuppressWarnings("unused")
    private static final String USERS_FILE = "users.dat";

    public RegisterDialog(Frame parent) {
        super(parent, "Register New Account", true);
        setSize(450, 520);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setBackground(ColorScheme.BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        // Title
        JLabel titleLabel = new JLabel("Create New Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, gbc);

        // Form fields
        usernameField = createStyledField("Username");
        passwordField = createStyledPasswordField("Password");
        confirmPasswordField = createStyledPasswordField("Confirm Password");
        emailField = createStyledField("Email Address");

        panel.add(usernameField, gbc);
        panel.add(passwordField, gbc);
        panel.add(confirmPasswordField, gbc);
        panel.add(emailField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        JButton registerButton = createStyledButton("Register", true);
        JButton cancelButton = createStyledButton("Cancel", false);

        registerButton.addActionListener(e -> register());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        gbc.insets = new Insets(25, 0, 0, 0);
        panel.add(buttonPanel, gbc);

        add(panel);
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

        // Add more padding inside the field
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
            // Add hover effect for primary button
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

    private void register() {
        try {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String email = emailField.getText().trim();

            // Enhanced validation
            if (username.length() < 3) {
                JOptionPane.showMessageDialog(this,
                        "Username must be at least 3 characters long!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (password.length() < 6) {
                JOptionPane.showMessageDialog(this,
                        "Password must be at least 6 characters long!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this,
                        "Passwords do not match!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(this,
                        "Invalid email format!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Read existing users or create new list
            @SuppressWarnings("unchecked")
            ArrayList<User> users = (ArrayList<User>) FileHandler.readFromFile("users.dat");

            // Check for existing username
            for (User user : users) {
                if (user.getUsername().equalsIgnoreCase(username)) {
                    JOptionPane.showMessageDialog(this,
                            "Username already exists!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Create and save new user
            User newUser = new User(username, password, email, "user");
            users.add(newUser);
            FileHandler.saveToFile("users.dat", users);

            JOptionPane.showMessageDialog(this,
                    "Registration successful! You can now login.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error during registration: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
