package app;


import java.net.URI;
import java.net.URISyntaxException;



public class Main {

    public static void main(String[] args) {
        ChessWebSocketClient client;
        try {
            client = new ChessWebSocketClient(new URI("ws://localhost:8887"));
            setUpConnection(client);
            new Game(client);
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