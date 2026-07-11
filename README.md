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
    <version>2.3.5</version>
</dependency>
```

## Usage examples

### [Initialisation and Authentication](https://www.GitHub.com/MCmoderSD/Helix-API/blob/master/src/test/java/examples/AuthExample.java)
You need to load the [configuration JSON](#configuration) and need to start or provide a `Server` for the OAuth2 redirect URI. <br>
After that you can initialize the `HelixHandler` and get the authorization URL for the required scopes. 

Alternatively, you can initialize the `HelixHandler` with provided `TwitchHelix` and `CredentialManager` instances.
```java
package examples;

import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.enums.Scope;
import de.MCmoderSD.helix.handler.*;

import de.MCmoderSD.json.JsonUtility;
import de.MCmoderSD.server.core.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

@SuppressWarnings("ALL")
public class AuthExample {

  public static HelixHandler initHelix() {

    // Load Config
    var config = JsonUtility.getInstance().loadResource("/config.json");

    // Load Config
    var applicationConfig = config.get("twitch").get("application");
    var databaseConfig = config.get("database");
    var serverConfig = config.get("server");

    // Init Server
    var server = new Server(serverConfig);
    server.start();

    // Init HelixHandler
    return new HelixHandler(applicationConfig, databaseConfig, server);
  }

  void main() {

    // Init HelixHandler
    var helixHandler = initHelix();

    // Setup Scopes
    var scopes = new ArrayList<>(Arrays.asList(UserHandler.REQUIRED_SCOPES, ChatHandler.REQUIRED_SCOPES, RoleHandler.REQUIRED_SCOPES, StreamHandler.REQUIRED_SCOPES, ChannelHandler.REQUIRED_SCOPES))
            .stream()
            .flatMap(Stream::of)
            .distinct()
            .toArray(Scope[]::new);

    // Get Authorization URL
    var authURL = helixHandler.getAuthorizationUrl(scopes);
    IO.println("Authorization URL: " + authURL);
  }
}
```

### [User Information](https://www.GitHub.com/MCmoderSD/Helix-API/blob/master/src/test/java/examples/UserExample.java)
In this example, we demonstrate how to retrieve Twitch user information using the `UserHandler`. <br>
We will fetch a user by their ID and username, compare the two results, and print out various details about the user.

Additionally, we will show how to retrieve the user's email address, which requires the `USER_READ_EMAIL` scope.
```java
import examples.AuthExample;

import static java.lang.IO.println;

void main() {

  // Init HelixHandler
  var helixHandler = AuthExample.initHelix();
  var userHandler = helixHandler.getUserHandler();

  // Example Variables
  var exampleId = 164284617;          // User ID
  var exampleUsername = "MCmoderSD";  // Username

  // Get TwitchUser
  var user = userHandler.getTwitchUser(exampleId);                    // By ID
  var userByName = userHandler.getTwitchUser(exampleUsername);        // By Username

  // Check both are the same
  println("Users are the same: " + user.equals(userByName));

  // Print User Info
  println("\n------- Twitch User Info -------");
  println("User ID: " + user.getId());
  println("Username: " + user.getUsername());
  println("Created At: " + user.getCreatedAt());
  println("Display Name: " + user.getDisplayName());
  println("Description: " + user.getDescription());
  println("Profile Image URL: " + user.getProfileImageUrl());
  println("Offline Image URL: " + user.getOfflineImageUrl());
  println("Broadcaster Type: " + user.getBroadcasterType());
  println("User Type: " + user.getType());
  println("-----------------------------------\n");

  // Get User Email (requires USER_READ_EMAIL scope)
  var email = userHandler.getUserMail(user);                          // By TwitchUser
  println("Email: " + email);

  // Get User Email by ID and Username
  println("ID: " + userHandler.getUserMail(exampleId));            // By ID
  println("Name: " + userHandler.getUserMail(exampleUsername));    // By Username

  // Exit
  System.exit(0);
}
```

### [Chat Management](https://www.GitHub.com/MCmoderSD/Helix-API/blob/master/src/test/java/examples/ChatExample.java)
In this example, we demonstrate how to retrieve the list of chatters in a Twitch channel using the `ChatHandler`.
```java
import de.MCmoderSD.helix.handler.*;

import examples.AuthExample;

import static java.lang.IO.println;

void main() {

  // Init HelixHandler
  var helixHandler = AuthExample.initHelix();
  var userHandler = helixHandler.getUserHandler();
  var chatHandler = helixHandler.getChatHandler();

  // Example Variables
  var channel = userHandler.getTwitchUser("MCmoderSD");

  // Get Chatters
  var chatters = chatHandler.getChatters(channel);
  println("Chatters in " + channel.getDisplayName() + "'s channel: " + chatters.size());
  for (var chatter : chatters) println(" - " + chatter.getDisplayName() + " (ID: " + chatter.getId() + ")");

  // Exit
  System.exit(0);
}
```

### [Role Management](https://www.GitHub.com/MCmoderSD/Helix-API/blob/master/src/test/java/examples/RoleExample.java)
In this example, we demonstrate how to check and list various channel roles using the `RoleHandler`.

Additionally, we show how to promote, demote, add, and remove users from roles such as moderators and VIPs.
```java
import de.MCmoderSD.helix.handler.*;
import de.MCmoderSD.helix.objects.*;

import examples.AuthExample;

import java.util.HashSet;
import java.util.List;

import static java.lang.IO.println;

void main() {

  // Init Handlers
  var helixHandler = AuthExample.initHelix();
  var userHandler = helixHandler.getUserHandler();
  var roleHandler = helixHandler.getRoleHandler();

  // Example Users
  var channel = userHandler.getTwitchUser("MCmoderSD");           // Your Channel Here (needs to be authenticated)
  var exampleMod = userHandler.getTwitchUser("LangerLanzenLeo");  // Example Moderator
  var exampleEditor = userHandler.getTwitchUser("r4kunnn");       // Example Editor
  var exampleVIP = userHandler.getTwitchUser("RebixDev");         // Example VIP
  var exampleSubscriber = userHandler.getTwitchUser("seybiiiii"); // Example Subscriber
  var exampleFollower = userHandler.getTwitchUser("RedSmileTV");  // Example Follower
  var exampleUser = userHandler.getTwitchUser("ModersEsel");      // Example User


  // Promote VIP to Moderator
  println("\nPromoting " + exampleVIP.getDisplayName() + " from VIP to Moderator...");
  println(" - Before Promotion: Moderator: " + roleHandler.isModerator(exampleVIP, channel) + " | Is VIP: " + roleHandler.isVIP(exampleVIP, channel));
  println(" - Success: " + roleHandler.addModerator(exampleVIP, channel));
  println(" - After Promotion: Moderator: " + roleHandler.isModerator(exampleVIP, channel) + " | Is VIP: " + roleHandler.isVIP(exampleVIP, channel));

  // Remove Moderator
  println("\nRemoving " + exampleMod.getDisplayName() + " from Moderator role...");
  println(" - Before Removal: Is Moderator: " + roleHandler.isModerator(exampleMod, channel) + " | Is VIP: " + roleHandler.isVIP(exampleMod, channel));
  println(" - Success: " + roleHandler.removeModerator(exampleMod, channel));
  println(" - After Removal: Is Moderator: " + roleHandler.isModerator(exampleMod, channel) + " | Is VIP: " + roleHandler.isVIP(exampleMod, channel));

  // Make user VIP
  println("\nAdding " + exampleUser.getDisplayName() + " to VIPs...");
  println(" - Before: Is VIP: " + roleHandler.isVIP(exampleUser, channel) + " | Is Moderator: " + roleHandler.isModerator(exampleUser, channel));
  println(" - Success: " + roleHandler.addVIP(exampleUser, channel));
  println(" - After: Is VIP: " + roleHandler.isVIP(exampleUser, channel) + " | Is Moderator: " + roleHandler.isModerator(exampleUser, channel));

  // Reset to original state
  println("\nResetting roles to original state...");
  println(" - Re-adding " + exampleMod.getDisplayName() + " to Moderators: " + roleHandler.addModerator(exampleMod, channel));     // Re-add Moderator
  println(" - Demoting " + exampleVIP.getDisplayName() + " from Moderator to VIP: " + roleHandler.addVIP(exampleVIP, channel));    // Demote to VIP from Moderator
  println(" - Removing " + exampleUser.getDisplayName() + " from VIPs: " + roleHandler.removeVIP(exampleUser, channel));           // Remove VIP
  println("\n\n--------------------------------------\n\n");


  // Check Roles for Example Users
  println("Checking roles for example users:");
  println(" - " + exampleMod.getDisplayName() + " is moderator: " + roleHandler.isModerator(exampleMod, channel));
  println(" - " + exampleEditor.getDisplayName() + " is editor: " + roleHandler.isEditor(exampleEditor, channel));
  println(" - " + exampleVIP.getDisplayName() + " is VIP: " + roleHandler.isVIP(exampleVIP, channel));
  println(" - " + exampleSubscriber.getDisplayName() + " is subscriber: " + roleHandler.isSubscriber(exampleSubscriber, channel));
  println(" - " + exampleFollower.getDisplayName() + " is follower: " + roleHandler.isFollower(exampleFollower, channel));
  println("\n\n--------------------------------------\n\n");


  // Get Roles Lists
  var moderators = roleHandler.getModerators(channel);    // Get all moderators
  var editors = roleHandler.getEditors(channel);          // Get all editors
  var vips = roleHandler.getVIPs(channel);                // Get all VIPs
  var subscribers = roleHandler.getSubscribers(channel);  // Get all subscribers
  var followers = roleHandler.getFollowers(channel);      // Get all followers

  // Print Sizes
  println("Found Roles for " + channel.getDisplayName() + ":");
  println(" - Moderators: " + moderators.size());
  println(" - Editors: " + editors.size());
  println(" - VIPs: " + vips.size());
  println(" - Subscribers: " + subscribers.size());
  println(" - Followers: " + followers.size());
  println("\n\n--------------------------------------\n\n");


  // Check Roles for Multiple Users
  var twitchUsers = new HashSet<>(List.of(new TwitchUser[]{ channel, exampleMod, exampleEditor, exampleVIP, exampleSubscriber, exampleFollower, exampleUser }));
  var modMap = roleHandler.checkModerators(twitchUsers, channel);         // Check Moderators
  var editorMap = roleHandler.checkEditors(twitchUsers, channel);         // Check Editors
  var vipMap = roleHandler.checkVIPs(twitchUsers, channel);               // Check VIPs
  var subscriberMap = roleHandler.checkSubscribers(twitchUsers, channel); // Check Subscribers
  var followerMap = roleHandler.checkFollowers(twitchUsers, channel);     // Check Followers

  // Print positive checks
  println("\nModerator Check:");
  for (var entry : modMap.entrySet()) if (entry.getValue()) println(" - " + entry.getKey().getDisplayName() + " is a moderator");

  println("\nEditor Check:");
  for (var entry : editorMap.entrySet()) if (entry.getValue()) println(" - " + entry.getKey().getDisplayName() + " is an editor");

  println("\nVIP Check:");
  for (var entry : vipMap.entrySet()) if (entry.getValue()) println(" - " + entry.getKey().getDisplayName() + " is a VIP");

  println("\nSubscriber Check:");
  for (var entry : subscriberMap.entrySet()) if (entry.getValue()) println(" - " + entry.getKey().getDisplayName() + " is a subscriber");

  println("\nFollower Check:");
  for (var entry : followerMap.entrySet()) if (entry.getValue()) println(" - " + entry.getKey().getDisplayName() + " is a follower");

  // Exit
  System.exit(0);
}
```

### [AD Management](https://www.GitHub.com/MCmoderSD/Helix-API/blob/master/src/test/java/examples/AdExample.java)
In this example, we demonstrate how to run a commercial ad on a Twitch channel using the `StreamHandler`. <br>
For this to work, the channel must be live and advertising must be enabled on the channel.
```java
import de.MCmoderSD.helix.handler.*;

import examples.AuthExample;

import static de.MCmoderSD.helix.handler.StreamHandler.CommercialLength.LENGTH_30;

void main() {

  // Init HelixHandler
  var helixHandler = AuthExample.initHelix();
  var userHandler = helixHandler.getUserHandler();
  var streamHandler = helixHandler.getStreamHandler();

  // Get Channel
  var channel = userHandler.getTwitchUser("MCmoderSD"); // Channel to run ad on

  // Run Ad
  streamHandler.runCommercial(channel, LENGTH_30);

  // Exit
  System.exit(0);
}
```

### [Raid Management](https://www.GitHub.com/MCmoderSD/Helix-API/blob/master/src/test/java/examples/RaidExample.java)
In this example, we demonstrate how to start and cancel a raid between two Twitch channels using the `StreamHandler`. <br>
For this to work, the source channel must be live must have already started a raid.
```java
import de.MCmoderSD.helix.handler.*;

import examples.AuthExample;

void main() {

    // Init HelixHandler
    var helixHandler = AuthExample.initHelix();
    var userHandler = helixHandler.getUserHandler();
    var streamHandler = helixHandler.getStreamHandler();

    // Get Channels
    var user = userHandler.getTwitchUser("MCmoderSD");      // Channel to raid to      (target)
    var channel = userHandler.getTwitchUser("ModersEsel");  // Channel to raid from    (source)

    // Cancel Raid
    streamHandler.cancelRaid(channel);

    // Start Raid
    streamHandler.startRaid(user, channel);

    // Exit
    System.exit(0);
}
```

### [Send Shoutouts](https://www.GitHub.com/MCmoderSD/Helix-API/blob/master/src/test/java/examples/ShoutoutExample.java)
In this example, we demonstrate how to send a shoutout from one Twitch channel to another using the `StreamHandler`. <br>
For this to work, the source channel must be live and must have at least one viewer.
```java
import de.MCmoderSD.helix.handler.*;

import examples.AuthExample;

void main() {

  // Init HelixHandler
  var helixHandler = AuthExample.initHelix();
  var userHandler = helixHandler.getUserHandler();
  var streamHandler = helixHandler.getStreamHandler();

  // Get Channels
  var user = userHandler.getTwitchUser("MCmoderSD");      // Channel to send shoutout to      (target)
  var channel = userHandler.getTwitchUser("ModersEsel");  // Channel to send shoutout from    (source)

  // Send Shoutout
  streamHandler.sendShoutout(user, channel);

  // Exit
  System.exit(0);
}
```

### [Channel Information](https://www.GitHub.com/MCmoderSD/Helix-API/blob/master/src/test/java/examples/InfoExample.java)
In this example, we demonstrate how to retrieve channel information using the `ChannelHandler`. <br>
We will fetch the channel info by ID, username, and `TwitchUser`, compare the results, and print out various details about the channel.
```java
import de.MCmoderSD.helix.handler.*;

import examples.AuthExample;

import java.util.Arrays;

import static java.lang.IO.println;

void main() {

  // Init HelixHandler
  var helixHandler = AuthExample.initHelix();
  var userHandler = helixHandler.getUserHandler();
  var channelHandler = helixHandler.getChannelHandler();

  // Example Variables
  var exampleId = 164284617;          // User ID
  var exampleUsername = "MCmoderSD";  // Username

  // Get Channel Info
  var infoById = channelHandler.getChannelInfo(exampleId);                        // By ID
  var infoByName = channelHandler.getChannelInfo(exampleUsername);                // By Username
  var info = channelHandler.getChannelInfo(userHandler.getTwitchUser(exampleId)); // By TwitchUser

  // Check both are the same
  println("ChannelInfos are the same: " + infoById.equals(infoByName));

  // Print Channel Info
  println("\n------- Channel Info -------");
  println("Channel ID: " + info.getId());
  println("Display Name: " + info.getDisplayName());
  println("Title: " + info.getTitle());
  println("Game ID: " + info.getGameId());
  println("Game Name: " + info.getGameName());
  println("Language: " + info.getLanguage());
  println("Tags: " + Arrays.toString(info.getTags().toArray()));
  println("Branded Content: " + info.isBrandedContent());
  println("-----------------------------------\n");

  // Exit
  System.exit(0);
}
```