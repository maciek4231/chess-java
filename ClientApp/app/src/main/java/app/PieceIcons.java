package app;

import java.util.HashMap;

import javax.swing.ImageIcon;

public class PieceIcons {
    private static final PieceIcons instance = new PieceIcons();

    HashMap<PieceType, ImageIcon> icons;

    private PieceIcons() {
        loadIcons();
    }

    public static ImageIcon getIcon(PieceType type) {
        return instance.icons.get(type);
    }

    private void loadIcons() {
        icons = new HashMap<PieceType, ImageIcon>();

        icons.put(PieceType.W_PAWN, new ImageIcon(getClass().getResource("/white-pawn.png")));
        icons.put(PieceType.W_ROOK, new ImageIcon(getClass().getResource("/white-rook.png")));
        icons.put(PieceType.W_KNIGHT, new ImageIcon(getClass().getResource("/white-knight.png")));
        icons.put(PieceType.W_BISHOP, new ImageIcon(getClass().getResource("/white-bishop.png")));
        icons.put(PieceType.W_QUEEN, new ImageIcon(getClass().getResource("/white-queen.png")));
        icons.put(PieceType.W_KING, new ImageIcon(getClass().getResource("/white-king.png")));
        icons.put(PieceType.B_PAWN, new ImageIcon(getClass().getResource("/black-pawn.png")));
        icons.put(PieceType.B_ROOK, new ImageIcon(getClass().getResource("/black-rook.png")));
        icons.put(PieceType.B_KNIGHT, new ImageIcon(getClass().getResource("/black-knight.png")));
        icons.put(PieceType.B_BISHOP, new ImageIcon(getClass().getResource("/black-bishop.png")));
        icons.put(PieceType.B_QUEEN, new ImageIcon(getClass().getResource("/black-queen.png")));
        icons.put(PieceType.B_KING, new ImageIcon(getClass().getResource("/black-king.png")));
    }
}
