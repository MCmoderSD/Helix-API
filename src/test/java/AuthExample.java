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