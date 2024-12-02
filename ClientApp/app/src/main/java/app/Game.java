package app;


import javax.swing.JLayeredPane;

public class Game {

    JLayeredPane window;

    public Game(ChessWebSocketClient client) {
        Board board = new Board();

        window = new JLayeredPane();
        window.setBounds(0, 0, 1024, 1044);
        window.setLayout(null);

        MessageHandler messageHandler = new MessageHandler(client, board);

        client.setHandler(messageHandler);
        board.setMessageHandler(messageHandler);

        window.add(board.getPane(), 0, 0);

        ConnectWindow connectWindow = new ConnectWindow(messageHandler);

        window.add(connectWindow.getPanel(), 1, 1);
    }

    public JLayeredPane getWindow() {
        return window;
    }
}
