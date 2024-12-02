package app;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ConnectWindow {
    private int gameCode = -1;
    JLayeredPane panel;
    JLabel responseLabel;

    public ConnectWindow(MessageHandler messageHandler) {
        messageHandler.setConnectWindow(this);
        messageHandler.anounceAvailable();
        while (gameCode == -1) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        panel = new JLayeredPane();
        panel.setLayout(null);
        panel.setBounds(262, 457, 500, 150);
        panel.setBackground((java.awt.Color.WHITE));
        panel.setOpaque(true);

        JLabel label = new JLabel("Share your game code: " + gameCode + " or enter a code:");
        label.setBounds(50, 25, 400, 20);
        panel.add(label, 1);


        JTextField input = new JTextField();
        input.setBounds(50, 50, 400, 20);
        panel.add(input, 1);

        JButton button = new JButton("Connect");
        button.setBounds(50, 75, 400, 20);

        panel.add(button, 1);

        input.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    button.doClick();
                }
            }
        });

        responseLabel = new JLabel("");
        responseLabel.setBounds(50, 100, 400, 20);
        panel.add(responseLabel, 1);

        button.addActionListener(e -> {
            try {
                int code = Integer.parseInt(input.getText());
                messageHandler.tryJoiningGame(code);
            } catch (NumberFormatException ex) {
                responseLabel.setText("Code must be numeric.");
            }
        });

        panel.setVisible(true);
    }

    public JLayeredPane getPanel() {
        return panel;
    }

    public void setGameCode(int code) {
        gameCode = code;
    }

    public void gameFound() {
        System.out.println("Game found");
        panel.setVisible(false);
        panel = null;
    }

    public void gameNotFound() {
        responseLabel.setText("Game with this code not found.");
    }

    public void opponentLeft() {
        responseLabel.setText("The player left the game.");
    }
}
