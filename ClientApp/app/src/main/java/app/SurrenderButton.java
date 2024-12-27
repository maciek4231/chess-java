package app;

public class SurrenderButton extends RequestButton {

    public SurrenderButton(Board board) {
        super(board, 1024, 896);
        button.setIcon(IconLoader.getSurrenderDisabledIcon());
        button.addActionListener(e -> {
            board.addPopUpWindow(new SurrenderSureWindow(board));
        });
    }

    public void resize(double xScale, double yScale) {
        super.resize(xScale, yScale);
        button.setIcon(IconLoader.getSurrenderDisabledIcon());
    }
}
