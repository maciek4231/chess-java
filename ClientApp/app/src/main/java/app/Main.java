package app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFrame;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {

    public static void main(String[] args) {
        ChessWebSocketClient client;
        String server = getServerAddress();

        try {
            client = new ChessWebSocketClient(new URI(server));
            setUpConnection(client);
            Game game = new Game(client);

            JFrame frame = new JFrame("illChess");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1152, 1056);
            frame.getContentPane().add(game.getWindow());
            frame.setVisible(true);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static void setUpConnection(ChessWebSocketClient client) {
        client.connect();
        while (!client.isOpen()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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