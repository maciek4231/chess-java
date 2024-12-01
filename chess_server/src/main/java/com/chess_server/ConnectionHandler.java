package com.chess_server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.java_websocket.WebSocket;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

public class ConnectionHandler {
    // thread safe for future use
    private ConcurrentHashMap<Integer, String> joinablePlayers = new ConcurrentHashMap<>();
    private Map<String, WebSocket> activeUsers = new ConcurrentHashMap<>();
    private AtomicInteger idCounter = new AtomicInteger(0);
    private Random rand = new Random();

    public Integer generateJoinCode(String clientId) {
        if (joinablePlayers.containsValue(clientId)) {
            joinablePlayers.values().remove(clientId);
        }
        Integer randN;
        do {
            randN = rand.nextInt(0, 1_000_000); // id od 000000 do 999999
        } while (joinablePlayers.containsKey(randN)); // poki nie zaczniemy doganiac lichessa to powinno byc ok
        joinablePlayers.put(randN, clientId);
        return randN;
    }

    public String joinGame(String clientId, int joinCode) {
        if (joinablePlayers.containsKey(joinCode)) {
            String opponentId = joinablePlayers.get(joinCode);
            joinablePlayers.remove(joinCode);
            if (joinablePlayers.containsValue(clientId)) {
                joinablePlayers.values().remove(clientId);
            }
            return opponentId;
        }
        return "";
    }

    public void addActiveUser(String clientId, WebSocket conn) {
        activeUsers.put(clientId, conn);
    }

    public void removeActiveUser(String clientId) {
        if (joinablePlayers.containsValue(clientId)) {
            joinablePlayers.values().remove(clientId);
        }
        activeUsers.remove(clientId);
    }

    public WebSocket getUserConnection(String clientId) {
        return activeUsers.get(clientId);
    }
}
