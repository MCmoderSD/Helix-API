package de.MCmoderSD.helix.core;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.TwitchHelix;
import de.MCmoderSD.helix.enums.Scope;

import java.util.Set;

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
        manager = new TokenManager(this, clientId, clientSecret);
    }

    // Methods
    public void authenticate() {
        System.out.println(manager.getAuthorizationUrl(Scope.values()));
    }

    // Setters
    public void addCredential(String accessToken) {
        credentialManager.addCredential(PROVIDER, new OAuth2Credential(PROVIDER, accessToken));
    }

    // Getters
    public TwitchHelix getHelix() {
        return helix;
    }
}
