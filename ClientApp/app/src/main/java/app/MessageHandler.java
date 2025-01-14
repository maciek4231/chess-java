package app;

import java.time.ZonedDateTime;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MessageHandler {

    Application application;
    ChessWebSocketClient client;
    Game game;
    Board board;
    ConnectWindow connectWindow;
    int gameCode;

    public MessageHandler(Application application, ChessWebSocketClient client) {
        this.application = application;
        this.client = client;
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
                case "drawOfferRes":
                    handleDrawOffer(msg);
                    break;
                case "drawDeclinedRes":
                    handleDrawDeclined(msg);
                    break;
                case "timeUpdateRes":
                    handleTimeUpdate(msg);
                    break;
                case "takebackRequestRes":
                    handleRewindOffer(msg);
                    break;
                case "takebackResponseRes":
                    handleRewindResponse(msg);
                    break;
                case "takebackStatusRes":
                    handleRewindStatus(msg);
                    break;
                case "loginRes":
                    handleLoginRes(msg);
                    break;
                case "registerRes":
                    handleRegisterRes(msg);
                    break;
                case "logoutRes":
                    handleLogOutRes(msg);
                    break;
                default:
                    System.out.println("Unknown message type: " + type);
            }
        } catch (Exception e) {
            System.out.println("Invalid JSON received: " + e.getMessage());
        }
    }

    public void announceAvailable(boolean rankedGameEnabled, boolean timedGameEnabled, int time, int inc) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "availability");
        msg.addProperty("avail", 1);
        msg.addProperty("rankedGameEnabled", rankedGameEnabled ? 1 : 0);
        msg.addProperty("timedGameEnabled", timedGameEnabled ? 1 : 0);
        msg.addProperty("time", time);
        msg.addProperty("inc", inc);
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
        boolean isTimed = false;
        boolean isRanked = false;
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
            try {
                isTimed = msg.get("isTimed").getAsInt() == 1;
                isRanked = msg.get("isRanked").getAsInt() == 1;
            } catch (Exception e) {
                System.out.println("Invalid game settings received.");
            }
            game.setRanked(isRanked);
            game.setTimed(isTimed);
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

        board.setSurrenderActive(true); // TODO: this probably should be somewhere else but it works
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
        board.startMyMove();
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
        board.endMyMove();
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

        GameConclusionStatus conclusionStatus = GameConclusionStatus.DRAW_OFFERED;
        int playerRank = 0, playerDelta = 0, opponentRank = 0, opponentDelta = 0;

        switch (status) {

            case "won":
                conclusionStatus = GameConclusionStatus.WIN;
                break;
            case "lost":
                conclusionStatus = GameConclusionStatus.LOSE;
                break;
            case "stalemate":
                conclusionStatus = GameConclusionStatus.DRAW_STALEMATE;
                break;
            case "material":
                conclusionStatus = GameConclusionStatus.DRAW_MATERIAL;
                break;
            case "drawAccept":
                conclusionStatus = GameConclusionStatus.DRAW_OFFERED;
                break;
            default:
                break;
        }

        if (game.isRanked()) {
            playerRank = msg.get("playerRank").getAsInt();
            playerDelta = msg.get("playerDelta").getAsInt();
            opponentRank = msg.get("opponentRank").getAsInt();
            opponentDelta = msg.get("opponentDelta").getAsInt();
        }

        game.showGameConclusionWindow(
                new GameConclusionWindow(game, conclusionStatus, playerRank, playerDelta, opponentRank, opponentDelta));
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
        board.endMyMove();
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

    public void sendDrawOffer() {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "drawOffer");
        msg.addProperty("gameId", gameCode);
        client.send(msg.toString());
    }

    private void handleDrawOffer(JsonObject msg) {
        board.addPopUpWindow(new AcceptDrawWindow(board));
        game.showPromptWindow("Your opponent offered a draw.");
    }

    public void sendAcceptDraw(boolean isAccepted) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "acceptDraw");
        msg.addProperty("status", isAccepted ? "accept" : "decline");
        msg.addProperty("gameId", gameCode);
        client.send(msg.toString());
    }

    private void handleDrawDeclined(JsonObject msg) {
        game.showPromptWindow("Your opponent declined the draw offer.");
    }

    public void sendRewindOffer() {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "takebackRequest");
        msg.addProperty("gameId", gameCode);
        client.send(msg.toString());
    }

    private void handleRewindOffer(JsonObject msg) {
        board.addPopUpWindow(new AcceptRewindWindow(board));
        game.showPromptWindow("Your opponent has asked for a takeback.");
    }

    public void sendAcceptRewind(boolean isAccepted) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "acceptTakeback");
        msg.addProperty("status", isAccepted ? "accept" : "decline");
        msg.addProperty("gameId", gameCode);
        client.send(msg.toString());
    }

    private void handleRewindResponse(JsonObject msg) {
        if (msg.get("status").getAsString().equals("accept")) {
            board.rewindMove();
        } else
            game.showPromptWindow("Your opponent declined the takeback.");
    }

    private void handleRewindStatus(JsonObject msg) {
        boolean isRewindActive = msg.get("status").getAsInt() == 1;
        board.setRewindActive(isRewindActive);
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
    }

    private void handleTimeUpdate(JsonObject msg) {
        boolean playerClockActive = msg.get("yourClockActive").getAsInt() == 1;
        boolean opponentClockActive = msg.get("opponentClockActive").getAsInt() == 1;
        ZonedDateTime playerTime, opponentTime;
        try {
            playerTime = ZonedDateTime.parse(msg.get("playerTime").getAsString());
            opponentTime = ZonedDateTime.parse(msg.get("opponentTime").getAsString());
        } catch (Exception e) {
            System.out.println("Invalid time update received.");
            return;
        }

        board.updatePlayerClock(playerTime);
        board.updateOpponentClock(opponentTime);
        board.setPlayerClockActive(playerClockActive);
        board.setOpponentClockActive(opponentClockActive);
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

    void setGame(Game game) {
        this.game = game;
        this.board = game.getBoard();
    }

    public void sendLoginRequest(String username, String password) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "loginRequest");
        msg.addProperty("username", username);
        msg.addProperty("password", password);
        client.send(msg.toString());
    }

    public void sendRegisterRequest(String username, String password) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "registerRequest");
        msg.addProperty("username", username);
        msg.addProperty("password", password);
        client.send(msg.toString());
    }

    public void sendLogoutRequest() {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "logoutRequest");
        client.send(msg.toString());
    }

    public void abandonGame() {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "abandonGame");
        msg.addProperty("gameId", gameCode);
        client.send(msg.toString());
        gameCode = -1;
    }

    public void askForMyStats() {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "getMyStats");
        client.send(msg.toString());
    }

    private void handleLoginRes(JsonObject msg)
    {
        boolean success = msg.get("status").getAsString().equals("OK");
        if (success)
        {
            application.logIn(msg.get("username").getAsString());
        }
        else
        {
            application.showLoginError("Invalid username or password.");
        }
    }

    private void handleRegisterRes(JsonObject msg)
    {
        String status = msg.get("status").getAsString();
        if (status.equals("SUCCESS"))
        {
            application.logIn(msg.get("username").getAsString());
        }
        else if (status.equals("USERNAME_EXISTS"))
        {
            application.showSignupError("Username already taken.");
        }
        else if (status.equals("ERROR"))
        {
            application.showSignupError("Something went wrong.");
        }
        else
        {
            System.out.println("Invalid registration status received.");
        }
    }

    public void getPlayerStats(String username) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "getPlayerStats");
        msg.addProperty("username", username);
        client.send(msg.toString());
    }

    private void handleLogOutRes(JsonObject msg) {
        application.logOut();
    }
}
