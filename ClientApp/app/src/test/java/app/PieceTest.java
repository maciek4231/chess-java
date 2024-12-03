package app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.JButton;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PieceTest {

    private Board mockBoard;
    private PieceType mockPieceType;
    private Coords mockCoords;
    private Piece piece;

    @BeforeEach
    void setUp() {
        mockBoard = mock(Board.class);
        mockPieceType = mock(PieceType.class);
        mockCoords = mock(Coords.class);

        when(mockCoords.getRelX(anyBoolean())).thenReturn(1);
        when(mockCoords.getRelY(anyBoolean())).thenReturn(1);
        when(mockBoard.getIsWhite()).thenReturn(true);

        piece = new Piece(mockBoard, mockPieceType, mockCoords);
    }

    @Test
    void testPieceInitialization() {
        assertNotNull(piece);
        assertNotNull(piece.getButton());
        assertEquals(mockCoords, piece.coords);
        assertEquals(mockPieceType, piece.type);
    }

    @Test
    void testConstructorWithIntegerCoordinates() {
        piece = new Piece(mockBoard, mockPieceType, 2, 3);
        assertNotNull(piece);
        assertEquals(2, piece.coords.getRelX(true));
        assertEquals(3, piece.coords.getRelY(true));
    }

    @Test
    void testCreateButton() {
        JButton button = piece.getButton();
        assertNotNull(button);
        assertEquals(128, button.getWidth());
        assertEquals(128, button.getHeight());
    }

    @Test
    void testButtonActionListenerCallsBoardSelectPiece() {
        JButton button = piece.getButton();
        button.doClick();

        verify(mockBoard, times(1)).selectPiece(mockCoords);
    }

    @Test
    void testMoveUpdatesCoordinatesAndButtonPosition() {
        Coords newCoords = mock(Coords.class);
        when(newCoords.getRelX(anyBoolean())).thenReturn(2);
        when(newCoords.getRelY(anyBoolean())).thenReturn(3);

        piece.move(newCoords);

        assertEquals(newCoords, piece.coords);
        assertEquals(2 * 128, piece.getButton().getX());
        assertEquals(3 * 128, piece.getButton().getY());
    }

    @Test
    void testMoveToBoundaryPosition() {
        Coords boundaryCoords = mock(Coords.class);
        when(boundaryCoords.getRelX(anyBoolean())).thenReturn(0);
        when(boundaryCoords.getRelY(anyBoolean())).thenReturn(0);

        piece.move(boundaryCoords);

        assertEquals(boundaryCoords, piece.coords);
        assertEquals(0, piece.getButton().getX());
        assertEquals(0, piece.getButton().getY());
    }

    @Test
    void testMoveWithNullCoordinates() {
        assertThrows(NullPointerException.class, () -> piece.move(null));
    }

    @Test
    void testChangeTypeUpdatesIcon() {
        PieceType newType = mock(PieceType.class);

        try (MockedStatic<PieceIcons> mockedIcons = Mockito.mockStatic(PieceIcons.class)) {
            mockedIcons.when(() -> PieceIcons.getIcon(newType)).thenReturn(null);

            piece.changeType(newType);

            assertEquals(newType, piece.type);
            mockedIcons.verify(() -> PieceIcons.getIcon(newType), times(1));
        }
    }
}
