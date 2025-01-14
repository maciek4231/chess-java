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

    JLabel eloLabel;
    JLabel gamesLabel;
    JLabel winsLabel;
    JLabel lossesLabel;
    JLabel drawsLabel;

    JButton logoutButton;

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

        contentPanel.add(Box.createRigidArea(new Dimension(0, 128)));

        usernameLabel = new JLabel("");
        usernameLabel.setFont(usernameLabel.getFont().deriveFont(32.0f));
        contentPanel.add(usernameLabel);
        usernameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        eloLabel = new JLabel("Elo: 0");
        eloLabel.setFont(eloLabel.getFont().deriveFont(24.0f));
        contentPanel.add(eloLabel);
        eloLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        gamesLabel = new JLabel("Games: 0");
        gamesLabel.setFont(gamesLabel.getFont().deriveFont(24.0f));
        contentPanel.add(gamesLabel);
        gamesLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        winsLabel = new JLabel("Wins: 0");
        winsLabel.setFont(winsLabel.getFont().deriveFont(24.0f));
        contentPanel.add(winsLabel);
        winsLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        lossesLabel = new JLabel("Losses: 0");
        lossesLabel.setFont(lossesLabel.getFont().deriveFont(24.0f));
        contentPanel.add(lossesLabel);
        lossesLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        drawsLabel = new JLabel("Draws: 0");
        drawsLabel.setFont(drawsLabel.getFont().deriveFont(24.0f));
        contentPanel.add(drawsLabel);
        drawsLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 64)));

        logoutButton = new JButton("Log out");
        logoutButton.setFont(logoutButton.getFont().deriveFont(32.0f));
        logoutButton.setMaximumSize(new Dimension(384, 64));
        logoutButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        contentPanel.add(logoutButton);

        logoutButton.addActionListener(e -> {
            messageHandler.sendLogoutRequest();
        });

        contentPanel.add(Box.createVerticalGlue());
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setUsername(String username) {
        usernameLabel.setText(username);
    }

    public void setStats(int elo, int games, int wins, int losses, int draws) {
        eloLabel.setText("Elo: " + elo);
        gamesLabel.setText("Games: " + games);
        winsLabel.setText("Wins: " + wins);
        lossesLabel.setText("Losses: " + losses);
        drawsLabel.setText("Draws: " + draws);
    }
}
