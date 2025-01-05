package app;

public class SurrenderSureWindow extends YesNoWindow {

    public SurrenderSureWindow(Board board) {
        super(board, "Are you sure you want to surrender?");
        noButton.addActionListener(e -> {
            board.cyclePopUpWindows();
        });
        yesButton.addActionListener(e -> {
            board.messageHandler.sendSurrenderMessage();
        });
    }

}
