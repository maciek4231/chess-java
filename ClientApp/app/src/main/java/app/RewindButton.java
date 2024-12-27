package app;

import javax.swing.Icon;

public class RewindButton extends RequestButton {

    public RewindButton(Board board) {
        super(board, 1024, 640);
        button.addActionListener(e -> {
            if (active) {
                board.addPopUpWindow(new RewindSureWindow(board));
            }
        });
    }

    @Override
    Icon getIcon()
    {
        if (active) {
            return IconLoader.getRewindActiveIcon();
        } else {
            return IconLoader.getRewindDisabledIcon();
        }
    }

}
