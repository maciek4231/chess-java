
package com.chess_server;

import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private ConcurrentHashMap<Integer, Game> games = new ConcurrentHashMap<>();

    public Game newGame(Integer gameCode, Integer player1, Integer player2) {
        Game game = new Game(player1, player2);
        games.put(gameCode, game); // gameCode now becomes gameId
        return game;
    }

    public void removeGame(Integer gameId) {
        games.remove(gameId);
    }

    public Game getGame(int gameId) {
        return games.get(gameId);
    }
}