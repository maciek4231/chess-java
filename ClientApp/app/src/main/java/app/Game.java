package app;

public class Game {
    public Game(ChessWebSocketClient client) {
        Board board = new Board();

        MessageHandler messageHandler = new MessageHandler(client, board);

        client.setHandler(messageHandler);
        board.setMessageHandler(messageHandler);

        new GameWindow(board);

        new ConnectWindow(messageHandler);
    }
}
