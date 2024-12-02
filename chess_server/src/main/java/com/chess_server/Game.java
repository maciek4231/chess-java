package com.chess_server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Random;

public class Game {

    public enum GameStatus {
        LOST, MATERIAL, STALEMATE, CONTINUE
    }

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

    public Integer getOpponentPlayer() {
        return whiteTurn ? playerBlack : playerWhite;
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

            if (isInCheck(whiteTurn)) {
                board[y1][x1] = piece;
                board[y2][x2] = ' ';
                throw new IllegalArgumentException("Invalid move: King would be in check");
            }
            whiteTurn = !whiteTurn;
        } else {
            throw new IllegalArgumentException("Invalid move");
        }
        return isValid;
    }

    private boolean isInCheck(boolean isWhite) {
        int kingRow = -1, kingCol = -1;
        char king = isWhite ? 'K' : 'k';

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == king) {
                    kingRow = i;
                    kingCol = j;
                    break;
                }
            }
        }

        return isSquareAttacked(kingRow, kingCol, !isWhite);
    }

    private boolean isSquareAttacked(int row, int col, boolean byWhite) {
        // Check for pawn attacks
        int direction = byWhite ? 1 : -1;
        if (isValidMove(row + direction, col - 1) && board[row + direction][col - 1] == (byWhite ? 'P' : 'p')) {
            return true;
        }
        if (isValidMove(row + direction, col + 1) && board[row + direction][col + 1] == (byWhite ? 'P' : 'p')) {
            return true;
        }

        // Check for knight attacks
        int[][] knightMoves = { { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 }, { 1, 2 }, { 1, -2 }, { -1, 2 },
                { -1, -2 } };
        for (int[] move : knightMoves) {
            int newRow = row + move[0];
            int newCol = col + move[1];
            if (isValidMove(newRow, newCol) && board[newRow][newCol] == (byWhite ? 'N' : 'n')) {
                return true;
            }
        }

        // Check for linear attacks (rook, queen)
        int[][] linearDirections = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
        for (int[] dir : linearDirections) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            while (isValidMove(newRow, newCol) && isEmpty(newRow, newCol)) {
                newRow += dir[0];
                newCol += dir[1];
            }
            if (isValidMove(newRow, newCol) && (board[newRow][newCol] == (byWhite ? 'R' : 'r')
                    || board[newRow][newCol] == (byWhite ? 'Q' : 'q'))) {
                return true;
            }
        }

        // Check for diagonal attacks (bishop, queen)
        int[][] diagonalDirections = { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
        for (int[] dir : diagonalDirections) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            while (isValidMove(newRow, newCol) && isEmpty(newRow, newCol)) {
                newRow += dir[0];
                newCol += dir[1];
            }
            if (isValidMove(newRow, newCol) && (board[newRow][newCol] == (byWhite ? 'B' : 'b')
                    || board[newRow][newCol] == (byWhite ? 'Q' : 'q'))) {
                return true;
            }
        }

        // Check for king attacks
        int[][] kingMoves = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
        for (int[] move : kingMoves) {
            int newRow = row + move[0];
            int newCol = col + move[1];
            if (isValidMove(newRow, newCol) && board[newRow][newCol] == (byWhite ? 'K' : 'k')) {
                return true;
            }
        }

        return false;
    }

    public JsonObject updateView(JsonElement move) {
        JsonObject update = new JsonObject();
        update.addProperty("type", "boardUpdateRes");
        update.add("move", move);
        return update;
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

        JsonArray validMovesArray = new JsonArray();
        for (JsonElement move : movesArray) {
            JsonObject moveObj = move.getAsJsonObject();
            int x1 = moveObj.get("x1").getAsInt();
            int y1 = moveObj.get("y1").getAsInt();
            int x2 = moveObj.get("x2").getAsInt();
            int y2 = moveObj.get("y2").getAsInt();

            char piece = board[y1][x1];
            char target = board[y2][x2];
            board[y1][x1] = ' ';
            board[y2][x2] = piece;

            if (!isInCheck(whiteTurn)) {
                validMovesArray.add(move);
            }

            board[y1][x1] = piece;
            board[y2][x2] = target;
        }

        this.currentLegalMoves = validMovesArray;
        possibleMoves.add("moves", validMovesArray);
        return possibleMoves;
    }

    public GameStatus getGameStatus() {

        if (isInCheck(whiteTurn) && currentLegalMoves.size() == 0) {
            return GameStatus.LOST; // checkmate
        }

        if (!isInCheck(whiteTurn) && currentLegalMoves.size() == 0) {
            return GameStatus.STALEMATE; // stalemate
        }
        if (checkInsufficientMaterial()) {
            return GameStatus.MATERIAL; // insufficient material
        }

        return GameStatus.CONTINUE;
    }

    private boolean checkInsufficientMaterial() {
        ArrayList<Character> whitePieces = new ArrayList<>();
        ArrayList<Character> blackPieces = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                char piece = board[i][j];
                if (piece == ' ' || piece == 'e' || piece == 'E' || piece == 'K' || piece == 'k') {
                    continue;
                }
                if (Character.isUpperCase(piece)) {
                    whitePieces.add(piece);
                } else {
                    blackPieces.add(piece);
                }
            }
        }

        return hasInsufficientMaterial(whitePieces) && hasInsufficientMaterial(blackPieces);
    }

    private boolean hasInsufficientMaterial(ArrayList<Character> pieces) {
        if (pieces.size() == 0) {
            return true; // lone king
        }
        if (pieces.size() == 1
                && (pieces.get(0) == 'B' || pieces.get(0) == 'N' || pieces.get(0) == 'b' || pieces.get(0) == 'n')) {
            return true; // king and bishop or king and knight
        }
        return false;
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
            while ((isValidMove(newRow, newCol) && isEmpty(newRow, newCol))) {
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
                    && (isEmpty(newRow, newCol) || isOpponentPiece(newRow, newCol, isWhite))) {
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

    private boolean isEmpty(int row, int col) {
        return board[row][col] == ' ' || board[row][col] == 'e' || board[row][col] == 'E';
    }

    private void prunePassants()
    {
        for (int i = 0; i < 8; i++)
        {
            if (board[2][i] == 'e')
            {
                board[2][i] = ' ';
            }
            if (board[5][i] == 'E')
            {
                board[5][i] = ' ';
            }
        }
    }

    private void addEnPassants(int x1, int y1, int x2, int y2, char piece)
    {
        if (piece == 'p' && y1 == 1 && y2 == 3) {
            board[2][x2] = 'e';
        }
        if (piece == 'P' && y1 == 6 && y2 == 4) {
            board[5][x2] = 'E';
        }
    }

    private boolean enPassantAttack(int x2, int y2)
    {
        char piece = board[y2][x2];
        if (piece == 'e')
        {
            deletePiece(x2, 3);
            return true;
        }
        if (piece == 'E')
        {
            deletePiece(x2, 4);
            return true;
        }
        return false;
    }

    public boolean handleEnPassants(JsonElement move)
    {
        int x1 = move.getAsJsonObject().get("x1").getAsInt();
        int y1 = move.getAsJsonObject().get("y1").getAsInt();
        int x2 = move.getAsJsonObject().get("x2").getAsInt();
        int y2 = move.getAsJsonObject().get("y2").getAsInt();

        char piece = board[y1][x1];

        boolean ret = enPassantAttack(x2, y2);
        prunePassants();
        addEnPassants(x1, y1, x2, y2, piece);

        return ret;
    }

    private void deletePiece(int x, int y)
    {
        board[y][x] = ' ';
        // TODO: send message to client
        System.out.println("Piece deleted at " + x + ", " + y);
    }
}
