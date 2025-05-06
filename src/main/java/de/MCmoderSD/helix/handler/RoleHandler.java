package de.MCmoderSD.helix.handler;

import de.MCmoderSD.helix.core.Client;
import de.MCmoderSD.helix.objects.*;

import java.util.HashMap;
import java.util.HashSet;

@SuppressWarnings("unused")
public class RoleHandler extends Handler {

    // Cache
    private final HashMap<Integer, HashSet<ChannelModerator>> moderators;
    private final HashMap<Integer, HashSet<ChannelVip>> vips;
    private final HashMap<Integer, HashSet<ChannelSubscriber>> subscriptions;
    private final HashMap<Integer, HashSet<ChannelFollower>> followers;

    // Constructor
    public RoleHandler(Client client) {

        // Call super constructor
        super(client);

        // Initialize caches
        moderators = new HashMap<>();
        vips = new HashMap<>();
        subscriptions = new HashMap<>();
        followers = new HashMap<>();
    }
}