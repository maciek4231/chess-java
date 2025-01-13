package app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void testBoardInitialization() {
        assertNotNull(board.getPane());
        assertTrue(board.pieces.isEmpty());
        assertTrue(board.availableMoves.isEmpty());
        assertTrue(board.selectableMoves.isEmpty());
        assertEquals(-1, board.gameCode);
        assertTrue(board.isWhite);
    }

    @Test
    void testSetMessageHandler() {
        MessageHandler mockHandler = mock(MessageHandler.class);
        board.setMessageHandler(mockHandler);
        assertEquals(mockHandler, board.messageHandler);
    }

    @Test
    void testAddPieceToBoard() {
        Coords coords = new Coords(1, 1);
        Piece piece = mock(Piece.class);
        board.pieces.put(coords, piece);
        assertEquals(piece, board.pieces.get(coords));
    }

    @Test
    void testRemovePieceFromBoard() {
        Coords coords = new Coords(1, 1);
        Piece piece = mock(Piece.class);
        board.pieces.put(coords, piece);
        board.pieces.remove(coords);
        assertNull(board.pieces.get(coords));
    }

    @Test
    void testAddMoveToAvailableMoves() {
        Move move = mock(Move.class);
        board.availableMoves.add(move);
        assertTrue(board.availableMoves.contains(move));
    }

    @Test
    void testRemoveMoveFromAvailableMoves() {
        Move move = mock(Move.class);
        board.availableMoves.add(move);
        board.availableMoves.remove(move);
        assertFalse(board.availableMoves.contains(move));
    }

    @Test
    void testAddMoveToSelectableMoves() {
        Move move = mock(Move.class);
        board.selectableMoves.add(move);
        assertTrue(board.selectableMoves.contains(move));
    }

    @Test
    void testRemoveMoveFromSelectableMoves() {
        Move move = mock(Move.class);
        board.selectableMoves.add(move);
        board.selectableMoves.remove(move);
        assertFalse(board.selectableMoves.contains(move));
    }

    @Test
    void testSetGameCode() {
        board.gameCode = 12345;
        assertEquals(12345, board.gameCode);
    }

    @Test
    void testSetIsWhite() {
        board.isWhite = false;
        assertFalse(board.isWhite);
    }
}
