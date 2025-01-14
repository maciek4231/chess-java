package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import java.awt.Color;
import java.awt.Image;
import java.time.ZonedDateTime;

public class Board {

    MessageHandler messageHandler;

    JLayeredPane jPane;

    ImageIcon icon;
    Image image;
    JLabel boardLabel;

    HashMap<Coords, Piece> pieces;

    ArrayList<Move> availableMoves;
    ArrayList<Move> selectableMoves;

    int gameCode;
    boolean isWhite;

    Promotion selectedPromotion;
    Piece checkedPiece;

    RequestButton surrenderButton;
    RequestButton drawButton;
    RequestButton rewindButton;

    Queue<PopUpWindow> popUpWindowQueue;
    boolean surrenderActive = false;
    boolean drawActive = false;
    boolean rewindActive = false;

    Clock playerClock;
    Clock opponentClock;
    Timer clockTimer;
    PlayerLabel playerLabel;
    PlayerLabel opponentLabel;

    public Board() {
        jPane = new JLayeredPane();
        jPane.setBounds(0, 0, 1252, 1024);

        pieces = new HashMap<Coords, Piece>();

        availableMoves = new ArrayList<Move>();
        selectableMoves = new ArrayList<Move>();

        gameCode = -1;
        isWhite = true;

        popUpWindowQueue = new java.util.LinkedList<PopUpWindow>();

        createPane();
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public JLayeredPane getPane() {
        return jPane;
    }

    private void createPane() {
        icon = new ImageIcon(Main.class.getResource("/board.png"));
        image = icon.getImage();

        boardLabel = new JLabel(new ImageIcon(image));
        boardLabel.setBounds(0, 0, 1024, 1024);

        jPane.add(boardLabel);
        jPane.setLayer(boardLabel, 0);

        surrenderButton = new SurrenderButton(this);
        jPane.add(surrenderButton.getButton());
        jPane.setLayer(surrenderButton.getButton(), 1);

        drawButton = new OfferDrawButton(this);
        jPane.add(drawButton.getButton());
        jPane.setLayer(drawButton.getButton(), 1);

        rewindButton = new RewindButton(this);
        jPane.add(rewindButton.getButton());
        jPane.setLayer(rewindButton.getButton(), 1);

        createClocks();
        playerLabel = new PlayerLabel(1024, 512);
        jPane.add(playerLabel.getLabel());
        jPane.setLayer(playerLabel.getLabel(), 4);
        opponentLabel = new PlayerLabel(1024, 448);
        jPane.add(opponentLabel.getLabel());
        jPane.setLayer(opponentLabel.getLabel(), 4);
    }

    public void addAvailableMove(Coords from, Coords to) {
        for (Move move : availableMoves) {
            if (move.getFrom().equals(from) && move.getTo().equals(to)) {
                return;
            }
        }
        availableMoves.add(new Move(this, from, to));
    }

    public void selectPiece(Coords coords) {
        deselectPromotion();
        for (Move move : selectableMoves) {
            move.getButton().setVisible(false);
            jPane.remove(move.getButton());
        }
        Piece piece = pieces.get(coords);
        if (piece != null) {
            for (Move move : availableMoves) {
                if (move.getFrom().equals(coords)) {
                    selectableMoves.add(move);
                    jPane.add(move.getButton());
                    jPane.setLayer(move.getButton(), 2);
                    move.getButton().setVisible(true);
                }
            }
        }
    }

    public boolean isOccupied(Coords coords) {
        return pieces.containsKey(coords);
    }

    public void clientMove(Coords from, Coords to) {
        makeMove(from, to);
        deselectPromotion();
        clearMoves();
        messageHandler.sendMove(from, to);
    }

    public void makeMove(Coords from, Coords to) {
        uncheckPiece();
        Piece piece = pieces.get(from);
        piece.move(to);
        if (isOccupied(to)) {
            jPane.remove(pieces.get(to).getButton());
            pieces.remove(to);
        }
        pieces.remove(from);
        pieces.put(to, piece);
    }

    private void clearMoves() {
        for (Move move : availableMoves) {
            move.getButton().setVisible(false);
            jPane.remove(move.getButton());
        }
        availableMoves.clear();
        selectableMoves.clear();
    }

    public void addPiece(PieceType type, Coords coords) {
        Piece piece = new Piece(this, type, coords);
        pieces.put(coords, piece);
        jPane.add(piece.getButton());
        jPane.setLayer(piece.getButton(), 1);
    }

    public void setGameCode(int code) {
        gameCode = code;
    }

    public void setToBlack() {
        isWhite = false;
    }

    public boolean getIsWhite() {
        return isWhite;
    }

    public void deletePiece(Coords coords) {
        pieces.get(coords).getButton().setVisible(false);
        jPane.remove(pieces.get(coords).getButton());
        pieces.remove(coords);
    }

    public void selectPromotion(Promotion promotion) {
        deselectPromotion();
        selectedPromotion = promotion;
        selectedPromotion.showPromotionButtons();
        moveClocksDown();
    }

    private void deselectPromotion() {
        if (selectedPromotion != null) {
            selectedPromotion.hidePromotionButtons();
            selectedPromotion = null;
        }
        moveClocksNormal();
    }

    public void clientPromotion(Coords from, Coords to, PieceType type) {
        makeMove(from, to);
        makePromotion(from, to, type);
        deselectPromotion();
        clearMoves();
        messageHandler.sendPromotion(from, to, type);
    }

    public void makePromotion(Coords from, Coords to, PieceType type) {
        System.out.println(from + "\n" + to);
        pieces.get(to).changeType(type);
    }

    public void addPromotion(Coords from, Coords to, ArrayList<PieceType> promotionOptions) {
        for (Move move : availableMoves) {
            if (move.getFrom().equals(from) && move.getTo().equals(to)) {
                availableMoves.remove(move);
            }
        }

        Promotion promotion = new Promotion(this, from, to, promotionOptions);
        availableMoves.add(promotion);
    }

    public void checkPiece(Coords coords) {
        Piece piece = pieces.get(coords);
        if (piece != null) {
            piece.getButton().setBackground(new Color(255, 70, 70));
            piece.getButton().setOpaque(true);
        }
        this.checkedPiece = piece;
    }

    private void uncheckPiece() {
        if (checkedPiece != null) {
            checkedPiece.getButton().setBackground(new Color(0, 0, 0, 0));
            checkedPiece.getButton().setOpaque(false);
        }
        this.checkedPiece = null;
    }

    public void resize(double xScale, double yScale) { // TODO: Seperate static resizes
        jPane.setBounds(0, 0, (int) (1252 * xScale), (int) (1024 * yScale));
        boardLabel.setBounds(0, 0, (int) (1024 * xScale), (int) (1024 * yScale));
        icon.setImage(image.getScaledInstance((int) (1024 * xScale), (int) (1024 * yScale), Image.SCALE_SMOOTH));
        boardLabel.setIcon(icon);
        IconLoader.resizeIcons(xScale, yScale);
        Piece.staticResize(xScale, yScale);
        for (Piece piece : pieces.values()) {
            piece.resize(xScale, yScale);
        }
        Move.staticResize(xScale, yScale);
        for (Move move : availableMoves) {
            move.resize(xScale, yScale);
        }
        RequestButton.staticResize(xScale, yScale);
        surrenderButton.resize(xScale, yScale);
        drawButton.resize(xScale, yScale);
        rewindButton.resize(xScale, yScale);
        PopUpWindow.staticResize(xScale, yScale);
        for (PopUpWindow popUpWindow : popUpWindowQueue) {
            popUpWindow.resize(xScale, yScale);
        }
        Clock.staticResize(xScale, yScale);
        playerClock.resize(xScale, yScale);
        opponentClock.resize(xScale, yScale);
        PlayerLabel.staticResize(xScale, yScale);
        playerLabel.resize(xScale, yScale);
        opponentLabel.resize(xScale, yScale);
    }

    public void addPopUpWindow(PopUpWindow popUpWindow) {
        popUpWindowQueue.add(popUpWindow);
        if (popUpWindowQueue.size() == 1) {
            jPane.add(popUpWindow.getPanel());
            jPane.setLayer(popUpWindow.getPanel(), 3);
        }
    }

    public void cyclePopUpWindows() {
        popUpWindowQueue.peek().hidePanel();
        jPane.remove(popUpWindowQueue.poll().getPanel());
        if (popUpWindowQueue.size() > 0) {
            jPane.add(popUpWindowQueue.peek().getPanel());
            jPane.setLayer(popUpWindowQueue.peek().getPanel(), 3);
        }
    }

    public void clearPopUpWindows() {
        if (popUpWindowQueue.size() == 0) {
            return;
        }
        popUpWindowQueue.peek().hidePanel();
        jPane.remove(popUpWindowQueue.poll().getPanel());
        popUpWindowQueue.clear();
    }

    public void setSurrenderActive(boolean active) {
        surrenderActive = active;
        surrenderButton.setActive(active);
    }

    public void setDrawActive(boolean active) {
        drawActive = active;
        drawButton.setActive(active);
    }

    public void setRewindActive(boolean active) {
        rewindActive = active;
        rewindButton.setActive(active);
    }

    public void endGame() {
        clearMoves();
        deselectPromotion();
        clearPopUpWindows();
        setSurrenderActive(false);
        setDrawActive(false);
        setRewindActive(false);

        if (clockTimer != null) {
            clockTimer.cancel();
        }
    }

    public void startMyMove() {
        setDrawActive(true);
    }

    public void endMyMove() {
        setDrawActive(false);
    }

    void moveClocksNormal() {
        playerClock.move(1024, 576);
        opponentClock.move(1024, 384);
        playerLabel.setVisible(true);
        opponentLabel.setVisible(true);
    }

    void moveClocksDown() {
        playerClock.move(1024, 576);
        opponentClock.move(1024, 512);
        playerLabel.setVisible(false);
        opponentLabel.setVisible(false);
    }

    void createClocks() {
        playerClock = new Clock(1024, 576);
        jPane.add(playerClock.getLabel());
        jPane.setLayer(playerClock.getLabel(), 4);
        opponentClock = new Clock(1024, 384);
        jPane.add(opponentClock.getLabel());
        jPane.setLayer(opponentClock.getLabel(), 4);

        clockTimer = new Timer();
        clockTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                playerClock.update();
                opponentClock.update();
            }
        }, 0, 100);
    }

    public void updatePlayerClock(ZonedDateTime timeStamp) {
        playerClock.updateTimeStamp(timeStamp);
    }

    public void updateOpponentClock(ZonedDateTime timeStamp) {
        opponentClock.updateTimeStamp(timeStamp);
    }

    public void setPlayerClockActive(boolean active) {
        playerClock.setRunning(active);
    }

    public void setOpponentClockActive(boolean active) {
        opponentClock.setRunning(active);
    }

    public void setPlayerClockVisible(boolean visible) {
        playerClock.setVisible(visible);
    }

    public void setOpponentClockVisible(boolean visible) {
        opponentClock.setVisible(visible);
    }

    public void rewindMove() {
        deselectPromotion();
        for (Move move : selectableMoves) {
            move.getButton().setVisible(false);
            jPane.remove(move.getButton());
        }
        selectableMoves.clear();
        availableMoves.clear();
        for (Piece piece : pieces.values()) {
            piece.getButton().setVisible(false);
            jPane.remove(piece.getButton());
        }
        pieces.clear();
    }

    public void setPlayerName(String playerName) {
        playerLabel.setText(playerName);
    }

    public void setOpponentName(String opponentName) {
        opponentLabel.setText(opponentName);
    }
}
