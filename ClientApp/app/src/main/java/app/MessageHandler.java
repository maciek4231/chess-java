package app;

import java.util.ArrayList;

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
                case "promotionRes":
                    handlePromotion(msg);
                    break;
                case "availablePromotionsRes":
                    handleAvailablePromotions(msg);
                    break;
                case "checkRes":
                    handleCheck(msg);
                    break;
                case "opponentDisconnectedRes":
                    handleOpponentDisconnected(msg);
                    break;
                default:
                    System.out.println("Unknown message type: " + type);
            }
        } catch (Exception e) {
            System.out.println("Invalid JSON received: " + e.getMessage());
        }
    }

    public void anounceAvailable() {
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

        pieceType = getPieceType(type);
        if (pieceType == null) {
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

        game.endGame();

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

    public void sendPromotion(Coords from, Coords to, PieceType type) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "makePromotion");
        msg.addProperty("gameId", gameCode);
        JsonObject move = new JsonObject();
        move.addProperty("x1", from.getX());
        move.addProperty("y1", from.getY());
        move.addProperty("x2", to.getX());
        move.addProperty("y2", to.getY());
        msg.add("move", move);
        msg.addProperty("pieceType", getPieceChar(type));
        client.send(msg.toString());
    }

    public void handlePromotion(JsonObject msg) {
        try {
            JsonObject move = msg.get("move").getAsJsonObject();
            int x1 = move.get("x1").getAsInt();
            int y1 = move.get("y1").getAsInt();
            int x2 = move.get("x2").getAsInt();
            int y2 = move.get("y2").getAsInt();
            PieceType type = getPieceType(msg.get("pieceType").getAsString().charAt(0));
            board.makePromotion(new Coords(x1, y1), new Coords(x2, y2), type);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Invalid promotion received.");
        }
    }

    public void handleAvailablePromotions(JsonObject msg) {
        try {
            JsonObject move = msg.get("move").getAsJsonObject();
            int x1 = move.get("x1").getAsInt();
            int y1 = move.get("y1").getAsInt();
            int x2 = move.get("x2").getAsInt();
            int y2 = move.get("y2").getAsInt();
            String types = msg.get("pieceTypes").getAsString();
            ArrayList<PieceType> promotionOptions = new ArrayList<>();
            for (int i = 0; i < types.length(); i++) {
                promotionOptions.add(getPieceType(types.charAt(i)));
            }
            board.addPromotion(new Coords(x1, y1), new Coords(x2, y2), promotionOptions);
        } catch (Exception e) {
            System.out.println("Invalid promotion received.");
        }
    }

    public void sendSurrenderMessage() {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "surrender");
        msg.addProperty("gameId", gameCode);
        client.send(msg.toString());
    }

    private void handleCheck(JsonObject msg) {
        try {
            int x = msg.get("x").getAsInt();
            int y = msg.get("y").getAsInt();
            board.checkPiece(new Coords(x, y));
        } catch (Exception e) {
            System.out.println("Invalid check received.");
        }
    }

    private void handleOpponentDisconnected(JsonObject msg) {
        game.showPromptWindow("Your opponent disconnected.");
        game.endGame();
    }

    private PieceType getPieceType(char type) {
        switch (type) {
            case 'P':
                return PieceType.W_PAWN;
            case 'R':
                return PieceType.W_ROOK;
            case 'N':
                return PieceType.W_KNIGHT;
            case 'B':
                return PieceType.W_BISHOP;
            case 'Q':
                return PieceType.W_QUEEN;
            case 'K':
                return PieceType.W_KING;
            case 'p':
                return PieceType.B_PAWN;
            case 'r':
                return PieceType.B_ROOK;
            case 'n':
                return PieceType.B_KNIGHT;
            case 'b':
                return PieceType.B_BISHOP;
            case 'q':
                return PieceType.B_QUEEN;
            case 'k':
                return PieceType.B_KING;
            default:
                return null;
        }
    }

    private char getPieceChar(PieceType type) {
        switch (type) {
            case W_PAWN:
                return 'P';
            case W_ROOK:
                return 'R';
            case W_KNIGHT:
                return 'N';
            case W_BISHOP:
                return 'B';
            case W_QUEEN:
                return 'Q';
            case W_KING:
                return 'K';
            case B_PAWN:
                return 'p';
            case B_ROOK:
                return 'r';
            case B_KNIGHT:
                return 'n';
            case B_BISHOP:
                return 'b';
            case B_QUEEN:
                return 'q';
            case B_KING:
                return 'k';
            default:
                return ' ';
        }
    }
}
