package app;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;

public class MenuButton extends JButton {

    public MenuButton(Application app, String text, String cardName) {
        super(text);
        super.setPreferredSize(new Dimension(384, 128));
        super.setMaximumSize(new Dimension(384, 128));
        super.setFocusPainted(false);

        super.setIcon(ImageLoader.getMenuButtonIcon(cardName));
        super.setHorizontalAlignment(JButton.LEFT);

        Font font = super.getFont();
        Font biggerFont = font.deriveFont(font.getSize() + 12f);
        super.setFont(biggerFont);

        super.setIconTextGap(15);

        super.addActionListener(e -> {
            app.changeCard(cardName);
        });
    }

}
