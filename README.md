# Helix-API

## Description
This is a Java Helix-API wrapper for communicating with the Twitch Helix API.
It provides a simple and easy-to-use interface for making requests to the Helix API endpoints.

## Features
- [Authentication](#initialisation-and-authentication): OAuth2 authentication and scope management
- [User Information](#user-information): Retrieve user details, email, and profile information.
- [Role Management](#role-management): Manage channel roles such as moderators, editors, VIPs, subscribers, and followers.
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
        Scope[] scopes = new ArrayList<>(Arrays.asList(UserHandler.REQUIRED_SCOPES, RoleHandler.REQUIRED_SCOPES, ChannelHandler.REQUIRED_SCOPES))
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

### Role Management
```java
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
        UserHandler userHandler = helixHandler.getUserHandler();
        ChannelHandler channelHandler = helixHandler.getChannelHandler();

        // Get Channels
        TwitchUser user = userHandler.getTwitchUser("MCmoderSD");       // Channel to send shoutout to      (target)
        TwitchUser channel = userHandler.getTwitchUser("ModersEsel");   // Channel to send shoutout from    (source)

        // Send Shoutout
        channelHandler.sendShoutout(user, channel);

        // Exit
        System.exit(0);
    }
}
```