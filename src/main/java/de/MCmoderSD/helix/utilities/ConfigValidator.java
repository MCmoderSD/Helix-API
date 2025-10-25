package de.MCmoderSD.helix.utilities;

import com.fasterxml.jackson.databind.JsonNode;


@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class ConfigValidator {

    public static boolean validateApplicationConfig(JsonNode application) throws IllegalArgumentException {

        // Check if application config is valid
        if (application.isNull() || application.isEmpty()) throw new IllegalArgumentException("Application config is null or empty");
        if (!application.has("oAuthRedirectURL") || application.get("oAuthRedirectURL").isNull()) throw new IllegalArgumentException("Application config is missing 'oAuthRedirectURL'");
        if (!application.has("credentials") || application.get("credentials").isNull() || application.get("credentials").isEmpty()) throw new IllegalArgumentException("Application config is missing 'credentials'");

        // Check Credentials
        JsonNode credentials = application.get("credentials");
        if (!credentials.has("clientId") || credentials.get("clientId").isNull()) throw new IllegalArgumentException("Credentials config is missing 'clientId'");
        if (!credentials.has("clientSecret") || credentials.get("clientSecret").isNull()) throw new IllegalArgumentException("Credentials config is missing 'clientSecret'");

        // Get Credentials
        String clientId = credentials.get("clientId").asText();
        String clientSecret = credentials.get("clientSecret").asText();

        // Check if clientId and clientSecret
        if (clientId == null || clientId.isBlank()) throw new IllegalArgumentException("Credentials config 'clientId' is empty");
        if (clientSecret == null || clientSecret.isBlank()) throw new IllegalArgumentException("Credentials config 'clientSecret' is empty");

        // Check oAuthRedirectURL
        String oAuthRedirectURL = application.get("oAuthRedirectURL").asText();
        if (oAuthRedirectURL == null || oAuthRedirectURL.isBlank()) throw new IllegalArgumentException("Application config 'oAuthRedirectURL' is empty");
        if (oAuthRedirectURL.endsWith("/")) throw new IllegalArgumentException("Application config 'oAuthRedirectURL' must not end with '/'");
        if (!oAuthRedirectURL.startsWith("http")) throw new IllegalArgumentException("Application config 'oAuthRedirectURL' is not a valid URL");

        return true;
    }

    public static boolean validateDatabaseConfig(JsonNode database) throws IllegalArgumentException {

        // Check if database config is valid
        if (database == null) throw new IllegalArgumentException("Database config is null");
        if (!database.has("host") || database.get("host").isNull()) throw new IllegalArgumentException("Database config is missing 'host'");
        if (!database.has("port") || database.get("port").isNull()) throw new IllegalArgumentException("Database config is missing 'port'");
        if (!database.has("database") || database.get("database").isNull()) throw new IllegalArgumentException("Database config is missing 'database'");
        if (!database.has("username") || database.get("username").isNull()) throw new IllegalArgumentException("Database config is missing 'username'");
        if (!database.has("password") || database.get("password").isNull()) throw new IllegalArgumentException("Database config is missing 'password'");

        // Get Database Config
        String host = database.get("host").asText();
        String dbName = database.get("database").asText();
        String user = database.get("username").asText();
        String password = database.get("password").asText();

        // Check if host, database, username and password are valid
        if (host == null || host.isBlank()) throw new IllegalArgumentException("Database config 'host' is empty");
        if (dbName == null || dbName.isBlank()) throw new IllegalArgumentException("Database config 'database' is empty");
        if (user == null || user.isBlank()) throw new IllegalArgumentException("Database config 'username' is empty");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Database config 'password' is empty");

        // Get and Check Port
        var port = database.get("port").asInt();
        if (port <= 0 || port > 65535) throw new IllegalArgumentException("Database config 'port' is not a valid port number");

        return true;
    }
}