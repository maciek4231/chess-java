package app;

import java.util.ArrayList;

public class Promotion extends Move {

    ArrayList<CustomButton> promotionButtons;
    ArrayList<PieceType> promotionOptions;

    public Promotion(Board board, Coords from, Coords to, ArrayList<PieceType> promotionOptions) {
        super(board, from, to);

        createPromotionButtons(promotionOptions);
        this.promotionOptions = promotionOptions;
    }

    @Override
    protected void createButton() {
        button = new CustomButton();
        button.setIcon(getButtonIcon());
        button.setBounds((int)(to.getRelX(board.getIsWhite()) * 128 * xScale), (int)(to.getRelY(board.getIsWhite()) * 128 * yScale), (int)(128 * xScale), (int)(128 * yScale));
        button.addActionListener(e -> {
            board.selectPromotion(this);
        });
    }

    private void createPromotionButtons(ArrayList<PieceType> promotionOptions) {
        promotionButtons = new ArrayList<CustomButton>();
        int i = 0;
        for (PieceType type : promotionOptions) {
            CustomButton button = new CustomButton();
            button.setIcon(IconLoader.getPieceIcon(type));
            button.setBounds((int)(1024 * xScale), (int)(i * 128 * yScale), (int)(128 * xScale), (int)(128 * yScale));
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

    @Override
    public void resize(double xScale, double yScale) {
        super.resize(xScale, yScale);
        int i = 0;
        for (CustomButton button : promotionButtons) {
            button.setBounds((int)(1024 * xScale), (int)(i * 128 * yScale), (int)(128 * xScale), (int)(128 * yScale));
            button.setIcon(IconLoader.getPieceIcon(promotionOptions.get(i)));
            i++;
        }
    }
}
