package com.chess_server;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        int port = 8887; // Port for WebSocket server
        try {
            ChessWebSocketServer server = new ChessWebSocketServer(new InetSocketAddress(port));
            server.setReuseAddr(true); // allows quickly restarting the server
            server.start();
            System.out.println("Chess server started on port: " + port);

            // String clientId = "127.0.0.1"; // Replace with the actual client ID
            // server.sendMessageToClient(clientId, "Hello, specific client!");
        } catch (Exception e) {
            System.out.println("Error occurred while starting chess-server: " + e.getMessage());
        }
    }
}