package app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.ImageIcon;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PromotionTest {

    private Board mockBoard;
    private Coords mockFrom;
    private Coords mockTo;
    private ArrayList<PieceType> promotionOptions;
    private Promotion promotion;

    @BeforeEach
    void setUp() {
        mockBoard = mock(Board.class);
        mockFrom = mock(Coords.class);
        mockTo = mock(Coords.class);
        promotionOptions = new ArrayList<>();
        promotionOptions.add(mock(PieceType.class));
        promotionOptions.add(mock(PieceType.class));
        when(mockFrom.getRelX(anyBoolean())).thenReturn(1);
        when(mockFrom.getRelY(anyBoolean())).thenReturn(1);
        when(mockTo.getRelX(anyBoolean())).thenReturn(2);
        when(mockTo.getRelY(anyBoolean())).thenReturn(3);
        when(mockBoard.getIsWhite()).thenReturn(true);

        promotion = new Promotion(mockBoard, mockFrom, mockTo, promotionOptions);
    }

    @Test
    void testPromotionInitialization() {
        assertNotNull(promotion);
        assertEquals(mockFrom, promotion.getFrom());
        assertEquals(mockTo, promotion.getTo());
        assertNotNull(promotion.promotionButtons);
        assertEquals(2, promotion.promotionButtons.size());
    }

    @Test
    void testCreateButtonSetsCorrectProperties() {
        assertNotNull(promotion.getButton());
        assertEquals(2 * 128, promotion.getButton().getX());
        assertEquals(3 * 128, promotion.getButton().getY());
        assertTrue(promotion.getButton().getIcon() instanceof ImageIcon);
    }

    @Test
    void testCreatePromotionButtons() {
        assertNotNull(promotion.promotionButtons);
        assertEquals(2, promotion.promotionButtons.size());
        for (int i = 0; i < promotion.promotionButtons.size(); i++) {
            assertNotNull(promotion.promotionButtons.get(i));
        }
    }

    @Test
    void testPromotionButtonActionListener() {
        CustomButton promotionButton = (CustomButton) promotion.getButton();
        promotionButton.doClick();
        verify(mockBoard, times(1)).selectPromotion(promotion);
    }

    @Test
    void testPromotionOptionButtonActionListener() {
        CustomButton promotionOptionButton = promotion.promotionButtons.get(0);
        promotionOptionButton.doClick();
        verify(mockBoard, times(1)).clientPromotion(mockFrom, mockTo, promotionOptions.get(0));
    }

}
