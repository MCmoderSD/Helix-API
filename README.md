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
    <version>1.1.0</version>
</dependency>
```

### Configuration

To use the Helix-API, you must supply your Twitch **Client ID** and **Client Secret** for authentication.
You also need to configure a `Server` object for HTTPS communication. There are two options:

#### Option 1: Provide certificate files directly
Supply the paths to your fullchain.pem and privkey.pem files.

### Option 2: Use a JSON keystore configuration
Alternatively, you can provide a `JsonNode` with the following structure to auto-generate a keystore:
```json
{
  "file": "KEYSTORE_FILE_NAME",
  "password": "KEYSTORE_PASSWORD",
  "validity": 365,
  "CN": "YOUR_COMPANY_NAME",
  "OU": "YOUR_ORGANIZATION_UNIT",
  "O": "YOUR_ORGANIZATION",
  "L": "LOCATION",
  "ST": "YOUR_STATE",
  "C": "YOUR_COUNTRY"
}
```

### Example

```java
import com.fasterxml.jackson.databind.JsonNode;

import de.MCmoderSD.helix.config.Configuration;
import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.enums.Scope;
import de.MCmoderSD.helix.handler.ChannelHandler;
import de.MCmoderSD.helix.handler.RoleHandler;
import de.MCmoderSD.helix.handler.UserHandler;
import de.MCmoderSD.helix.objects.ChannelFollower;

import de.MCmoderSD.json.JsonUtility;
import de.MCmoderSD.server.Server;
import de.MCmoderSD.sql.Driver;

import java.io.IOException;

import java.net.URISyntaxException;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import java.util.ArrayList;
import java.util.Scanner;

@SuppressWarnings("ALL")
public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, InterruptedException, KeyManagementException {

        // Load Config
        JsonNode config = JsonUtility.loadJson("/config.json", false);
        JsonNode jks = JsonUtility.loadJson("/server.json", false);

        // Configure Helix
        Configuration.setClientId(config.get("clientId").asText());
        Configuration.setClientSecret(config.get("clientSecret").asText());

        // Configure Database
        Configuration.setDatabaseType(Driver.DatabaseType.SQLITE);
        Configuration.setDatabase("database.db");

        // Initialize Server
        Server server = new Server("localhost", 8000, null, jks, true);
        server.start();

        // Initialize HelixHandler
        HelixHandler helixHandler = new HelixHandler(server);
        System.out.println("Auth URL: " + helixHandler.getTokenHandler().getAuthorizationUrl(Scope.values()));

        // Test API
        Scanner scanner = new Scanner(System.in);
        while (true) printChannelInfo(scanner.nextLine(), helixHandler.getUserHandler(), helixHandler.getChannelHandler());
    }

    private static void printChannelInfo(String channel, UserHandler userHandler, ChannelHandler channelHandler) {

        // Get User
        var user = userHandler.getTwitchUser(channel);
        System.out.println("User: " + user.getDisplayName() + " (" + user.getId() + ")");

        // Get Channel Information and print it
        channelHandler.getChannelInformation(user.getId()).print();
    }

    private static void printModerators(String channel, UserHandler userHandler, RoleHandler roleHandler) {

        // Get User
        var user = userHandler.getTwitchUser(channel);
        System.out.println("User: " + user.getDisplayName() + " (" + user.getId() + ")");

        // Get Moderators
        var moderators = roleHandler.getModerators(user.getId(), null);
        System.out.println("\nModerators: " + moderators.size());
        for (var moderator : moderators) System.out.println(String.format(" - %s (%d) - %s",
                moderator.getDisplayName(),
                moderator.getId(),
                moderator.getBroadcasterType()
        ));
    }

    private static void printEditors(String channel, UserHandler userHandler, RoleHandler roleHandler) {

        // Get User
        var user = userHandler.getTwitchUser(channel);
        System.out.println("User: " + user.getDisplayName() + " (" + user.getId() + ")");

        // Get Editors
        var editors = roleHandler.getEditors(user.getId());
        System.out.println("\nEditors: " + editors.size());
        for (var editor : editors) System.out.println(String.format(" - %s (%d) - %s",
                editor.getDisplayName(),
                editor.getId(),
                editor.getBroadcasterType()
        ));
    }

    private static void printVips(String channel, UserHandler userHandler, RoleHandler roleHandler) {

        // Get User
        var user = userHandler.getTwitchUser(channel);
        System.out.println("User: " + user.getDisplayName() + " (" + user.getId() + ")");

        // Get Vips
        var vips = roleHandler.getVIPs(user.getId(), null);
        System.out.println("\nVIPs: " + vips.size());
        for (var vip : vips) System.out.println(String.format(" - %s (%d) - %s",
                vip.getDisplayName(),
                vip.getId(),
                vip.getBroadcasterType()
        ));
    }

    private static void printSubscribers(String channel, UserHandler userHandler, RoleHandler roleHandler) {

        // Get User
        var user = userHandler.getTwitchUser(channel);
        System.out.println("User: " + user.getDisplayName() + " (" + user.getId() + ")");

        // Get Subscriber
        var subscriber = roleHandler.getSubscribers(user.getId(), null);
        System.out.println("\nSubscriber: " + subscriber.size());
        for (var sub : subscriber) sub.print();
    }

    private static void printFollowers(String channel, UserHandler userHandler, RoleHandler roleHandler) {

        // Get User
        var user = userHandler.getTwitchUser(channel);
        System.out.println("User: " + user.getDisplayName() + " (" + user.getId() + ")");

        // Get Follower
        var followers = roleHandler.getFollowers(user.getId(), null);
        var sortedFollowers = new ArrayList<>(followers);
        sortedFollowers.sort((o1, o2) -> {
            if (o1.getFollowedAt() == null || o2.getFollowedAt() == null) return 0;
            return o1.getFollowedAt().compareTo(o2.getFollowedAt());
        });
        System.out.println("\nFollowers: " + followers.size());
        sortedFollowers.forEach(ChannelFollower::print);
    }
}
```