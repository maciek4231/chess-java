package com.chess_server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class LoginManagerTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    private LoginManager loginManager;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        loginManager = new LoginManager(connection);
    }

    @Test
    void testRegisterUser_UsernameExists() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        ResultSet resultSet = mock(ResultSet.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        RegistrationStatus status = loginManager.registerUser("existingUser", "password");
        
        assertEquals(RegistrationStatus.USERNAME_EXISTS, status);
    }

    @Test
    void testRegisterUser_Success() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        ResultSet resultSet = mock(ResultSet.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        RegistrationStatus status = loginManager.registerUser("newUser", "password");

        assertEquals(RegistrationStatus.SUCCESS, status);
    }

    @Test
    void testRegisterUser_ErrorOnDatabase() throws SQLException {
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        RegistrationStatus status = loginManager.registerUser("newUser", "password");
        assertEquals(RegistrationStatus.ERROR, status);
    }

    @Test
    void testCheckLogin_Success() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        ResultSet resultSet = mock(ResultSet.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getBytes("passwordHash")).thenReturn(new byte[]{1, 2, 3});
        when(resultSet.getBytes("salt")).thenReturn(new byte[]{4, 5, 6});
        LoginManager spyLoginManager = spy(loginManager);
        doReturn(new byte[]{1, 2, 3}).when(spyLoginManager).hashPasswordWithSalt(anyString(), any(byte[].class));
        boolean isLoginSuccessful = spyLoginManager.checkLogin("existingUser", "correctPassword");
        assertTrue(isLoginSuccessful);
    }

    @Test
    void testCheckLogin_Failure_WrongPassword() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        ResultSet resultSet = mock(ResultSet.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getBytes("passwordHash")).thenReturn(new byte[]{1, 2, 3});
        when(resultSet.getBytes("salt")).thenReturn(new byte[]{4, 5, 6});
        LoginManager spyLoginManager = spy(loginManager);
        doReturn(new byte[]{7, 8, 9}).when(spyLoginManager).hashPasswordWithSalt(anyString(), any(byte[].class));

        boolean isLoginSuccessful = spyLoginManager.checkLogin("existingUser", "wrongPassword");

        assertFalse(isLoginSuccessful);
    }

    @Test
    void testCheckLogin_UserNotFound() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        ResultSet resultSet = mock(ResultSet.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        boolean isLoginSuccessful = loginManager.checkLogin("nonExistentUser", "password");
        assertFalse(isLoginSuccessful);
    }
}
