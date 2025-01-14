package com.chess_server;

import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) {
        int port = 8887; // Port for WebSocket server

        DatabaseManager db = new DatabaseManager();
        try {
            ChessWebSocketServer server = new ChessWebSocketServer(new InetSocketAddress(port), db);
            server.setReuseAddr(true); // allows quickly restarting the server

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        server.stop(1000, "server shutdown");
                    } catch (Exception e) {
                        System.out.println("Cannot stop server gracefully: " + e.getMessage());
                    }
                    db.closeConnection();
                    System.out.println("Chess server stopped.");
                }
            });

            server.start();
            System.out.println("Chess server started on port: " + port);

        } catch (

        Exception e) {
            System.out.println("Error occurred while starting chess-server: " + e.getMessage());
            db.closeConnection();
            System.exit(2);
        }
    }
}