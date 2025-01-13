package app;

public class AcceptRewindWindow extends YesNoWindow {

    public AcceptRewindWindow(Board board) {
        super(board, "Your opponent has asked for a rewind. Do you accept?");
        noButton.addActionListener(e -> {
            board.cyclePopUpWindows();
            board.messageHandler.sendAcceptDraw(false);
        });
        yesButton.addActionListener(e -> {
            board.cyclePopUpWindows();
            board.messageHandler.sendAcceptDraw(true);
        });
    }

}
