package com.chess_server;

import java.sql.Connection;

public class GameStatsUpdater {
    private final Connection connection;

    public GameStatsUpdater(Connection connection) {
        this.connection = connection;
    }

    public void updateStats(int winnerId, int loserId) {
        // String query = "UPDATE stats SET wins = wins + 1 WHERE user_id = ?";
        // try {
        // connection.prepareStatement(query).setInt(1, winnerId).executeUpdate();
        // } catch (Exception e) {
        // System.out.println("Error occurred while updating stats: " + e.getMessage());
        // }

        // query = "UPDATE stats SET losses = losses + 1 WHERE user_id = ?";
        // try {
        // connection.prepareStatement(query).setInt(1, loserId).executeUpdate();
        // } catch (Exception e) {
        // System.out.println("Error occurred while updating stats: " + e.getMessage());
        // }
    }
}
