package app;

import javax.swing.JFrame;

public class GameWindow {
    public GameWindow(Board board) {


        JFrame frame = new JFrame("illChess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 1054);

        frame.getContentPane().add(board.getPane());
        frame.setVisible(true);
    }
}
