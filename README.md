# Helix-API

## Description
This is a Java Helix-API wrapper for communicating with the Twitch Helix API.
It provides a simple and easy-to-use interface for making requests to the Helix API endpoints.

## Usage

### Maven
Make sure you have my Sonatype Nexus OSS repository added to your `pom.xml` file:
```xml
<repositories>
    <repository>
        <id>Nexus</id>
        <name>Sonatype Nexus</name>
        <url>https://mcmodersd.de/nexus/repository/maven-releases/</url>
    </repository>
</repositories>
```
Add the dependency to your `pom.xml` file:
```xml
<dependency>
    <groupId>de.MCmoderSD</groupId>
    <artifactId>Helix-API</artifactId>
    <version>2.0.0</version>
</dependency>
```

## Usage Examples

### Initialisation and Authentication
```java
import com.fasterxml.jackson.databind.JsonNode;

import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.enums.Scope;
import de.MCmoderSD.helix.handler.ChannelHandler;
import de.MCmoderSD.helix.handler.RoleHandler;
import de.MCmoderSD.helix.handler.UserHandler;

import de.MCmoderSD.json.JsonUtility;
import de.MCmoderSD.server.core.Server;

import java.io.IOException;

import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

@SuppressWarnings("ALL")
public class AuthExample {

    public static HelixHandler initHelix() {

        // Load Config
        JsonNode config;
        try {
            config = JsonUtility.getInstance().load("/config.json");
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Could not read config.json", e);
        }

        // Load Config
        JsonNode applicationConfig = config.get("twitch").get("application");
        JsonNode databaseConfig = config.get("database");
        JsonNode serverConfig = config.get("server");

        // Init Server
        Server server = new Server(serverConfig);
        server.start();

        // Init HelixHandler
        return new HelixHandler(applicationConfig, databaseConfig, server);
    }

    public static void main(String[] args) {

        // Init HelixHandler
        HelixHandler helixHandler = initHelix();

        // Print Authorization URL
        Scope[] scopes = new ArrayList<>(Arrays.asList(UserHandler.REQUIRED_SCOPES, ChannelHandler.REQUIRED_SCOPES, RoleHandler.REQUIRED_SCOPES))
                .stream()
                .flatMap(Stream::of)
                .distinct()
                .toArray(Scope[]::new);

        String authURL = helixHandler.getAuthorizationUrl(scopes);
        System.out.println("Authorization URL: " + authURL);
    }
}
```

### User Example
```java
import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.handler.UserHandler;
import de.MCmoderSD.helix.objects.TwitchUser;

@SuppressWarnings("ALL")
public class UserExample {

    public static void main(String[] args) {

        // Init HelixHandler
        HelixHandler helixHandler = AuthExample.initHelix();
        UserHandler userHandler = helixHandler.getUserHandler();

        // Example Variables
        var exampleId = 164284617;              // User ID
        var exampleUsername = "MCmoderSD";      // Username/Display Name

        // Get TwitchUser
        TwitchUser user = userHandler.getTwitchUser(exampleId);                 // By ID
        TwitchUser userByName = userHandler.getTwitchUser(exampleUsername);     // By Username/Display Name

        // Check both are the same
        System.out.println("Users are the same: " + user.equals(userByName));

        // Print User Info
        System.out.println("\n------- Twitch User Info -------");
        System.out.println("User ID: " + user.getId());
        System.out.println("Username: " + user.getUsername());
        System.out.println("Created At: " + user.getCreatedAt());
        System.out.println("Display Name: " + user.getDisplayName());
        System.out.println("Description: " + user.getDescription());
        System.out.println("Profile Image URL: " + user.getProfileImageUrl());
        System.out.println("Offline Image URL: " + user.getOfflineImageUrl());
        System.out.println("Broadcaster Type: " + user.getBroadcasterType());
        System.out.println("User Type: " + user.getType());
        System.out.println("-----------------------------------\n");

        // Get User Email (requires USER_READ_EMAIL scope)
        String email = userHandler.getUserMail(user);                               // By TwitchUser
        System.out.println("Email: " + email);

        // Get User Email by ID and Username
        System.out.println("ID: " + userHandler.getUserMail(exampleId));            // By ID
        System.out.println("Name: " + userHandler.getUserMail(exampleUsername));    // By Username
    }
}
```