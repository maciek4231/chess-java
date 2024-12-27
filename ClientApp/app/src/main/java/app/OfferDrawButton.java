package app;

import javax.swing.Icon;

public class OfferDrawButton extends RequestButton {

    public OfferDrawButton(Board board) {
        super(board, 1024, 768);
        button.addActionListener(e -> {
            if (active) {
                board.addPopUpWindow(new OfferDrawSureWindow(board));
            }
        });
    }

    @Override
    Icon getIcon()
    {
        if (active) {
            return IconLoader.getDrawActiveIcon();
        } else {
            return IconLoader.getDrawDisabledIcon();
        }
    }

}
