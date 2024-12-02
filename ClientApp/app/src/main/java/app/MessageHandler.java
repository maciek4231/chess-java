package app;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MessageHandler {

    ChessWebSocketClient client;
    Board board;
    ConnectWindow connectWindow;
    int gameCode;

    public MessageHandler(ChessWebSocketClient client, Board board) {
        this.client = client;
        this.board = board;
    }

    public void handleMessage(String message) {
        try {
            JsonObject msg = JsonParser.parseString(message).getAsJsonObject();
            String type = msg.get("type").getAsString();
            switch (type) {
                case "availabilityRes":
                    handleGetGameCode(msg);
                    break;
                case "joinGameRes":
                    handleJoinGame(msg);
                    break;
                case "placementRes":
                    handlePlacement(msg);
                    break;
                case "possibleMovesRes":
                    handlePossibleMoves(msg);
                    break;
                default:
                    System.out.println("Unknown message type: " + type);
            }
        } catch (Exception e) {
            System.out.println("Invalid JSON received: " + e.getMessage());
        }
    }

    public void anounceAvailable()
    {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "availability");
        msg.addProperty("avail", 1);
        client.send(msg.toString());
    }

    public void setConnectWindow(ConnectWindow connectWindow) {
        this.connectWindow = connectWindow;
    }

    private void handleGetGameCode(JsonObject msg) {
        int code = msg.get("gameCode").getAsInt();
        connectWindow.setGameCode(code);
    }

    public void tryJoiningGame(int code) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "joinGame");
        msg.addProperty("gameCode", code);
        client.send(msg.toString());
    }

    private void handleJoinGame(JsonObject msg) {
        int status = -1;
        int code = -1;
        try {
            status = msg.get("status").getAsInt();
            code = msg.get("gameCode").getAsInt();
        } catch (Exception e) {
            System.out.println("Invalid game code received.");
        }
        if (status == 0) {
            connectWindow.gameFound();
            gameCode = code;
            board.setGameCode(code);
        } else {
            if (status == -1) {
                connectWindow.gameNotFound();
            } else {
                connectWindow.opponentLeft();
            }
        }
    }

    private void handlePlacement(JsonObject msg) {
        try {
            for (int i = 0; i < 8; i++) {
                String row = msg.get(Integer.toString(i)).getAsString();
                for (int j = 0; j < 8; j++) {
                    placePiece(row.charAt(j), new Coords(j, i));
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid placement received.");
        }
    }

    private void placePiece(char type, Coords coords) {
        PieceType pieceType;
        switch (type) {
            case 'P':
                pieceType = PieceType.B_PAWN;
                break;
            case 'R':
                pieceType = PieceType.B_ROOK;
                break;
            case 'N':
                pieceType = PieceType.B_KNIGHT;
                break;
            case 'B':
                pieceType = PieceType.B_BISHOP;
                break;
            case 'Q':
                pieceType = PieceType.B_QUEEN;
                break;
            case 'K':
                pieceType = PieceType.B_KING;
                break;
            case 'p':
                pieceType = PieceType.W_PAWN;
                break;
            case 'r':
                pieceType = PieceType.W_ROOK;
                break;
            case 'n':
                pieceType = PieceType.W_KNIGHT;
                break;
            case 'b':
                pieceType = PieceType.W_BISHOP;
                break;
            case 'q':
                pieceType = PieceType.W_QUEEN;
                break;
            case 'k':
                pieceType = PieceType.W_KING;
                break;
            default:
                return;
        }
        board.addPiece(pieceType, coords);
    }

    private void handlePossibleMoves(JsonObject msg) {
        try {
            JsonArray moves = msg.get("moves").getAsJsonArray();
            for (JsonElement entry : moves) {
                JsonObject move = entry.getAsJsonObject();
                int x1 = move.get("x1").getAsInt();
                int y1 = move.get("y1").getAsInt();
                int x2 = move.get("x2").getAsInt();
                int y2 = move.get("y2").getAsInt();
                board.addAvailableMove(new Coords(x1, y1), new Coords(x2, y2));
            }
        } catch (Exception e) {
            System.out.println("Invalid possible moves received.");
        }
    }

    public void sendMove(Coords from, Coords to) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "pickMove");
        msg.addProperty("gameId", gameCode);
        JsonObject move = new JsonObject();
        move.addProperty("x1", from.getX());
        move.addProperty("y1", from.getY());
        move.addProperty("x2", to.getX());
        move.addProperty("y2", to.getY());
        msg.add("move", move);
        client.send(msg.toString());
    }
}
