package com.chess_server;

import org.java_websocket.WebSocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MessageHandler {

    // private Gson gson;
    private final ChessWebSocketServer server;

    public MessageHandler(ChessWebSocketServer server) {
        this.server = server;
        // gson = new GsonBuilder()
        // .registerTypeAdapter(Message.class, (JsonDeserializer<Message>) (json,
        // typeOfT, context) -> {
        // String type = json.getAsJsonObject().get("type").getAsString();
        // switch (type) {
        // case "pickMove":
        // return context.deserialize(json, PickMoveMessage.class);
        // case "abort":
        // return context.deserialize(json, AbortMessage.class);
        // case "takeback":
        // return context.deserialize(json, TakebackMessage.class);
        // case "availability":
        // return context.deserialize(json, AvailabilityMessage.class);
        // default:
        // throw new IllegalArgumentException("Unknown message type: " + type);
        // }
        // }).create();
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
                default:
                    System.out.println("Unknown message type: " + type);
            }
        } catch (Exception e) {
            System.out.println("Invalid JSON received: " + e.getMessage());
        }
    }

    // private void handleMessage(WebSocket conn, String message) {
    // switch (msg.type) {
    // case "pickMove":
    // handleMove(conn, (PickMoveMessage) msg);
    // break;
    // case "abort":
    // handleAbort(conn, (AbortMessage) msg);
    // break;
    // case "takeback":
    // handleTakeback(conn, (TakebackMessage) msg);
    // break;
    // case "availability":
    // handleAvailability(conn, (AvailabilityMessage) msg);
    // break;
    // default:
    // System.out.println("Unknown message type: " + msg.type);
    // }
    // }

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
        Integer availability = msg.get("availability").getAsInt();
        if (availability.equals(1)) {
            server.sendMessageToClient(conn, "Available");
        } else {
            // Handle unavailability?
        }
        System.out.println("Handling availability");
    }
}