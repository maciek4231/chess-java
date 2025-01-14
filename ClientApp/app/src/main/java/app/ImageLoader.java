package app;

import java.util.HashMap;

import javax.swing.ImageIcon;

public class ImageLoader {
    private static final ImageLoader instance = new ImageLoader();

    HashMap<String, ImageIcon> menuButtonIcons;

    private ImageLoader() {
        loadMenuButtonIcons();
    }

    public static ImageIcon getMenuButtonIcon(String name) {
        if (!instance.menuButtonIcons.containsKey(name)) {
            return null;
        }
        return instance.menuButtonIcons.get(name);
    }

    private void loadMenuButtonIcons() {
        menuButtonIcons = new HashMap<String, ImageIcon>();

        menuButtonIcons.put("game", new ImageIcon(getClass().getResource("/menuButtonPlay.png")));
        menuButtonIcons.put("login", new ImageIcon(getClass().getResource("/menuButtonAccount.png")));
    }
}
