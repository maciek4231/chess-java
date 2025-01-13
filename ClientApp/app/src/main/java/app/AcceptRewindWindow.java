package app;

public class AcceptRewindWindow extends YesNoWindow {

    public AcceptRewindWindow(Board board) {
        super(board, "Your opponent has asked for a takeback. Do you accept?");
        noButton.addActionListener(e -> {
            board.cyclePopUpWindows();
            board.messageHandler.sendAcceptRewind(false);
        });
        yesButton.addActionListener(e -> {
            board.cyclePopUpWindows();
            board.messageHandler.sendAcceptRewind(true);
        });
    }

}
