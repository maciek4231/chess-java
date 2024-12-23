package app;

import java.util.HashMap;

import javax.swing.ImageIcon;

public class IconLoader {
    private static final IconLoader instance = new IconLoader();

    HashMap<PieceType, ImageIcon> pieceIcons;
    ImageIcon moveIcon;
    ImageIcon attackIcon;

    private IconLoader() {
        loadPieceIcons();
        loadMoveIcons();
    }

    public static ImageIcon getPieceIcon(PieceType type) {
        return instance.pieceIcons.get(type);
    }

    public static ImageIcon getMoveIcon() {
        return instance.moveIcon;
    }

    public static ImageIcon getAttackIcon() {
        return instance.attackIcon;
    }

    public static void resizeIcons(double xScale, double yScale) {
        instance.loadPieceIcons();
        for (PieceType type : instance.pieceIcons.keySet()) {
            ImageIcon icon = instance.pieceIcons.get(type);
            instance.pieceIcons.put(type, new ImageIcon(icon.getImage().getScaledInstance((int)(128*xScale), (int)(128*yScale), java.awt.Image.SCALE_SMOOTH)));
        }

        instance.loadMoveIcons();
        instance.moveIcon = new ImageIcon(instance.moveIcon.getImage().getScaledInstance((int)(128*xScale), (int)(128*yScale), java.awt.Image.SCALE_SMOOTH));
        instance.attackIcon = new ImageIcon(instance.attackIcon.getImage().getScaledInstance((int)(128*xScale), (int)(128*yScale), java.awt.Image.SCALE_SMOOTH));
    }


    private void loadPieceIcons() {
        pieceIcons = new HashMap<PieceType, ImageIcon>();

        pieceIcons.put(PieceType.W_PAWN, new ImageIcon(getClass().getResource("/white-pawn.png")));
        pieceIcons.put(PieceType.W_ROOK, new ImageIcon(getClass().getResource("/white-rook.png")));
        pieceIcons.put(PieceType.W_KNIGHT, new ImageIcon(getClass().getResource("/white-knight.png")));
        pieceIcons.put(PieceType.W_BISHOP, new ImageIcon(getClass().getResource("/white-bishop.png")));
        pieceIcons.put(PieceType.W_QUEEN, new ImageIcon(getClass().getResource("/white-queen.png")));
        pieceIcons.put(PieceType.W_KING, new ImageIcon(getClass().getResource("/white-king.png")));
        pieceIcons.put(PieceType.B_PAWN, new ImageIcon(getClass().getResource("/black-pawn.png")));
        pieceIcons.put(PieceType.B_ROOK, new ImageIcon(getClass().getResource("/black-rook.png")));
        pieceIcons.put(PieceType.B_KNIGHT, new ImageIcon(getClass().getResource("/black-knight.png")));
        pieceIcons.put(PieceType.B_BISHOP, new ImageIcon(getClass().getResource("/black-bishop.png")));
        pieceIcons.put(PieceType.B_QUEEN, new ImageIcon(getClass().getResource("/black-queen.png")));
        pieceIcons.put(PieceType.B_KING, new ImageIcon(getClass().getResource("/black-king.png")));
    }

    private void loadMoveIcons() {
        moveIcon = new ImageIcon(getClass().getResource("/move.png"));
        attackIcon = new ImageIcon(getClass().getResource("/attack.png"));
    }

}
