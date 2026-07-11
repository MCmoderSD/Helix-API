package examples;

import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.enums.Scope;
import de.MCmoderSD.helix.handler.*;

import de.MCmoderSD.json.JsonUtility;
import de.MCmoderSD.server.core.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import static java.lang.IO.println;

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
        println("Authorization URL: " + authURL);
    }
}