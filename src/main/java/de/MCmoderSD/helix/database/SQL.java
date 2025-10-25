package de.MCmoderSD.helix.database;

import com.fasterxml.jackson.databind.JsonNode;

import de.MCmoderSD.encryption.core.Encryption;
import de.MCmoderSD.helix.objects.AuthToken;
import de.MCmoderSD.sql.Driver;

import java.io.IOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashSet;

import static de.MCmoderSD.sql.Driver.DatabaseType.MARIADB;
import static de.MCmoderSD.encryption.core.Encryption.*;
import static de.MCmoderSD.tools.GZIP.*;

@SuppressWarnings("unused")
public class SQL extends Driver {

    // Associations
    private final Encryption encryption;

    // Constructor
    public SQL(JsonNode config, Encryption encryption) {

        // Call super constructor
        super(MARIADB, config);

        // Set Associations
        this.encryption = encryption;

        try {

            // Initialize tables
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                """
                AuthTokens (
                    id INT PRIMARY KEY,
                    token BLOB NOT NULL
                );
                """
            ).execute();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database tables: " + e.getMessage(), e);
        }
    }

    private static AuthToken decrypt(byte[] data, Encryption encryption) {
        try {
            byte[] encryptedBytes = inflate(data);
            byte[] decryptedBytes = encryption.decrypt(encryptedBytes);
            return (AuthToken) deserialize(decryptedBytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to inflate and decrypt AuthToken: " + e.getMessage(), e);
        }
    }

    private static byte[] encrypt(AuthToken token, Encryption encryption) {
        try {
            byte[] serializedBytes = serialize(token);
            byte[] encryptedBytes = encryption.encrypt(serializedBytes);
            return deflate(encryptedBytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize, encrypt, and deflate AuthToken: " + e.getMessage(), e);
        }
    }

    public AuthToken getAuthToken(Integer id) {

        // Check id
        if (id == null) throw new IllegalArgumentException("AuthToken id cannot be null");
        if (id < 1) throw new IllegalArgumentException("AuthToken id must be greater than 0");

        try {

            // SQL statement to select the token
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT token FROM AuthTokens WHERE id = ?"
            );

            // Set the parameters
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Process the result set
            AuthToken authToken = null;
            if (resultSet.next()) authToken = decrypt(resultSet.getBytes("token"), encryption);

            // Close the result set and statement
            resultSet.close();
            preparedStatement.close();

            // Return the auth token
            return authToken;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve AuthToken with id " + id + ": " + e.getMessage(), e);
        }
    }

    public HashSet<AuthToken> getAuthTokens() {
        try {

            // SQL statement to select all tokens
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT token FROM AuthTokens"
            );

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Process the result set
            HashSet<AuthToken> authTokens = new HashSet<>();
            while (resultSet.next()) authTokens.add(decrypt(resultSet.getBytes("token"), encryption));

            // Close the result set and statement
            resultSet.close();
            preparedStatement.close();

            // Return the auth tokens
            return authTokens;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve AuthTokens: " + e.getMessage(), e);
        }
    }

    public void addAuthToken(AuthToken authToken) {

        // Check authToken
        if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");

        try {

            // Variables
            byte[] data = encrypt(authToken, encryption);

            // SQL statement to insert or update the token
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO AuthTokens (id, token) VALUES (?, ?) ON DUPLICATE KEY UPDATE token = ?"
            );

            // Set the parameters
            preparedStatement.setInt(1, authToken.getId()); // insert id
            preparedStatement.setBytes(2, data); // insert token
            preparedStatement.setBytes(3, data); // update token
            preparedStatement.executeUpdate(); // execute

            // Close the statement
            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to add or update AuthToken with id " + authToken.getId() + ": " + e.getMessage(), e);
        }
    }

    public void deleteAuthToken(Integer id) {

        // Check id
        if (id == null) throw new IllegalArgumentException("AuthToken id cannot be null");
        if (id < 1) throw new IllegalArgumentException("AuthToken id must be greater than 0");

        try {

            // SQL statement to delete the token
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM AuthTokens WHERE id = ?"
            );

            // Set the parameters
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            // Close the statement
            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete AuthToken with id " + id + ": " + e.getMessage(), e);
        }
    }
}