package app;


import javax.swing.JLayeredPane;

public class Game {

    JLayeredPane window;
    PromptWindow promptWindow;
    Board board;
    ConnectWindow connectWindow;

    public Game(ChessWebSocketClient client) {
        board = new Board();

        window = new JLayeredPane();
        window.setBounds(0, 0, 1252, 1024);
        window.setLayout(null);

        MessageHandler messageHandler = new MessageHandler(client, this, board);

        client.setHandler(messageHandler);
        board.setMessageHandler(messageHandler);

        window.add(board.getPane(), 0, 0);

        connectWindow = new ConnectWindow(messageHandler, this);

        window.add(connectWindow.getPanel(), 1, 1);
    }

    public JLayeredPane getWindow() {
        return window;
    }

    public void showPromptWindow(String message) {
        promptWindow = new PromptWindow(this, message);
        window.add(promptWindow.getPanel(), 2, 2);
    }

    public void closePromptWindow() {
        window.remove(promptWindow.getPanel());
        promptWindow = null;
    }

    public void closeConnectWindow() {
        connectWindow = null;
    }

    public void resize(double xScale, double yScale) {
        window.setBounds(0, 0, (int) (1252 * xScale), (int) (1024 * yScale));
        board.resize(xScale, yScale);
        ConnectWindow.staticResize(xScale, yScale);
        if (connectWindow != null)
        {
            connectWindow.resize(xScale, yScale);
        }
        PromptWindow.staticResize(xScale, yScale);
        if (promptWindow != null) {
            promptWindow.resize(xScale, yScale);
        }
    }

    public void endGame() {
        board.endGame();
    }
}
