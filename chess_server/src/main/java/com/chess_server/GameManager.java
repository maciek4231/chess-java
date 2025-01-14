
package com.chess_server;

import java.util.concurrent.ConcurrentHashMap;

import com.chess_server.Game.GameStatus;
import com.google.gson.JsonElement;

public class GameManager {
    private ConcurrentHashMap<Integer, Game> games = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Integer[]> gameProperties = new ConcurrentHashMap<>(); // gameId -> [time, inc,
                                                                                              // isRanked]
    private final MessageHandler messageHandler;

    public GameManager(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public Game newGame(Integer gameCode, Integer player1, Integer player2) {
        Integer time = gameProperties.get(gameCode)[0];
        Integer inc = gameProperties.get(gameCode)[1];
        if (time == null) {
            System.out.println("Warning: Time is null");
            time = -1;
        }
        Game game = new Game(player1, player2, gameCode, time, inc, this.messageHandler, this);
        games.put(gameCode, game); // gameCode now becomes gameId
        messageHandler.sendPlayerIsBlack(game.playerBlack);
        messageHandler.sendBoardState(game.gameId, game.getBoard());
        messageHandler.sendPossibleMoves(game.getCurrentPlayer(), game.getPossibleMoves());
        return game;
    }

    public void setGameProperties(Integer gameCode, Integer time, Integer inc, Integer isRanked) {
        gameProperties.put(gameCode, new Integer[] { time, inc, isRanked });
    }

    public Integer[] getGameProperties(Integer gameCode) {
        return gameProperties.get(gameCode);
    }

    public void removeProperties(Integer gameCode) {
        gameProperties.remove(gameCode);
    }

    public void removeGameAndTime(Integer gameId) {
        games.remove(gameId);
        gameProperties.remove(gameId);
    }

    public Integer removePlayerFromGame(Integer clientId, Integer gameId) {
        if (verifyPlayerInGame(clientId, gameId)) {
            Game game = getGame(gameId);
            Integer theOtherPlayer = game.getTheOtherPlayer(clientId);
            eraseGame(game);
            return theOtherPlayer;
        }
        return null;
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

    public void handleTakebackRequest(Integer clientId, Integer gameId) {
        if (verifyPlayerInGame(clientId, gameId)) {
            Game game = getGame(gameId);
            if (game.isAbleToOfferTakeback(clientId)) {
                messageHandler.sendTakebackRequest(game.getTheOtherPlayer(clientId));
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

    public void handleAcceptTakeback(Integer clientId, Integer gameId, String status) {
        if (verifyPlayerInGame(clientId, gameId)) {
            Game game = getGame(gameId);
            if (game.isAbleToAcceptTakeback(clientId)) {
                if (status.equals("accept")) {
                    messageHandler.sendTakebackResponse(game.getTheOtherPlayer(clientId), status);
                    messageHandler.sendTakebackResponse(clientId, status);
                    game.takeback(game.getTheOtherPlayer(clientId));
                } else {
                    messageHandler.sendTakebackResponse(game.getTheOtherPlayer(clientId), "decline");
                }
            }
        } else {
            System.out.println("Player " + clientId + " didn't pass verification");
        }
    }

    public void eraseGame(Integer gameId) {
        eraseGame(games.get(gameId));
    }

    private void eraseGame(Game game) {
        if (game != null) {
            messageHandler.connectionHandler.removeGame(game);
            removeProperties(game.gameId);
            games.remove(game.gameId);
        }
    }

    public void gameLost(Game game) {
        gameLost(game, game.getCurrentPlayer());
    }

    public void gameLost(Game game, Integer loser) {
        messageHandler.sendLost(loser);
        messageHandler.sendWin(game.getTheOtherPlayer(loser));
        if (gameProperties.get(game.gameId)[2] == 1) {
            messageHandler.updateStats(loser, game.getTheOtherPlayer(loser), false);
        }
        eraseGame(game);
    }

    public void gameDraw(Game game, GameStatus status) {
        if (status == GameStatus.STALEMATE) {
            messageHandler.sendStalemate(game.gameId);
        } else if (status == GameStatus.MATERIAL) {
            messageHandler.sendMaterial(game.gameId);
        } else {
            messageHandler.sendDrawAccepted(game.gameId);
        }
        if (gameProperties.get(game.gameId)[2] == 1) {
            messageHandler.updateStats(game.playerWhite, game.playerBlack, true);
        }
        eraseGame(game);
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