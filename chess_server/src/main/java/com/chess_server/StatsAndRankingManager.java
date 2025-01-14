package com.chess_server;

import java.sql.Connection;
import java.sql.PreparedStatement;

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
}
