package app;

public class RewindButton extends RequestButton {

    public RewindButton(Board board) {
        super(board, 1024, 640);
        button.setIcon(IconLoader.getRewindDisabledIcon());
        button.addActionListener(e -> {
            System.out.println("Ask for rewind");
        });
    }

    public void resize(double xScale, double yScale) {
        super.resize(xScale, yScale);
        button.setIcon(IconLoader.getRewindDisabledIcon());
    }

}
