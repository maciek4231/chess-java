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
import javax.swing.JTextField;

public class StatsCard {

    JPanel panel;
    JPanel contentPanel;

    JLabel usernameLabel;
    JTextField usernameField;
    JButton button;
    JLabel responseLabel;

    JLabel usernameResLabel;

    JLabel eloLabel;
    JLabel gamesLabel;
    JLabel winsLabel;
    JLabel lossesLabel;
    JLabel drawsLabel;

    MessageHandler messageHandler;

    public StatsCard(Application application, MessageHandler messageHandler) {
        this.messageHandler = messageHandler;

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

        usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(usernameLabel.getFont().deriveFont(42.0f));
        contentPanel.add(usernameLabel);
        usernameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 16)));

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

        button = new JButton("Get Stats");
        button.setMaximumSize(new Dimension(384, 64));
        button.setFont(button.getFont().deriveFont(32.0f));
        button.setAlignmentX(JButton.CENTER_ALIGNMENT);
        button.addActionListener(e -> {
            setResponse("");
            if(usernameField.getText().equals("")) {
                setResponse("Please enter a username.");
                return;
            }

            messageHandler.getPlayerStats(usernameField.getText());
        });
        contentPanel.add(button);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        responseLabel = new JLabel("");
        responseLabel.setFont(responseLabel.getFont().deriveFont(24.0f));
        contentPanel.add(responseLabel);
        responseLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 64)));

        usernameResLabel = new JLabel("");
        usernameResLabel.setFont(usernameResLabel.getFont().deriveFont(32.0f));
        contentPanel.add(usernameResLabel);
        usernameResLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        eloLabel = new JLabel("");
        eloLabel.setFont(eloLabel.getFont().deriveFont(24.0f));
        contentPanel.add(eloLabel);
        eloLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        gamesLabel = new JLabel("");
        gamesLabel.setFont(gamesLabel.getFont().deriveFont(24.0f));
        contentPanel.add(gamesLabel);
        gamesLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        winsLabel = new JLabel("");
        winsLabel.setFont(winsLabel.getFont().deriveFont(24.0f));
        contentPanel.add(winsLabel);
        winsLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        lossesLabel = new JLabel("");
        lossesLabel.setFont(lossesLabel.getFont().deriveFont(24.0f));
        contentPanel.add(lossesLabel);
        lossesLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        drawsLabel = new JLabel("");
        drawsLabel.setFont(drawsLabel.getFont().deriveFont(24.0f));
        contentPanel.add(drawsLabel);
        drawsLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        contentPanel.add(Box.createVerticalGlue());
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setStats(String username, int elo, int games, int wins, int losses, int draws) {
        usernameResLabel.setText(username);
        eloLabel.setText("ELO: " + elo);
        gamesLabel.setText("Games: " + games);
        winsLabel.setText("Wins: " + wins);
        lossesLabel.setText("Losses: " + losses);
        drawsLabel.setText("Draws: " + draws);
    }

    public void setResponse(String response) {
        responseLabel.setText(response);
    }
}
