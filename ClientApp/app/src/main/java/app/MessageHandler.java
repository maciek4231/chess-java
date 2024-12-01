package app;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MessageHandler {

    ChessWebSocketClient client;
    Board board;
    ConnectWindow connectWindow;

    public MessageHandler(ChessWebSocketClient client, Board board) {
        this.client = client;
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
            board.setGameCode(code);
        } else {
            if (status == -1) {
                connectWindow.gameNotFound();
            } else {
                connectWindow.opponentLeft();
            }
        }
    }
}
