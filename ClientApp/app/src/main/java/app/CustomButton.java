package app;

import javax.swing.JButton;

public class CustomButton extends JButton {
    public CustomButton() {
        super("");
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setBorder(null);
    }

}
