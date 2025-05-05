package de.MCmoderSD.helix.handler;

import com.github.twitch4j.helix.TwitchHelix;
import de.MCmoderSD.helix.core.Client;
import de.MCmoderSD.helix.core.TokenManager;

public abstract class Handler {

    // Associations
    protected final Client client;
    protected final TwitchHelix helix;
    protected final TokenManager manager;

    // Constructor
    public Handler(Client client) {
        this.client = client;
        this.helix = client.getHelix();
        this.manager = client.getTokenManager();
    }
}
