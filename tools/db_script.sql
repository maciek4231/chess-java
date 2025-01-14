CREATE DATABASE IF NOT EXISTS chess_db;
USE chess_db;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    passwordHash varbinary(255) NOT NULL,
    salt varbinary(255) NOT NULL
);

-- insert into users (username, password, email) values ('admin', 'admin', 'admin@szachy.pl');

CREATE TABLE IF NOT EXISTS stats (
    user_id INT,
    wins INT DEFAULT 0,
    losses INT DEFAULT 0,
    draws INT DEFAULT 0,
    elo INT DEFAULT 1200,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
