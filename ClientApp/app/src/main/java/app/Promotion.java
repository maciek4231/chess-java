package app;

import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Promotion extends Move {

    ArrayList<CustomButton> promotionButtons;

    public Promotion(Board board, Coords from, Coords to, ArrayList<PieceType> promotionOptions) {
        super(board, from, to);

        createPromotionButtons(promotionOptions);
    }

    @Override
    protected void createButton() {
        button = new CustomButton();
        button.setIcon(new ImageIcon(getClass().getResource("/move.png")));
        button.setBounds(to.getRelX(board.getIsWhite())*128, to.getRelY(board.getIsWhite())*128, 128, 128);
        button.addActionListener(e -> {
            board.selectPromotion(this);
        });
    }

    private void createPromotionButtons(ArrayList<PieceType> promotionOptions) {
        promotionButtons = new ArrayList<CustomButton>();
        int i = 0;
        for (PieceType type : promotionOptions) {
            CustomButton button = new CustomButton();
            button.setIcon(PieceIcons.getIcon(type));
            button.setBounds(1024, i*128, 128, 128);
            button.addActionListener(e -> {
                board.clientPromotion(from, to, type);
                System.out.println("Promotion to " + type);
            });
            promotionButtons.add(button);
            i++;
        }
    }

    public void showPromotionButtons() {
        for (CustomButton button : promotionButtons) {
            board.getPane().add(button);
            board.getPane().setLayer(button, 1);
            button.setVisible(true);
        }
    }

    public void hidePromotionButtons() {
        for (CustomButton button : promotionButtons) {
            button.setVisible(false);
            board.getPane().remove(button);
        }
    }
}
