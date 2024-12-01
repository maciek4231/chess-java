package com.chess_server;

import com.google.gson.JsonObject;

public class Game {

    char[][] board = {
            { 'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r' },
            { 'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p' },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' },
            { 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P' },
            { 'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R' }
    };

    Game(Integer player1, Integer player2) {
        System.out.println("Game started!");
    }

    public JsonObject getBoardState() {
        JsonObject initialBoard = new JsonObject();
        initialBoard.addProperty("type", "placementRes");
        for (int i = 0; i < 8; i++) {
            initialBoard.addProperty(Integer.toString(i), new String(board[i]));
        }
        return initialBoard;
    }

}
