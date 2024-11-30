package com.chess_server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

public class ConnectionHandler {
    // thread safe for future use
    private ConcurrentHashMap<String, Integer> joinablePlayers = new ConcurrentHashMap<>();
    private AtomicInteger idCounter = new AtomicInteger(0);
    private Random rand = new Random();

    public void generateJoinCode(String clientId) {
        var randN = rand.nextInt(1000, 10000);
        joinablePlayers.put(clientId, randN);
    }

    public boolean joinGame(String clientId, String client2Id, int joinCode) {
        if (joinablePlayers.containsKey(clientId)) {
            if (joinablePlayers.get(clientId).equals(joinCode)) {
                joinablePlayers.remove(clientId);
                if (joinablePlayers.containsKey(client2Id))
                    joinablePlayers.remove(client2Id);
                return true;
            }
        }
        return false;
    }
}
