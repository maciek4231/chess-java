package com.chess_server;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnector {

    Connection connection;

    public DatabaseConnector() {
        int port = 3306; // default port for MySQL
        String url = "jdbc:mysql://localhost:" + port + "/chess_db"; // mysql needs to be running

        // user admin with password admin needs to be created in mysql
        // with access to chess_db database

        String username = "admin";
        String password = "admin";

        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to MySQL database running on local port " + port);
        } catch (Exception e) {
            System.out.println("Error occurred while connecting to MySQL database: " + e.getMessage());
            System.out.println("*Make sure MySQL server is running on port " + port);
            // e.printStackTrace();
        }
    }
}