package com.chess_server;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChessWebSocketServer extends WebSocketServer {

    private Map<String, WebSocket> clients = new ConcurrentHashMap<>();
    private final MessageHandler messageHandler;

    public ChessWebSocketServer(InetSocketAddress address) {
        super(address);
        messageHandler = new MessageHandler(this);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String clientId = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        clients.put(clientId, conn);
        System.out.println("New connection from " + clientId);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String clientId = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        clients.remove(clientId);
        System.out.println("Closed connection from " + clientId);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println(
                "Message from " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + ": " + message);
        // Handle incoming messages from clients
        try {
            messageHandler.handleMessage(conn, message);
        } catch (Exception e) {
            System.out.println("Invalid JSON received: " + e.getMessage());
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Awaiting connections...");
    }

    public void sendMessageToClient(WebSocket client, String message) {
        if (client != null) {
            client.send(message);
        } else {
            System.out.println("Client not found.");
        }
    }
}