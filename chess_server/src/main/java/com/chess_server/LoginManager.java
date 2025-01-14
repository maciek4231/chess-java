package com.chess_server;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class LoginManager {
    private Connection connection;

    public LoginManager(Connection connection) {
        this.connection = connection;
    }

    public RegistrationStatus registerUser(String username, String password) {
        if (password == null) {
            return RegistrationStatus.ERROR;
        }
        // Check if username already exists
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            System.out.println(username);
            statement.setString(1, username);
            if (statement.executeQuery().next()) {
                System.out.println("Username already exists");
                return RegistrationStatus.USERNAME_EXISTS;
            }
        } catch (Exception e) {
            System.out.println("Error occurred while checking if username exists: " + e.getMessage());
            return RegistrationStatus.ERROR;
        }

        byte[][] hashedWithSalt = hashPassword(password); // hash, salt
        password = null; // Clear password from memory

        if (hashedWithSalt == null) {
            return RegistrationStatus.ERROR;
        }
        byte[] hash = hashedWithSalt[0];
        byte[] salt = hashedWithSalt[1];

        // Insert new user into database
        String query = "INSERT INTO users (username, passwordHash, salt) VALUES (?, ?, ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setBytes(2, hash);
            statement.setBytes(3, salt);
            statement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error occurred while registering user: " + e.getMessage());
            return RegistrationStatus.ERROR;
        }
        return RegistrationStatus.SUCCESS;
    }

    private byte[][] hashPassword(String password) {

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return new byte[][] { hashPasswordWithSalt(password, salt), salt };

    }

    private byte[] hashPasswordWithSalt(String password, byte[] salt) {
        try {
            if (salt.length != 16) {
                System.out.println("Salt length is not 16 bytes");
                return null;
            }
            // 210 000 iterations, 64 bytes length
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 210_000, 512);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512"); // Using SHA-512
            byte[] hash = f.generateSecret(spec).getEncoded();
            return hash;
        } catch (Exception e) {
            System.out.println("Error occurred while hashing password: " + e.getMessage());
            return null;
        }
    }

    public boolean checkLogin(String username, String password) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            statement.setString(1, username);
            var result = statement.executeQuery();
            if (!result.next()) {
                return false;
            }
            byte[] hash = result.getBytes("passwordHash");
            byte[] salt = result.getBytes("salt");

            byte[] hashToCheck = hashPasswordWithSalt(password, salt);
            if (hashToCheck == null) {
                return false;
            }
            return Arrays.equals(hash, hashToCheck);
        } catch (Exception e) {
            System.out.println("Error occurred while checking login: " + e.getMessage());
            return false;
        }
    }
}
