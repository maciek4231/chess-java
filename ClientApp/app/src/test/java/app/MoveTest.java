package app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.JButton;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MoveTest {

    private Board mockBoard;
    private Coords mockFrom;
    private Coords mockTo;

    @BeforeEach
    void setUp() {
        mockBoard = mock(Board.class);
        mockFrom = mock(Coords.class);
        mockTo = mock(Coords.class);

        when(mockFrom.getRelX(anyBoolean())).thenReturn(1);
        when(mockFrom.getRelY(anyBoolean())).thenReturn(1);
        when(mockTo.getRelX(anyBoolean())).thenReturn(3);
        when(mockTo.getRelY(anyBoolean())).thenReturn(3);
        when(mockBoard.getIsWhite()).thenReturn(true);
    }

    @Test
    void testMoveInitialization() {
        Move move = new Move(mockBoard, mockFrom, mockTo);
        assertEquals(mockFrom, move.getFrom());
        assertEquals(mockTo, move.getTo());
        assertNotNull(move.getButton());
    }

    @Test
    void testCreateButtonSetsCorrectBounds() {
        Move move = new Move(mockBoard, mockFrom, mockTo);
        JButton button = move.getButton();
        assertEquals(3 * 128, button.getX());
        assertEquals(3 * 128, button.getY());
    }

    @Test
    void testButtonActionListenerTriggersClientMove() {
        Move move = new Move(mockBoard, mockFrom, mockTo);
        JButton button = move.getButton();
        button.doClick();
        verify(mockBoard, times(1)).clientMove(mockFrom, mockTo);
    }

    @Test
    void testGetButtonIconReturnsAttackIconWhenOccupied() {
        when(mockBoard.isOccupied(mockTo)).thenReturn(true);
        Move move = new Move(mockBoard, mockFrom, mockTo);

        JButton button = move.getButton();
        assertNotNull(button.getIcon());
    }

    @Test
    void testGetButtonIconReturnsMoveIconWhenNotOccupied() {
        when(mockBoard.isOccupied(mockTo)).thenReturn(false);
        Move move = new Move(mockBoard, mockFrom, mockTo);

        JButton button = move.getButton();
        assertNotNull(button.getIcon());
    }

    @Test
    void testMoveWithNullFromThrowsException() {
        assertThrows(NullPointerException.class, () -> new Move(mockBoard, null, mockTo));
    }

    @Test
    void testMoveWithNullToThrowsException() {
        assertThrows(NullPointerException.class, () -> new Move(mockBoard, mockFrom, null));
    }
}
