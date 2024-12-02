package app;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MessageHandler {

    ChessWebSocketClient client;
    Game game;
    Board board;
    ConnectWindow connectWindow;
    int gameCode;

    public MessageHandler(ChessWebSocketClient client, Game game, Board board) {
        this.client = client;
        this.game = game;
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
                case "boardUpdateRes":
                    handleServerMove(msg);
                    break;
                case "playerIsBlackRes":
                    handlePlayerIsBlack();
                    break;
                case "gameOverRes":
                    handleGameOver(msg);
                    break;
                case "deletePieceRes":
                    handleDeletePiece(msg);
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
                pieceType = PieceType.W_PAWN;
                break;
            case 'R':
                pieceType = PieceType.W_ROOK;
                break;
            case 'N':
                pieceType = PieceType.W_KNIGHT;
                break;
            case 'B':
                pieceType = PieceType.W_BISHOP;
                break;
            case 'Q':
                pieceType = PieceType.W_QUEEN;
                break;
            case 'K':
                pieceType = PieceType.W_KING;
                break;
            case 'p':
                pieceType = PieceType.B_PAWN;
                break;
            case 'r':
                pieceType = PieceType.B_ROOK;
                break;
            case 'n':
                pieceType = PieceType.B_KNIGHT;
                break;
            case 'b':
                pieceType = PieceType.B_BISHOP;
                break;
            case 'q':
                pieceType = PieceType.B_QUEEN;
                break;
            case 'k':
                pieceType = PieceType.B_KING;
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

    private void handleServerMove(JsonObject msg) {
        try {
            JsonObject move = msg.get("move").getAsJsonObject();
            int x1 = move.get("x1").getAsInt();
            int y1 = move.get("y1").getAsInt();
            int x2 = move.get("x2").getAsInt();
            int y2 = move.get("y2").getAsInt();
            board.makeMove(new Coords(x1, y1), new Coords(x2, y2));
        } catch (Exception e) {
            System.out.println("Invalid move received.");
        }
    }

    private void handlePlayerIsBlack() {
        board.setToBlack();
    }

    private void handleGameOver(JsonObject msg) {
        String status = msg.get("status").getAsString();

        switch (status) {
            case "won":
                game.showPromptWindow("You won!");
                break;
            case "lost":
                game.showPromptWindow("You lost!");
                break;
            case "stalemate":
                game.showPromptWindow("You drew, because there are no legal moves left.");
                break;
            case "material":
                game.showPromptWindow("You drew, because there is not enough material to win.");
                break;
            default:
                break;
        }
    }

    private void handleDeletePiece(JsonObject msg) {
        try {
            int x = msg.get("x").getAsInt();
            int y = msg.get("y").getAsInt();
            board.deletePiece(new Coords(x, y));
        } catch (Exception e) {
            System.out.println("Invalid piece deletion received.");
        }
    }
}
