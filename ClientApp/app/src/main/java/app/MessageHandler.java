package app;

import java.time.ZonedDateTime;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MessageHandler {
    // This class is responsible for managing the connection between the application and the websocket.

    Application application;
    ChessWebSocketClient client;
    Game game;
    Board board;
    ConnectWindow connectWindow;
    int gameCode;

    public MessageHandler(Application application, ChessWebSocketClient client) {
        // This creates the MessageHandler object.
        this.application = application;
        this.client = client;
    }

    public void handleMessage(String message) {
        // This function is called by the WebSocket and it resolves the message and calls appropriate functions.
        try {
            JsonObject msg = JsonParser.parseString(message).getAsJsonObject();
            String type = msg.get("type").getAsString();
            switch (type) {
                case "availabilityRes": // This is called when the server sends a game code to the app.
                    handleGetGameCode(msg);
                    break;
                case "joinGameRes": // This is called when another player joins the game.
                    handleJoinGame(msg);
                    break;
                case "placementRes": // This is called when the server wants the app to place figures on the board.
                    handlePlacement(msg);
                    break;
                case "possibleMovesRes": // This is called when the app gets the available moves for the current turn.
                    handlePossibleMoves(msg);
                    break;
                case "boardUpdateRes": // This is called when the other player makes a move.
                    handleServerMove(msg);
                    break;
                case "playerIsBlackRes": // This is called when the player plays as black.
                    handlePlayerIsBlack();
                    break;
                case "gameOverRes": // This is called when the game ends.
                    handleGameOver(msg);
                    break;
                case "deletePieceRes": // This is called when a piece is removed from the board.
                    handleDeletePiece(msg);
                    break;
                case "promotionRes": // This is called when the player promotes a pawn.
                    handlePromotion(msg);
                    break;
                case "availablePromotionsRes": // This adds the promotions to the avaialable moves.
                    handleAvailablePromotions(msg);
                    break;
                case "checkRes": // This is called when a king is checked.
                    handleCheck(msg);
                    break;
                case "opponentDisconnectedRes": // This is called when the opponent disconnects.
                    handleOpponentDisconnected(msg);
                    break;
                case "drawOfferRes": // This is called when the opponent offers a draw.
                    handleDrawOffer(msg);
                    break;
                case "drawDeclinedRes": // This is called when the opponent declines a draw.
                    handleDrawDeclined(msg);
                    break;
                case "timeUpdateRes": // This is called to update the clocks.
                    handleTimeUpdate(msg);
                    break;
                case "takebackRequestRes": // This is called when the opponent asks for a takeback.
                    handleRewindOffer(msg);
                    break;
                case "takebackResponseRes": // This is called when the opponent accepts or declines a takeback.
                    handleRewindResponse(msg);
                    break;
                case "takebackStatusRes": // This is called to update the availability of the takeback button.
                    handleRewindStatus(msg);
                    break;
                case "loginRes": // This is called when the server responds to a login request.
                    handleLoginRes(msg);
                    break;
                case "registerRes": // This is called when the server responds to a registration request.
                    handleRegisterRes(msg);
                    break;
                case "logoutRes": // This is called when the server responds to a logout request.
                    handleLogOutRes(msg);
                    break;
                case "myStatsRes": // This is called to update the player's stats.
                    handleMyStatsRes(msg);
                    break;
                case "playerStatsRes": // This is called to update the stats of the player in the statistics card.
                    handleStatsRes(msg);
                    break;
                case "leaderboardRes": // This is called to update the leaderboard.
                    handleLeaderboardRes(msg);
                    break;
                default:
                    System.out.println("Unknown message type: " + type);
            }
        } catch (Exception e) {
            System.out.println("Invalid JSON received: " + e.getMessage());
        }
    }

    public void announceAvailable(boolean rankedGameEnabled, boolean timedGameEnabled, int time, int inc) {
        // This sends a messege to the server that the player wants to generate a game code.
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
        // This connects the messenge handler with the connect window.
        this.connectWindow = connectWindow;
    }

    private void handleGetGameCode(JsonObject msg) {
        // This is called when the game code is received.
        int code = msg.get("gameCode").getAsInt();
        connectWindow.setGameCode(code);
    }

    public void tryJoiningGame(int code) {
        // This is called when the player tries to join a game.
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "joinGame");
        msg.addProperty("gameCode", code);
        client.send(msg.toString());
    }

    private void handleJoinGame(JsonObject msg) {
        // This is called when the server sends a response to the join game request.
        int status = -1;
        int code = -1;
        boolean isTimed = false;
        boolean isRanked = false;
        String user = "";
        String opponent = "";
        try {
            status = msg.get("status").getAsInt();
            code = msg.get("gameCode").getAsInt();
        } catch (Exception e) {
            System.out.println("Invalid game code received.");
        }
        if (status == 0) {
            // This is executed when the game is successfully joined.
            connectWindow.gameFound();
            gameCode = code;
            board.setGameCode(code);
            try {
                isTimed = msg.get("isTimed").getAsInt() == 1;
                isRanked = msg.get("isRanked").getAsInt() == 1;
                user = msg.get("userName").getAsString();
                opponent = msg.get("opponentName").getAsString();
            } catch (Exception e) {
                System.out.println("Invalid game settings received.");
            }
            game.setRanked(isRanked);
            game.setTimed(isTimed);
            game.setPlayerName(user);
            game.setOpponentName(opponent);
        } else {
            if (status == -1) {
                // This is executed when the game is not found.
                connectWindow.gameNotFound();
            }
            else if (status == -2)
            {
                // This is executed when the player tries to join a game that no longer exists.
                connectWindow.opponentLeft();
            }
            else if (status == -3)
            {
                // This is executed when the player is a Guest and tries to join a ranked game.
                connectWindow.gameRankedGuestJoining();
            }
        }
    }

    private void handlePlacement(JsonObject msg) {
        // This is called when the server wants to set the board to a certain state.
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
        // This handles placing a single piece.
        PieceType pieceType;

        pieceType = getPieceType(type);
        if (pieceType == null) {
            // When the piece type is invalid return.
            return;
        }
        board.addPiece(pieceType, coords);
    }

    private void handlePossibleMoves(JsonObject msg) {
        // This is called when the server sends the available moves for the current turn.
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
        // This is called when the player makes a move.
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
        // This is called when the server sends a move made by the opponent.
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
        // This is called when the player plays as black.
        board.setToBlack();
    }

    private void handleGameOver(JsonObject msg) {
        // This is called when the game ends.
        String status = msg.get("status").getAsString();

        game.endGame();

        GameConclusionStatus conclusionStatus = GameConclusionStatus.DRAW_OFFERED;
        int playerRank = 0, playerDelta = 0, opponentRank = 0, opponentDelta = 0;

        switch (status) {

            case "won": // This is executed when the player wins.
                conclusionStatus = GameConclusionStatus.WIN;
                break;
            case "lost": // This is executed when the player loses.
                conclusionStatus = GameConclusionStatus.LOSE;
                break;
            case "stalemate": // This is executed when the game ends in a stalemate.
                conclusionStatus = GameConclusionStatus.DRAW_STALEMATE;
                break;
            case "material": // This is executed when the game ends in a draw due to insufficient material.
                conclusionStatus = GameConclusionStatus.DRAW_MATERIAL;
                break;
            case "drawAccept": // This is executed when the game ends in a draw.
                conclusionStatus = GameConclusionStatus.DRAW_OFFERED;
                break;
            default: // There shouldn't be any other status.
                break;
        }

        if (game.isRanked()) {
            // If the game is ranked, parse the changes in ELO.
            playerRank = msg.get("playerRank").getAsInt();
            playerDelta = msg.get("playerDelta").getAsInt();
            opponentRank = msg.get("opponentRank").getAsInt();
            opponentDelta = msg.get("opponentDelta").getAsInt();
        }

        game.showGameConclusionWindow(
                new GameConclusionWindow(game, conclusionStatus, playerRank, playerDelta, opponentRank, opponentDelta));
    }

    private void handleDeletePiece(JsonObject msg) {
        // This is called when a piece is deleted by the server.
        try {
            int x = msg.get("x").getAsInt();
            int y = msg.get("y").getAsInt();
            board.deletePiece(new Coords(x, y));
        } catch (Exception e) {
            System.out.println("Invalid piece deletion received.");
        }
    }

    public void sendPromotion(Coords from, Coords to, PieceType type) {
        // This is called when the player wants to promote a pawn.
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
        // This is called when the opponent promotes a pawn.
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
        // This is called when the server sends the available promotions for the turn.
        // This replaces a move with a promotion.
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
        // This is called when the player surrenders.
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "surrender");
        msg.addProperty("gameId", gameCode);
        client.send(msg.toString());
    }

    public void sendDrawOffer() {
        // This is called when the player offers a draw.
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "drawOffer");
        msg.addProperty("gameId", gameCode);
        client.send(msg.toString());
    }

    private void handleDrawOffer(JsonObject msg) {
        // This is called when the opponent offers a draw.
        board.addPopUpWindow(new AcceptDrawWindow(board));
        game.showPromptWindow("Your opponent offered a draw.");
    }

    public void sendAcceptDraw(boolean isAccepted) {
        // This is called when the player accepts a draw.
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "acceptDraw");
        msg.addProperty("status", isAccepted ? "accept" : "decline");
        msg.addProperty("gameId", gameCode);
        client.send(msg.toString());
    }

    private void handleDrawDeclined(JsonObject msg) {
        // This is called when the opponent declines a draw.
        game.showPromptWindow("Your opponent declined the draw offer.");
    }

    public void sendRewindOffer() {
        // This is called when the player asks for a takeback.
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "takebackRequest");
        msg.addProperty("gameId", gameCode);
        client.send(msg.toString());
    }

    private void handleRewindOffer(JsonObject msg) {
        // This is called when the opponent asks for a takeback.
        board.addPopUpWindow(new AcceptRewindWindow(board));
        game.showPromptWindow("Your opponent has asked for a takeback.");
    }

    public void sendAcceptRewind(boolean isAccepted) {
        // This is called when the player accepts a takeback.
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "acceptTakeback");
        msg.addProperty("status", isAccepted ? "accept" : "decline");
        msg.addProperty("gameId", gameCode);
        client.send(msg.toString());
    }

    private void handleRewindResponse(JsonObject msg) {
        // This is called when the opponent accepts or declines a takeback.
        // If the takeback is accepted, the board is cleared and further packets will
        // handle the placement of the pieces and moves.
        if (msg.get("status").getAsString().equals("accept")) {
            board.rewindMove();
        } else
            game.showPromptWindow("Your opponent declined the takeback.");
    }

    private void handleRewindStatus(JsonObject msg) {
        // This is called to either activate or deactivate the takeback button.
        boolean isRewindActive = msg.get("status").getAsInt() == 1;
        board.setRewindActive(isRewindActive);
    }

    private void handleCheck(JsonObject msg) {
        // This is called when a king is checked.
        try {
            int x = msg.get("x").getAsInt();
            int y = msg.get("y").getAsInt();
            board.checkPiece(new Coords(x, y));
        } catch (Exception e) {
            System.out.println("Invalid check received.");
        }
    }

    private void handleOpponentDisconnected(JsonObject msg) {
        // This is called when the opponent disconnects.
        game.showPromptWindow("Your opponent disconnected.");
    }

    private void handleTimeUpdate(JsonObject msg) {
        // This is called to update the clocks.
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
        // This decodes the piece type from a character.
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
        // This encodes the piece type to a character.
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
        // This is called to connect the handler to a game.
        this.game = game;
        this.board = game.getBoard();
    }

    public void sendLoginRequest(String username, String password) {
        // This is called when the player tries to log in.
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "loginRequest");
        msg.addProperty("username", username);
        msg.addProperty("password", password);
        client.send(msg.toString());
    }

    public void sendRegisterRequest(String username, String password) {
        // This is called when the player tries to register.
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "registerRequest");
        msg.addProperty("username", username);
        msg.addProperty("password", password);
        client.send(msg.toString());
    }

    public void sendLogoutRequest() {
        // This is called when the player tries to log out.
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "logoutRequest");
        client.send(msg.toString());
    }

    public void abandonGame() {
        // This is called when the player leaves the game by clicking the OK button
        // in the game conclusion window.
        // This will also be used when the player wants to switch to another game,
        // but this is yet to be implemented.
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "abandonGame");
        msg.addProperty("gameId", gameCode);
        client.send(msg.toString());
    }

    public void askForMyStats() {
        // This is called when the player is logged in and the Account card is opened,
        // to update the stats of the logged in player.
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "getMyStats");
        client.send(msg.toString());
    }

    private void handleLoginRes(JsonObject msg) {
        // This is called when the server responds to a login request.
        String status = msg.get("status").getAsString();
        if (status.equals("OK")) {
            application.logIn(msg.get("username").getAsString()); // This is called when the log in is successful.
        } else if (status.equals("ALREADY_LOGGED_IN")) {
            application.showLoginError("User already logged in."); // This is called when the player is already logged in on another account.
        } else {
            application.showLoginError("Invalid username or password."); // This is called when the log in info is invalid.
        }
    }

    private void handleRegisterRes(JsonObject msg) {
        // This is called when the server responds to a registration request.
        String status = msg.get("status").getAsString();
        if (status.equals("SUCCESS")) {
            application.logIn(msg.get("username").getAsString());
        } else if (status.equals("USERNAME_EXISTS")) {
            application.showSignupError("Username already taken.");
        } else if (status.equals("ERROR")) {
            application.showSignupError("Something went wrong.");
        } else {
            System.out.println("Invalid registration status received.");
        }
    }

    public void getPlayerStats(String username) {
        // This is called when the player fetches another player's stats in the statistics card.
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "getPlayerStats");
        msg.addProperty("username", username);
        client.send(msg.toString());
    }

    private void handleLogOutRes(JsonObject msg) {
        // This is called when the server responds to a logout request.
        // This changes the application state to logged out.
        application.logOut();
    }

    public void getLeaderboard(int page) {
        // This is called when the player fetches the leaderboard.
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "getLeaderboard");
        msg.addProperty("page", page);
        client.send(msg.toString());
    }

    public void getGameHistory(int page) {
        // This is called when the player fetches the game history.
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "getGameHistory");
        msg.addProperty("page", page);
        client.send(msg.toString());
    }

    private void handleMyStatsRes(JsonObject msg) {
        // This is called when the server sends the player's stats.
        int elo = msg.get("elo").getAsInt();
        int wins = msg.get("wins").getAsInt();
        int losses = msg.get("losses").getAsInt();
        int draws = msg.get("draws").getAsInt();
        int games = wins + losses + draws;
        application.setMyStats(elo, games, wins, losses, draws);
    }

    private void handleStatsRes(JsonObject msg) {
        // This is called when the server sends the stats of a player in the statistics card.
        int elo = msg.get("elo").getAsInt();
        int wins = msg.get("wins").getAsInt();
        int losses = msg.get("losses").getAsInt();
        int draws = msg.get("draws").getAsInt();
        int games = wins + losses + draws;
        application.setStats(elo, games, wins, losses, draws);
    }

    private void handleLeaderboardRes(JsonObject msg) {
        // This is called when the server sends the leaderboard.
        JsonArray leaderboard = msg.get("leaderboard").getAsJsonArray();
        for (JsonElement leaderboardEntry : leaderboard) {
            JsonObject entry = leaderboardEntry.getAsJsonObject();
            String username = entry.get("username").getAsString();
            int rank = entry.get("rank").getAsInt();
            int elo = entry.get("elo").getAsInt();
            application.addLeaderboardEntry(username, rank, elo);
        }
    }
}
