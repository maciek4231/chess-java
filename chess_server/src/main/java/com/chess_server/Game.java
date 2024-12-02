package com.chess_server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Random;

public class Game {

    private char[][] board = {
            { 'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r' },
            { 'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p' },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' },
            { 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P' },
            { 'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R' }
    };
    boolean whiteTurn = true;
    Integer playerWhite;
    Integer playerBlack;
    JsonArray currentLegalMoves;

    Game(Integer player1Id, Integer player2Id) {
        if (new Random().nextBoolean()) {
            Integer temp = player1Id;
            player1Id = player2Id;
            player2Id = temp;
        }
        this.playerWhite = player1Id;
        this.playerBlack = player2Id;
        System.out.println("Game started!");
    }

    public boolean isPlayerRound(Integer clientId) {
        if (clientId.equals(getCurrentPlayer())) {
            return true;
        }
        return false;
    }

    public Integer getCurrentPlayer() {
        return whiteTurn ? playerWhite : playerBlack;
    }

    public JsonObject getBoardState() {
        JsonObject initialBoard = new JsonObject();
        initialBoard.addProperty("type", "placementRes");
        for (int i = 0; i < 8; i++) {
            initialBoard.addProperty(Integer.toString(i), new String(board[i]));
        }
        return initialBoard;
    }

    public boolean makeMove(JsonElement move) {
        int x1 = move.getAsJsonObject().get("x1").getAsInt();
        int y1 = move.getAsJsonObject().get("y1").getAsInt();
        int x2 = move.getAsJsonObject().get("x2").getAsInt();
        int y2 = move.getAsJsonObject().get("y2").getAsInt();

        boolean isValid = false;

        for (JsonElement possibleMove : currentLegalMoves) {
            JsonObject possibleMoveObj = possibleMove.getAsJsonObject();
            if (possibleMoveObj.get("x1").getAsInt() == x1 &&
                    possibleMoveObj.get("y1").getAsInt() == y1 &&
                    possibleMoveObj.get("x2").getAsInt() == x2 &&
                    possibleMoveObj.get("y2").getAsInt() == y2) {
                isValid = true;
                break;
            }
        }

        if (isValid) {
            char piece = board[y1][x1];
            board[y1][x1] = ' ';
            board[y2][x2] = piece;
            whiteTurn = !whiteTurn;
        } else {
            throw new IllegalArgumentException("Invalid move");
        }
        return isValid;
    }

    public JsonObject getPossibleMoves() {
        JsonObject possibleMoves = new JsonObject();
        possibleMoves.addProperty("type", "possibleMovesRes");
        JsonArray movesArray = new JsonArray();

        if (whiteTurn) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    char piece = board[i][j];
                    switch (piece) {
                        case 'P':
                            addPawnMoves(movesArray, i, j, true);
                            break;
                        case 'R':
                            addRookMoves(movesArray, i, j, true);
                            break;
                        case 'N':
                            addKnightMoves(movesArray, i, j, true);
                            break;
                        case 'B':
                            addBishopMoves(movesArray, i, j, true);
                            break;
                        case 'Q':
                            addQueenMoves(movesArray, i, j, true);
                            break;
                        case 'K':
                            addKingMoves(movesArray, i, j, true);
                            break;
                    }
                }
            }
        } else {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    char piece = board[i][j];
                    switch (piece) {
                        case 'p':
                            addPawnMoves(movesArray, i, j, false);
                            break;
                        case 'r':
                            addRookMoves(movesArray, i, j, false);
                            break;
                        case 'n':
                            addKnightMoves(movesArray, i, j, false);
                            break;
                        case 'b':
                            addBishopMoves(movesArray, i, j, false);
                            break;
                        case 'q':
                            addQueenMoves(movesArray, i, j, false);
                            break;
                        case 'k':
                            addKingMoves(movesArray, i, j, false);
                            break;
                    }
                }
            }
        }
        this.currentLegalMoves = movesArray;
        possibleMoves.add("moves", movesArray);
        return possibleMoves;
    }

    private void addPawnMoves(JsonArray movesArray, int row, int col, boolean isWhite) {
        int direction = isWhite ? -1 : 1;
        int startRow = isWhite ? 6 : 1;

        if (isValidMove(row + direction, col) && board[row + direction][col] == ' ') {
            addMove(movesArray, row, col, row + direction, col);
            if (row == startRow && board[row + 2 * direction][col] == ' ') {
                addMove(movesArray, row, col, row + 2 * direction, col);
            }
        }

        if (isValidMove(row + direction, col - 1) && isOpponentPiece(row + direction, col - 1, isWhite)) {
            addMove(movesArray, row, col, row + direction, col - 1);
        }
        if (isValidMove(row + direction, col + 1) && isOpponentPiece(row + direction, col + 1, isWhite)) {
            addMove(movesArray, row, col, row + direction, col + 1);
        }
    }

    private void addRookMoves(JsonArray movesArray, int row, int col, boolean isWhite) {
        addLinearMoves(movesArray, row, col, isWhite, new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } });
    }

    private void addBishopMoves(JsonArray movesArray, int row, int col, boolean isWhite) {
        addLinearMoves(movesArray, row, col, isWhite, new int[][] { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } });
    }

    private void addQueenMoves(JsonArray movesArray, int row, int col, boolean isWhite) {
        addLinearMoves(movesArray, row, col, isWhite,
                new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } });
    }

    private void addKingMoves(JsonArray movesArray, int row, int col, boolean isWhite) {
        addSingleStepMoves(movesArray, row, col, isWhite,
                new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } });
    }

    private void addKnightMoves(JsonArray movesArray, int row, int col, boolean isWhite) {
        addSingleStepMoves(movesArray, row, col, isWhite,
                new int[][] { { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 }, { 1, 2 }, { 1, -2 }, { -1, 2 }, { -1, -2 } });
    }

    private void addLinearMoves(JsonArray movesArray, int row, int col, boolean isWhite, int[][] directions) {
        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];
            while (isValidMove(newRow, newCol) && board[newRow][newCol] == ' ') {
                addMove(movesArray, row, col, newRow, newCol);
                newRow += direction[0];
                newCol += direction[1];
            }
            if (isValidMove(newRow, newCol) && isOpponentPiece(newRow, newCol, isWhite)) {
                addMove(movesArray, row, col, newRow, newCol);
            }
        }
    }

    private void addSingleStepMoves(JsonArray movesArray, int row, int col, boolean isWhite, int[][] directions) {
        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];
            if (isValidMove(newRow, newCol)
                    && (board[newRow][newCol] == ' ' || isOpponentPiece(newRow, newCol, isWhite))) {
                addMove(movesArray, row, col, newRow, newCol);
            }
        }
    }

    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private boolean isOpponentPiece(int row, int col, boolean isWhite) {
        char piece = board[row][col];
        return (isWhite && Character.isLowerCase(piece)) || (!isWhite && Character.isUpperCase(piece));
    }

    private void addMove(JsonArray movesArray, int startRow, int startCol, int endRow, int endCol) {
        JsonObject move = new JsonObject();
        move.addProperty("x1", startCol);
        move.addProperty("y1", startRow);
        move.addProperty("x2", endCol);
        move.addProperty("y2", endRow);
        movesArray.add(move);
    }
}
