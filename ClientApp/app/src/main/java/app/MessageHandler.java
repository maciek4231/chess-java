package app;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MessageHandler {

    ChessWebSocketClient client;
    Board board;

    public MessageHandler(ChessWebSocketClient client, Board board) {
        this.client = client;
        this.board = board;
    }

    public void handleMessage(String message) {
        try {
            JsonObject msg = JsonParser.parseString(message).getAsJsonObject();
            String type = msg.get("type").getAsString();
            switch (type) {
                case "pickMove":
                    // handleMove(msg);
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
}
