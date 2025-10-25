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

### Example
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
public class Main {

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
        System.out.println("\n\nAuthorization URL: " + authURL + "\n");
    }
}
```