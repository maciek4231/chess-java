package app;

import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        ChessWebSocketClient client;
        try {
            client = new ChessWebSocketClient(new URI("ws://localhost:8887"));
            setUpConnection(client);
            Game game = new Game(client);

            JFrame frame = new JFrame("illChess");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1152, 1056);
            frame.getContentPane().add(game.getWindow());
            frame.setVisible(true);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static void setUpConnection(ChessWebSocketClient client) {
        client.connect();
        while (!client.isOpen()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}