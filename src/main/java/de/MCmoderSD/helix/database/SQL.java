package de.MCmoderSD.helix.database;

import de.MCmoderSD.encryption.core.Encryption;
import de.MCmoderSD.helix.objects.AuthToken;
import de.MCmoderSD.sql.Driver;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class SQL extends Driver {

    // Associations
    private final Encryption encryption;

    // Constructor
    public SQL(Builder builder, Encryption encryption) {

        // Call Super
        super(builder);

        // Configure Database
        setAutoReconnect(true);
        connect();

        // Set Associations
        this.encryption = encryption;

        // Initialize Database Tables
        try (var bis = new BufferedInputStream(Objects.requireNonNull(SQL.class.getClassLoader().getResourceAsStream("database/RefreshToken.sql")))) {

            // Read SQL file
            var table = new String(bis.readAllBytes());

            // Execute SQL statement
            var preparedStatement = connection.prepareStatement(table);
            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (IOException | SQLException e) {
            throw new RuntimeException("Failed to initialize database tables: " + e.getMessage(), e);
        }
    }

    // Add or update a refresh token
    public void addRefreshToken(AuthToken authToken) {
        try {

            // Check authToken
            if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");

            // Variables
            var token = encryption.encrypt(authToken.getRefreshToken());

            // SQL statement to insert or update the token
            var preparedStatement = connection.prepareStatement(
                    "INSERT INTO RefreshToken (id, token) VALUES (?, ?) ON DUPLICATE KEY UPDATE token = ?"
            );

            // Set the parameters
            preparedStatement.setInt(1, authToken.getId()); // insert id
            preparedStatement.setString(2, token);          // insert token
            preparedStatement.setString(3, token);          // update token
            preparedStatement.executeUpdate(); // execute

            // Close the statement
            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to add or update RefreshToken with id " + authToken.getId() + ": " + e.getMessage(), e);
        }
    }

    // Delete a refresh token
    public void deleteRefreshToken(Integer id) {
        try {

            // Check ID
            if (id == null || id < 1) throw new IllegalArgumentException("ID cannot be null or less than 1");

            // SQL statement to delete the token
            var preparedStatement = connection.prepareStatement(
                    "DELETE FROM RefreshToken WHERE id = ?"
            );

            // Set the parameters
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            // Close the statement
            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete RefreshToken with id " + id + ": " + e.getMessage(), e);
        }
    }

    // Retrieve all refresh Tokens
    public HashMap<Integer, String> getRefreshTokens() {
        try {

            // SQL statement to select all tokens
            var preparedStatement = connection.prepareStatement(
                    "SELECT * FROM RefreshToken"
            );

            // Execute the query
            var resultSet = preparedStatement.executeQuery();

            // Process the result set
            var refreshTokens = new HashMap<Integer, String>();
            while (resultSet.next()) refreshTokens.put(resultSet.getInt("id"), encryption.decrypt(resultSet.getString("token")));

            // Close the result set and statement
            resultSet.close();
            preparedStatement.close();

            // Return the refresh tokens
            return refreshTokens;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve refresh tokens: " + e.getMessage(), e);
        }
    }
}