
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
        if (verifyCurrentPlayer(clientId, gameId)) {
            Game game = getGame(gameId);
            game.makeMove(move);
        } else {
            System.out.println("Player " + clientId + " didn't pass verification");
        }
    }

    public void handlePromotion(Integer clientId, Integer gameId, JsonElement move, char piece) {
        if (verifyCurrentPlayer(clientId, gameId)) {
            Game game = getGame(gameId);
            game.makePromotion(move, piece);
        } else {
            System.out.println("Player " + clientId + " didn't pass verification");
        }
    }

    public void handleSurrender(Integer clientId, Integer gameId) {
        if (verifyPlayerInGame(clientId, gameId)) {
            Game game = getGame(gameId);
            gameLost(game, clientId);
        } else {
            System.out.println("Player " + clientId + " didn't pass verification");
        }
    }

    public void handleDrawOffer(Integer clientId, Integer gameId) {
        if (verifyCurrentPlayer(clientId, gameId)) {
            Game game = getGame(gameId);
            if (game.isAbleToOfferDraw(clientId)) {
                messageHandler.sendDrawOffer(game.getOpponentPlayer());
            }
        } else {
            System.out.println("Player " + clientId + " didn't pass verification");
        }
    }

    public void handleAcceptDraw(Integer clientId, Integer gameId, String status) {
        if (verifyPlayerInGame(clientId, gameId)) {
            Game game = getGame(gameId);
            if (game.isAbleToAcceptDraw(clientId)) {
                if (status.equals("accept")) {
                    gameDraw(game, GameStatus.DRAWACCEPT);
                } else {
                    messageHandler.sendDrawDeclined(game.getTheOtherPlayer(clientId));
                }
            }
        } else {
            System.out.println("Player " + clientId + " didn't pass verification");
        }
    }

    public void gameLost(Game game) {
        messageHandler.sendLost(game.getCurrentPlayer());
        messageHandler.sendWin(game.getOpponentPlayer());
        messageHandler.connectionHandler.removeGame(game);
        removeGame(game);
    }

    public void gameLost(Game game, Integer loser) {
        messageHandler.sendLost(loser);
        messageHandler.sendWin(game.getTheOtherPlayer(loser));
        messageHandler.connectionHandler.removeGame(game);
        removeGame(game);
    }

    public void gameDraw(Game game, GameStatus status) {
        if (status == GameStatus.STALEMATE) {
            messageHandler.sendStalemate(game.gameId);
        } else if (status == GameStatus.MATERIAL) {
            messageHandler.sendMaterial(game.gameId);
        } else {
            messageHandler.sendDrawAccepted(game.gameId);
        }
        messageHandler.connectionHandler.removeGame(game);
        removeGame(game);
    }

    public boolean verifyCurrentPlayer(Integer clientId, Integer gameId) {
        Game game = games.get(gameId);
        if (game == null || !game.isPlayerRound(clientId)) {
            return false;
        }
        return true;
    }

    public boolean verifyPlayerInGame(Integer clientId, Integer gameId) {
        Game game = games.get(gameId);
        if (game == null || !game.isPlayerInGame(clientId)) {
            return false;
        }
        return true;
    }
}