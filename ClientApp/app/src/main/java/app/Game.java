package app;


import javax.swing.JLayeredPane;

public class Game {
    Application application;

    JLayeredPane window;
    PromptWindow promptWindow;
    Board board;
    ConnectWindow connectWindow;
    GameConclusionWindow gameConclusionWindow;
    String playerName = "You", opponentName = "Opp";
    boolean isRanked = false, isTimed = false;
    MessageHandler messageHandler;

    public Game(Application application, MessageHandler messageHandler) {
        this.application = application;
        board = new Board();

        window = new JLayeredPane();
        window.setBounds(0, 0, 1252, 1024);
        window.setLayout(null);

        board.setMessageHandler(messageHandler);

        window.add(board.getPane(), 0, 0);

        connectWindow = new ConnectWindow(messageHandler, this, application);

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

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public void setRanked(boolean ranked) {
        isRanked = ranked;
    }

    public void setTimed(boolean timed) {
        isTimed = timed;
        board.setPlayerClockVisible(timed);
        board.setOpponentClockVisible(timed);
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public boolean isRanked() {
        return isRanked;
    }

    public boolean isTimed() {
        return isTimed;
    }

    public void appResetGame() {
        application.resetGame();
    }

    public Board getBoard() {
        return board;
    }

    public void logIn() // this is only used to enable the ranked checkbox in the connect window
    {
        connectWindow.logIn();
    }
}
