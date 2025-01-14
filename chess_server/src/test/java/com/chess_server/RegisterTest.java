package com.chess_server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class RegisterTest {

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
    void testRegister_Success() throws SQLException {
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
    void testRegister_UsernameExists() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        ResultSet resultSet = mock(ResultSet.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        RegistrationStatus status = loginManager.registerUser("existingUser", "password");

        assertEquals(RegistrationStatus.USERNAME_EXISTS, status);
    }

    @Test
    void testRegister_NullPassword() {
        RegistrationStatus status = loginManager.registerUser("newUser", null);
        assertEquals(RegistrationStatus.ERROR, status);
    }

    @Test
    void testRegister_NullUsername() {
        RegistrationStatus status = loginManager.registerUser(null, "password");
        assertEquals(RegistrationStatus.ERROR, status);
    }

    @Test
    void testRegister_ErrorOnDatabase() throws SQLException {
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);

        RegistrationStatus status = loginManager.registerUser("newUser", "password");

        assertEquals(RegistrationStatus.ERROR, status);
    }
}
