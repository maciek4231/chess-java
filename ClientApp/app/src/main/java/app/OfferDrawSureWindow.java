package app;

public class OfferDrawSureWindow extends YesNoWindow {

    public OfferDrawSureWindow(Board board) {
        super(board, "Are you sure you want to offer a draw?");
        noButton.addActionListener(e -> {
            board.cyclePopUpWindows();
        });
        yesButton.addActionListener(e -> {
            board.messageHandler.sendDrawOffer();
            board.cyclePopUpWindows();
        });
    }
}
