package app;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LeaderboardCard {

    JPanel panel;
    JPanel contentPanel;

    JLabel titleLabel;
    JPanel entries;
    JPanel buttonPanel;

    JPanel entriesPanel;

    int page = 0;

    MessageHandler messageHandler;
    Application application;

    public LeaderboardCard(Application application, MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        this.application = application;

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

        contentPanel.add(Box.createRigidArea(new Dimension(0, 32)));

        titleLabel = new JLabel("Leaderboard:");
        titleLabel.setFont(titleLabel.getFont().deriveFont(42.0f));
        contentPanel.add(titleLabel);
        titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 32)));

        entries = new JPanel();
        entries.setLayout(new BoxLayout(entries, BoxLayout.Y_AXIS));
        entries.setPreferredSize(new Dimension(448, 400));
        entries.setMaximumSize(new Dimension(448, 400));
        entries.setOpaque(false);
        entries.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        contentPanel.add(entries);

        entriesPanel = new JPanel();
        entriesPanel.setLayout(new BoxLayout(entriesPanel, BoxLayout.Y_AXIS));
        entriesPanel.setPreferredSize(new Dimension(448, 400));
        entriesPanel.setMaximumSize(new Dimension(448, 400));
        entriesPanel.setOpaque(false);
        entriesPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        entries.add(entriesPanel);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setPreferredSize(new Dimension(448, 64));
        buttonPanel.setMaximumSize(new Dimension(448, 64));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        contentPanel.add(buttonPanel);

        JButton previousButton = new JButton("Previous");
        previousButton.setPreferredSize(new Dimension(256, 64));
        previousButton.setMaximumSize(new Dimension(256, 64));
        previousButton.setFont(previousButton.getFont().deriveFont(24.0f));
        previousButton.addActionListener(e -> {
            if (page > 0) {
                page--;
                fetchEntries();
            }
        });

        JButton nextButton = new JButton("Next");
        nextButton.setPreferredSize(new Dimension(256, 64));
        nextButton.setMaximumSize(new Dimension(256, 64));
        nextButton.setFont(nextButton.getFont().deriveFont(24.0f));
        nextButton.addActionListener(e -> {
            page++;
            fetchEntries();
        });

        buttonPanel.add(previousButton);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(nextButton);

        contentPanel.add(Box.createVerticalGlue());
    }

    public JPanel getPanel() {
        return panel;
    }

    public void addEntry(int rank, String username, int elo) {
        entriesPanel.add(new LeaderboardEntry(rank, username, elo).getPanel());
        application.repaintMainPanel();

    }

    public void fetchEntries() {
        entriesPanel.setVisible(false);
        entries.remove(entriesPanel);

        entriesPanel = new JPanel();
        entriesPanel.setLayout(new BoxLayout(entriesPanel, BoxLayout.Y_AXIS));
        entriesPanel.setPreferredSize(new Dimension(448, 400));
        entriesPanel.setMaximumSize(new Dimension(448, 400));
        entriesPanel.setOpaque(false);
        entriesPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        entriesPanel.setVisible(true);
        entries.add(entriesPanel);

        messageHandler.getLeaderboard(page);
    }

    public void updateEntries() {
        fetchEntries();
    }

    private class LeaderboardEntry {
        JPanel panel;
        JLabel rankLabel;
        JLabel usernameLabel;
        JLabel eloLabel;

        public LeaderboardEntry(int rank, String username, int elo) {
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            panel.setPreferredSize(new Dimension(448, 40));
            panel.setMaximumSize(new Dimension(448, 40));
            panel.setBackground(Color.LIGHT_GRAY);

            rankLabel = new JLabel(rank + ". ");
            rankLabel.setFont(rankLabel.getFont().deriveFont(24.0f));
            panel.add(rankLabel);

            usernameLabel = new JLabel(username);
            usernameLabel.setFont(usernameLabel.getFont().deriveFont(24.0f));
            panel.add(usernameLabel);

            panel.add(Box.createHorizontalGlue());

            eloLabel = new JLabel("ELO: " + elo);
            eloLabel.setFont(eloLabel.getFont().deriveFont(24.0f));
            panel.add(eloLabel);
        }

        public JPanel getPanel() {
            return panel;
        }
    }
}
