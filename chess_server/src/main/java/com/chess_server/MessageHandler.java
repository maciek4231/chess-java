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
                case "abort":
                    handleAbort(clientId, msg);
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
                default:
                    System.out.println("Unknown message type: " + type);
            }
        } catch (Exception e) {
            System.out.println("Invalid JSON received: " + e.getMessage());
        }
    }

    private void handleMove(Integer clientId, JsonObject msg) {
        int gameId = msg.get("gameId").getAsInt();
        JsonElement move = msg.get("move");
        gameManager.handleMove(clientId, gameId, move);
    }

    private void handleAbort(Integer clientId, JsonObject msg) {
        // Handle abort message
        System.out.println("Handling abort");
    }

    private void handleTakeback(Integer clientId, JsonObject msg) {
        // Handle takeback message
        System.out.println("Handling takeback");
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
        if (opponentId.equals(-1)) {
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

    public void sendUpdateView(Integer UserId, JsonElement move) {
        JsonObject update = new JsonObject();
        update.addProperty("type", "boardUpdateRes");
        update.add("move", move);
        WebSocket conn = connectionHandler.getClientConn(UserId);
        server.sendMessageToClient(conn, update.toString());
    }

    public void sendLost(Integer UserId) {
        WebSocket conn = connectionHandler.getClientConn(UserId);
        JsonObject response = new JsonObject();
        response.addProperty("type", "gameOverRes");
        response.addProperty("status", "lost");
        server.sendMessageToClient(conn, response.toString());
    }

    public void sendWin(Integer UserId) {
        WebSocket conn = connectionHandler.getClientConn(UserId);
        JsonObject response = new JsonObject();
        response.addProperty("type", "gameOverRes");
        response.addProperty("status", "won");
        server.sendMessageToClient(conn, response.toString());
    }

    public void sendStalemate(Game game) {
        sendToPlayers(game.gameId, "{\"type\":\"gameOverRes\",\"status\":\"stalemate\"}");
    }

    public void sendMaterial(Game game) {
        sendToPlayers(game.gameId, "{\"type\":\"gameOverRes\",\"status\":\"material\"}");
    }

    public void sendPlayerIsBlack(Integer UserId) {
        WebSocket conn = connectionHandler.getClientConn(UserId);
        JsonObject response = new JsonObject();
        response.addProperty("type", "playerIsBlackRes");
        server.sendMessageToClient(conn, response.toString());
    }

    public void sendBoardState(Game game, ArrayList<String> board) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "placementRes");
        for (int i = 0; i < board.size(); i++) {
            response.addProperty(Integer.toString(i), board.get(i));
        }
        sendToPlayers(game.gameId, response.toString());
    }

    public void sendPossibleMoves(Integer UserId, JsonArray moves) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "possibleMovesRes");
        response.add("moves", moves);
        WebSocket conn = connectionHandler.getClientConn(UserId);
        server.sendMessageToClient(conn, response.toString());
    }
}
