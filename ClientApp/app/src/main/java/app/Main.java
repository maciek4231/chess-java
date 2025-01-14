package app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Main {
    public static void main(String[] args) {
        ChessWebSocketClient client = null;
        String server = getServerAddress();

        try {
            client = new ChessWebSocketClient(new URI(server));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        new Application(client);
    }

    private static String getServerAddress() {
        FileInputStream file = null;
        String server = "wss://illchess.serwerasus.duckdns.org";

        try {
            file = new FileInputStream("config.json");
            byte[] data = new byte[file.available()];
            file.read(data);
            String string = new String(data);
            JsonObject json = JsonParser.parseString(string).getAsJsonObject();
            server = json.get("server").getAsString();
            file.close();
        } catch (FileNotFoundException e) {
            System.out.println("config.json not found, using default server address");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return server;
    }
}