package app;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import java.awt.Image;


public class Board {

    MessageHandler messageHandler;

    JLayeredPane jPane;

    HashMap<Coords, Piece> pieces;

    ArrayList<Move> availableMoves;
    ArrayList<Move> selectableMoves;

    int gameCode;
    boolean isWhite;

    Promotion selectedPromotion;

    public Board()
    {
        jPane = new JLayeredPane();
        jPane.setBounds(0, 0, 1252, 1054);

        pieces = new HashMap<Coords, Piece>();

        availableMoves = new ArrayList<Move>();
        selectableMoves = new ArrayList<Move>();

        gameCode = -1;
        isWhite = true;

        createPane();
    }

    public void setMessageHandler(MessageHandler messageHandler)
    {
        this.messageHandler = messageHandler;
    }

    public JLayeredPane getPane()
    {
        return jPane;
    }

    private void createPane()
    {
        ImageIcon icon = new ImageIcon(Main.class.getResource("/board.png"));
        Image image = icon.getImage();

        JLabel label = new JLabel(new ImageIcon(image));
        label.setBounds(0, 0, 1024, 1024);

        jPane.add(label);
        jPane.setLayer(label, 0);
    }

    public void addAvailableMove(Coords from, Coords to)
    {
        availableMoves.add(new Move(this, from, to));
    }

    public void selectPiece(Coords coords)
    {
        deselectPromotion();
        for (Move move : selectableMoves)
        {
            move.getButton().setVisible(false);
            jPane.remove(move.getButton());
        }
        Piece piece = pieces.get(coords);
        if (piece != null)
        {
            for (Move move : availableMoves)
            {
                if (move.getFrom().equals(coords))
                {
                    selectableMoves.add(move);
                    jPane.add(move.getButton());
                    jPane.setLayer(move.getButton(), 2);
                    move.getButton().setVisible(true);
                }
            }
        }
    }

    public boolean isOccupied(Coords coords)
    {
        return pieces.containsKey(coords);
    }

    public void clientMove(Coords from, Coords to)
    {
        makeMove(from, to);
        deselectPromotion();
        clearMoves();
        messageHandler.sendMove(from, to);
    }

    public void makeMove(Coords from, Coords to)
    {
        Piece piece = pieces.get(from);
        piece.move(to);
        if (isOccupied(to))
        {
            jPane.remove(pieces.get(to).getButton());
            pieces.remove(to);
        }
        pieces.remove(from);
        pieces.put(to, piece);
    }

    private void clearMoves() {
        for (Move move : availableMoves)
        {
            move.getButton().setVisible(false);
            jPane.remove(move.getButton());
        }
        availableMoves.clear();
        selectableMoves.clear();
    }

    public void addPiece(PieceType type, Coords coords)
    {
        Piece piece = new Piece(this, type, coords);
        pieces.put(coords, piece);
        jPane.add(piece.getButton());
        jPane.setLayer(piece.getButton(), 1);
    }

    public void setGameCode(int code)
    {
        gameCode = code;
    }

    public void setToBlack()
    {
        isWhite = false;
    }

    public boolean getIsWhite()
    {
        return isWhite;
    }

    public void deletePiece(Coords coords)
    {
        pieces.get(coords).getButton().setVisible(false);
        jPane.remove(pieces.get(coords).getButton());
        pieces.remove(coords);
    }

    public void selectPromotion(Promotion promotion)
    {
        deselectPromotion();
        selectedPromotion = promotion;
        selectedPromotion.showPromotionButtons();
    }

    private void deselectPromotion()
    {
        if (selectedPromotion != null)
        {
            selectedPromotion.hidePromotionButtons();
            selectedPromotion = null;
        }
    }

    public void clientPromotion(Coords from, Coords to, PieceType type)
    {
        makePromotion(from, to, type);
        deselectPromotion();
        clearMoves();
        messageHandler.sendPromotion(from, to, type);
    }

    public void makePromotion(Coords from, Coords to, PieceType type)
    {
        makeMove(from, to);
        pieces.get(to).changeType(type);
    }

    public void addPromotion(Coords from, Coords to, ArrayList<PieceType> promotionOptions)
    {
        for (Move move : availableMoves)
        {
            if (move.getFrom().equals(from) && move.getTo().equals(to))
            {
                availableMoves.remove(move);
            }
        }

        Promotion promotion = new Promotion(this, from, to, promotionOptions);
        availableMoves.add(promotion);
    }
}
