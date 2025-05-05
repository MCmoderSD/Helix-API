package de.MCmoderSD.helix.handler;

import com.github.twitch4j.helix.domain.*;
import de.MCmoderSD.helix.core.Client;
import de.MCmoderSD.helix.enums.Scope;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;

@SuppressWarnings("unused")
public class RoleHandler extends Handler {

    // Constants
    private static final Integer LIMIT = 100; // Max 100 per request (Default 20)

    // Cache
    private final HashMap<Integer, HashSet<Moderator>> moderators;
    private final HashMap<Integer, HashSet<ChannelVip>> vips;
    private final HashMap<Integer, HashSet<Subscription>> subscriptions;
    private final HashMap<Integer, HashSet<InboundFollow>> followers;

    // Constructor
    public RoleHandler(Client client) {

        // Call super constructor
        super(client);

        // Initialize caches
        moderators = new HashMap<>();
        vips = new HashMap<>();
        subscriptions = new HashMap<>();
        followers = new HashMap<>();

        // Load caches
        manager.getAuthTokens().forEach((id, token) -> {

            // Load Moderators
            if (token.hasScope(Scope.MODERATION_READ)) moderators.put(id, getModerators(id, null));

            // Load VIPs
            if (token.hasScope(Scope.CHANNEL_READ_VIPS)) vips.put(id, getVips(id, null));

            // Load Subscriptions
            if (token.hasScope(Scope.CHANNEL_READ_SUBSCRIPTIONS)) subscriptions.put(id, getSubscriptions(id, null));

            // Load Followers
            if (token.hasScope(Scope.MODERATOR_READ_FOLLOWERS)) followers.put(id, getFollowers(id, null));

        });
    }

    // Get moderators
    public HashSet<Moderator> getModerators(Integer channelId, @Nullable String cursor) {

        // Check Parameters
        if (channelId == null || channelId < 1) throw new IllegalArgumentException("Channel ID cannot be null or less than 1");

        // Get access token
        String accessToken = manager.getToken(channelId, Scope.MODERATOR_READ_MODERATORS);

        // Null check
        if (accessToken == null || accessToken.isBlank()) {
            System.err.println("Failed to get access token");
            return null;
        }

        // Get moderators
        ModeratorList moderatorList = helix.getModerators(accessToken, channelId.toString(), null, cursor, LIMIT).execute();

        // Null check
        if (moderatorList == null || moderatorList.getModerators() == null || moderatorList.getModerators().isEmpty()) {
            System.err.println("Failed to get moderators");
            return null;
        }

        // Variables
        HashSet<Moderator> moderators = new HashSet<>(moderatorList.getModerators());

        // Check if the cache is up to date
        boolean cacheUpToDate = cursor == null;
        HashSet<Moderator> cachedModerators = this.moderators.get(channelId);
        for (Moderator moderator : moderators) if (cachedModerators == null || !cachedModerators.contains(moderator)) {
            cacheUpToDate = false;
            break;
        }
        if (cacheUpToDate) return cachedModerators;

        // Check if there are more moderators
        HelixPagination pagination = moderatorList.getPagination();
        String nextCursor = pagination != null ? pagination.getCursor() : null;
        if (nextCursor != null) moderators.addAll(getModerators(channelId, nextCursor));

        // Update cache
        this.moderators.replace(channelId, moderators);

        // Return moderators
        return moderators;
    }

    // Get editors
    public HashSet<ChannelEditor> getEditors(Integer channelId) {

        // Check Parameters
        if (channelId == null || channelId < 1) throw new IllegalArgumentException("Channel ID cannot be null or less than 1");

        // Get access token
        String accessToken = manager.getToken(channelId, Scope.CHANNEL_READ_EDITORS);

        // Null check
        if (accessToken == null || accessToken.isBlank()) {
            System.err.println("Failed to get access token");
            return null;
        }

        // Get editors
        ChannelEditorList editorList = helix.getChannelEditors(accessToken, channelId.toString()).execute();

        // Null check
        if (editorList == null || editorList.getEditors() == null || editorList.getEditors().isEmpty()) {
            System.err.println("Failed to get editors");
            return null;
        }

        // Add editors
        return new HashSet<>(editorList.getEditors());
    }

    // Get VIPs
    public HashSet<ChannelVip> getVips(Integer channelId, @Nullable String cursor) {

        // Check Parameters
        if (channelId == null || channelId < 1) throw new IllegalArgumentException("Channel ID cannot be null or less than 1");

        // Get access token
        String accessToken = manager.getToken(channelId, Scope.CHANNEL_READ_VIPS);

        // Null check
        if (accessToken == null || accessToken.isBlank()) {
            System.err.println("Failed to get access token");
            return null;
        }

        // Get VIPs
        ChannelVipList vipList = helix.getChannelVips(accessToken, channelId.toString(), null, LIMIT, cursor).execute();

        // Null check
        if (vipList == null || vipList.getData() == null || vipList.getData().isEmpty()) {
            System.err.println("Failed to get VIPs");
            return null;
        }

        // Variables
        HashSet<ChannelVip> vips = new HashSet<>(vipList.getData());

        // Check if the cache is up to date
        boolean cacheUpToDate = cursor == null;
        HashSet<ChannelVip> cachedVips = this.vips.get(channelId);
        for (ChannelVip vip : vips) if (cachedVips == null || !cachedVips.contains(vip)) {
            cacheUpToDate = false;
            break;
        }
        if (cacheUpToDate) return cachedVips;

        // Check if there are more VIPs
        HelixPagination pagination = vipList.getPagination();
        String nextCursor = pagination != null ? pagination.getCursor() : null;
        if (nextCursor != null) vips.addAll(getVips(channelId, nextCursor));

        // Update cache
        this.vips.replace(channelId, vips);

        // Return VIPs
        return vips;
    }

    // Get subscribers
    public HashSet<Subscription> getSubscriptions(Integer channelId, @Nullable String cursor) {

        // Check Parameters
        if (channelId == null || channelId < 1) throw new IllegalArgumentException("Channel ID cannot be null or less than 1");

        // Get access token
        String accessToken = manager.getToken(channelId, Scope.CHANNEL_READ_SUBSCRIPTIONS);

        // Null check
        if (accessToken == null || accessToken.isBlank()) {
            System.err.println("Failed to get access token");
            return null;
        }

        // Get subscribers
        SubscriptionList subscriptionList = helix.getSubscriptions(accessToken, channelId.toString(), cursor, null, LIMIT).execute();

        // Null check
        if (subscriptionList == null || subscriptionList.getSubscriptions() == null || subscriptionList.getSubscriptions().isEmpty()) {
            System.err.println("Failed to get subscribers");
            return null;
        }

        // Variables
        HashSet<Subscription> subscriptions = new HashSet<>(subscriptionList.getSubscriptions());

        // Check if the cache is up to date
        boolean cacheUpToDate = cursor == null;
        HashSet<Subscription> cachedSubscriptions = this.subscriptions.get(channelId);
        for (Subscription subscription : subscriptions) if (cachedSubscriptions == null || !cachedSubscriptions.contains(subscription)) {
            cacheUpToDate = false;
            break;
        }
        if (cacheUpToDate) return cachedSubscriptions;

        // Check if there are more subscribers
        HelixPagination pagination = subscriptionList.getPagination();
        String nextCursor = pagination != null ? pagination.getCursor() : null;
        if (nextCursor != null && subscriptions.size() >= LIMIT) subscriptions.addAll(getSubscriptions(channelId, nextCursor));

        // Update cache
        this.subscriptions.replace(channelId, subscriptions);

        // Return subscribers
        return subscriptions;
    }

    // Get followers
    public HashSet<InboundFollow> getFollowers(Integer channelId, @Nullable String cursor) {

        // Check Parameters
        if (channelId == null || channelId < 1) throw new IllegalArgumentException("Channel ID cannot be null or less than 1");

        // Get access token
        String accessToken = manager.getToken(channelId, Scope.MODERATOR_READ_FOLLOWERS);

        // Null check
        if (accessToken == null || accessToken.isBlank()) {
            System.err.println("Failed to get access token");
            return null;
        }

        // Get followers
        InboundFollowers inboundFollowers = helix.getChannelFollowers(accessToken, channelId.toString(), null, LIMIT, cursor).execute();

        // Null check
        if (inboundFollowers == null || inboundFollowers.getFollows() == null || inboundFollowers.getFollows().isEmpty()) {
            System.err.println("Failed to get followers");
            return null;
        }

        // Variables
        HashSet<InboundFollow> followers = new HashSet<>(inboundFollowers.getFollows());

        // Check if the cache is up to date
        boolean cacheUpToDate = cursor == null;
        HashSet<InboundFollow> cachedFollowers = this.followers.get(channelId);
        for (InboundFollow follower : followers) if (cachedFollowers == null || !cachedFollowers.contains(follower)) {
            cacheUpToDate = false;
            break;
        }
        if (cacheUpToDate) return followers;

        // Check if there are more followers
        HelixPagination pagination = inboundFollowers.getPagination();
        String nextCursor = pagination != null ? pagination.getCursor() : null;
        if (nextCursor != null) followers.addAll(getFollowers(channelId, nextCursor));

        // Update cache
        this.followers.replace(channelId, followers);

        // Return followers
        return followers;
    }
}