package de.MCmoderSD.helix.handler;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.HelixPagination;
import org.jetbrains.annotations.Nullable;

import de.MCmoderSD.helix.core.TokenHandler;
import de.MCmoderSD.helix.enums.Scope;
import de.MCmoderSD.helix.objects.AuthToken;
import de.MCmoderSD.helix.objects.ChannelModerator;
import de.MCmoderSD.helix.objects.ChannelEditor;
import de.MCmoderSD.helix.objects.ChannelVip;
import de.MCmoderSD.helix.objects.ChannelSubscriber;
import de.MCmoderSD.helix.objects.ChannelFollower;
import de.MCmoderSD.helix.objects.TwitchUser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import static de.MCmoderSD.helix.enums.Scope.MODERATION_READ;
import static de.MCmoderSD.helix.enums.Scope.CHANNEL_READ_EDITORS;
import static de.MCmoderSD.helix.enums.Scope.CHANNEL_READ_VIPS;
import static de.MCmoderSD.helix.enums.Scope.CHANNEL_READ_SUBSCRIPTIONS;
import static de.MCmoderSD.helix.enums.Scope.MODERATOR_READ_FOLLOWERS;
import static de.MCmoderSD.helix.enums.Scope.CHANNEL_MANAGE_MODERATORS;
import static de.MCmoderSD.helix.enums.Scope.CHANNEL_MANAGE_VIPS;

@SuppressWarnings("unused")
public class RoleHandler extends Handler {

    // Constants
    public static final Scope[] REQUIRED_SCOPES = {
            MODERATION_READ,                // Moderators
            CHANNEL_READ_EDITORS,           // Editors
            CHANNEL_READ_VIPS,              // VIPs
            CHANNEL_READ_SUBSCRIPTIONS,     // Subscribers
            MODERATOR_READ_FOLLOWERS,       // Followers
            CHANNEL_MANAGE_MODERATORS,      // Add/Remove Moderators
            CHANNEL_MANAGE_VIPS             // Add/Remove VIPs
    };

    // Cache
    private final ConcurrentHashMap<Integer, HashSet<ChannelModerator>> moderatorCache;
    private final ConcurrentHashMap<Integer, HashSet<ChannelVip>> vipCache;
    private final ConcurrentHashMap<Integer, HashSet<ChannelSubscriber>> subscriberCache;
    private final ConcurrentHashMap<Integer, HashSet<ChannelFollower>> followerCache;

    // Constructor
    public RoleHandler(TwitchHelix helix, TokenHandler tokenHandler) {

        // Call super constructor
        super(helix, tokenHandler);

        // Initialize caches
        moderatorCache = new ConcurrentHashMap<>();
        vipCache = new ConcurrentHashMap<>();
        subscriberCache = new ConcurrentHashMap<>();
        followerCache = new ConcurrentHashMap<>();

        // Initialize caches
        tokenHandler.getAuthTokens().forEach((id, authToken) -> new Thread(() -> {

            // Initialize empty cache
            if (authToken.hasScope(MODERATION_READ)) moderatorCache.put(id, getModerators(id));                 // Moderators
            if (authToken.hasScope(CHANNEL_READ_VIPS)) vipCache.put(id, getVIPs(id));                           // VIPs
            if (authToken.hasScope(CHANNEL_READ_SUBSCRIPTIONS)) subscriberCache.put(id, getSubscribers(id));    // Subscribers
            if (authToken.hasScope(MODERATOR_READ_FOLLOWERS)) followerCache.put(id, getFollowers(id));          // Followers

        }).start());
    }










    private HashSet<ChannelModerator> getModerators(TwitchUser channel, String accessToken, @Nullable String cursor) {

        // Check Parameters
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");
        if (accessToken == null || accessToken.isBlank()) throw new IllegalArgumentException("Access Token cannot be null or blank");

        // Get ID
        var id = channel.getId();

        // Get moderators
        var moderatorList = helix.getModerators(accessToken, id.toString(), null, cursor, LIMIT).execute();

        // Null check
        if (moderatorList == null) throw new IllegalStateException("Failed to get moderators for channel ID: " + id);
        var moderators = moderatorList.getModerators();
        if (moderators == null) throw new IllegalStateException("Failed to get moderators for channel ID: " + id);
        if (moderators.isEmpty()) return new HashSet<>(); // No moderators found

        // Check if cache is up to date
        HashSet<ChannelModerator> cache = moderatorCache.get(id);
        if (cursor == null && cache != null) {
            HashSet<String> cachedIds = new HashSet<>();
            HashSet<String> fetchedIds = new HashSet<>();
            cache.forEach(moderator -> cachedIds.add(moderator.getId().toString()));
            moderators.forEach(moderator -> fetchedIds.add(moderator.getUserId()));
            if (cachedIds.containsAll(fetchedIds)) return null; // Cache is up to date
        }

        // Convert to ChannelModerator HashSet
        HashSet<Integer> moderatorIds = new HashSet<>();
        for (var moderator : moderators) moderatorIds.add(Integer.parseInt(moderator.getUserId()));
        HashMap<Integer, User> userMap = getUsersByIDsMap(moderatorIds);
        HashSet<ChannelModerator> fetchedModerators = new HashSet<>();
        for (var moderator : moderators) fetchedModerators.add(new ChannelModerator(userMap.get(Integer.parseInt(moderator.getUserId())), channel));

        // Check if there are more moderators
        HelixPagination pagination = moderatorList.getPagination();
        String nextCursor = pagination != null ? pagination.getCursor() : null;
        if (nextCursor != null) fetchedModerators.addAll(Objects.requireNonNull(getModerators(channel, accessToken, nextCursor)));

        // Return moderators
        return fetchedModerators;
    }

    public HashSet<ChannelModerator> getModerators(TwitchUser channel) {

        // Check Parameters
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");

        // Get Access Token
        var id = channel.getId();
        AuthToken authToken = tokenHandler.getAuthToken(id);

        // Check AuthToken
        if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");
        if (!authToken.hasScope(MODERATION_READ)) throw new IllegalArgumentException("AuthToken does not have the required scope: " + MODERATION_READ.getScope());

        // Get moderators
        HashSet<ChannelModerator> moderators = getModerators(channel, authToken.getAccessToken(), null);
        if (moderators == null) return moderatorCache.get(id);  // Cache is up to date
        if (moderators.isEmpty()) return new HashSet<>();       // No moderators found

        // Update cache
        moderatorCache.put(id, moderators);

        // Return moderators
        return moderators;
    }

    public HashSet<ChannelModerator> getModerators(Integer channel) {
        return getModerators(new TwitchUser(getUser(channel)));
    }

    public HashSet<ChannelModerator> getModerators(String channel) {
        return getModerators(new TwitchUser(getUser(channel)));
    }





    public HashSet<ChannelEditor> getEditors(TwitchUser channel) {

        // Check Parameters
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");

        // Get Access Token
        var id = channel.getId();
        AuthToken authToken = tokenHandler.getAuthToken(id);

        // Check AuthToken
        if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");
        if (!authToken.hasScope(CHANNEL_READ_EDITORS)) throw new IllegalArgumentException("AuthToken does not have the required scope: " + CHANNEL_READ_EDITORS.getScope());

        // Get editors
        var editorList = helix.getChannelEditors(authToken.getAccessToken(), id.toString()).execute();

        // Null check
        if (editorList == null) throw new IllegalStateException("Failed to get editors for channel ID: " + id);
        var editors = editorList.getEditors();
        if (editors == null) throw new IllegalStateException("Failed to get editors for channel ID: " + id);
        if (editors.isEmpty()) return new HashSet<>();

        // Convert to ChannelEditor HashSet
        HashSet<Integer> editorIds = new HashSet<>();
        for (var editor : editors) editorIds.add(Integer.parseInt(editor.getUserId()));
        HashMap<Integer, User> users = getUsersByIDsMap(editorIds);
        HashSet<ChannelEditor> channelEditors = new HashSet<>();
        for (var editor : editors) channelEditors.add(new ChannelEditor(
                users.get(Integer.parseInt(editor.getUserId())),    // Editor
                channel                                             // Channel
        ));

        // Return editors
        return channelEditors;
    }

    public HashSet<ChannelEditor> getEditors(Integer channel) {
        return getEditors(new TwitchUser(getUser(channel)));
    }

    public HashSet<ChannelEditor> getEditors(String channel) {
        return getEditors(new TwitchUser(getUser(channel)));
    }





    private HashSet<ChannelVip> getVIPs(TwitchUser channel, String accessToken, @Nullable String cursor) {

        // Check Parameters
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");
        if (accessToken == null || accessToken.isBlank()) throw new IllegalArgumentException("Access Token cannot be null or blank");

        // Get ID
        var id = channel.getId();

        // Get VIPs
        var vipList = helix.getChannelVips(accessToken, id.toString(), null, LIMIT, cursor).execute();

        // Null check
        if (vipList == null) throw new IllegalStateException("Failed to get VIPs for channel ID: " + id);
        var vips = vipList.getData();
        if (vips == null) throw new IllegalStateException("Failed to get VIPs for channel ID: " + id);
        if (vips.isEmpty()) return new HashSet<>();

        // Check if cache is up to date
        HashSet<ChannelVip> cache = vipCache.get(id);
        if (cursor == null && cache != null) {
            HashSet<String> cachedIds = new HashSet<>();
            HashSet<String> fetchedIds = new HashSet<>();
            cache.forEach(vip -> cachedIds.add(vip.getId().toString()));
            vips.forEach(vip -> fetchedIds.add(vip.getUserId()));
            if (cachedIds.containsAll(fetchedIds)) return null; // Cache is up to date
        }

        // Convert to ChannelVip HashSet
        HashSet<Integer> vipIds = new HashSet<>();
        for (var vip : vips) vipIds.add(Integer.parseInt(vip.getUserId()));
        HashMap<Integer, User> userMap = getUsersByIDsMap(vipIds);
        HashSet<ChannelVip> fetchedVips = new HashSet<>();
        for (var vip : vips) fetchedVips.add(new ChannelVip(
                userMap.get(Integer.parseInt(vip.getUserId())),     // VIP
                channel                                             // Channel
        ));

        // Check if there are more VIPs
        HelixPagination pagination = vipList.getPagination();
        String nextCursor = pagination != null ? pagination.getCursor() : null;
        if (nextCursor != null) fetchedVips.addAll(Objects.requireNonNull(getVIPs(channel, accessToken, nextCursor)));

        // Return VIPs
        return fetchedVips;
    }

    public HashSet<ChannelVip> getVIPs(TwitchUser channel) {

        // Check Parameters
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");

        // Get Access Token
        var id = channel.getId();
        AuthToken authToken = tokenHandler.getAuthToken(id);

        // Check AuthToken
        if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");
        if (!authToken.hasScope(CHANNEL_READ_VIPS)) throw new IllegalArgumentException("AuthToken does not have the required scope: " + CHANNEL_READ_VIPS.getScope());

        // Get VIPs
        HashSet<ChannelVip> vips = getVIPs(channel, authToken.getAccessToken(), null);
        if (vips == null) return vipCache.get(id);      // Cache is up to date
        if (vips.isEmpty()) return new HashSet<>();     // No VIPs found

        // Update cache
        vipCache.put(id, vips);

        // Return VIPs
        return vips;
    }

    public HashSet<ChannelVip> getVIPs(Integer channel) {
        return getVIPs(new TwitchUser(getUser(channel)));
    }

    public HashSet<ChannelVip> getVIPs(String channel) {
        return getVIPs(new TwitchUser(getUser(channel)));
    }





    private HashSet<ChannelSubscriber> getSubscribers(TwitchUser channel, String accessToken, @Nullable String cursor) {

        // Check Parameters
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");
        if (accessToken == null || accessToken.isBlank()) throw new IllegalArgumentException("Access Token cannot be null or blank");

        // Get ID
        var id = channel.getId();

        // Get Subscribers
        var subscriptionList = helix.getSubscriptions(accessToken, id.toString(), cursor, null, LIMIT).execute();

        // Null check
        if (subscriptionList == null) throw new IllegalStateException("Failed to get subscribers for channel ID: " + id);
        var subscriptions = subscriptionList.getSubscriptions();
        if (subscriptions == null) throw new IllegalStateException("Failed to get subscribers for channel ID: " + id);
        if (subscriptions.isEmpty()) return new HashSet<>();

        // Check if cache is up to date
        HashSet<ChannelSubscriber> cache = subscriberCache.get(id);
        if (cursor == null && cache != null) {
            HashSet<String> cachedIds = new HashSet<>();
            HashSet<String> fetchedIds = new HashSet<>();
            cache.forEach(subscriber -> cachedIds.add(subscriber.getId().toString()));
            subscriptions.forEach(subscriber -> fetchedIds.add(subscriber.getUserId()));
            if (cachedIds.containsAll(fetchedIds)) return null; // Cache is up to date
        }

        // Convert to ChannelSubscriber HashSet
        HashSet<Integer> subscriberIds = new HashSet<>();
        for (var subscription : subscriptions) {
            subscriberIds.add(Integer.parseInt(subscription.getUserId()));
            if (subscription.getIsGift()) subscriberIds.add(Integer.parseInt(subscription.getGifterId()));
        }
        HashMap<Integer, User> userMap = getUsersByIDsMap(subscriberIds);
        HashSet<ChannelSubscriber> fetchedChannelSubscribers = new HashSet<>();
        for (var subscription : subscriptions) fetchedChannelSubscribers.add(new ChannelSubscriber(
                subscription,                                                                                   // Subscription
                userMap.get(Integer.parseInt(subscription.getUserId())),                                        // Subscriber
                channel,                                                                                        // Channel
                subscription.getIsGift() ? userMap.get(Integer.parseInt(subscription.getGifterId())) : null     // Gifter (if gifted)
        ));

        // Check if there are more Subscribers
        HelixPagination pagination = subscriptionList.getPagination();
        String nextCursor = pagination != null ? pagination.getCursor() : null;
        if (nextCursor != null) fetchedChannelSubscribers.addAll(Objects.requireNonNull(getSubscribers(channel, accessToken, nextCursor)));

        // Return Subscribers
        return fetchedChannelSubscribers;
    }

    public HashSet<ChannelSubscriber> getSubscribers(TwitchUser channel) {

        // Check Parameters
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");

        // Get Access Token
        var id = channel.getId();
        AuthToken authToken = tokenHandler.getAuthToken(id);

        // Check AuthToken
        if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");
        if (!authToken.hasScope(CHANNEL_READ_SUBSCRIPTIONS)) throw new IllegalArgumentException("AuthToken does not have the required scope: " + CHANNEL_READ_SUBSCRIPTIONS.getScope());

        // Get Subscribers
        HashSet<ChannelSubscriber> subscribers = getSubscribers(channel, authToken.getAccessToken(), null);
        if (subscribers == null) return subscriberCache.get(id);  // Cache is up to date
        if (subscribers.isEmpty()) return new HashSet<>();        // No Subscribers found

        // Update cache
        subscriberCache.put(id, subscribers);

        // Return Subscribers
        return subscribers;
    }

    public HashSet<ChannelSubscriber> getSubscribers(Integer channel) {
        return getSubscribers(new TwitchUser(getUser(channel)));
    }

    public HashSet<ChannelSubscriber> getSubscribers(String channel) {
        return getSubscribers(new TwitchUser(getUser(channel)));
    }





    private HashSet<ChannelFollower> getFollowers(TwitchUser channel, String accessToken, @Nullable String cursor) {

        // Check Parameters
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");
        if (accessToken == null || accessToken.isBlank()) throw new IllegalArgumentException("Access Token cannot be null or blank");

        // Get ID
        var id = channel.getId();

        // Get Followers
        var inboundFollowers = helix.getChannelFollowers(accessToken, id.toString(), null, LIMIT, cursor).execute();

        // Null check
        if (inboundFollowers == null) throw new IllegalStateException("Failed to get followers for channel ID: " + id);
        var follows = inboundFollowers.getFollows();
        if (follows == null) throw new IllegalStateException("Failed to get followers for channel ID: " + id);
        if (follows.isEmpty()) return new HashSet<>();

        // Check if cache is up to date
        HashSet<ChannelFollower> cache = followerCache.get(id);
        if (cursor == null && cache != null) {
            HashSet<String> cachedIds = new HashSet<>();
            HashSet<String> fetchedIds = new HashSet<>();
            cache.forEach(follower -> cachedIds.add(follower.getId().toString()));
            follows.forEach(follower -> fetchedIds.add(follower.getUserId()));
            if (cachedIds.containsAll(fetchedIds)) return null; // Cache is up to date
        }

        // Convert to ChannelFollower HashSet
        HashSet<Integer> followerIds = new HashSet<>();
        for (var follow : follows) followerIds.add(Integer.parseInt(follow.getUserId()));
        HashMap<Integer, User> userMap = getUsersByIDsMap(followerIds);
        HashSet<ChannelFollower> fetchedFollowers = new HashSet<>();
        for (var follow : follows) fetchedFollowers.add(new ChannelFollower(
                follow,                                             // Follow
                userMap.get(Integer.parseInt(follow.getUserId())),  // Follower
                channel                                             // Channel
        ));

        // Check if there are more Followers
        HelixPagination pagination = inboundFollowers.getPagination();
        String nextCursor = pagination != null ? pagination.getCursor() : null;
        if (nextCursor != null) fetchedFollowers.addAll(Objects.requireNonNull(getFollowers(channel, accessToken, nextCursor)));

        // Return Followers
        return fetchedFollowers;
    }

    public HashSet<ChannelFollower> getFollowers(TwitchUser channel) {

        // Check Parameters
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");

        // Get Access Token
        var id = channel.getId();
        AuthToken authToken = tokenHandler.getAuthToken(id);

        // Check AuthToken
        if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");
        if (!authToken.hasScope(MODERATOR_READ_FOLLOWERS)) throw new IllegalArgumentException("AuthToken does not have the required scope: " + MODERATOR_READ_FOLLOWERS.getScope());

        // Get Followers
        HashSet<ChannelFollower> followers = getFollowers(channel, authToken.getAccessToken(), null);
        if (followers == null) return followerCache.get(id);    // Cache is up to date
        if (followers.isEmpty()) return new HashSet<>();        // No Followers found

        // Update cache
        followerCache.put(id, followers);

        // Return Followers
        return followers;
    }

    public HashSet<ChannelFollower> getFollowers(Integer channel) {
        return getFollowers(new TwitchUser(getUser(channel)));
    }

    public HashSet<ChannelFollower> getFollowers(String channel) {
        return getFollowers(new TwitchUser(getUser(channel)));
    }










    private HashMap<TwitchUser, Boolean> checkModerators(HashSet<TwitchUser> users, TwitchUser channel, String accessToken) {

        //  Check Parameters
        if (users == null || users.isEmpty()) throw new IllegalArgumentException("Users cannot be null or empty");
        for (var user : users) if (user == null) throw new IllegalArgumentException("User in Users cannot be null");
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");
        if (accessToken == null || accessToken.isBlank()) throw new IllegalArgumentException("Access Token cannot be null or blank");

        // Variables
        var id = channel.getId().toString();
        HashMap<TwitchUser, Boolean> userMap = new HashMap<>();

        // Get moderators
        var moderatorList = helix.getModerators(accessToken, id, users.stream().map(user -> user.getId().toString()).toList(), null, LIMIT).execute();

        // Null check
        if (moderatorList == null) throw new IllegalStateException("Failed to get moderators for channel ID: " + id);
        var moderators = moderatorList.getModerators();
        if (moderators == null) throw new IllegalStateException("Failed to get moderators for channel ID: " + id);
        if (moderators.isEmpty()) { // None of the users are moderators
            for (var user : users) userMap.put(user, false);
            return userMap;
        }

        // Mark found moderators
        HashSet<Integer> moderatorIds = new HashSet<>(moderators.stream().map(moderator -> Integer.parseInt(moderator.getUserId())).toList());
        for (var user : users) {
            if (moderatorIds.contains(user.getId())) userMap.put(user, true);
            else userMap.put(user, false);
        }

        // Return moderators
        return userMap;
    }

    public HashMap<TwitchUser, Boolean> checkModerators(HashSet<TwitchUser> users, TwitchUser channel) {

        // Check Parameters
        if (users == null || users.isEmpty()) throw new IllegalArgumentException("Users cannot be null or empty");
        for (var user : users) if (user == null) throw new IllegalArgumentException("User in Users cannot be null");
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");

        // Get size
        var size = users.size();
        if (size == 1 && users.iterator().next().equals(channel)) { // Broadcaster cannot be moderator
            HashMap<TwitchUser, Boolean> userMap = new HashMap<>();
            userMap.put(users.iterator().next(), false);
            return userMap;
        }

        // Get Access Token
        var id = channel.getId();
        AuthToken authToken = tokenHandler.getAuthToken(id);

        // Check AuthToken
        if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");
        if (!authToken.hasScope(MODERATION_READ)) throw new IllegalArgumentException("AuthToken does not have the required scope: " + MODERATION_READ.getScope());

        // Check size and chunk
        if (size > LIMIT) {
            HashMap<TwitchUser, Boolean> userMap = new HashMap<>();
            for (var i = 0; i < size; i += LIMIT) userMap.putAll(checkModerators(new HashSet<>(users.stream().toList().subList(i, Math.min(i + LIMIT, size))), channel, authToken.getAccessToken()));
            return userMap;
        } else return checkModerators(users, channel, authToken.getAccessToken());
    }

    public HashMap<TwitchUser, Boolean> checkModerators(HashSet<TwitchUser> users, Integer channel) {
        return checkModerators(users, new TwitchUser(getUser(channel)));
    }

    public HashMap<TwitchUser, Boolean> checkModerators(HashSet<TwitchUser> users, String channel) {
        return checkModerators(users, new TwitchUser(getUser(channel)));
    }

    public boolean isModerator(TwitchUser user, TwitchUser channel) {
        return checkModerators(new HashSet<>(Collections.singletonList(user)), channel).get(user);
    }

    public boolean isModerator(TwitchUser user, Integer channel) {
        return isModerator(user, new TwitchUser(getUser(channel)));
    }

    public boolean isModerator(TwitchUser user, String channel) {
        return isModerator(user, new TwitchUser(getUser(channel)));
    }

    public boolean isModerator(Integer user, TwitchUser channel) {
        return isModerator(new TwitchUser(getUser(user)), channel);
    }

    public boolean isModerator(String user, TwitchUser channel) {
        return isModerator(new TwitchUser(getUser(user)), channel);
    }

    public boolean isModerator(Integer user, String channel) {
        return isModerator(new TwitchUser(getUser(user)), new TwitchUser(getUser(channel)));
    }

    public boolean isModerator(String user, Integer channel) {
        return isModerator(new TwitchUser(getUser(user)), new TwitchUser(getUser(channel)));
    }





    public HashMap<TwitchUser, Boolean> checkEditors(HashSet<TwitchUser> users, TwitchUser channel) {

        // Check Parameters
        if (users == null || users.isEmpty()) throw new IllegalArgumentException("Users cannot be null or empty");
        for (var user : users) if (user == null) throw new IllegalArgumentException("User in Users cannot be null");
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");

        // Check if channel is in users
        if (users.size() == 1 && users.iterator().next().equals(channel)) { // Broadcaster cannot be editor
            HashMap<TwitchUser, Boolean> userMap = new HashMap<>();
            userMap.put(users.iterator().next(), false);
            return userMap;
        }

        // Variables
        HashMap<TwitchUser, Boolean> userMap = new HashMap<>();

        // Get Editors
        HashSet<ChannelEditor> editors = getEditors(channel);

        // Mark found editors
        HashSet<Integer> editorIds = new HashSet<>(editors.stream().map(TwitchUser::getId).toList());
        for (var user : users) {
            if (editorIds.contains(user.getId())) userMap.put(user, true);
            else userMap.put(user, false);
        }

        // Return Editors
        return userMap;
    }

    public HashMap<TwitchUser, Boolean> checkEditors(HashSet<TwitchUser> users, Integer channel) {
        return checkEditors(users, new TwitchUser(getUser(channel)));
    }

    public HashMap<TwitchUser, Boolean> checkEditors(HashSet<TwitchUser> users, String channel) {
        return checkEditors(users, new TwitchUser(getUser(channel)));
    }

    public boolean isEditor(TwitchUser user, TwitchUser channel) {
        return checkEditors(new HashSet<>(Collections.singletonList(user)), channel).get(user);
    }

    public boolean isEditor(TwitchUser user, Integer channel) {
        return isEditor(user, new TwitchUser(getUser(channel)));
    }

    public boolean isEditor(TwitchUser user, String channel) {
        return isEditor(user, new TwitchUser(getUser(channel)));
    }

    public boolean isEditor(Integer user, TwitchUser channel) {
        return isEditor(new TwitchUser(getUser(user)), channel);
    }

    public boolean isEditor(String user, TwitchUser channel) {
        return isEditor(new TwitchUser(getUser(user)), channel);
    }

    public boolean isEditor(Integer user, String channel) {
        return isEditor(new TwitchUser(getUser(user)), new TwitchUser(getUser(channel)));
    }

    public boolean isEditor(String user, Integer channel) {
        return isEditor(new TwitchUser(getUser(user)), new TwitchUser(getUser(channel)));
    }





    private HashMap<TwitchUser, Boolean> checkVIPs(HashSet<TwitchUser> users, TwitchUser channel, String accessToken) {

        //  Check Parameters
        if (users == null || users.isEmpty()) throw new IllegalArgumentException("Users cannot be null or empty");
        for (var user : users) if (user == null) throw new IllegalArgumentException("User in Users cannot be null");
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");
        if (accessToken == null || accessToken.isBlank()) throw new IllegalArgumentException("Access Token cannot be null or blank");

        // Variables
        var id = channel.getId().toString();
        HashMap<TwitchUser, Boolean> userMap = new HashMap<>();

        // Get vips
        var vipList = helix.getChannelVips(accessToken, id, users.stream().map(user -> user.getId().toString()).toList(), LIMIT, null).execute();

        // Null check
        if (vipList == null) throw new IllegalStateException("Failed to get vips for channel ID: " + id);
        var vips = vipList.getData();
        if (vips == null) throw new IllegalStateException("Failed to get vips for channel ID: " + id);
        if (vips.isEmpty()) { // None of the users are vips
            for (var user : users) userMap.put(user, false);
            return userMap;
        }

        // Mark found vips
        HashSet<Integer> vipIds = new HashSet<>(vips.stream().map(vip -> Integer.parseInt(vip.getUserId())).toList());
        for (var user : users) {
            if (vipIds.contains(user.getId())) userMap.put(user, true);
            else userMap.put(user, false);
        }

        // Return vips
        return userMap;
    }

    public HashMap<TwitchUser, Boolean> checkVIPs(HashSet<TwitchUser> users, TwitchUser channel) {

        // Check Parameters
        if (users == null || users.isEmpty()) throw new IllegalArgumentException("Users cannot be null or empty");
        for (var user : users) if (user == null) throw new IllegalArgumentException("User in Users cannot be null");
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");

        // Get size
        var size = users.size();
        if (size == 1 && users.iterator().next().equals(channel)) { // Broadcaster cannot be VIP
            HashMap<TwitchUser, Boolean> userMap = new HashMap<>();
            userMap.put(users.iterator().next(), false);
            return userMap;
        }

        // Get Access Token
        var id = channel.getId();
        AuthToken authToken = tokenHandler.getAuthToken(id);

        // Check AuthToken
        if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");
        if (!authToken.hasScope(CHANNEL_READ_VIPS)) throw new IllegalArgumentException("AuthToken does not have the required scope: " + CHANNEL_READ_VIPS.getScope());

        // Check size and chunk
        if (size > LIMIT) {
            HashMap<TwitchUser, Boolean> userMap = new HashMap<>();
            for (var i = 0; i < size; i += LIMIT) userMap.putAll(checkVIPs(new HashSet<>(users.stream().toList().subList(i, Math.min(i + LIMIT, size))), channel, authToken.getAccessToken()));
            return userMap;
        } else return checkVIPs(users, channel, authToken.getAccessToken());
    }

    public HashMap<TwitchUser, Boolean> checkVIPs(HashSet<TwitchUser> users, Integer channel) {
        return checkVIPs(users, new TwitchUser(getUser(channel)));
    }

    public HashMap<TwitchUser, Boolean> checkVIPs(HashSet<TwitchUser> users, String channel) {
        return checkVIPs(users, new TwitchUser(getUser(channel)));
    }

    public boolean isVIP(TwitchUser user, TwitchUser channel) {
        return checkVIPs(new HashSet<>(Collections.singletonList(user)), channel).get(user);
    }

    public boolean isVIP(TwitchUser user, Integer channel) {
        return isVIP(user, new TwitchUser(getUser(channel)));
    }

    public boolean isVIP(TwitchUser user, String channel) {
        return isVIP(user, new TwitchUser(getUser(channel)));
    }

    public boolean isVIP(Integer user, TwitchUser channel) {
        return isVIP(new TwitchUser(getUser(user)), channel);
    }

    public boolean isVIP(String user, TwitchUser channel) {
        return isVIP(new TwitchUser(getUser(user)), channel);
    }

    public boolean isVIP(Integer user, String channel) {
        return isVIP(new TwitchUser(getUser(user)), new TwitchUser(getUser(channel)));
    }

    public boolean isVIP(String user, Integer channel) {
        return isVIP(new TwitchUser(getUser(user)), new TwitchUser(getUser(channel)));
    }





    public HashMap<TwitchUser, Boolean> checkSubscribers(HashSet<TwitchUser> users, TwitchUser channel) {

        // Check Parameters
        if (users == null || users.isEmpty()) throw new IllegalArgumentException("Users cannot be null or empty");
        for (var user : users) if (user == null) throw new IllegalArgumentException("User in Users cannot be null");
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");

        // Variables
        HashMap<TwitchUser, Boolean> userMap = new HashMap<>();

        // Get Subscribers
        HashSet<ChannelSubscriber> subscribers = getSubscribers(channel);

        // Mark found subscribers
        HashSet<Integer> subscriberIds = new HashSet<>(subscribers.stream().map(TwitchUser::getId).toList());
        for (var user : users) {
            if (subscriberIds.contains(user.getId())) userMap.put(user, true);
            else userMap.put(user, false);
        }

        // Return Subscribers
        return userMap;
    }

    public HashMap<TwitchUser, Boolean> checkSubscribers(HashSet<TwitchUser> users, Integer channel) {
        return checkSubscribers(users, new TwitchUser(getUser(channel)));
    }

    public HashMap<TwitchUser, Boolean> checkSubscribers(HashSet<TwitchUser> users, String channel) {
        return checkSubscribers(users, new TwitchUser(getUser(channel)));
    }

    public boolean isSubscriber(TwitchUser user, TwitchUser channel) {
        return checkSubscribers(new HashSet<>(Collections.singletonList(user)), channel).get(user);
    }

    public boolean isSubscriber(TwitchUser user, Integer channel) {
        return isSubscriber(user, new TwitchUser(getUser(channel)));
    }

    public boolean isSubscriber(TwitchUser user, String channel) {
        return isSubscriber(user, new TwitchUser(getUser(channel)));
    }

    public boolean isSubscriber(Integer user, TwitchUser channel) {
        return isSubscriber(new TwitchUser(getUser(user)), channel);
    }

    public boolean isSubscriber(String user, TwitchUser channel) {
        return isSubscriber(new TwitchUser(getUser(user)), channel);
    }

    public boolean isSubscriber(Integer user, String channel) {
        return isSubscriber(new TwitchUser(getUser(user)), new TwitchUser(getUser(channel)));
    }

    public boolean isSubscriber(String user, Integer channel) {
        return isSubscriber(new TwitchUser(getUser(user)), new TwitchUser(getUser(channel)));
    }





    public HashMap<TwitchUser, Boolean> checkFollowers(HashSet<TwitchUser> users, TwitchUser channel) {

        // Check Parameters
        if (users == null || users.isEmpty()) throw new IllegalArgumentException("Users cannot be null or empty");
        for (var user : users) if (user == null) throw new IllegalArgumentException("User in Users cannot be null");
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");

        // Check if channel is in users
        if (users.size() == 1 && users.iterator().next().equals(channel)) { // Broadcaster cannot be follower
            HashMap<TwitchUser, Boolean> userMap = new HashMap<>();
            userMap.put(users.iterator().next(), false);
            return userMap;
        }

        // Variables
        HashMap<TwitchUser, Boolean> userMap = new HashMap<>();

        // Get Subscribers
        HashSet<ChannelFollower> followers = getFollowers(channel);

        // Mark found followers
        HashSet<Integer> followerIds = new HashSet<>(followers.stream().map(TwitchUser::getId).toList());
        for (var user : users) {
            if (followerIds.contains(user.getId())) userMap.put(user, true);
            else userMap.put(user, false);
        }

        // Return Subscribers
        return userMap;
    }

    public HashMap<TwitchUser, Boolean> checkFollowers(HashSet<TwitchUser> users, Integer channel) {
        return checkFollowers(users, new TwitchUser(getUser(channel)));
    }

    public HashMap<TwitchUser, Boolean> checkFollowers(HashSet<TwitchUser> users, String channel) {
        return checkFollowers(users, new TwitchUser(getUser(channel)));
    }

    public boolean isFollower(TwitchUser user, TwitchUser channel) {

        // Check Parameters
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");

        // Check if user is channel
        if (user.equals(channel)) return false;

        // Get Access Token
        var id = channel.getId();
        var userId = user.getId().toString();
        AuthToken authToken = tokenHandler.getAuthToken(id);

        // Check AuthToken
        if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");
        if (!authToken.hasScope(MODERATOR_READ_FOLLOWERS)) throw new IllegalArgumentException("AuthToken does not have the required scope: " + MODERATOR_READ_FOLLOWERS.getScope());

        // Get Followers
        var inboundFollowers = helix.getChannelFollowers(authToken.getAccessToken(), id.toString(), userId, 1, null).execute();

        // Null check
        if (inboundFollowers == null) throw new IllegalStateException("Failed to get followers for channel ID: " + id);
        var follows = inboundFollowers.getFollows();
        if (follows == null) throw new IllegalStateException("Failed to get followers for channel ID: " + id);
        if (follows.isEmpty()) return false;
        if (follows.size() > 1) throw new IllegalStateException("Received multiple follow records for user ID: " + userId + " and channel ID: " + id);

        // Return if user is follower
        return userId.equals(follows.getFirst().getUserId());
    }

    public boolean isFollower(TwitchUser user, Integer channel) {
        return isFollower(user, new TwitchUser(getUser(channel)));
    }

    public boolean isFollower(TwitchUser user, String channel) {
        return isFollower(user, new TwitchUser(getUser(channel)));
    }

    public boolean isFollower(Integer user, TwitchUser channel) {
        return isFollower(new TwitchUser(getUser(user)), channel);
    }

    public boolean isFollower(String user, TwitchUser channel) {
        return isFollower(new TwitchUser(getUser(user)), channel);
    }

    public boolean isFollower(Integer user, String channel) {
        return isFollower(new TwitchUser(getUser(user)), new TwitchUser(getUser(channel)));
    }

    public boolean isFollower(String user, Integer channel) {
        return isFollower(new TwitchUser(getUser(user)), new TwitchUser(getUser(channel)));
    }










    // Manage Roles
    public boolean addModerator(TwitchUser user, TwitchUser channel) {

        // Check Parameters
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");

        // Check if user is already a moderator
        if (isModerator(user, channel)) return false;

        // Check if user is VIP
        if (isVIP(user, channel) && !removeVIP(user, channel)) return false;

        // Get Access Token
        var id = channel.getId();
        AuthToken authToken = tokenHandler.getAuthToken(id);

        // Check AuthToken
        if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");
        if (!authToken.hasScope(CHANNEL_MANAGE_MODERATORS)) throw new IllegalArgumentException("AuthToken does not have the required scope: " + CHANNEL_MANAGE_MODERATORS.getScope());

        // Add moderator
        helix.addChannelModerator(authToken.getAccessToken(), id.toString(), user.getId().toString()).execute();

        // Check if moderator was added
        return isModerator(user, channel);
    }

    public boolean removeModerator(TwitchUser user, TwitchUser channel) {

        // Check Parameters
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");

        // Check if user is not a moderator
        if (!isModerator(user, channel)) return false;

        // Get Access Token
        var id = channel.getId();
        AuthToken authToken = tokenHandler.getAuthToken(id);

        // Check AuthToken
        if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");
        if (!authToken.hasScope(CHANNEL_MANAGE_MODERATORS)) throw new IllegalArgumentException("AuthToken does not have the required scope: " + CHANNEL_MANAGE_MODERATORS.getScope());

        // Remove moderator
        helix.removeChannelModerator(authToken.getAccessToken(), id.toString(), user.getId().toString()).execute();

        // Check if moderator was removed
        return !isModerator(user, channel);
    }

    public boolean addVIP(TwitchUser user, TwitchUser channel) {

        // Check Parameters
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");

        // Check if user is already a VIP
        if (isVIP(user, channel)) return false;

        // Check if user is moderator
        if (isModerator(user, channel) && !removeModerator(user, channel)) return false;

        // Get Access Token
        var id = channel.getId();
        AuthToken authToken = tokenHandler.getAuthToken(id);

        // Check AuthToken
        if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");
        if (!authToken.hasScope(CHANNEL_MANAGE_VIPS)) throw new IllegalArgumentException("AuthToken does not have the required scope: " + CHANNEL_MANAGE_VIPS.getScope());

        // Add VIP
        helix.addChannelVip(authToken.getAccessToken(), id.toString(), user.getId().toString()).execute();

        // Check if VIP was added
        return isVIP(user, channel);
    }

    public boolean removeVIP(TwitchUser user, TwitchUser channel) {

        // Check Parameters
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");

        // Check if user is not a VIP
        if (!isVIP(user, channel)) return false;

        // Get Access Token
        var id = channel.getId();
        AuthToken authToken = tokenHandler.getAuthToken(id);

        // Check AuthToken
        if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");
        if (!authToken.hasScope(CHANNEL_MANAGE_VIPS)) throw new IllegalArgumentException("AuthToken does not have the required scope: " + CHANNEL_MANAGE_VIPS.getScope());

        // Remove VIP
        helix.removeChannelVip(authToken.getAccessToken(), id.toString(), user.getId().toString()).execute();

        // Check if VIP was removed
        return !isVIP(user, channel);
    }










    // Get Caches
    public HashMap<Integer, HashSet<ChannelModerator>> getModeratorCache() {
        return new HashMap<>(moderatorCache);
    }

    public HashMap<Integer, HashSet<ChannelVip>> getVIPCache() {
        return new HashMap<>(vipCache);
    }

    public HashMap<Integer, HashSet<ChannelSubscriber>> getSubscriberCache() {
        return new HashMap<>(subscriberCache);
    }

    public HashMap<Integer, HashSet<ChannelFollower>> getFollowerCache() {
        return new HashMap<>(followerCache);
    }

    // Clear Caches
    public void clearModeratorCache() {
        moderatorCache.clear();
    }

    public void clearVIPCache() {
        vipCache.clear();
    }

    public void clearSubscriberCache() {
        subscriberCache.clear();
    }

    public void clearFollowerCache() {
        followerCache.clear();
    }

    public void clearCaches() {
        clearModeratorCache();
        clearVIPCache();
        clearSubscriberCache();
        clearFollowerCache();
    }
}