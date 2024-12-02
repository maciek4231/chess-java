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
    final private GameManager gameManager;

    private Random rand = new Random();

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

    /**
     * Removes user from activeUsers, joinablePlayers and removes user's game from
     * activeGames and gameManager (if applicable)
     * 
     * @param clientId
     * @return clientId of opponent or -1 if user wasn't in any game
     */
    public Integer removeActiveUser(Integer clientId) { // player disconnected
        Integer opponentId = -1;
        if (joinablePlayers.containsValue(clientId)) {
            joinablePlayers.values().remove(clientId);
        }
        for (Map.Entry<Integer, List<Integer>> entry : activeGames.entrySet()) { // na razie usuwamy gre jak gracz sie
                                                                                 // rozlaczy
            List<Integer> players = entry.getValue();
            if (players.contains(clientId)) { // w przyszlosci moze szybsza implementacja na 3? mapy
                Integer gameCode = entry.getKey();
                activeGames.remove(gameCode);
                gameManager.removeGame(gameCode);
                opponentId = players.get(0).equals(clientId) ? players.get(1)
                        : players.get(0);
                break; // na razie zakladamy ze gracz jest w jednej grze
            }
        }
        activeUsers.remove(clientId);
        return opponentId; //  w przyszlosci potencjalnie wielu przeciwnikow
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
