package app;


import javax.swing.JFrame;


public class Main {

    static Board board = new Board();
    public static void main(String[] args) {

        JFrame frame = new JFrame("Chess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 1054);

        frame.getContentPane().add(board.getPane());
        frame.setVisible(true);
    }
}