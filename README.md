# Helix-API

## Description
This is a Java Helix-API wrapper for communicating with the Twitch Helix API.
It provides a simple and easy-to-use interface for making requests to the Helix API endpoints.

## Features
- [Authentication](#initialisation-and-authentication): OAuth2 authentication and scope management
- [User Information](#user-information): Retrieve user details, email, and profile information.
- [Channel Information](#channel-information): Get channel details, stream information, and tags.
- [Shoutouts](#send-shoutouts): Send shoutouts from one channel to another.

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

### User Information
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

        // Exit
        System.exit(0);
    }
}
```

## Channel Information
```java
import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.objects.ChannelInfo;
import de.MCmoderSD.helix.handler.*;
import java.util.Arrays;

@SuppressWarnings("ALL")
public class InfoExample {

    public static void main(String[] args) {

        // Init HelixHandler
        HelixHandler helixHandler = AuthExample.initHelix();
        ChannelHandler channelHandler = helixHandler.getChannelHandler();
        UserHandler userHandler = helixHandler.getUserHandler();

        // Example Variables
        var exampleId = 164284617;              // User ID
        var exampleUsername = "MCmoderSD";      // Username/Display Name

        // Get Channel Info
        ChannelInfo infoById = channelHandler.getChannelInfo(exampleId);                        // By ID
        ChannelInfo infoByName = channelHandler.getChannelInfo(exampleUsername);                // By Username/Display Name
        ChannelInfo info = channelHandler.getChannelInfo(userHandler.getTwitchUser(exampleId)); // By TwitchUser

        // Check both are the same
        System.out.println("ChannelInfos are the same: " + infoById.equals(infoByName));

        // Print Channel Info
        System.out.println("\n------- Channel Info -------");
        System.out.println("Channel ID: " + info.getId());
        System.out.println("Display Name: " + info.getDisplayName());
        System.out.println("Title: " + info.getTitle());
        System.out.println("Game ID: " + info.getGameId());
        System.out.println("Game Name: " + info.getGameName());
        System.out.println("Language: " + info.getLanguage());
        System.out.println("Tags: " + Arrays.toString(info.getTags().toArray()));
        System.out.println("Branded Content: " + info.isBrandedContent());
        System.out.println("-----------------------------------\n");

        // Exit
        System.exit(0);
    }
}
```

### Send Shoutouts
```java
import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.objects.TwitchUser;
import de.MCmoderSD.helix.handler.*;

@SuppressWarnings("ALL")
public class ShoutoutExample {

    /**
     * <p>Preconditions for sending a shoutout:</p>
     * <ul>
     *   <li>The source channel must be live and have at least one viewer.</li>
     *   <li>The calling credentials must include the {@code MODERATOR_MANAGE_SHOUTOUTS} scope for the source channel.</li>
     * </ul>
     */
    public static void main(String[] args) {

        // Init HelixHandler
        HelixHandler helixHandler = AuthExample.initHelix();
        ChannelHandler channelHandler = helixHandler.getChannelHandler();
        UserHandler userHandler = helixHandler.getUserHandler();

        // Get Channels
        TwitchUser user = userHandler.getTwitchUser("MCmoderSD");       // Channel to send shoutout to      (target)
        TwitchUser channel = userHandler.getTwitchUser("Modersesel");   // Channel to send shoutout from    (source)

        // Send Shoutout
        channelHandler.sendShoutout(user, channel);

        // Exit
        System.exit(0);
    }
}
```