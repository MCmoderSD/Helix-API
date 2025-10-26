# Helix-API

## Description
This is a Java Helix-API wrapper for communicating with the Twitch Helix API. <br>
It provides a simple and easy-to-use interface for making requests to the Helix API endpoints.

## Features
- [Authentication](#initialisation-and-authentication): OAuth2 authentication and scope management
- [User Management](#user-information): Retrieve user information and email addresses
- [Chat Management](#chat-management): Retrieve chatters in a channel and manage chat settings
- [Role Management](#role-management): Manage channel roles such as moderators, editors, VIPs, subscribers, and followers
- [Ad Management](#ad-management): Run commercials on a channel
- [Raid Management](#raid-management): Start and cancel raids between channels
- [Shoutout Management](#send-shoutouts): Send shoutouts from one channel to another
- [Channel Information](#channel-information): Retrieve channel information such as title, game, language, and tags

## Configuration
You need to create a `config.json` file in your resources folder with the following structure:
```json
{
  "twitch": {
    "application": {
      "oAuthRedirectURL": "https://YourDomain.com/callback",
      "credentials": {
        "clientId":"Your Client ID",
        "clientSecret":"Your Client Secret"
      }
    }
  },

  "server": {
    "host": "0.0.0.0",
    "httpPort": 80,
    "httpsPort": 443,
    "baseUrl": "/",

    "certificate": {
      "keyPassword": "Your Key Password",
      "keySize": 4096,

      "createIfMissing": true,
      "paths": {
        "privateKey": "path/to/your/private.pem",
        "certificate": "path/to/your/certificate.pem"
      },

      "acmeSigned": {
        "debug": true,
        "email": "Your@Email.com",
        "accountKey": "account.key",

        "cloudflare": {
          "zoneId": "Your Zone ID",
          "apiToken": "Your API Key"
        },

        "domains": [
          "example.com",
          "www.example.com"
        ]
      },

      "selfSigned": {
        "expirationDays": 366,

        "subject": {
          "commonName": "localhost",
          "organization": "Your Organization",
          "organizationalUnit": "Your Organizational Unit",
          "locality": "Your City",
          "state": "Your State",
          "country": "DE"
        },

        "subjectAltNames": {

          "dns": [
            "localhost"
          ],

          "ip": [
            "127.0.0.1",
            "::1"
          ]
        }
      }
    }
  },

  "database": {
    "host":"Your Database Host",
    "port":3306,
    "database": "Your Database Name",
    "username": "Your Database User",
    "password": "Your Database Password"
  }
}
```

The configuration consists of three main sections:
1. [Twitch Application](#1-twitch-application): Contains the OAuth2 redirect URL and application credentials (Client ID and Client Secret).
2. [Server](#2-server): Configuration for the embedded server, including host, ports, base URL, and SSL certificate settings.
3. [Database](#3-database): Configuration for the database connection, including host, port, database name, username, and password.

### 1. Twitch Application
- `oAuthRedirectURL`: The URL where Twitch will redirect after authentication.
- `credentials`: Contains the `clientId` and `clientSecret` for your Twitch application.
  - `clientId`: Your Twitch application's Client ID.
  - `clientSecret`: Your Twitch application's Client Secret.

You can create a Twitch application and obtain the Client ID and Client Secret from the [Twitch Developer Console](https://dev.twitch.tv/console/apps).
> Note: The database stores all data encrypted using the Client Secret as the encryption key. <br>
> Make sure to keep your Client Secret secure and do not share it publicly.
>
> Also, changing the Client Secret will invalidate all existing tokens stored in the database.

### 2. Server
Since the Helix-API uses the [HTTPS-Server](https://www.GitHub.com/MCmoderSD/HTTPS-Server) for handling HTTP and SSL connections, simply look [here](https://www.GitHub.com/MCmoderSD/HTTPS-Server#configuration) for detailed information about the server configuration options.

### 3. Database
The Helix-API uses a MariaDB database to store user credentials and tokens. <br>
- `host`: The database host address.
- `port`: The database port (default is 3306 for MariaDB).
- `database`: The name of the database to connect to.
- `username`: The username for the database connection.
- `password`: The password for the database connection.

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

## Usage examples

### [Initialisation and Authentication](https://www.GitHub.com/MCmoderSD/Helix-API/blob/master/src/test/java/examples/AuthExample.java)
You need to load the [configuration JSON](#configuration) and need to start or provide a `Server` for the OAuth2 redirect URI. <br>
After that you can initialise the `HelixHandler` and get the authorization URL for the required scopes. 

Alternatively, you can initialize the `HelixHandler` with provided `TwitchHelix` and `CredentialManager` instances.
```java
package examples;

import com.fasterxml.jackson.databind.JsonNode;

import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.enums.Scope;
import de.MCmoderSD.helix.handler.*;

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

        // Setup Scopes
        Scope[] scopes = new ArrayList<>(Arrays.asList(UserHandler.REQUIRED_SCOPES, ChatHandler.REQUIRED_SCOPES, RoleHandler.REQUIRED_SCOPES, StreamHandler.REQUIRED_SCOPES, ChannelHandler.REQUIRED_SCOPES))
                .stream()
                .flatMap(Stream::of)
                .distinct()
                .toArray(Scope[]::new);

        // Get Authorization URL
        String authURL = helixHandler.getAuthorizationUrl(scopes);
        System.out.println("Authorization URL: " + authURL);
    }
}
```

### [User Information](https://www.GitHub.com/MCmoderSD/Helix-API/blob/master/src/test/java/examples/UserExample.java)
In this example, we demonstrate how to retrieve Twitch user information using the `UserHandler`. <br>
We will fetch a user by their ID and username, compare the two results, and print out various details about the user.

Additionally, we will show how to retrieve the user's email address, which requires the `USER_READ_EMAIL` scope.
```java
package examples;

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
        var exampleUsername = "MCmoderSD";      // Username

        // Get TwitchUser
        TwitchUser user = userHandler.getTwitchUser(exampleId);                 // By ID
        TwitchUser userByName = userHandler.getTwitchUser(exampleUsername);     // By Username

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

### [Chat Management](https://www.GitHub.com/MCmoderSD/Helix-API/blob/master/src/test/java/examples/ChatExample.java)
In this example, we demonstrate how to retrieve the list of chatters in a Twitch channel using the `ChatHandler`.
```java
package examples;

import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.objects.TwitchUser;
import de.MCmoderSD.helix.handler.*;

import java.util.HashSet;

@SuppressWarnings("ALL")
public class ChatExample {

    public static void main(String[] args) {

        // Init HelixHandler
        HelixHandler helixHandler = AuthExample.initHelix();
        UserHandler userHandler = helixHandler.getUserHandler();
        ChatHandler chatHandler = helixHandler.getChatHandler();

        // Example Variables
        TwitchUser channel = userHandler.getTwitchUser("MCmoderSD");

        // Get Chatters
        HashSet<TwitchUser> chatters = chatHandler.getChatters(channel);
        System.out.println("Chatters in " + channel.getDisplayName() + "'s channel: " + chatters.size());
        for (TwitchUser chatter : chatters) System.out.println(" - " + chatter.getDisplayName() + " (ID: " + chatter.getId() + ")");

        // Exit
        System.exit(0);
    }
}
```

### [Role Management](https://www.GitHub.com/MCmoderSD/Helix-API/blob/master/src/test/java/examples/RoleExample.java)
In this example, we demonstrate how to check and list various channel roles using the `RoleHandler`.

Additionally, we show how to promote, demote, add, and remove users from roles such as moderators and VIPs.
```java
package examples;

import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.handler.*;
import de.MCmoderSD.helix.objects.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("ALL")
public class RoleExample {

    public static void main(String[] args) {

        // Init Handlers
        HelixHandler helixHandler = AuthExample.initHelix();
        UserHandler userHandler = helixHandler.getUserHandler();
        RoleHandler roleHandler = helixHandler.getRoleHandler();

        // Example Users
        TwitchUser channel = userHandler.getTwitchUser("MCmoderSD");            // Your Channel Here (needs to be authenticated)
        TwitchUser exampleMod = userHandler.getTwitchUser("LangerLanzenLeo");   // Example Moderator
        TwitchUser exampleEditor = userHandler.getTwitchUser("r4kunnn");        // Example Editor
        TwitchUser exampleVIP = userHandler.getTwitchUser("RebixDev");          // Example VIP
        TwitchUser exampleSubscriber = userHandler.getTwitchUser("seybiiiii");  // Example Subscriber
        TwitchUser exampleFollower = userHandler.getTwitchUser("RedSmileTV");   // Example Follower
        TwitchUser exampleUser = userHandler.getTwitchUser("ModersEsel");       // Example User


        // Promote VIP to Moderator
        System.out.println("\nPromoting " + exampleVIP.getDisplayName() + " from VIP to Moderator...");
        System.out.println(" - Before Promotion: Moderator: " + roleHandler.isModerator(exampleVIP, channel) + " | Is VIP: " + roleHandler.isVIP(exampleVIP, channel));
        System.out.println(" - Success: " + roleHandler.addModerator(exampleVIP, channel));
        System.out.println(" - After Promotion: Moderator: " + roleHandler.isModerator(exampleVIP, channel) + " | Is VIP: " + roleHandler.isVIP(exampleVIP, channel));

        // Remove Moderator
        System.out.println("\nRemoving " + exampleMod.getDisplayName() + " from Moderator role...");
        System.out.println(" - Before Removal: Is Moderator: " + roleHandler.isModerator(exampleMod, channel) + " | Is VIP: " + roleHandler.isVIP(exampleMod, channel));
        System.out.println(" - Success: " + roleHandler.removeModerator(exampleMod, channel));
        System.out.println(" - After Removal: Is Moderator: " + roleHandler.isModerator(exampleMod, channel) + " | Is VIP: " + roleHandler.isVIP(exampleMod, channel));

        // Make user VIP
        System.out.println("\nAdding " + exampleUser.getDisplayName() + " to VIPs...");
        System.out.println(" - Before: Is VIP: " + roleHandler.isVIP(exampleUser, channel) + " | Is Moderator: " + roleHandler.isModerator(exampleUser, channel));
        System.out.println(" - Success: " + roleHandler.addVIP(exampleUser, channel));
        System.out.println(" - After: Is VIP: " + roleHandler.isVIP(exampleUser, channel) + " | Is Moderator: " + roleHandler.isModerator(exampleUser, channel));

        // Reset to original state
        System.out.println("\nResetting roles to original state...");
        System.out.println(" - Re-adding " + exampleMod.getDisplayName() + " to Moderators: " + roleHandler.addModerator(exampleMod, channel));     // Re-add Moderator
        System.out.println(" - Demoting " + exampleVIP.getDisplayName() + " from Moderator to VIP: " + roleHandler.addVIP(exampleVIP, channel));    // Demote to VIP from Moderator
        System.out.println(" - Removing " + exampleUser.getDisplayName() + " from VIPs: " + roleHandler.removeVIP(exampleUser, channel));           // Remove VIP
        System.out.println("\n\n--------------------------------------\n\n");


        // Check Roles for Example Users
        System.out.println("Checking roles for example users:");
        System.out.println(" - " + exampleMod.getDisplayName() + " is moderator: " + roleHandler.isModerator(exampleMod, channel));
        System.out.println(" - " + exampleEditor.getDisplayName() + " is editor: " + roleHandler.isEditor(exampleEditor, channel));
        System.out.println(" - " + exampleVIP.getDisplayName() + " is VIP: " + roleHandler.isVIP(exampleVIP, channel));
        System.out.println(" - " + exampleSubscriber.getDisplayName() + " is subscriber: " + roleHandler.isSubscriber(exampleSubscriber, channel));
        System.out.println(" - " + exampleFollower.getDisplayName() + " is follower: " + roleHandler.isFollower(exampleFollower, channel));
        System.out.println("\n\n--------------------------------------\n\n");


        // Get Roles Lists
        HashSet<ChannelModerator> moderators = roleHandler.getModerators(channel);      // Get all moderators
        HashSet<ChannelEditor> editors = roleHandler.getEditors(channel);               // Get all editors
        HashSet<ChannelVip> vips = roleHandler.getVIPs(channel);                        // Get all VIPs
        HashSet<ChannelSubscriber> subscribers = roleHandler.getSubscribers(channel);   // Get all subscribers
        HashSet<ChannelFollower> followers = roleHandler.getFollowers(channel);         // Get all followers

        // Print Sizes
        System.out.println("Found Roles for " + channel.getDisplayName() + ":");
        System.out.println(" - Moderators: " + moderators.size());
        System.out.println(" - Editors: " + editors.size());
        System.out.println(" - VIPs: " + vips.size());
        System.out.println(" - Subscribers: " + subscribers.size());
        System.out.println(" - Followers: " + followers.size());
        System.out.println("\n\n--------------------------------------\n\n");


        // Check Roles for Multiple Users
        HashSet<TwitchUser> twitchUsers = new HashSet<>(List.of(new TwitchUser[]{ channel, exampleMod, exampleEditor, exampleVIP, exampleSubscriber, exampleFollower, exampleUser }));
        HashMap<TwitchUser, Boolean> modMap = roleHandler.checkModerators(twitchUsers, channel);            // Check Moderators
        HashMap<TwitchUser, Boolean> editorMap = roleHandler.checkEditors(twitchUsers, channel);            // Check Editors
        HashMap<TwitchUser, Boolean> vipMap = roleHandler.checkVIPs(twitchUsers, channel);                  // Check VIPs
        HashMap<TwitchUser, Boolean> subscriberMap = roleHandler.checkSubscribers(twitchUsers, channel);    // Check Subscribers
        HashMap<TwitchUser, Boolean> followerMap = roleHandler.checkFollowers(twitchUsers, channel);        // Check Followers

        // Print positive checks
        System.out.println("\nModerator Check:");
        for (var entry : modMap.entrySet()) if (entry.getValue()) System.out.println(" - " + entry.getKey().getDisplayName() + " is a moderator");
        System.out.println("\nEditor Check:");
        for (var entry : editorMap.entrySet()) if (entry.getValue()) System.out.println(" - " + entry.getKey().getDisplayName() + " is an editor");
        System.out.println("\nVIP Check:");
        for (var entry : vipMap.entrySet()) if (entry.getValue()) System.out.println(" - " + entry.getKey().getDisplayName() + " is a VIP");
        System.out.println("\nSubscriber Check:");
        for (var entry : subscriberMap.entrySet()) if (entry.getValue()) System.out.println(" - " + entry.getKey().getDisplayName() + " is a subscriber");
        System.out.println("\nFollower Check:");
        for (var entry : followerMap.entrySet()) if (entry.getValue()) System.out.println(" - " + entry.getKey().getDisplayName() + " is a follower");

        // Exit
        System.exit(0);
    }
}
```

### [AD Management](https://www.GitHub.com/MCmoderSD/Helix-API/blob/master/src/test/java/examples/AdExample.java)
In this example, we demonstrate how to run a commercial ad on a Twitch channel using the `StreamHandler`. <br>
For this to work, the channel must be live and advertising must be enabled on the channel.
```java
package examples;

import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.objects.TwitchUser;
import de.MCmoderSD.helix.handler.*;

import static de.MCmoderSD.helix.handler.StreamHandler.CommercialLength.LENGTH_30;

@SuppressWarnings("ALL")
public class AdExample {

    public static void main(String[] args) {

        // Init HelixHandler
        HelixHandler helixHandler = AuthExample.initHelix();
        UserHandler userHandler = helixHandler.getUserHandler();
        StreamHandler streamHandler = helixHandler.getStreamHandler();

        // Get Channel
        TwitchUser channel = userHandler.getTwitchUser("MCmoderSD");    // Channel to run ad on

        // Run Ad
        streamHandler.runCommercial(channel, LENGTH_30);

        // Exit
        System.exit(0);
    }
}
```

### [Raid Management](https://www.GitHub.com/MCmoderSD/Helix-API/blob/master/src/test/java/examples/RaidExample.java)
In this example, we demonstrate how to start and cancel a raid between two Twitch channels using the `StreamHandler`. <br>
For this to work, the source channel must be live must have already started a raid.
```java
package examples;

import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.objects.TwitchUser;
import de.MCmoderSD.helix.handler.*;

@SuppressWarnings("ALL")
public class RaidExample {

    public static void main(String[] args) {

        // Init HelixHandler
        HelixHandler helixHandler = AuthExample.initHelix();
        UserHandler userHandler = helixHandler.getUserHandler();
        StreamHandler streamHandler = helixHandler.getStreamHandler();

        // Get Channels
        TwitchUser user = userHandler.getTwitchUser("MCmoderSD");       // Channel to raid to      (target)
        TwitchUser channel = userHandler.getTwitchUser("ModersEsel");   // Channel to raid from    (source)

        // Cancel Raid
        streamHandler.cancelRaid(channel);

        // Start Raid
        streamHandler.startRaid(user, channel);

        // Exit
        System.exit(0);
    }
}
```

### [Send Shoutouts](https://www.GitHub.com/MCmoderSD/Helix-API/blob/master/src/test/java/examples/ShoutoutExample.java)
In this example, we demonstrate how to send a shoutout from one Twitch channel to another using the `StreamHandler`. <br>
For this to work, the source channel must be live and must have at least one viewer.
```java
package examples;

import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.objects.TwitchUser;
import de.MCmoderSD.helix.handler.*;

@SuppressWarnings("ALL")
public class ShoutoutExample {

    public static void main(String[] args) {

        // Init HelixHandler
        HelixHandler helixHandler = AuthExample.initHelix();
        UserHandler userHandler = helixHandler.getUserHandler();
        StreamHandler streamHandler = helixHandler.getStreamHandler();

        // Get Channels
        TwitchUser user = userHandler.getTwitchUser("MCmoderSD");       // Channel to send shoutout to      (target)
        TwitchUser channel = userHandler.getTwitchUser("ModersEsel");   // Channel to send shoutout from    (source)

        // Send Shoutout
        streamHandler.sendShoutout(user, channel);

        // Exit
        System.exit(0);
    }
}
```

### [Channel Information](https://www.GitHub.com/MCmoderSD/Helix-API/blob/master/src/test/java/examples/InfoExample.java)
In this example, we demonstrate how to retrieve channel information using the `ChannelHandler`. <br>
We will fetch the channel info by ID, username, and `TwitchUser`, compare the results, and print out various details about the channel.
```java
package examples;

import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.objects.ChannelInfo;
import de.MCmoderSD.helix.handler.*;
import java.util.Arrays;

@SuppressWarnings("ALL")
public class InfoExample {

    public static void main(String[] args) {

        // Init HelixHandler
        HelixHandler helixHandler = AuthExample.initHelix();
        UserHandler userHandler = helixHandler.getUserHandler();
        ChannelHandler channelHandler = helixHandler.getChannelHandler();

        // Example Variables
        var exampleId = 164284617;              // User ID
        var exampleUsername = "MCmoderSD";      // Username

        // Get Channel Info
        ChannelInfo infoById = channelHandler.getChannelInfo(exampleId);                        // By ID
        ChannelInfo infoByName = channelHandler.getChannelInfo(exampleUsername);                // By Username
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