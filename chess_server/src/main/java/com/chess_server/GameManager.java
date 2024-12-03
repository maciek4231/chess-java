
package com.chess_server;

import java.util.concurrent.ConcurrentHashMap;

import com.chess_server.Game.GameStatus;
import com.google.gson.JsonElement;

public class GameManager {
    private ConcurrentHashMap<Integer, Game> games = new ConcurrentHashMap<>();
    private final MessageHandler messageHandler;

    public GameManager(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public Game newGame(Integer gameCode, Integer player1, Integer player2) {

        Game game = new Game(player1, player2, gameCode, this.messageHandler, this);
        games.put(gameCode, game); // gameCode now becomes gameId
        messageHandler.sendPlayerIsBlack(game.playerBlack);
        messageHandler.sendBoardState(game.gameId, game.getBoard());
        messageHandler.sendPossibleMoves(game.getCurrentPlayer(), game.getPossibleMoves());
        return game;
    }

    public void removeGame(Integer gameId) {
        games.remove(gameId);
    }

    public void removeGame(Game game) {
        games.remove(game.gameId);
    }

    public Game getGame(int gameId) {
        return games.get(gameId);
    }

    public void handleMove(Integer clientId, Integer gameId, JsonElement move) {
        if (verifyPlayer(clientId, gameId)) {
            Game game = getGame(gameId);
            game.makeMove(move);
        } else {
            System.out.println("Invalid player");
        }
    }

    public void gameLost(Game game) {
        messageHandler.sendLost(game.getCurrentPlayer());
        messageHandler.sendWin(game.getOpponentPlayer());
        messageHandler.connectionHandler.removeGame(game);
        removeGame(game);
    }

    public void gameDraw(Game game, GameStatus status) {
        if (status == GameStatus.STALEMATE) {
            messageHandler.sendStalemate(game.gameId);
        } else {
            messageHandler.sendMaterial(game.gameId);
        }
        messageHandler.connectionHandler.removeGame(game);
        removeGame(game);
    }

    public boolean verifyPlayer(Integer clientId, Integer gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            return false;
        }
        if (!game.isPlayerRound(clientId)) {
            return false;
        }
        return true;
    }
}