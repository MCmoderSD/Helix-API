package de.MCmoderSD.helix.config;

import de.MCmoderSD.sql.Driver;

@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
public class Configuration {

    // Twitch API
    public static String clientId;
    public static String clientSecret;

    // Database
    public static Driver.DatabaseType databaseType;
    public static String host;
    public static Integer port;
    public static String database;
    public static String username;
    public static String password;

    // Setters
    public static void setClientId(String clientId) {
        Configuration.clientId = clientId;
    }

    public static void setClientSecret(String clientSecret) {
        Configuration.clientSecret = clientSecret;
    }

    public static void setDatabaseType(Driver.DatabaseType databaseType) {
        Configuration.databaseType = databaseType;
    }

    public static void setHost(String host) {
        Configuration.host = host;
    }

    public static void setPort(Integer port) {
        Configuration.port = port;
    }

    public static void setDatabase(String database) {
        Configuration.database = database;
    }

    public static void setUsername(String username) {
        Configuration.username = username;
    }

    public static void setPassword(String password) {
        Configuration.password = password;
    }

    public static boolean validate() {

        // Variable
        boolean valid = true;

        if (clientId == null || clientId.isEmpty()) {
            System.out.println("Client ID is not set.");
            valid = false;
        }

        if (clientSecret == null || clientSecret.isEmpty()) {
            System.out.println("Client Secret is not set.");
            valid = false;
        }

        if (databaseType == null) {
            System.out.println("Database Type is not set.");
            valid = false;
        }

        if ((host == null || host.isEmpty()) && !databaseType.equals(Driver.DatabaseType.SQLITE)) {
            System.out.println("Host is not set.");
            valid = false;
        }

        if ((port == null || port < 1 || port > 65535) && !databaseType.equals(Driver.DatabaseType.SQLITE)) {
            System.out.println("Port is not set.");
            valid = false;
        }

        if (database == null || database.isEmpty()) {
            System.out.println("Database is not set.");
            valid = false;
        }

        if ((username == null || username.isEmpty()) && !databaseType.equals(Driver.DatabaseType.SQLITE)) {
            System.out.println("Username is not set.");
            valid = false;
        }

        if ((password == null || password.isEmpty()) && !databaseType.equals(Driver.DatabaseType.SQLITE)) {
            System.out.println("Password is not set.");
            valid = false;
        }

        // Return result
        return valid;
    }
}