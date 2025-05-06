package de.MCmoderSD.helix.core;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;

import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.TwitchHelix;

import de.MCmoderSD.helix.config.Configuration;
import de.MCmoderSD.helix.handler.ChannelHandler;
import de.MCmoderSD.helix.handler.RoleHandler;
import de.MCmoderSD.helix.handler.UserHandler;

import de.MCmoderSD.server.Server;

@SuppressWarnings("unused")
public class Client {

    // Constants
    public static final String PROVIDER = "twitch";

    // Credentials
    private final String clientId;
    private final String clientSecret;

    // Attributes
    private final TwitchHelix helix;
    private final CredentialManager credentialManager;
    private final TokenManager manager;

    // Handlers
    private final UserHandler userHandler;
    private final RoleHandler roleHandler;
    private final ChannelHandler channelHandler;

    // Constructor
    public Client(Server server) {

        // Validate Configuration
        if (!Configuration.validate()) throw new IllegalStateException("Configuration is not valid. Please check your config.");

        // Set Credentials
        this.clientId = Configuration.clientId;
        this.clientSecret = Configuration.clientSecret;

        // Initialize Credential TokenManager
        credentialManager = CredentialManagerBuilder.builder().build();

        // Initialize Twitch Client
        var twitchClient = TwitchClientBuilder.builder()
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withCredentialManager(credentialManager)
                .withChatCommandsViaHelix(true)
                .withEnableHelix(true)
                .build();

        // Initialize Helix
        helix = twitchClient.getHelix();

        // Initialize TokenManager
        manager = new TokenManager(this, server);

        // Initialize Handler
        userHandler = new UserHandler(this);
        roleHandler = new RoleHandler(this);
        channelHandler = new ChannelHandler(this);
    }

    // Setters
    public void addCredential(String accessToken) {
        credentialManager.addCredential(PROVIDER, new OAuth2Credential(PROVIDER, accessToken));
    }

    // Getters
    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public TwitchHelix getHelix() {
        return helix;
    }

    public TokenManager getTokenManager() {
        return manager;
    }

    public UserHandler getUserHandler() {
        return userHandler;
    }

    public RoleHandler getRoleHandler() {
        return roleHandler;
    }

    public ChannelHandler getChannelHandler() {
        return channelHandler;
    }
}