package com.chess_server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.chess_server.LoginManager.RegistrationStatus;

// TODO: proper database mocking
public class LoginTest {

    @Test
    void testLogin() {

        LoginManager loginManager = new LoginManager(new DatabaseManager().connection);
        assertEquals(RegistrationStatus.SUCCESS, loginManager.registerUser("testLogin", "hasloTestowe"));

        assertEquals(loginManager.checkLogin("testLogin", "hasloTestowe"), true);
    }

    @Test
    void testLoginNullPassword() {

        LoginManager loginManager = new LoginManager(new DatabaseManager().connection);
        assertEquals(loginManager.checkLogin("testLogin", null), false);
    }

    @Test
    void testLoginNullUsername() {

        LoginManager loginManager = new LoginManager(new DatabaseManager().connection);
        assertEquals(loginManager.checkLogin(null, "hasloTestowe"), false);
    }

    @Test
    void testLoginWrongPassword() {

        LoginManager loginManager = new LoginManager(new DatabaseManager().connection);
        assertEquals(RegistrationStatus.SUCCESS, loginManager.registerUser("testLoginWrongPassword", "hasloTestowe"));

        assertEquals(loginManager.checkLogin("testLoginWrongPassword", "hasloTestoweZle"), false);
    }
}
