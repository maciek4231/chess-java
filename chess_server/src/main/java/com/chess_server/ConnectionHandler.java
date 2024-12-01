package com.chess_server;

import java.util.List;
import java.util.Map;

import org.java_websocket.WebSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

public class ConnectionHandler {
    // thread safe for future use
    private ConcurrentHashMap<Integer, Integer> joinablePlayers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, WebSocket> activeUsers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, List<Integer>> activeGames = new ConcurrentHashMap<>(); // gameCode -> [player1,
                                                                                               // player2]
    private Random rand = new Random();
    private final GameManager gameManager;

    public ConnectionHandler(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public Integer GenerateClientID() {
        Integer randID;
        do {
            randID = rand.nextInt(0, 1_000_000);
        } while (activeUsers.containsKey(randID));
        return randID;
    }

    public Integer generateJoinCode(Integer clientId) {
        if (joinablePlayers.containsValue(clientId)) {
            joinablePlayers.values().remove(clientId);
        }
        Integer randN;
        do {
            randN = rand.nextInt(0, 1_000_000); // id od 000000 do 999999
        } while (joinablePlayers.containsKey(randN) || activeGames.containsKey(randN)); // poki nie zaczniemy doganiac
                                                                                        // lichessa to powinno byc ok
        joinablePlayers.put(randN, clientId);
        return randN;
    }

    public void removeJoinCode(Integer clientId) {
        if (joinablePlayers.containsValue(clientId)) {
            joinablePlayers.values().remove(clientId); // to trzeba bedzie lepiej robic
        }
    }

    public Integer joinGame(Integer clientId, Integer joinCode) {
        if (joinablePlayers.containsKey(joinCode)) {
            Integer opponentId = joinablePlayers.get(joinCode);
            joinablePlayers.remove(joinCode);
            if (joinablePlayers.containsValue(clientId)) {
                joinablePlayers.values().remove(clientId);
            }
            activeGames.put(joinCode, List.of(clientId, opponentId));
            return opponentId;
        }
        return -1;
    }

    public void addActiveUser(Integer clientId, WebSocket conn) {
        activeUsers.put(clientId, conn);
    }

    public void removeActiveUser(Integer clientId) { // player disconnected
        if (joinablePlayers.containsValue(clientId)) {
            joinablePlayers.values().remove(clientId);
        }
        for (Map.Entry<Integer, List<Integer>> entry : activeGames.entrySet()) { // na razie usuwamy gre jak gracz sie
                                                                                 // rozlaczy
            if (entry.getValue().contains(clientId)) { // w przyszlosci moze szybsza implementacja na 3 mapy
                activeGames.remove(entry.getKey());
                gameManager.removeGame(entry.getKey());
            }
        }
        activeUsers.remove(clientId);
    }

    public WebSocket getClientConn(Integer clientId) {
        return activeUsers.get(clientId);
    }

    public Integer getClientId(WebSocket conn) {
        for (Map.Entry<Integer, WebSocket> entry : activeUsers.entrySet()) {
            if (entry.getValue().equals(conn)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    public List<Integer> getActiveGamePlayers(Integer gameCode) {
        return activeGames.get(gameCode);
    }
}
