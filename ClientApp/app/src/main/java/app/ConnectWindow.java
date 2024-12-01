package app;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class ConnectWindow {
    private int gameCode = -1;
    JFrame frame;
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

        frame = new JFrame("New game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 170);

        JLabel label = new JLabel("Share your game code: " + gameCode + " or enter a code:");
        label.setBounds(50, 25, 400, 20);
        frame.getContentPane().add(label);

        JTextField input = new JTextField();
        input.setBounds(50, 50, 400, 20);
        frame.getContentPane().add(input);

        JButton button = new JButton("Connect");
        button.setBounds(50, 75, 400, 20);

        frame.getContentPane().add(button);

        responseLabel = new JLabel("");
        responseLabel.setBounds(50, 100, 400, 20);
        frame.getContentPane().add(responseLabel);

        JLabel trash = new JLabel("");
        trash.setBounds(350, 100, 50, 20);
        frame.getContentPane().add(trash);

        button.addActionListener(e -> {
            try {
                int code = Integer.parseInt(input.getText());
                messageHandler.tryJoiningGame(code);
            } catch (NumberFormatException ex) {
                responseLabel.setText("Code must be numeric.");
            }
        });

        frame.setVisible(true);
    }

    public void setGameCode(int code) {
        gameCode = code;
    }

    public void gameFound() {
        System.out.println("Game found");
        frame.dispose();
    }

    public void gameNotFound() {
        responseLabel.setText("Game with this code not found.");
    }

    public void opponentLeft() {
        responseLabel.setText("The player left the game.");
    }
}
