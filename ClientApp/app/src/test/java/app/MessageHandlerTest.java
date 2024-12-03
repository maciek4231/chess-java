package app;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MessageHandlerTest {

    private ChessWebSocketClient mockClient;
    private Game mockGame;
    private Board mockBoard;
    private ConnectWindow mockConnectWindow;
    private MessageHandler messageHandler;

    @BeforeEach
    void setUp() {
        mockClient = mock(ChessWebSocketClient.class);
        mockGame = mock(Game.class);
        mockBoard = mock(Board.class);
        mockConnectWindow = mock(ConnectWindow.class); // Add mock for ConnectWindow
        messageHandler = new MessageHandler(mockClient, mockGame, mockBoard);
        messageHandler.connectWindow = mockConnectWindow; // Inject the mock
    }

    @Test
    void testMessageHandlerInitialization() {
        assertNotNull(messageHandler);
        assertEquals(mockClient, messageHandler.client);
        assertEquals(mockGame, messageHandler.game);
        assertEquals(mockBoard, messageHandler.board);
    }

    // @Test
    // void testHandleMessageWithAvailabilityRes() {
    //     String message = """
    //     {
    //         "type": "availabilityRes",
    //         "gameCode": 12345
    //     }
    // """;

    //     messageHandler.handleMessage(message);

    //     verify(mockConnectWindow, times(1)).setGameCode(12345); // Verify interaction
    // }


    @Test
    void testHandleMessageWithJoinGameRes() {
        Board spyBoard = spy(new Board());
        messageHandler = new MessageHandler(mockClient, mockGame, spyBoard);

        String message = "{\"type\": \"joinGameRes\", \"isWhite\": true}";
        messageHandler.handleMessage(message);

        assertTrue(spyBoard.isWhite); // Verifies the actual Board instance is updated
    }


    // @Test
    // void testHandleMessageWithPlacementRes() {
    //     String message = """
    //     {
    //         "type": "placementRes",
    //         "0": "rnbqkbnr",
    //         "1": "pppppppp",
    //         "2": "        ",
    //         "3": "        ",
    //         "4": "        ",
    //         "5": "        ",
    //         "6": "PPPPPPPP",
    //         "7": "RNBQKBNR"
    //     }
    // """;

    //     messageHandler.handleMessage(message);

    //     // Verify the placement of specific pieces
    //     verify(mockBoard, times(1)).addPiece(eq(PieceType.W_PAWN), eq(new Coords(0, 6)));
    //     verify(mockBoard, times(1)).addPiece(eq(PieceType.B_ROOK), eq(new Coords(0, 0)));
    //     verify(mockBoard, times(1)).addPiece(eq(PieceType.W_KING), eq(new Coords(4, 7)));
    //     verify(mockBoard, times(1)).addPiece(eq(PieceType.B_QUEEN), eq(new Coords(3, 0)));
    // }






    @Test
    void testHandleMessageWithUnknownType() {
        String message = "{\"type\": \"unknownType\"}";
        messageHandler.handleMessage(message);
        verifyNoInteractions(mockGame);
        verifyNoInteractions(mockBoard);
        verifyNoInteractions(mockClient);
    }

    @Test
    void testHandleInvalidMessageFormat() {
        String message = "Invalid JSON";
        assertDoesNotThrow(() -> messageHandler.handleMessage(message));
    }
}
