package app;

import javax.swing.Icon;

public class SurrenderButton extends RequestButton {

    public SurrenderButton(Board board) {
        super(board, 1024, 896);
        button.addActionListener(e -> {
            if (active) {
                board.addPopUpWindow(new SurrenderSureWindow(board));
            }
        });
    }

    @Override
    Icon getIcon()
    {
        if (active) {
            return IconLoader.getSurrenderActiveIcon();
        } else {
            return IconLoader.getSurrenderDisabledIcon();
        }
    }
}
