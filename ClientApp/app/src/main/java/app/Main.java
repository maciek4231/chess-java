package app;


import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFrame;


public class Main {

    static Board board = new Board();
    static ChessWebSocketClient client;
    static MessageHandler messageHandler;
    public static void main(String[] args) throws URISyntaxException {

        client = new ChessWebSocketClient(new URI("ws://localhost:8887"));
        messageHandler = new MessageHandler(client, board);

        client.setHandler(messageHandler);
        board.setMessageHandler(messageHandler);

        client.connect();


        JFrame frame = new JFrame("Chess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 1054);

        frame.getContentPane().add(board.getPane());
        frame.setVisible(true);

        messageHandler.anounceAvailable();
    }
}