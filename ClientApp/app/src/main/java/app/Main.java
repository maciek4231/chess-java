package app;


import java.net.URI;
import java.net.URISyntaxException;



public class Main {

    Board board;
    ChessWebSocketClient client;
    MessageHandler messageHandler;

    public Main() {
        setUpConnection();

        new GameWindow(board);

        new ConnectWindow(messageHandler);
    }

    public static void main(String[] args) {
        new Main();
    }


    private void setUpConnection() {
        board = new Board();

        try {
            client = new ChessWebSocketClient(new URI("ws://localhost:8887"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        messageHandler = new MessageHandler(client, board);

        client.setHandler(messageHandler);
        board.setMessageHandler(messageHandler);

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