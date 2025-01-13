package com.chess_server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseConnector {
    public void test() {
        // Dane do połączenia
        String url = "jdbc:mysql://localhost:3306/chess_db";
        String username = "admin";
        String password = "admin";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Połączenie nawiązane!");

            String query = "SELECT * FROM users";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("username");
                String nazwa = resultSet.getString("password");
                String ranking = resultSet.getString("email");
                System.out.println("ID: " + id + ", Nazwa: " + nazwa + ", Ranking: " + ranking);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}