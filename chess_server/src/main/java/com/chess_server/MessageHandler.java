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
                case "takeback":
                    handleTakeback(clientId, msg);
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

    private void handleTakeback(Integer clientId, JsonObject msg) {
        // Handle takeback message
        System.out.println("Handling takeback");
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
        Integer availability = msg.get("avail").getAsInt();
        WebSocket conn = connectionHandler.getClientConn(clientId);
        JsonObject response = new JsonObject();
        if (availability.equals(1)) {
            Integer gameCode = connectionHandler.generateJoinCode(clientId);
            response.addProperty("type", "availabilityRes");
            response.addProperty("gameCode", gameCode);
        } else {
            connectionHandler.removeJoinCode(clientId);
        }
        server.sendMessageToClient(conn, response.toString());
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

    public void playerDisconnected(Integer clientId) {
        Integer opponentId = connectionHandler.removeActiveUser(clientId);
        if (opponentId.equals(-1)) {
            return;
        } else {
            WebSocket conn = connectionHandler.getClientConn(opponentId);
            server.sendMessageToClient(conn, "{\"type\":\"opponentDisconnectedRes\"}");
        }
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

    public void sendDrawOffer(Integer opponentId) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "drawOfferRes");
        sendToClientById(opponentId, response.toString());
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
}
