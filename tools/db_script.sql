CREATE DATABASE IF NOT EXISTS chess_db;
USE chess_db;

CREATE USER IF NOT EXISTS'admin'@'localhost' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON chess_db.* TO 'admin'@'localhost';

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    passwordHash varbinary(255) NOT NULL,
    salt varbinary(255) NOT NULL
);


CREATE TABLE IF NOT EXISTS stats (
    user_id INT,
    wins INT DEFAULT 0,
    losses INT DEFAULT 0,
    draws INT DEFAULT 0,
    elo INT DEFAULT 1200,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS games (
    id INT AUTO_INCREMENT PRIMARY KEY,
    white_id INT NOT NULL,
    black_id INT NOT NULL,
    white_user VARCHAR(50) NOT NULL,
    black_user VARCHAR(50) NOT NULL,
    winner INT,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (white_id) REFERENCES users(id),
    FOREIGN KEY (black_id) REFERENCES users(id)
);