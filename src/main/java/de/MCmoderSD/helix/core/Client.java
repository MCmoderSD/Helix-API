package de.MCmoderSD.helix.core;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.TwitchHelix;
import de.MCmoderSD.helix.handler.RoleHandler;
import de.MCmoderSD.helix.handler.UserHandler;

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

    // Constructor
    public Client(String clientId, String clientSecret) {

        // Set Credentials
        this.clientId = clientId;
        this.clientSecret = clientSecret;

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
        manager = new TokenManager(this);

        // Initialize Handler
        userHandler = new UserHandler(this);
        roleHandler = new RoleHandler(this);
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
}
