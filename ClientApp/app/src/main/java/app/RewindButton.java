package app;

public class RewindButton extends RequestButton {

    public RewindButton(Board board) {
        super(board, 1024, 640);
        button.setIcon(IconLoader.getRewindDisabledIcon());
        button.addActionListener(e -> {
            board.addPopUpWindow(new RewindSureWindow(board));
        });
    }

    public void resize(double xScale, double yScale) {
        super.resize(xScale, yScale);
        button.setIcon(IconLoader.getRewindDisabledIcon());
    }

}
