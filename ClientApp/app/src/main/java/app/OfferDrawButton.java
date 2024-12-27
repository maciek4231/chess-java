package app;

public class OfferDrawButton extends RequestButton {

    public OfferDrawButton(Board board) {
        super(board, 1024, 768);
        button.setIcon(IconLoader.getDrawDisabledIcon());
        button.addActionListener(e -> {
            board.addPopUpWindow(new OfferDrawSureWindow(board));
        });
    }

    public void resize(double xScale, double yScale) {
        super.resize(xScale, yScale);
        button.setIcon(IconLoader.getDrawDisabledIcon());
    }

}
