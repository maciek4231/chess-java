package app;

public class SurrenderButton extends RequestButton {

    public SurrenderButton(Board board) {
        super(board, 1024, 896);
        button.setIcon(IconLoader.getSurrenderDisabledIcon());
        button.addActionListener(e -> {
            System.out.println("Surrender");
        });
    }

}
