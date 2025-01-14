package app;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ConnectWindow {
    private int gameCode = -1;

    JLayeredPane panel;
    JLabel mainLabel;
    JLabel responseLabel;
    JTextField codeInput;
    JButton connectButton;

    JCheckBox enableRankedGamCheckBox;

    JCheckBox enableTimeCheckBox;
    JLabel timeLabel, timeHoursLabel, timeMinutesLabel, timeSecondsLabel;
    JTextField timeHours, timeMinutes, timeSeconds;
    JLabel incLabel, incHoursLabel, incMinutesLabel, incSecondsLabel;
    JTextField incHours, incMinutes, incSeconds;
    JButton generateCodeButton;
    JLabel generatedCodeLabel;

    Game game;
    MessageHandler messageHandler;
    Application application;

    private static double xScale = 1.0;
    private static double yScale = 1.0;

    public ConnectWindow(MessageHandler messageHandler, Game game, Application application) {
        messageHandler.setConnectWindow(this);
        this.application = application;
        this.game = game;
        this.messageHandler = messageHandler;

        panel = new JLayeredPane();
        panel.setLayout(null);
        panel.setBounds((int) (262 * xScale), (int) (362 * yScale), (int) (500 * xScale), (int) (300 * yScale));
        panel.setBackground((java.awt.Color.WHITE));
        panel.setOpaque(true);

        mainLabel = new JLabel("Enter game code or generate a new code:");
        mainLabel.setBounds((int) (50 * xScale), (int) (25 * yScale), (int) (400 * xScale), (int) (20 * yScale));
        panel.add(mainLabel, 1);

        codeInput = new JTextField();
        codeInput.setBounds((int) (50 * xScale), (int) (50 * yScale), (int) (400 * xScale), (int) (20 * yScale));
        panel.add(codeInput, 1);

        connectButton = new JButton("Connect");
        connectButton.setBounds((int) (50 * xScale), (int) (75 * yScale), (int) (400 * xScale), (int) (20 * yScale));

        panel.add(connectButton, 1);

        codeInput.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    connectButton.doClick();
                }
            }
        });

        responseLabel = new JLabel("");
        responseLabel.setBounds((int) (50 * xScale), (int) (100 * yScale), (int) (400 * xScale), (int) (20 * yScale));
        panel.add(responseLabel, 1);

        connectButton.addActionListener(e -> {
            try {
                int code = Integer.parseInt(codeInput.getText());
                messageHandler.tryJoiningGame(code);
            } catch (NumberFormatException ex) {
                responseLabel.setText("Code must be numeric.");
            }
        });

        creteCodeGeneratingBlock();

        panel.setVisible(true);
    }

    public JLayeredPane getPanel() {
        return panel;
    }

    public void setGameCode(int code) {
        gameCode = code;
        generatedCodeLabel.setText("Share this code: " + code);
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

    public void gameRankedGuestJoining() {
        responseLabel.setText("Guests can't join ranked games.");
    }

    public static void staticResize(double x, double y) {
        xScale = x;
        yScale = y;
    }

    public void resize(double xScale, double yScale) {
        panel.setBounds((int) (262 * xScale), (int) (362 * yScale), (int) (500 * xScale), (int) (300 * yScale));
        codeInput.setBounds((int) (50 * xScale), (int) (50 * yScale), (int) (400 * xScale), (int) (20 * yScale));
        connectButton.setBounds((int) (50 * xScale), (int) (75 * yScale), (int) (400 * xScale), (int) (20 * yScale));
        responseLabel.setBounds((int) (50 * xScale), (int) (100 * yScale), (int) (400 * xScale), (int) (20 * yScale));
        enableRankedGamCheckBox.setBounds((int) (50 * xScale), (int) (125 * yScale), (int) (400 * xScale), (int) (20 * yScale));
        enableTimeCheckBox.setBounds((int) (50 * xScale), (int) (150 * yScale), (int) (400 * xScale), (int) (20 * yScale));
        timeLabel.setBounds((int) (50 * xScale), (int) (175 * yScale), (int) (100 * xScale), (int) (20 * yScale));
        incLabel.setBounds((int) (50 * xScale), (int) (200 * yScale), (int) (100 * xScale), (int) (20 * yScale));
        timeHours.setBounds((int) (150 * xScale), (int) (175 * yScale), (int) (50 * xScale), (int) (20 * yScale));
        timeHoursLabel.setBounds((int) (200 * xScale), (int) (175 * yScale), (int) (20 * xScale), (int) (20 * yScale));
        timeMinutes.setBounds((int) (220 * xScale), (int) (175 * yScale), (int) (50 * xScale), (int) (20 * yScale));
        timeMinutesLabel.setBounds((int) (270 * xScale), (int) (175 * yScale), (int) (20 * xScale), (int) (20 * yScale));
        timeSeconds.setBounds((int) (290 * xScale), (int) (175 * yScale), (int) (50 * xScale), (int) (20 * yScale));
        timeSecondsLabel.setBounds((int) (340 * xScale), (int) (175 * yScale), (int) (20 * xScale), (int) (20 * yScale));
        incHours.setBounds((int) (150 * xScale), (int) (200 * yScale), (int) (50 * xScale), (int) (20 * yScale));
        incHoursLabel.setBounds((int) (200 * xScale), (int) (200 * yScale), (int) (20 * xScale), (int) (20 * yScale));
        incMinutes.setBounds((int) (220 * xScale), (int) (200 * yScale), (int) (50 * xScale), (int) (20 * yScale));
        incMinutesLabel.setBounds((int) (270 * xScale), (int) (200 * yScale), (int) (20 * xScale), (int) (20 * yScale));
        incSeconds.setBounds((int) (290 * xScale), (int) (200 * yScale), (int) (50 * xScale), (int) (20 * yScale));
        incSecondsLabel.setBounds((int) (340 * xScale), (int) (200 * yScale), (int) (20 * xScale), (int) (20 * yScale));
        generateCodeButton.setBounds((int) (50 * xScale), (int) (225 * yScale), (int) (400 * xScale), (int) (20 * yScale));
        generatedCodeLabel.setBounds((int) (50 * xScale), (int) (250 * yScale), (int) (400 * xScale), (int) (20 * yScale));
    }

    void creteCodeGeneratingBlock() {
        enableRankedGamCheckBox = new JCheckBox("Enable ranked game");
        enableRankedGamCheckBox.setBounds((int) (50 * xScale), (int) (125 * yScale), (int) (400 * xScale), (int) (20 * yScale));
        panel.add(enableRankedGamCheckBox, 1);
        enableRankedGamCheckBox.setBackground(new Color(0, 0, 0, 0));
        enableRankedGamCheckBox.setEnabled(application.isLoggedIn());

        enableTimeCheckBox = new JCheckBox("Enable timed game");
        enableTimeCheckBox.setBounds((int) (50 * xScale), (int) (150 * yScale), (int) (400 * xScale), (int) (20 * yScale));
        panel.add(enableTimeCheckBox, 1);
        enableTimeCheckBox.setBackground(new Color(0, 0, 0, 0));

        timeLabel = new JLabel("Time:");
        timeLabel.setBounds((int) (50 * xScale), (int) (175 * yScale), (int) (100 * xScale), (int) (20 * yScale));
        panel.add(timeLabel, 1);

        timeHours = new JTextField("00");
        timeHours.setBounds((int) (150 * xScale), (int) (175 * yScale), (int) (50 * xScale), (int) (20 * yScale));
        panel.add(timeHours, 1);
        timeHours.setEnabled(false);
        timeHours.setHorizontalAlignment(JTextField.RIGHT);

        timeHoursLabel = new JLabel("H");
        timeHoursLabel.setBounds((int) (200 * xScale), (int) (175 * yScale), (int) (20 * xScale), (int) (20 * yScale));
        panel.add(timeHoursLabel, 1);

        timeMinutes = new JTextField("00");
        timeMinutes.setBounds((int) (220 * xScale), (int) (175 * yScale), (int) (50 * xScale), (int) (20 * yScale));
        panel.add(timeMinutes, 1);
        timeMinutes.setEnabled(false);
        timeMinutes.setHorizontalAlignment(JTextField.RIGHT);

        timeMinutesLabel = new JLabel("M");
        timeMinutesLabel.setBounds((int) (270 * xScale), (int) (175 * yScale), (int) (20 * xScale), (int) (20 * yScale));
        panel.add(timeMinutesLabel, 1);

        timeSeconds = new JTextField("00");
        timeSeconds.setBounds((int) (290 * xScale), (int) (175 * yScale), (int) (50 * xScale), (int) (20 * yScale));
        panel.add(timeSeconds, 1);
        timeSeconds.setEnabled(false);
        timeSeconds.setHorizontalAlignment(JTextField.RIGHT);

        timeSecondsLabel = new JLabel("S");
        timeSecondsLabel.setBounds((int) (340 * xScale), (int) (175 * yScale), (int) (20 * xScale), (int) (20 * yScale));
        panel.add(timeSecondsLabel, 1);

        incLabel = new JLabel("Increment:");
        incLabel.setBounds((int) (50 * xScale), (int) (200 * yScale), (int) (100 * xScale), (int) (20 * yScale));
        panel.add(incLabel, 1);

        incHours = new JTextField("00");
        incHours.setBounds((int) (150 * xScale), (int) (200 * yScale), (int) (50 * xScale), (int) (20 * yScale));
        panel.add(incHours, 1);
        incHours.setEnabled(false);
        incHours.setHorizontalAlignment(JTextField.RIGHT);

        incHoursLabel = new JLabel("H");
        incHoursLabel.setBounds((int) (200 * xScale), (int) (200 * yScale), (int) (20 * xScale), (int) (20 * yScale));
        panel.add(incHoursLabel, 1);

        incMinutes = new JTextField("00");
        incMinutes.setBounds((int) (220 * xScale), (int) (200 * yScale), (int) (50 * xScale), (int) (20 * yScale));
        panel.add(incMinutes, 1);
        incMinutes.setEnabled(false);
        incMinutes.setHorizontalAlignment(JTextField.RIGHT);

        incMinutesLabel = new JLabel("M");
        incMinutesLabel.setBounds((int) (270 * xScale), (int) (200 * yScale), (int) (20 * xScale), (int) (20 * yScale));
        panel.add(incMinutesLabel, 1);

        incSeconds = new JTextField("00");
        incSeconds.setBounds((int) (290 * xScale), (int) (200 * yScale), (int) (50 * xScale), (int) (20 * yScale));
        panel.add(incSeconds, 1);
        incSeconds.setEnabled(false);
        incSeconds.setHorizontalAlignment(JTextField.RIGHT);

        incSecondsLabel = new JLabel("S");
        incSecondsLabel.setBounds((int) (340 * xScale), (int) (200 * yScale), (int) (20 * xScale), (int) (20 * yScale));
        panel.add(incSecondsLabel, 1);

        generateCodeButton = new JButton("Generate code");
        generateCodeButton.setBounds((int) (50 * xScale), (int) (225 * yScale), (int) (400 * xScale), (int) (20 * yScale));
        panel.add(generateCodeButton, 1);

        generatedCodeLabel = new JLabel("");
        generatedCodeLabel.setBounds((int) (50 * xScale), (int) (250 * yScale), (int) (400 * xScale), (int) (20 * yScale));
        panel.add(generatedCodeLabel, 1);

        generateCodeButton.addActionListener(e -> {
            boolean rankedEnabled = enableRankedGamCheckBox.isSelected();
            boolean timeEnabled = enableTimeCheckBox.isSelected();
            int timeHours = 0;
            int timeMinutes = 0;
            int timeSeconds = 0;
            int incHours = 0;
            int incMinutes = 0;
            int incSeconds = 0;
            if (timeEnabled)
            {
                try {
                    timeHours = Integer.parseInt(this.timeHours.getText());
                    timeMinutes = Integer.parseInt(this.timeMinutes.getText());
                    timeSeconds = Integer.parseInt(this.timeSeconds.getText());
                    incHours = Integer.parseInt(this.incHours.getText());
                    incMinutes = Integer.parseInt(this.incMinutes.getText());
                    incSeconds = Integer.parseInt(this.incSeconds.getText());
                } catch (NumberFormatException ex) {
                    generatedCodeLabel.setText("Time and increment must be numeric.");
                    return;
                }

                if (timeHours < 0 || timeMinutes < 0 || timeSeconds < 0 || incHours < 0 || incMinutes < 0 || incSeconds < 0)
                {
                    generatedCodeLabel.setText("Time and increment must be positive.");
                    return;
                }
            }

            if (gameCode != -1)
            {
                // TODO: Implement abandoning codes.
            }

            int time = timeHours * 3600 + timeMinutes * 60 + timeSeconds;
            int inc = incHours * 3600 + incMinutes * 60 + incSeconds;

            messageHandler.announceAvailable(rankedEnabled, timeEnabled, time, inc);
        });

        enableTimeCheckBox.addActionListener(e -> {
            boolean enabled = enableTimeCheckBox.isSelected();
            timeHours.setEnabled(enabled);
            timeMinutes.setEnabled(enabled);
            timeSeconds.setEnabled(enabled);
            incHours.setEnabled(enabled);
            incMinutes.setEnabled(enabled);
            incSeconds.setEnabled(enabled);
        });
    }

    public void logIn() // this is only used to enable the ranked checkbox
    {
        enableRankedGamCheckBox.setEnabled(true);
    }
}
