package app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CustomButtonTest {

    @Test
    void testCustomButtonInitialization() {
        CustomButton button = new CustomButton();
        assertEquals("", button.getText());
        assertFalse(button.isOpaque());
        assertFalse(button.isContentAreaFilled());
        assertFalse(button.isBorderPainted());
        assertFalse(button.isFocusPainted());
        assertNull(button.getBorder());
    }
}
