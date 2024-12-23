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
    JLabel label;
    JTextField input;
    JButton button;
    Game game;

    private static double xScale = 1.0;
    private static double yScale = 1.0;

    public ConnectWindow(MessageHandler messageHandler, Game game) {
        messageHandler.setConnectWindow(this);
        messageHandler.anounceAvailable();
        this.game = game;
        while (gameCode == -1) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        panel = new JLayeredPane();
        panel.setLayout(null);
        panel.setBounds((int) (262 * xScale), (int) (457 * yScale), (int) (500 * xScale), (int) (150 * yScale));
        panel.setBackground((java.awt.Color.WHITE));
        panel.setOpaque(true);

        label = new JLabel("Share your game code: " + gameCode + " or enter a code:");
        label.setBounds((int) (50 * xScale), (int) (25 * yScale), (int) (400 * xScale), (int) (20 * yScale));
        panel.add(label, 1);


        input = new JTextField();
        input.setBounds((int) (50 * xScale), (int) (50 * yScale), (int) (400 * xScale), (int) (20 * yScale));
        panel.add(input, 1);

        button = new JButton("Connect");
        button.setBounds((int) (50 * xScale), (int) (75 * yScale), (int) (400 * xScale), (int) (20 * yScale));

        panel.add(button, 1);

        input.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    button.doClick();
                }
            }
        });

        responseLabel = new JLabel("");
        responseLabel.setBounds((int) (50 * xScale), (int) (100 * yScale), (int) (400 * xScale), (int) (20 * yScale));
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
        game.closeConnectWindow();
    }

    public void gameNotFound() {
        responseLabel.setText("Game with this code not found.");
    }

    public void opponentLeft() {
        responseLabel.setText("The player left the game.");
    }

    public static void staticResize(double x, double y) {
        xScale = x;
        yScale = y;
    }

    public void resize(double xScale, double yScale) {
        panel.setBounds((int) (262 * xScale), (int) (457 * yScale), (int) (500 * xScale), (int) (150 * yScale));
        label.setBounds((int) (50 * xScale), (int) (25 * yScale), (int) (400 * xScale), (int) (20 * yScale));
        input.setBounds((int) (50 * xScale), (int) (50 * yScale), (int) (400 * xScale), (int) (20 * yScale));
        button.setBounds((int) (50 * xScale), (int) (75 * yScale), (int) (400 * xScale), (int) (20 * yScale));
        responseLabel.setBounds((int) (50 * xScale), (int) (100 * yScale), (int) (400 * xScale), (int) (20 * yScale));
    }
}
