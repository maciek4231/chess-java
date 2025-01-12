package app;


import javax.swing.JLayeredPane;

public class Game {

    JLayeredPane window;
    PromptWindow promptWindow;
    Board board;
    ConnectWindow connectWindow;
    GameConclusionWindow gameConclusionWindow;

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

        // showGameConclusionWindow(new GameConclusionWindow(this, GameConclusionStatus.DRAW_STALEMATE, 0, 0, 0, 0)); // TODO: Remove this line
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
        GameConclusionWindow.staticResize(xScale, yScale);
        if (gameConclusionWindow != null) {
            gameConclusionWindow.resize(xScale, yScale);
        }
    }

    public void endGame() {
        board.endGame();
    }

    public void showGameConclusionWindow(GameConclusionWindow gameConclusionWindow) {
        this.gameConclusionWindow = gameConclusionWindow;
        window.add(gameConclusionWindow.getWindow(), 3, 3);
    }

    public void closeGameConclusionWindow() {
        window.remove(gameConclusionWindow.getWindow());
        gameConclusionWindow = null;
    }
}
