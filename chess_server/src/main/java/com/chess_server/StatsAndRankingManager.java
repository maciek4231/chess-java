package com.chess_server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class StatsAndRankingManager {
    private Connection connection;

    public StatsAndRankingManager(Connection connection) {
        this.connection = connection;
    }

    public void addStatsEntry(int user_id) {
        String query = "INSERT INTO stats (user_id) VALUES (?)";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, user_id);
            statement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error occurred while adding stats entry: " + e.getMessage());
        }
    }

    public Double[] updateStats(String winner, String loser, boolean isDraw) {
        String query = "UPDATE stats SET wins = wins + ?, losses = losses + ?, draws = draws + ?, elo = ? WHERE user_id = ?";
        int winner_id = getIdByUsername(winner);
        int loser_id = getIdByUsername(loser);
        Double eloWinner = (double) getEloById(winner_id);
        Double eloLoser = (double) getEloById(loser_id);
        Double deltaWinner;
        Double deltaLoser;
        if (isDraw) {
            Double[] elo = EloRating(eloWinner, eloLoser, 30, 0.5);
            deltaWinner = elo[0] - eloWinner;
            deltaLoser = elo[1] - eloLoser;
            eloWinner = elo[0];
            eloLoser = elo[1];
        } else {
            Double[] elo = EloRating(eloWinner, eloLoser, 30, 1.0);
            deltaWinner = elo[0] - eloWinner;
            deltaLoser = elo[1] - eloLoser;
            eloWinner = elo[0];
            eloLoser = elo[1];
        }
        System.out.println("eloWinner: " + eloWinner);
        System.out.println("eloLoser: " + eloLoser);
        System.out.println("winner_id: " + winner_id);
        System.out.println("loser_id: " + loser_id);

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            if (isDraw) {
                statement.setInt(1, 0);
                statement.setInt(2, 0);
                statement.setInt(3, 1);
                statement.setInt(5, winner_id);
                statement.setInt(4, eloWinner.intValue());
                statement.executeUpdate();
                statement.setInt(4, eloLoser.intValue());
                statement.setInt(5, loser_id);

                statement.executeUpdate();
            } else {
                statement.setInt(1, 1);
                statement.setInt(2, 0);
                statement.setInt(3, 0);
                statement.setInt(4, eloWinner.intValue());
                statement.setInt(5, winner_id);
                statement.executeUpdate();

                statement.setInt(1, 0);
                statement.setInt(2, 1);
                statement.setInt(3, 0);
                statement.setInt(4, eloLoser.intValue());
                statement.setInt(5, loser_id);
                statement.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Error occurred while updating stats: " + e.getMessage());
        }
        return new Double[] { eloWinner, deltaWinner, eloLoser, deltaLoser };
    }

    private int getIdByUsername(String username) {
        String query = "SELECT id FROM users WHERE username = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.executeQuery();
            ResultSet res = statement.getResultSet();
            if (res.next()) {
                return res.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error occurred while getting user id: " + e.getMessage());
        }
        return -1;
    }

    private int getEloById(int id) {
        String query = "SELECT elo FROM stats WHERE user_id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeQuery();
            ResultSet res = statement.getResultSet();
            if (res.next()) {
                return res.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error occurred while getting elo: " + e.getMessage());
        }
        return -1;
    }

    public static Double probabilityElo(Double rating1, Double rating2) {

        return 1.0 / (1 + Math.pow(10, (rating1 - rating2) / 400.0));
    }

    // calculate Elo rating
    // outcome determines the outcome: 1 for Player A win, 0 for Player B win, 0.5
    // for draw.
    public static Double[] EloRating(Double ratingA, Double ratingB, int K, Double outcome) {
        Double probA = probabilityElo(ratingB, ratingA);
        Double probB = probabilityElo(ratingA, ratingB);

        ratingA = ratingA + K * (outcome - probA);
        ratingB = ratingB + K * ((1 - outcome) - probB);
        return new Double[] { ratingA, ratingB };
    }

    public JsonArray getLeaderboard(Integer page) {
        String query = "SELECT username, elo FROM stats JOIN users ON stats.user_id = users.id ORDER BY elo DESC";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.executeQuery();
            ResultSet res = statement.getResultSet();
            JsonArray leaderboard = new JsonArray();
            for (int i = 0; i < page * 10; i++) {
                res.next();
            }
            for (int i = 0; i < 10; i++) {
                res.next();
                JsonObject entry = new JsonObject();
                if (res.isAfterLast()) {
                    break;
                }
                entry.addProperty("rank", i + (page * 10) + 1);
                entry.addProperty("username", res.getString(1));
                entry.addProperty("elo", res.getInt(2));
                leaderboard.add(entry);

            }
            return leaderboard;
        } catch (Exception e) {
            System.out.println("Error occurred while getting leaderboard: " + e.getMessage());
        }
        return null;
    }

    public int[] getPlayerStats(String username) {
        String query = "SELECT wins, losses, draws, elo FROM stats JOIN users ON stats.user_id = users.id WHERE username = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.executeQuery();
            ResultSet res = statement.getResultSet();
            if (res.next()) {
                int[] stats = new int[4];
                stats[0] = res.getInt(1);
                stats[1] = res.getInt(2);
                stats[2] = res.getInt(3);
                stats[3] = res.getInt(4);
                return stats;
            }
        } catch (Exception e) {
            System.out.println("Error occurred while getting player stats: " + e.getMessage());
        }
        return null;
    }
}
