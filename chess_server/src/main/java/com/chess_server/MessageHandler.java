package com.chess_server;

import org.java_websocket.WebSocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MessageHandler {

    private final ChessWebSocketServer server;
    public final ConnectionHandler connectionHandler;
    private final GameManager gameManager;

    public MessageHandler(ChessWebSocketServer server) {
        this.server = server;
        this.gameManager = new GameManager();
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
        // Handle move message
        System.out.println("Picked move number: " + msg.get("moveNo").getAsInt());
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
        System.out.println("Handling availability");
    }

    private void handleJoinGame(Integer clientId, JsonObject msg) {
        boolean isSuccess = false;
        WebSocket conn = connectionHandler.getClientConn(clientId);
        int joinCode = msg.get("gameCode").getAsInt();
        JsonObject response = new JsonObject();
        response.addProperty("type", "joinGameRes");
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
            Game game = gameManager.newGame(joinCode, clientId, opponentId);
            sendToPlayers(joinCode, game.getBoardState().toString());
        }
    }

    private boolean sendToPlayers(Integer gameCode, String message) {
        var players = connectionHandler.getActiveGamePlayers(gameCode);
        WebSocket conn1 = connectionHandler.getClientConn(players.get(0));
        WebSocket conn2 = connectionHandler.getClientConn(players.get(1));
        if (conn1 != null && conn2 != null) {
            server.sendMessageToClient(conn1, message);
            server.sendMessageToClient(conn2, message);
        } else {
            if (conn1 != null) {
                server.sendMessageToClient(conn1, "{\"type\":\"opponentDisconnected\"}");
            } else {
                server.sendMessageToClient(conn2, "{\"type\":\"opponentDisconnected\"}");
            }
            return false;
        }
        return true;
    }
}