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

    JButton button;

    public GameConclusionWindow(Game game, GameConclusionStatus status, int playerRank, int playerDelta, int opponentRank, int opponentDelta) {
        this.game = game;

        window = new JLayeredPane();
        window.setBounds((int) (262 * xScale), (int) (362 * yScale), (int) (500 * xScale), (int) (300 * yScale));
        window.setLayout(null);
        window.setBackground((java.awt.Color.WHITE));
        window.setOpaque(true);

        title = new JLabel();
        title.setBounds((int) (50 * xScale), (int) (25 * yScale), (int) (400 * xScale), (int) (100 * yScale));
        title.setFont(title.getFont().deriveFont(32f));
        setTitleLabel(status);
        title.setHorizontalAlignment(JLabel.CENTER);
        window.add(title, 1, 1);

        comment = new JLabel("");
        comment.setBounds((int) (0 * xScale), (int) (100 * yScale), (int) (500 * xScale), (int) (30 * yScale));
        comment.setFont(comment.getFont().deriveFont(12f));
        comment.setHorizontalAlignment(JLabel.CENTER);
        setCommentLabel(status);
        window.add(comment, 1, 1);

        button = new JButton("OK");
        button.setBounds((int) (50 * xScale), (int) (150 * yScale), (int) (400 * xScale), (int) (100 * yScale));
        window.add(button, 1);
        button.addActionListener(e -> {
            window.setVisible(false);
            game.closeGameConclusionWindow();
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
        title.setBounds((int) (50 * xScale), (int) (25 * yScale), (int) (400 * xScale), (int) (100 * yScale));
        comment.setBounds((int) (0 * xScale), (int) (100 * yScale), (int) (500 * xScale), (int) (30 * yScale));
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
}
