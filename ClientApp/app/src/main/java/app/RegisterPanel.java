package app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class RegisterPanel {
    JPanel panel;
    JPanel contentPanel;

    JLabel title;
    JLabel usernameLabel;
    JTextField usernameField;
    JLabel passwordLabel;
    JPasswordField passwordField;
    JLabel confirmPasswordLabel;
    JPasswordField confirmPasswordField;
    JButton registerButton;
    JLabel responseLabel;

    JLabel loginLabel;
    JButton loginButton;

    public RegisterPanel(LoginCard loginCard, MessageHandler messageHandler) {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        contentPanel = new JPanel();
        contentPanel.setPreferredSize(new Dimension(512, 640));
        contentPanel.setMaximumSize(new Dimension(512, 640));
        contentPanel.setBackground(Color.LIGHT_GRAY);
        panel.add(Box.createVerticalGlue());
        panel.add(contentPanel);
        panel.add(Box.createVerticalGlue());

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(Box.createVerticalGlue());

        title = new JLabel("illChess");
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(64.0f));
        contentPanel.add(title);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 32)));

        usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(usernameLabel.getFont().deriveFont(16.0f));
        contentPanel.add(usernameLabel);
        usernameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        usernameField = new JTextField();
        usernameField.setFont(usernameField.getFont().deriveFont(32.0f));
        usernameField.setMaximumSize(new Dimension(384, 64));
        contentPanel.add(usernameField);
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '"') {
                    e.consume();
                }
            }
        });


        contentPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(passwordLabel.getFont().deriveFont(16.0f));
        contentPanel.add(passwordLabel);
        passwordLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setFont(passwordField.getFont().deriveFont(32.0f));
        passwordField.setMaximumSize(new Dimension(384, 64));
        contentPanel.add(passwordField);
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '"') {
                    e.consume();
                }
            }
        });

        contentPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(confirmPasswordLabel.getFont().deriveFont(16.0f));
        contentPanel.add(confirmPasswordLabel);
        confirmPasswordLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(confirmPasswordField.getFont().deriveFont(32.0f));
        confirmPasswordField.setMaximumSize(new Dimension(384, 64));
        contentPanel.add(confirmPasswordField);
        confirmPasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '"') {
                    e.consume();
                }
            }
        });

        contentPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        registerButton = new JButton("Sign up");
        registerButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        registerButton.setFont(registerButton.getFont().deriveFont(32.0f));
        registerButton.setMaximumSize(new Dimension(384, 64));
        contentPanel.add(registerButton);

        registerButton.addActionListener(e -> {
            if (usernameField.getText().length() == 0 || new String(passwordField.getPassword()).length() == 0 || new String(confirmPasswordField.getPassword()).length() == 0) {
                responseLabel.setForeground(Color.RED);
                responseLabel.setText("Please fill in all fields.");
                return;
            }
            if (!new String(passwordField.getPassword()).equals(new String(confirmPasswordField.getPassword()))) {
                responseLabel.setForeground(Color.RED);
                responseLabel.setText("Passwords do not match.");
                return;
            }

            responseLabel.setForeground(Color.BLACK);
            responseLabel.setText("Registering...");
            messageHandler.sendRegisterRequest(usernameField.getText(), new String(passwordField.getPassword()));
        });

        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        responseLabel = new JLabel("");
        responseLabel.setFont(responseLabel.getFont().deriveFont(24.0f));
        responseLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        responseLabel.setForeground(Color.RED);
        responseLabel.setMaximumSize(new Dimension(384, 32));
        contentPanel.add(responseLabel);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 24)));

        loginLabel = new JLabel("Already have an account?");
        loginLabel.setFont(loginLabel.getFont().deriveFont(24.0f));
        loginLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        contentPanel.add(loginLabel);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        loginButton = new JButton("Log in");
        loginButton.setFont(loginButton.getFont().deriveFont(24.0f));
        loginButton.setMaximumSize(new Dimension(384, 64));
        loginButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        contentPanel.add(loginButton);

        loginButton.addActionListener(e -> {
            loginCard.changeCard("login");
        });

        contentPanel.add(Box.createVerticalGlue());
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setResponseError(String response) {
        responseLabel.setForeground(Color.RED);
        responseLabel.setText(response);
    }
}
