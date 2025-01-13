package app;

public class AcceptDrawWindow extends YesNoWindow {

    public AcceptDrawWindow(Board board) {
        super(board, "Your opponent has offered a draw. Do you accept?");
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
