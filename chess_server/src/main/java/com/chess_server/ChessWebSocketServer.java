package com.chess_server;

import org.java_websocket.server.WebSocketServer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;

public class ChessWebSocketServer extends WebSocketServer {

    private final MessageHandler messageHandler;
    private final DatabaseManager databaseManager;
    final LoginManager loginManager;

    public ChessWebSocketServer(InetSocketAddress address, DatabaseManager databaseManager) {
        super(address);
        messageHandler = new MessageHandler(this);
        this.databaseManager = databaseManager;
        this.loginManager = new LoginManager(databaseManager.connection);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Integer clientId = messageHandler.connectionHandler.GenerateClientID();
        messageHandler.connectionHandler.addActiveUser(clientId, conn);
        System.out.println("New connection from " + clientId + " with ip: "
                + handshake.getFieldValue("X-FORWARDED-FOR"));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Integer clientId = messageHandler.connectionHandler.getClientId(conn);
        messageHandler.playerDisconnected(clientId);
        System.out.println("Closed connection from client id: " + clientId);
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