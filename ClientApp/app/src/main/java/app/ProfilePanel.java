package app;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ProfilePanel {
    JPanel panel;
    JPanel contentPanel;

    JLabel title;
    JLabel usernameLabel;

    JLabel registerLabel;
    JButton registerButton;

    public ProfilePanel(LoginCard loginCard, MessageHandler messageHandler) {
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

        contentPanel.add(Box.createRigidArea(new Dimension(0, 192)));

        usernameLabel = new JLabel("Logged in as:");
        usernameLabel.setFont(usernameLabel.getFont().deriveFont(32.0f));
        contentPanel.add(usernameLabel);
        usernameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 64)));

        registerButton = new JButton("Log out");
        registerButton.setFont(registerButton.getFont().deriveFont(32.0f));
        registerButton.setMaximumSize(new Dimension(384, 64));
        registerButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        contentPanel.add(registerButton);

        registerButton.addActionListener(e -> {
            messageHandler.sendLogoutRequest();
        });

        contentPanel.add(Box.createVerticalGlue());
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setUsername(String username) {
        usernameLabel.setText("Logged in as: " + username);
    }
}
