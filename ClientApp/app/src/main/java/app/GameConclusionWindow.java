package app;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

public class GameConclusionWindow {
    static double xScale = 1;
    static double yScale = 1;

    Game game;

    JLayeredPane window;
    JLabel title;
    JLabel comment;

    JLabel opponentLabel;
    JLabel playerLabel;

    JButton button;

    public GameConclusionWindow(Game game, GameConclusionStatus status, int playerRank, int playerDelta, int opponentRank, int opponentDelta) {
        this.game = game;

        window = new JLayeredPane();
        window.setBounds((int) (262 * xScale), (int) (362 * yScale), (int) (500 * xScale), (int) (300 * yScale));
        window.setLayout(null);
        window.setBackground((java.awt.Color.WHITE));
        window.setOpaque(true);

        title = new JLabel();
        title.setBounds((int) (50 * xScale), (int) (0 * yScale), (int) (400 * xScale), (int) (100 * yScale));
        title.setFont(title.getFont().deriveFont(32f));
        setTitleLabel(status);
        title.setHorizontalAlignment(JLabel.CENTER);
        window.add(title, 1, 1);

        comment = new JLabel("");
        comment.setBounds((int) (0 * xScale), (int) (75 * yScale), (int) (500 * xScale), (int) (30 * yScale));
        comment.setFont(comment.getFont().deriveFont(12f));
        comment.setHorizontalAlignment(JLabel.CENTER);
        setCommentLabel(status);
        window.add(comment, 1, 1);

        opponentLabel = new JLabel("Guest");
        opponentLabel.setBounds((int) (50 * xScale), (int) (110 * yScale), (int) (400 * xScale), (int) (40 * yScale));
        opponentLabel.setHorizontalAlignment(JLabel.CENTER);
        opponentLabel.setFont(opponentLabel.getFont().deriveFont(16f));
        window.add(opponentLabel, 1, 1);

        playerLabel = new JLabel("You");
        playerLabel.setBounds((int) (50 * xScale), (int) (140 * yScale), (int) (400 * xScale), (int) (40 * yScale));
        playerLabel.setHorizontalAlignment(JLabel.CENTER);
        playerLabel.setFont(playerLabel.getFont().deriveFont(16f));
        window.add(playerLabel, 1, 1);

        setPlayersLabels(playerRank, playerDelta, opponentRank, opponentDelta);

        button = new JButton("OK");
        button.setBounds((int) (50 * xScale), (int) (200 * yScale), (int) (400 * xScale), (int) (50 * yScale));
        window.add(button, 1);
        button.addActionListener(e -> {
            window.setVisible(false);
            game.closeGameConclusionWindow();
            game.appResetGame();
        });
    }

    public JLayeredPane getWindow() {
        return window;
    }

    public static void staticResize(double xScale, double yScale)
    {
        GameConclusionWindow.xScale = xScale;
        GameConclusionWindow.yScale = yScale;
    }

    public void resize(double xScale, double yScale)
    {
        window.setBounds((int) (262 * xScale), (int) (362 * yScale), (int) (500 * xScale), (int) (300 * yScale));
        title.setBounds((int) (50 * xScale), (int) (0 * yScale), (int) (400 * xScale), (int) (100 * yScale));
        comment.setBounds((int) (0 * xScale), (int) (75 * yScale), (int) (500 * xScale), (int) (30 * yScale));
        button.setBounds((int) (50 * xScale), (int) (200 * yScale), (int) (400 * xScale), (int) (50 * yScale));
        opponentLabel.setBounds((int) (50 * xScale), (int) (110 * yScale), (int) (400 * xScale), (int) (40 * yScale));
        playerLabel.setBounds((int) (50 * xScale), (int) (140 * yScale), (int) (400 * xScale), (int) (40 * yScale));
    }

    void setTitleLabel(GameConclusionStatus status)
    {
        switch (status)
        {
            case WIN:
                title.setText("You Won!");
                title.setForeground(Color.GREEN);
                break;
            case LOSE:
                title.setText("You Lost!");
                title.setForeground(Color.RED);
                break;
            case DRAW_STALEMATE:
            case DRAW_MATERIAL:
            case DRAW_OFFERED:
                title.setText("You drew");
                break;
        }
    }

    void setCommentLabel(GameConclusionStatus stats)
    {
        switch (stats)
        {
            case WIN:
            case LOSE:
                break;
            case DRAW_STALEMATE:
                comment.setText("because there are no legal moves left.");
                break;
            case DRAW_MATERIAL:
                comment.setText("because there is insufficient material to checkmate.");
                break;
            case DRAW_OFFERED:
                comment.setText("because both players agreed to a draw.");
                break;
        }
    }

    void setPlayersLabels(int playerRank, int playerDelta, int opponentRank, int opponentDelta)
    {
        if (game.isRanked())
        {
            String playerDeltaString = playerDelta >= 0 ? "<font color='green'>+" + playerDelta : "<font  color='red'>" + playerDelta;
            String opponentDeltaString = opponentDelta >= 0 ? "<font color='green'>+" + opponentDelta : "<font  color='red'>" + opponentDelta;
            playerLabel.setText("<html>" + game.getPlayerName() + " - " + playerRank + " (" + playerDeltaString + "</font>)</html>");
            opponentLabel.setText("<html>" + game.getOpponentName() + " - " + opponentRank + " (" + opponentDeltaString + "</font>)</html>");
        }
        else
        {
            playerLabel.setText(game.getPlayerName());
            opponentLabel.setText(game.getOpponentName());
        }
    }
}
