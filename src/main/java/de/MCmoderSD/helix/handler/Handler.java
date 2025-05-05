package de.MCmoderSD.helix.handler;

import com.github.twitch4j.helix.TwitchHelix;
import de.MCmoderSD.helix.core.Client;
import de.MCmoderSD.helix.core.TokenManager;

public abstract class Handler {

    // Associations
    protected final Client client;
    protected final TwitchHelix helix;
    protected final TokenManager tokenManager;

    // Constructor
    public Handler(Client client, TokenManager tokenManager) {
        // Set Associations
        this.client = client;
        this.helix = client.getHelix();
        this.tokenManager = tokenManager;
    }

    // Getters
    public Client getClient() {
        return client;
    }

    public TwitchHelix getHelix() {
        return helix;
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }
}
