package de.MCmoderSD.helix.handler;

import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.Subscription;
import com.github.twitch4j.helix.domain.SubscriptionList;
import com.github.twitch4j.helix.domain.InboundFollow;
import com.github.twitch4j.helix.domain.InboundFollowers;
import com.github.twitch4j.helix.domain.HelixPagination;
import com.github.twitch4j.helix.domain.Moderator;
import com.github.twitch4j.helix.domain.ModeratorList;
import com.github.twitch4j.helix.domain.ChannelEditorList;
import com.github.twitch4j.helix.domain.ChannelVipList;

import de.MCmoderSD.helix.core.Client;
import de.MCmoderSD.helix.enums.Scope;
import de.MCmoderSD.helix.objects.ChannelFollower;
import de.MCmoderSD.helix.objects.ChannelModerator;
import de.MCmoderSD.helix.objects.ChannelSubscriber;
import de.MCmoderSD.helix.objects.ChannelEditor;
import de.MCmoderSD.helix.objects.ChannelVip;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;

@SuppressWarnings("unused")
public class RoleHandler extends Handler {

    // Constants
    public static final Scope[] REQUIRED_SCOPES = {
            Scope.MODERATION_READ,              // Moderators
            Scope.CHANNEL_READ_VIPS,            // VIPs
            Scope.CHANNEL_READ_SUBSCRIPTIONS,   // Subscribers
            Scope.MODERATOR_READ_FOLLOWERS,     // Followers
            Scope.CHANNEL_MANAGE_MODERATORS,    // Add/Remove Moderators
            Scope.CHANNEL_MANAGE_VIPS           // Add/Remove VIPs
    };

    // Cache
    private final HashMap<Integer, HashSet<ChannelModerator>> moderatorCache;
    private final HashMap<Integer, HashSet<ChannelVip>> vipCache;
    private final HashMap<Integer, HashSet<ChannelSubscriber>> subscriberCache;
    private final HashMap<Integer, HashSet<ChannelFollower>> followerCache;

    // Constructor
    public RoleHandler(Client client) {

        // Call super constructor
        super(client);

        // Initialize caches
        moderatorCache = new HashMap<>();
        vipCache = new HashMap<>();
        subscriberCache = new HashMap<>();
        followerCache = new HashMap<>();

        // Initialize caches
        manager.getAuthTokens().forEach((id, authToken) -> new Thread(() -> {

            // Load moderators
            if (authToken.hasScope(Scope.MODERATION_READ)) moderatorCache.put(id, getModerators(id, null));

            // Load VIPs
            if (authToken.hasScope(Scope.CHANNEL_READ_VIPS)) vipCache.put(id, getVIPs(id, null));

            // Load subscriber
            if (authToken.hasScope(Scope.CHANNEL_READ_SUBSCRIPTIONS)) subscriberCache.put(id, getSubscribers(id, null));

            // Load followers
            if (authToken.hasScope(Scope.MODERATOR_READ_FOLLOWERS)) followerCache.put(id, getFollowers(id, null));

        }).start());
    }

    private HashSet<ChannelModerator> convertModerators(Integer id, HashSet<Moderator> moderators) {

        // Variables
        User channel = getUser(id);
        HashSet<ChannelModerator> channelModerators = new HashSet<>();
        HashSet<Integer> moderatorUsers = new HashSet<>();

        // Convert IDs to Users
        for (Moderator moderator : moderators) moderatorUsers.add(Integer.parseInt(moderator.getUserId()));
        HashSet<User> users = getUsersByIDs(moderatorUsers);

        // Convert to ChannelModerator
        for (Moderator moderator : moderators) {

            // Get user
            User user = users.stream().filter(u -> u.getId().equals(moderator.getUserId())).findFirst().orElse(null);
            if (user == null) continue;

            // Create ChannelModerator
            channelModerators.add(new ChannelModerator(user, channel));
        }

        return channelModerators;
    }

    private HashSet<ChannelVip> convertVIPs(Integer id, HashSet<com.github.twitch4j.helix.domain.ChannelVip> vips) {

        // Variables
        User channel = getUser(id);
        HashSet<ChannelVip> channelVips = new HashSet<>();
        HashSet<Integer> vipUsers = new HashSet<>();

        // Convert IDs to Users
        for (com.github.twitch4j.helix.domain.ChannelVip vip : vips) vipUsers.add(Integer.parseInt(vip.getUserId()));
        HashSet<User> users = getUsersByIDs(vipUsers);

        // Convert to ChannelVIP
        for (com.github.twitch4j.helix.domain.ChannelVip vip : vips) {

            // Get user
            User user = users.stream().filter(u -> u.getId().equals(vip.getUserId())).findFirst().orElse(null);
            if (user == null) continue;

            // Create ChannelVIP
            channelVips.add(new ChannelVip(user, channel));
        }

        return channelVips;
    }

    private HashSet<ChannelSubscriber> convertSubscriber(Integer id, HashSet<Subscription> subscriptions) {

        // Variables
        User channel = getUser(id);
        HashSet<ChannelSubscriber> channelSubscribers = new HashSet<>();
        HashSet<Integer> userIDs = new HashSet<>();

        // Convert IDs to Users
        for (Subscription subscription : subscriptions) {
            userIDs.add(Integer.parseInt(subscription.getUserId()));
            if (subscription.getIsGift()) userIDs.add(Integer.parseInt(subscription.getGifterId()));
        }
        HashSet<User> users = getUsersByIDs(userIDs);

        // Convert to ChannelSubscriber
        for (Subscription subscription : subscriptions) {

            // Get user
            User user = users.stream().filter(u -> u.getId().equals(subscription.getUserId())).findFirst().orElse(null);
            User gifter = users.stream().filter(u -> u.getId().equals(subscription.getGifterId())).findFirst().orElse(null);
            if (user == null || (subscription.getIsGift() && gifter == null)) continue;

            // Create ChannelSubscriber
            channelSubscribers.add(new ChannelSubscriber(subscription, user, channel, gifter));
        }

        return channelSubscribers;
    }

    private HashSet<ChannelFollower> convertFollowers(Integer id, HashSet<InboundFollow> follows) {

        // Variables
        User channel = getUser(id);
        HashSet<ChannelFollower> channelFollowers = new HashSet<>();
        HashSet<Integer> userIDs = new HashSet<>();

        // Convert IDs to Users
        for (InboundFollow follow : follows) userIDs.add(Integer.parseInt(follow.getUserId()));
        HashSet<User> users = getUsersByIDs(userIDs);

        // Convert to ChannelFollower
        for (InboundFollow follow : follows) {

            // Get user
            User user = users.stream().filter(u -> u.getId().equals(follow.getUserId())).findFirst().orElse(null);
            if (user == null) continue;

            // Create ChannelFollower
            channelFollowers.add(new ChannelFollower(follow, user, channel));
        }

        return channelFollowers;
    }

    public HashSet<ChannelModerator> getModerators(Integer id, @Nullable String cursor) {

        // Check Parameters
        if (id == null || id < 1) throw new IllegalArgumentException("Channel ID cannot be null or less than 1");

        // Get access token
        String accessToken = manager.getToken(id, Scope.MODERATION_READ);

        // Null check
        if (accessToken == null || accessToken.isBlank()) {
            System.err.println("Failed to get access token");
            return null;
        }

        // Get moderators
        ModeratorList moderatorList = helix.getModerators(accessToken, id.toString(), null, cursor, 100).execute();

        // Null check
        if (moderatorList == null || moderatorList.getModerators() == null || moderatorList.getModerators().isEmpty()) {
            System.err.println("Failed to get moderators");
            return null;
        }

        // Convert to ChannelModerator
        HashSet<ChannelModerator> channelModerators = convertModerators(id, new HashSet<>(moderatorList.getModerators()));

        // Check if cache is up to date
        HashSet<ChannelModerator> cache = moderatorCache.get(id);
        HashSet<Integer> cacheIDs = new HashSet<>();
        boolean cacheUpToDate = cursor == null && cache !=null;
        if (cacheUpToDate) {
            cache.forEach(channelModerator -> cacheIDs.add(channelModerator.getId()));
            for (ChannelModerator channelModerator : channelModerators) {
                if (!cacheIDs.contains(channelModerator.getId())) {
                    cacheUpToDate = false;
                    break;
                }
            }
        }
        if (cacheUpToDate) return cache;

        // Check if there are more moderators
        HelixPagination pagination = moderatorList.getPagination();
        String nextCursor = pagination != null ? pagination.getCursor() : null;
        if (nextCursor != null) channelModerators.addAll(getModerators(id, nextCursor));

        // Update cache
        moderatorCache.replace(id, channelModerators);

        // Return moderators
        return channelModerators;
    }

    public HashSet<ChannelEditor> getEditors(Integer id) {

        // Check Parameters
        if (id == null || id < 1) throw new IllegalArgumentException("Channel ID cannot be null or less than 1");

        // Get access token
        String accessToken = manager.getToken(id, Scope.CHANNEL_READ_EDITORS);

        // Null check
        if (accessToken == null || accessToken.isBlank()) {
            System.err.println("Failed to get access token");
            return null;
        }

        // Get editors
        ChannelEditorList channelEditorList = helix.getChannelEditors(accessToken, id.toString()).execute();

        // Null check
        if (channelEditorList == null || channelEditorList.getEditors() == null || channelEditorList.getEditors().isEmpty()) {
            System.err.println("Failed to get editors");
            return null;
        }

        // Variables
        User channel = getUser(id);
        HashSet<com.github.twitch4j.helix.domain.ChannelEditor> editors = new HashSet<>(channelEditorList.getEditors());
        HashSet<ChannelEditor> channelEditors = new HashSet<>();
        HashSet<Integer> editorUsers = new HashSet<>();

        // Convert IDs to Users
        for (com.github.twitch4j.helix.domain.ChannelEditor editor : editors) editorUsers.add(Integer.parseInt(editor.getUserId()));
        HashSet<User> users = getUsersByIDs(editorUsers);

        // Convert to ChannelEditor
        for (com.github.twitch4j.helix.domain.ChannelEditor editor : editors) {

            // Get user
            User user = users.stream().filter(u -> u.getId().equals(editor.getUserId())).findFirst().orElse(null);
            if (user == null) continue;

            // Create ChannelEditor
            channelEditors.add(new ChannelEditor(user, channel));
        }

        return channelEditors;
    }

    public HashSet<ChannelVip> getVIPs(Integer id, @Nullable String cursor) {

        // Check Parameters
        if (id == null || id < 1) throw new IllegalArgumentException("Channel ID cannot be null or less than 1");

        // Get access token
        String accessToken = manager.getToken(id, Scope.CHANNEL_READ_VIPS);

        // Null check
        if (accessToken == null || accessToken.isBlank()) {
            System.err.println("Failed to get access token");
            return null;
        }

        // Get VIPs
        ChannelVipList channelVipList = helix.getChannelVips(accessToken, id.toString(), null, 100, cursor).execute();

        // Null check
        if (channelVipList == null || channelVipList.getData() == null || channelVipList.getData().isEmpty()) {
            System.err.println("Failed to get VIPs");
            return null;
        }

        // Convert to ChannelVIP
        HashSet<ChannelVip> channelVips = convertVIPs(id, new HashSet<>(channelVipList.getData()));

        // Check if cache is up to date
        HashSet<ChannelVip> cache = vipCache.get(id);
        HashSet<Integer> cacheIDs = new HashSet<>();
        boolean cacheUpToDate = cursor == null && cache !=null;
        if (cacheUpToDate) {
            cache.forEach(channelVip -> cacheIDs.add(channelVip.getId()));
            for (ChannelVip channelVip : channelVips) {
                if (!cacheIDs.contains(channelVip.getId())) {
                    cacheUpToDate = false;
                    break;
                }
            }
        }
        if (cacheUpToDate) return cache;

        // Check if there are more VIPs
        HelixPagination pagination = channelVipList.getPagination();
        String nextCursor = pagination != null ? pagination.getCursor() : null;
        if (nextCursor != null) channelVips.addAll(getVIPs(id, nextCursor));

        // Update cache
        vipCache.replace(id, channelVips);

        // Return VIPs
        return channelVips;
    }

    public HashSet<ChannelSubscriber> getSubscribers(Integer id, @Nullable String cursor) {

        // Check Parameters
        if (id == null || id < 1) throw new IllegalArgumentException("Channel ID cannot be null or less than 1");

        // Get access token
        String accessToken = manager.getToken(id, Scope.CHANNEL_READ_SUBSCRIPTIONS);

        // Null check
        if (accessToken == null || accessToken.isBlank()) {
            System.err.println("Failed to get access token");
            return null;
        }

        // Get subscriber
        SubscriptionList subscriptionList = helix.getSubscriptions(accessToken, id.toString(), cursor, null, 100).execute();

        // Null check
        if (subscriptionList == null || subscriptionList.getSubscriptions() == null || subscriptionList.getSubscriptions().isEmpty()) {
            System.err.println("Failed to get subscribers");
            return null;
        }

        // Convert to ChannelSubscriber
        HashSet<ChannelSubscriber> channelSubscribers = convertSubscriber(id, new HashSet<>(subscriptionList.getSubscriptions()));

        // Check if cache is up to date
        HashSet<ChannelSubscriber> cache = subscriberCache.get(id);
        boolean cacheUpToDate = cursor == null && cache !=null;
        if (cacheUpToDate) {
            for (ChannelSubscriber channelSubscriber : channelSubscribers) {
                if (!cache.contains(channelSubscriber)) {
                    cacheUpToDate = false;
                    break;
                }
            }
        }
        if (cacheUpToDate) return cache;

        // Check if there are more subscriber
        HelixPagination pagination = subscriptionList.getPagination();
        String nextCursor = pagination != null ? pagination.getCursor() : null;
        if (nextCursor != null && channelSubscribers.size() >= LIMIT) channelSubscribers.addAll(getSubscribers(id, nextCursor));

        // Update cache
        subscriberCache.replace(id, channelSubscribers);

        // Return subscribers
        return channelSubscribers;
    }

    public HashSet<ChannelFollower> getFollowers(Integer id, @Nullable String cursor) {

        // Check Parameters
        if (id == null || id < 1) throw new IllegalArgumentException("Channel ID cannot be null or less than 1");

        // Get access token
        String accessToken = manager.getToken(id, Scope.MODERATOR_READ_FOLLOWERS);

        // Null check
        if (accessToken == null || accessToken.isBlank()) {
            System.err.println("Failed to get access token");
            return null;
        }

        // Get followers
        InboundFollowers inboundFollowers = helix.getChannelFollowers(accessToken, id.toString(), null, 100, cursor).execute();

        // Null check
        if (inboundFollowers == null || inboundFollowers.getFollows() == null || inboundFollowers.getFollows().isEmpty()) {
            System.err.println("Failed to get followers");
            return null;
        }

        // Convert to ChannelFollower
        HashSet<ChannelFollower> channelFollowers = convertFollowers(id, new HashSet<>(inboundFollowers.getFollows()));

        // Check if cache is up to date
        HashSet<ChannelFollower> cache = followerCache.get(id);
        HashSet<Integer> cacheIDs = new HashSet<>();
        boolean cacheUpToDate = cursor == null && cache !=null;
        if (cacheUpToDate) {
            cache.forEach(channelFollower -> cacheIDs.add(channelFollower.getId()));
            for (ChannelFollower channelFollower : channelFollowers) {
                if (!cacheIDs.contains(channelFollower.getId())) {
                    cacheUpToDate = false;
                    break;
                }
            }
        }
        if (cacheUpToDate) return cache;

        // Check if there are more followers
        HelixPagination pagination = inboundFollowers.getPagination();
        String nextCursor = pagination != null ? pagination.getCursor() : null;
        if (nextCursor != null) channelFollowers.addAll(getFollowers(id, nextCursor));

        // Update cache
        followerCache.replace(id, channelFollowers);

        // Return followers
        return channelFollowers;
    }

    public boolean isModerator(Integer user, Integer channel) {
        return getModerators(channel, null).stream().anyMatch(moderator -> moderator.getId().equals(user));
    }

    public boolean isEditor(Integer user, Integer channel) {
        return getEditors(channel).stream().anyMatch(editor -> editor.getId().equals(user));
    }

    public boolean isVIP(Integer user, Integer channel) {
        return getVIPs(channel, null).stream().anyMatch(vip -> vip.getId().equals(user));
    }

    public boolean isSubscriber(Integer user, Integer channel) {
        return getSubscribers(channel, null).stream().anyMatch(subscriber -> subscriber.getId().equals(user));
    }

    public boolean isFollower(Integer user, Integer channel) {
        return getFollowers(channel, null).stream().anyMatch(follower -> follower.getId().equals(user));
    }

    public boolean addModerator(Integer user, Integer channel) {

        // Check Parameters
        if (user == null || user < 1) throw new IllegalArgumentException("User ID cannot be null or less than 1");
        if (channel == null || channel < 1) throw new IllegalArgumentException("Channel ID cannot be null or less than 1");

        // Check if user is already a moderator
        if (isModerator(user, channel)) {
            System.err.println("User is already a moderator");
            return false;
        }

        // Check if user is VIP
        if (isVIP(user, channel)) if (!removeVIP(user, channel)) return false;

        // Get access token
        String accessToken = manager.getToken(channel, Scope.CHANNEL_MANAGE_MODERATORS);

        // Null check
        if (accessToken == null || accessToken.isBlank()) {
            System.err.println("Failed to get access token");
            return false;
        }

        // Add moderator
        helix.addChannelModerator(accessToken, channel.toString(), user.toString()).execute();

        // Check if moderator was added
        return isModerator(user, channel);
    }

    public boolean removeModerator(Integer user, Integer channel) {

        // Check Parameters
        if (user == null || user < 1) throw new IllegalArgumentException("User ID cannot be null or less than 1");
        if (channel == null || channel < 1) throw new IllegalArgumentException("Channel ID cannot be null or less than 1");

        // Check if user is not a moderator
        if (!isModerator(user, channel)) {
            System.err.println("User is not a moderator");
            return false;
        }

        // Get access token
        String accessToken = manager.getToken(channel, Scope.CHANNEL_MANAGE_MODERATORS);

        // Null check
        if (accessToken == null || accessToken.isBlank()) {
            System.err.println("Failed to get access token");
            return false;
        }

        // Remove moderator
        helix.removeChannelModerator(accessToken, channel.toString(), user.toString()).execute();

        // Check if moderator was removed
        return isModerator(user, channel);
    }

    public boolean addVIP(Integer user, Integer channel) {

        // Check Parameters
        if (user == null || user < 1) throw new IllegalArgumentException("User ID cannot be null or less than 1");
        if (channel == null || channel < 1) throw new IllegalArgumentException("Channel ID cannot be null or less than 1");

        // Check if user is already a VIP
        if (isVIP(user, channel)) {
            System.err.println("User is already a VIP");
            return false;
        }

        // Check if user is moderator
        if (isModerator(user, channel)) if (!removeModerator(user, channel)) return false;

        // Get access token
        String accessToken = manager.getToken(channel, Scope.CHANNEL_MANAGE_VIPS);

        // Null check
        if (accessToken == null || accessToken.isBlank()) {
            System.err.println("Failed to get access token");
            return false;
        }

        // Add VIP
        helix.addChannelVip(accessToken, channel.toString(), user.toString()).execute();

        // Check if VIP was added
        return isVIP(user, channel);
    }

    public boolean removeVIP(Integer user, Integer channel) {

        // Check Parameters
        if (user == null || user < 1) throw new IllegalArgumentException("User ID cannot be null or less than 1");
        if (channel == null || channel < 1) throw new IllegalArgumentException("Channel ID cannot be null or less than 1");

        // Check if user is not a VIP
        if (!isVIP(user, channel)) {
            System.err.println("User is not a VIP");
            return false;
        }

        // Get access token
        String accessToken = manager.getToken(channel, Scope.CHANNEL_MANAGE_VIPS);

        // Null check
        if (accessToken == null || accessToken.isBlank()) {
            System.err.println("Failed to get access token");
            return false;
        }

        // Remove VIP
        helix.removeChannelVip(accessToken, channel.toString(), user.toString()).execute();

        // Check if VIP was removed
        return isVIP(user, channel);
    }

    // Getters
    public HashMap<Integer, HashSet<ChannelModerator>> getModeratorCache() {
        return moderatorCache;
    }

    public HashMap<Integer, HashSet<ChannelVip>> getVipCache() {
        return vipCache;
    }

    public HashMap<Integer, HashSet<ChannelSubscriber>> getSubscriberCache() {
        return subscriberCache;
    }

    public HashMap<Integer, HashSet<ChannelFollower>> getFollowerCache() {
        return followerCache;
    }
}