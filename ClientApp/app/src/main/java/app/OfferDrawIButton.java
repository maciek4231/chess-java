package app;

public class OfferDrawIButton extends RequestButton {

    public OfferDrawIButton(Board board) {
        super(board, 1024, 768);
        button.setIcon(IconLoader.getDrawDisabledIcon());
        button.addActionListener(e -> {
            System.out.println("Offer Draw");
        });
    }

}
