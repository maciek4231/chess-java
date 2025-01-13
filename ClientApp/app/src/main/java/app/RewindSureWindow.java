package app;

public class RewindSureWindow extends YesNoWindow {

    public RewindSureWindow(Board board) {
        super(board, "Are you sure you want to ask for a takeback?");
        noButton.addActionListener(e -> {
            board.cyclePopUpWindows();
        });
        yesButton.addActionListener(e -> {
            board.cyclePopUpWindows();
            board.messageHandler.sendRewindOffer();
        });
    }
}
