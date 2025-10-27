package de.MCmoderSD.helix.core;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;

import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.TwitchHelix;

import tools.jackson.databind.JsonNode;

import de.MCmoderSD.helix.enums.Scope;
import de.MCmoderSD.helix.handler.UserHandler;
import de.MCmoderSD.helix.handler.ChatHandler;
import de.MCmoderSD.helix.handler.RoleHandler;
import de.MCmoderSD.helix.handler.StreamHandler;
import de.MCmoderSD.helix.handler.ChannelHandler;
import de.MCmoderSD.server.core.Server;


import static de.MCmoderSD.helix.utilities.ConfigValidator.*;

public class HelixHandler {

    // Constants
    public static final String PROVIDER = "twitch";

    // Attributes
    private final TwitchHelix helix;
    private final CredentialManager credentialManager;

    // Handler
    private final TokenHandler tokenHandler;
    private final UserHandler userHandler;
    private final ChatHandler chatHandler;
    private final RoleHandler roleHandler;
    private final StreamHandler streamHandler;
    private final ChannelHandler channelHandler;

    // Constructor
    public HelixHandler(JsonNode application, JsonNode database, Server server) {

        // Validate Config
        if (!validateApplicationConfig(application)) throw new IllegalArgumentException("Invalid Application Config");
        if (!validateDatabaseConfig(database)) throw new IllegalArgumentException("Invalid Database Config");

        // Check Server
        if (server == null) throw new IllegalArgumentException("Server instance is null");

        // Get Twitch Credentials
        JsonNode credentials = application.get("credentials");
        String clientId = credentials.get("clientId").asString();
        String clientSecret = credentials.get("clientSecret").asString();

        // Initialize Credential TokenHandler
        credentialManager = CredentialManagerBuilder.builder().build();

        // Initialize Twitch HelixHandler
        var twitchClient = TwitchClientBuilder.builder()
                .withClientId(clientId)                     // Client ID
                .withClientSecret(clientSecret)             // Client Secret
                .withCredentialManager(credentialManager)   // Credential Manager
                .withChatCommandsViaHelix(true)             // Enable Chat Commands via Helix
                .withEnableHelix(true)                      // Enable Helix
                .build();

        // Initialize Helix
        helix = twitchClient.getHelix();

        // Initialize TokenHandler
        tokenHandler = new TokenHandler(application, database, server, this);

        // Initialize Handlers
        userHandler = new UserHandler(helix, tokenHandler);
        chatHandler = new ChatHandler(helix, tokenHandler);
        roleHandler = new RoleHandler(helix, tokenHandler);
        streamHandler = new StreamHandler(helix, tokenHandler);
        channelHandler = new ChannelHandler(helix, tokenHandler);
    }

    public HelixHandler(JsonNode application, JsonNode database, Server server, TwitchHelix helix, CredentialManager credentialManager) {

        // Validate Config
        if (!validateApplicationConfig(application)) throw new IllegalArgumentException("Invalid Application Config");
        if (!validateDatabaseConfig(database)) throw new IllegalArgumentException("Invalid Database Config");

        // Check Parameters
        if (server == null) throw new IllegalArgumentException("Server instance is null");
        if (helix == null) throw new IllegalArgumentException("TwitchHelix instance is null");
        if (credentialManager == null) throw new IllegalArgumentException("CredentialManager instance is null");

        // Set Attributes
        this.helix = helix;
        this.credentialManager = credentialManager;

        // Initialize TokenHandler
        tokenHandler = new TokenHandler(application, database, server, this);

        // Initialize Handlers
        userHandler = new UserHandler(helix, tokenHandler);
        chatHandler = new ChatHandler(helix, tokenHandler);
        roleHandler = new RoleHandler(helix, tokenHandler);
        streamHandler = new StreamHandler(helix, tokenHandler);
        channelHandler = new ChannelHandler(helix, tokenHandler);
    }

    // Setters
    public void addCredential(String accessToken) {
        credentialManager.addCredential(PROVIDER, new OAuth2Credential(PROVIDER, accessToken));
    }

    // Getters
    public TwitchHelix getHelix() {
        return helix;
    }

    public String getAuthorizationUrl(Scope... scopes) {
        return tokenHandler.getAuthorizationUrl(scopes);
    }

    public UserHandler getUserHandler() {
        return userHandler;
    }

    public ChatHandler getChatHandler() {
        return chatHandler;
    }

    public RoleHandler getRoleHandler() {
        return roleHandler;
    }

    public StreamHandler getStreamHandler() {
        return streamHandler;
    }

    public ChannelHandler getChannelHandler() {
        return channelHandler;
    }
}