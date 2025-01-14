package com.chess_server;

import java.util.ArrayList;

import org.java_websocket.WebSocket;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MessageHandler {

    private final ChessWebSocketServer server;
    public final ConnectionHandler connectionHandler;
    private final GameManager gameManager;

    public MessageHandler(ChessWebSocketServer server) {
        this.server = server;
        this.gameManager = new GameManager(this);
        this.connectionHandler = new ConnectionHandler(gameManager);
    }

    public void handleMessage(WebSocket conn, String message) {
        try {
            JsonObject msg = JsonParser.parseString(message).getAsJsonObject();
            String type = msg.get("type").getAsString();
            Integer clientId = connectionHandler.getClientId(conn);
            switch (type) {
                case "pickMove":
                    handleMove(clientId, msg);
                    break;
                case "takebackRequest":
                    handleTakebackRequest(clientId, msg);
                    break;
                case "acceptTakeback":
                    acceptTakeback(clientId, msg);
                    break;
                case "availability":
                    handleAvailability(clientId, msg);
                    break;
                case "joinGame":
                    handleJoinGame(clientId, msg);
                    break;
                case "makePromotion":
                    handlePromotion(clientId, msg);
                    break;
                case "surrender":
                    handleSurrender(clientId, msg);
                    break;
                case "drawOffer":
                    handleDrawOffer(clientId, msg);
                    break;
                case "acceptDraw":
                    handleAcceptDraw(clientId, msg);
                    break;
                case "loginRequest":
                    handleLoginRequest(conn, msg, clientId);
                    break;
                case "registerRequest":
                    handleRegisterRequest(conn, msg, clientId);
                    break;
                default:
                    System.out.println("Unknown message type: " + type);
            }
        } catch (Exception e) {
            System.out.println("Invalid JSON received: " + e.getMessage());
        }
    }

    private void sendToClientById(Integer clientId, String message) {
        WebSocket conn = connectionHandler.getClientConn(clientId);
        server.sendMessageToClient(conn, message);
    }

    private void handleMove(Integer clientId, JsonObject msg) {
        int gameId = msg.get("gameId").getAsInt();
        JsonElement move = msg.get("move");
        gameManager.handleMove(clientId, gameId, move);
    }

    private void handleTakebackRequest(Integer clientId, JsonObject msg) {
        Integer gameId = msg.get("gameId").getAsInt();
        gameManager.handleTakebackRequest(clientId, gameId);
    }

    private void handleSurrender(Integer clientId, JsonObject msg) {
        int gameId = msg.get("gameId").getAsInt();
        gameManager.handleSurrender(clientId, gameId);
    }

    private void handleDrawOffer(Integer clientId, JsonObject msg) {
        int gameId = msg.get("gameId").getAsInt();
        gameManager.handleDrawOffer(clientId, gameId);
    }

    private void handleAvailability(Integer clientId, JsonObject msg) {
        if (msg.get("avail").getAsInt() == 1) {
            WebSocket conn = connectionHandler.getClientConn(clientId);
            JsonObject response = new JsonObject();
            response.addProperty("type", "availabilityRes");
            Integer isTimed = msg.get("timedGameEnabled").getAsInt();
            Integer isRanked = msg.get("rankedGameEnabled").getAsInt();
            Integer time = -1;
            Integer inc = 0;
            if (isTimed.equals(1)) {
                time = msg.get("time").getAsInt();
                inc = msg.get("inc").getAsInt();
            }

            Integer gameCode = connectionHandler.generateJoinCode(clientId);
            gameManager.setGameProperties(gameCode, time, inc, isRanked);
            response.addProperty("gameCode", gameCode);
            server.sendMessageToClient(conn, response.toString());
        }
    }

    private void handleJoinGame(Integer clientId, JsonObject msg) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "joinGameRes");
        boolean isSuccess = false;
        WebSocket conn = connectionHandler.getClientConn(clientId);
        int joinCode = msg.get("gameCode").getAsInt();

        Integer opponentId = connectionHandler.joinGame(clientId, joinCode);
        if (opponentId.equals(-1) || opponentId.equals(clientId)) {
            response.addProperty("status", -1); // game not found
        } else {
            response.addProperty("status", 0);
            response.addProperty("gameCode", joinCode);
            Integer[] properties = gameManager.getGameProperties(joinCode);
            boolean isTimed = properties[0] != -1;
            response.addProperty("isTimed", isTimed ? 1 : 0);
            response.addProperty("isRanked", properties[2]);
            WebSocket connOpp = connectionHandler.getClientConn(opponentId);
            if (connOpp != null) {
                server.sendMessageToClient(connOpp, response.toString()); // if successful both players get the same
                                                                          // response
                isSuccess = true;
            } else {
                response.addProperty("status", -2); // opponent is not available
            }
        }
        server.sendMessageToClient(conn, response.toString());
        if (isSuccess) {
            gameManager.newGame(joinCode, clientId, opponentId);
        }
    }

    private void handlePromotion(Integer clientId, JsonObject msg) {
        int gameId = msg.get("gameId").getAsInt();
        JsonElement move = msg.get("move");
        char piece = msg.get("pieceType").getAsString().charAt(0);
        gameManager.handlePromotion(clientId, gameId, move, piece);
    }

    private void handleAcceptDraw(Integer clientId, JsonObject msg) {
        int gameId = msg.get("gameId").getAsInt();
        String status = msg.get("status").getAsString();
        gameManager.handleAcceptDraw(clientId, gameId, status);
    }

    private void acceptTakeback(Integer clientId, JsonObject msg) {
        int gameId = msg.get("gameId").getAsInt();
        String status = msg.get("status").getAsString();
        gameManager.handleAcceptTakeback(clientId, gameId, status);
    }

    public void playerDisconnected(Integer clientId) {
        Integer opponentId = connectionHandler.removeActiveUser(clientId);
        if (opponentId.equals(-1)) {
            return;
        } else {
            WebSocket conn = connectionHandler.getClientConn(opponentId);
            server.sendMessageToClient(conn, "{\"type\":\"opponentDisconnectedRes\"}");
        }
    }

    private void handleLoginRequest(WebSocket conn, JsonObject msg, Integer clientId) {
        String username = msg.get("username").getAsString();
        String password = msg.get("password").getAsString();
        boolean success = server.loginManager.checkLogin(username, password);
        JsonObject response = new JsonObject();
        if (success) {
            connectionHandler.addActiveUserLoggedIn(clientId, conn, username);
        }
        response.addProperty("type", "loginRes");
        response.addProperty("status", success ? "OK" : "ERROR");
        response.addProperty("username", success ? username : "");
        server.sendMessageToClient(conn, response.toString());
    }

    private void handleRegisterRequest(WebSocket conn, JsonObject msg, Integer clientId) {
        String username = msg.get("username").getAsString();
        String password = msg.get("password").getAsString();
        RegistrationStatus status = server.loginManager.registerUser(username, password);
        JsonObject response = new JsonObject();
        boolean success = status == RegistrationStatus.SUCCESS;
        response.addProperty("type", "registerRes");
        response.addProperty("status", status.name());
        response.addProperty("username", success ? username : "");
        if (success) {
            connectionHandler.addActiveUserLoggedIn(clientId, conn, username); // login after registration
        }
        server.sendMessageToClient(conn, response.toString());
    }

    public void sendToPlayers(Integer gameId, String message) {
        var players = connectionHandler.getActiveGamePlayers(gameId);
        WebSocket conn1 = connectionHandler.getClientConn(players.get(0));
        WebSocket conn2 = connectionHandler.getClientConn(players.get(1));
        server.sendMessageToClient(conn1, message);
        server.sendMessageToClient(conn2, message);

    }

    public void sendDeleteToPlayers(Integer gameId, Integer x, Integer y) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "deletePieceRes");
        response.addProperty("x", x);
        response.addProperty("y", y);
        sendToPlayers(gameId, response.toString());
    }

    public void sendUpdateView(Integer userId, JsonElement move) {
        JsonObject update = new JsonObject();
        update.addProperty("type", "boardUpdateRes");
        update.add("move", move);
        WebSocket conn = connectionHandler.getClientConn(userId);
        server.sendMessageToClient(conn, update.toString());
    }

    public void sendLost(Integer userId) {
        WebSocket conn = connectionHandler.getClientConn(userId);
        JsonObject response = new JsonObject();
        response.addProperty("type", "gameOverRes");
        response.addProperty("status", "lost");
        server.sendMessageToClient(conn, response.toString());
    }

    public void sendWin(Integer userId) {
        WebSocket conn = connectionHandler.getClientConn(userId);
        JsonObject response = new JsonObject();
        response.addProperty("type", "gameOverRes");
        response.addProperty("status", "won");
        server.sendMessageToClient(conn, response.toString());
    }

    public void sendStalemate(Integer gameId) {
        sendToPlayers(gameId, "{\"type\":\"gameOverRes\",\"status\":\"stalemate\"}");
    }

    public void sendMaterial(Integer gameId) {
        sendToPlayers(gameId, "{\"type\":\"gameOverRes\",\"status\":\"material\"}");
    }

    public void sendDrawAccepted(Integer gameId) {
        sendToPlayers(gameId, "{\"type\":\"gameOverRes\",\"status\":\"drawAccept\"}");
    }

    public void sendDrawDeclined(Integer userId) {
        WebSocket conn = connectionHandler.getClientConn(userId);
        JsonObject response = new JsonObject();
        response.addProperty("type", "drawDeclinedRes");
        server.sendMessageToClient(conn, response.toString());
    }

    public void sendTakebackResponse(Integer userId, String status) {
        WebSocket conn = connectionHandler.getClientConn(userId);
        JsonObject response = new JsonObject();
        response.addProperty("type", "takebackResponseRes");
        response.addProperty("status", status);
        server.sendMessageToClient(conn, response.toString());
    }

    public void sendDrawOffer(Integer opponentId) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "drawOfferRes");
        sendToClientById(opponentId, response.toString());
    }

    public void sendTakebackRequest(Integer opponentId) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "takebackRequestRes");
        sendToClientById(opponentId, response.toString());
    }

    public void sendTakebackStatus(Integer gameId, boolean isActive) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "takebackStatusRes");
        response.addProperty("status", isActive ? 1 : 0);
        sendToPlayers(gameId, response.toString());
    }

    public void sendPlayerIsBlack(Integer userId) {
        WebSocket conn = connectionHandler.getClientConn(userId);
        JsonObject response = new JsonObject();
        response.addProperty("type", "playerIsBlackRes");
        server.sendMessageToClient(conn, response.toString());
    }

    public void sendBoardState(Integer gameId, ArrayList<String> board) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "placementRes");
        for (int i = 0; i < board.size(); i++) {
            response.addProperty(Integer.toString(i), board.get(i));
        }
        sendToPlayers(gameId, response.toString());
    }

    public void sendPossibleMoves(Integer userId, JsonArray moves) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "possibleMovesRes");
        response.add("moves", moves);
        WebSocket conn = connectionHandler.getClientConn(userId);
        server.sendMessageToClient(conn, response.toString());
    }

    public void sendCheck(Integer gameId, Integer x, Integer y) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "checkRes");
        response.addProperty("x", x);
        response.addProperty("y", y);
        sendToPlayers(gameId, response.toString());
    }

    public void sendAvailablePromotion(Integer userId, Integer x1, Integer y1, Integer x2, Integer y2,
            String pieceString) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "availablePromotionsRes");
        JsonObject move = new JsonObject();
        move.addProperty("x1", x1);
        move.addProperty("y1", y1);
        move.addProperty("x2", x2);
        move.addProperty("y2", y2);
        response.add("move", move);
        response.addProperty("pieceTypes", pieceString);
        WebSocket conn = connectionHandler.getClientConn(userId);
        server.sendMessageToClient(conn, response.toString());
    }

    public void sendPromotion(Integer userId, Integer x1, Integer y1, Integer x2, Integer y2, char piece) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "promotionRes");
        JsonObject move = new JsonObject();
        move.addProperty("x1", x1);
        move.addProperty("y1", y1);
        move.addProperty("x2", x2);
        move.addProperty("y2", y2);
        response.add("move", move);
        response.addProperty("pieceType", piece);
        WebSocket conn = connectionHandler.getClientConn(userId);
        server.sendMessageToClient(conn, response.toString());
    }

    public void sendUpdateToPlayers(Integer gameId, Integer x1, Integer y1, Integer x2, Integer y2) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "boardUpdateRes");
        JsonObject move = new JsonObject();
        move.addProperty("x1", x1);
        move.addProperty("y1", y1);
        move.addProperty("x2", x2);
        move.addProperty("y2", y2);
        response.add("move", move);
        sendToPlayers(gameId, response.toString());
    }

    public void sendTimeUpdate(Integer gameId, Integer userId, String playerTime, String opponentTime, boolean isPlayer,
            boolean isOpponent) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "timeUpdateRes");
        response.addProperty("playerTime", playerTime);
        response.addProperty("opponentTime", opponentTime);
        response.addProperty("yourClockActive", isPlayer ? 1 : 0);
        response.addProperty("opponentClockActive", isOpponent ? 1 : 0);
        response.addProperty("gameId", gameId);
        WebSocket conn = connectionHandler.getClientConn(userId);
        server.sendMessageToClient(conn, response.toString());
    }
}
