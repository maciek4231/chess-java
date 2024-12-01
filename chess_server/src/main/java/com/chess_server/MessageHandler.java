package com.chess_server;

import org.java_websocket.WebSocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MessageHandler {

    private final ChessWebSocketServer server;
    public final ConnectionHandler connectionHandler;

    public MessageHandler(ChessWebSocketServer server) {
        this.server = server;
        this.connectionHandler = new ConnectionHandler();
    }

    public void handleMessage(WebSocket conn, String message) {
        try {
            JsonObject msg = JsonParser.parseString(message).getAsJsonObject();
            String type = msg.get("type").getAsString();
            switch (type) {
                case "pickMove":
                    handleMove(conn, msg);
                    break;
                case "abort":
                    handleAbort(conn, msg);
                    break;
                case "takeback":
                    handleTakeback(conn, msg);
                    break;
                case "availability":
                    handleAvailability(conn, msg);
                    break;
                case "joinGame":
                    handleJoinGame(conn, msg);
                    break;
                default:
                    System.out.println("Unknown message type: " + type);
            }
        } catch (Exception e) {
            System.out.println("Invalid JSON received: " + e.getMessage());
        }
    }

    private void handleMove(WebSocket conn, JsonObject msg) {
        // Handle move message
        System.out.println("Picked move number: " + msg.get("moveNo").getAsInt());
    }

    private void handleAbort(WebSocket conn, JsonObject msg) {
        // Handle abort message
        System.out.println("Handling abort");
    }

    private void handleTakeback(WebSocket conn, JsonObject msg) {
        // Handle takeback message
        System.out.println("Handling takeback");
    }

    private void handleAvailability(WebSocket conn, JsonObject msg) {
        Integer availability = msg.get("avail").getAsInt();
        String clientId = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        JsonObject response = new JsonObject();
        if (availability.equals(1)) {
            Integer gameCode = connectionHandler.generateJoinCode(clientId);
            response.addProperty("gameCode", gameCode);
        } else {
            // obslugiwac unavailable?
        }
        server.sendMessageToClient(conn, response.toString());
        System.out.println("Handling availability");
    }

    private void handleJoinGame(WebSocket conn, JsonObject msg) {
        String clientId = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        int joinCode = msg.get("gameCode").getAsInt();
        JsonObject response = new JsonObject();
        response.addProperty("type", "joinGameRes");
        String opponentId = connectionHandler.joinGame(clientId, joinCode);
        if (opponentId.isEmpty()) {
            response.addProperty("status", -1); // nie udalo sie dolaczyc
        } else {
            response.addProperty("status", 0);
            response.addProperty("gameCode", joinCode);
            WebSocket connOpp = connectionHandler.getUserConnection(opponentId);
            if (connOpp != null) {
                server.sendMessageToClient(connOpp, response.toString());
            } else {
                response.addProperty("status", -2); // przeciwnik nie jest online
            }
        }
        server.sendMessageToClient(conn, response.toString());
    }
}