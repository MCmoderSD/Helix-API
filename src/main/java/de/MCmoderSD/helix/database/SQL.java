package de.MCmoderSD.helix.database;

import de.MCmoderSD.encryption.Encryption;
import de.MCmoderSD.helix.objects.AuthToken;
import de.MCmoderSD.sql.Driver;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;

@SuppressWarnings("unused")
public class SQL extends Driver {

    // Attributes
    private final Encryption encryption;
    private final ObjectOutputStream objectOutputStream;
    private final ByteArrayOutputStream byteArrayOutputStream;

    // Constructor
    public SQL(DatabaseType databaseType, String database, Encryption encryption) {

        // Call super constructor
        super(databaseType, null, null, database, null, null);

        try {

            // Set attributes
            this.encryption = encryption;
            this.byteArrayOutputStream = new ByteArrayOutputStream();
            this.objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            // Initialize tables
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                """
                AuthTokens (
                    id INT PRIMARY KEY,
                    token BLOB NOT NULL
                );
                """
            ).execute();

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private AuthToken decrypt(ResultSet resultSet) throws SQLException, IOException {
        try {
            String decrypted = encryption.decrypt(new String(resultSet.getBytes("token")));
            byte[] data = Base64.getDecoder().decode(decrypted);
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data))) {
                return (AuthToken) objectInputStream.readObject();
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Failed to deserialize AuthToken", e);
        }
    }

    private String encrypt(AuthToken token) {
        try {

            // Serialize the AuthToken object
            objectOutputStream.writeObject(token);
            objectOutputStream.flush();

            // Convert the ByteArrayOutputStream to a byte array and encode it to Base64
            byte[] bytes = byteArrayOutputStream.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(bytes);

            // Clear the ByteArrayOutputStream
            byteArrayOutputStream.reset();

            // Encrypt the Base64 string
            return encryption.encrypt(base64);

        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public AuthToken getAuthToken(Integer id) {
        try {

            // SQL statement to select the token
            var statement = connection.prepareStatement(
                    "SELECT * FROM AuthTokens WHERE id = ?"
            );

            // Set the parameters
            statement.setInt(1, id);
            var resultSet = statement.executeQuery();

            // Process the result set
            if (resultSet.next()) return decrypt(resultSet);

        } catch (SQLException | IOException e) {
            System.err.println(e.getMessage());
        }

        // Return null if no token is found
        return null;
    }

    public HashMap<Integer, AuthToken> getAuthTokens(Integer... id) {
        try {

            // Variables
            StringBuilder condition = new StringBuilder();
            HashMap<Integer, AuthToken> authTokens = new HashMap<>();

            // Build the condition string
            for (var i = 0; i < id.length; i++) {
                condition.append("id = ").append(id[i]);
                if (i != id.length - 1) condition.append(" OR ");
            }

            // SQL statement to select the token
            var statement = connection.prepareStatement(
                    "SELECT * FROM AuthTokens WHERE " + condition
            );

            // Execute the query
            var resultSet = statement.executeQuery();

            // Process the result set
            while (resultSet.next()) {
                var token = decrypt(resultSet);
                authTokens.put(token.getId(), token);
            }

            // Close the result set
            resultSet.close();
            statement.close();

            // Return the auth tokens
            return authTokens;

        } catch (SQLException | IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public HashMap<Integer, AuthToken> getAuthTokens() {
        try {

            // Variables
            HashMap<Integer, AuthToken> authTokens = new HashMap<>();

            // SQL statement to select the token
            var statement = connection.prepareStatement(
                    "SELECT token FROM AuthTokens"
            );

            // Execute the query
            var resultSet = statement.executeQuery();

            // Process the result set
            while (resultSet.next()) {
                var token = decrypt(resultSet);
                authTokens.put(token.getId(), token);
            }

            // Close the result set
            resultSet.close();
            statement.close();

            // Return the auth tokens
            return authTokens;

        } catch (SQLException | IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public void addAuthToken(AuthToken authToken) {
        try {

            // Variables
            String encrypted = encrypt(authToken);
            System.out.println("ID: " + authToken.getId());

            // SQL statement to insert or update the token
            var statement = connection.prepareStatement(
                    "INSERT INTO AuthTokens (id, token) VALUES (?, ?) ON CONFLICT(id) DO UPDATE SET token = ?"
            );

            // Set the parameters
            statement.setInt(1, authToken.getId()); // insert id
            statement.setString(2, encrypted);      // insert token
            statement.setString(3, encrypted);      // update token
            statement.executeUpdate(); // execute

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void deleteAuthToken(Integer id) {
        try {

            // SQL statement to delete the token
            var statement = connection.prepareStatement(
                    "DELETE FROM AuthTokens WHERE id = ?"
            );

            // Set the parameters
            statement.setInt(1, id);
            statement.executeUpdate(); // execute

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}