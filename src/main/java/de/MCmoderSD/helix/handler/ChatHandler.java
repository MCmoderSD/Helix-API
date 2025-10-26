package de.MCmoderSD.helix.handler;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.Chatter;
import com.github.twitch4j.helix.domain.HelixPagination;
import com.github.twitch4j.helix.domain.User;

import de.MCmoderSD.helix.core.TokenHandler;
import de.MCmoderSD.helix.enums.Scope;
import de.MCmoderSD.helix.objects.AuthToken;
import de.MCmoderSD.helix.objects.TwitchUser;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import static de.MCmoderSD.helix.enums.Scope.MODERATOR_READ_CHATTERS;
import static de.MCmoderSD.helix.enums.Scope.MODERATOR_MANAGE_CHAT_MESSAGES;

@SuppressWarnings("unused")
public class ChatHandler extends Handler {

    // Constants
    public static final Scope[] REQUIRED_SCOPES = {
            MODERATOR_READ_CHATTERS,
            MODERATOR_MANAGE_CHAT_MESSAGES
    };

    // Constructor
    public ChatHandler(TwitchHelix helix, TokenHandler tokenHandler) {
        super(helix, tokenHandler);
    }






    private HashSet<TwitchUser> getChatters(TwitchUser channel, String accessToken, @Nullable String cursor) {

        // Check Parameters
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");
        if (accessToken == null || accessToken.isBlank()) throw new IllegalArgumentException("Access Token cannot be null or blank");

        // Get ID
        var id = channel.getId();

        // Get Chatters
        var chattersList = helix.getChatters(accessToken, id.toString(), id.toString(), LIMIT, cursor).execute();

        // Null check
        if (chattersList == null) throw new IllegalStateException("Failed to get chatters for channel ID: " + id);
        var chatters = chattersList.getChatters();
        if (chatters == null) throw new IllegalStateException("Failed to get chatters for channel ID: " + id);
        if (chatters.isEmpty()) return new HashSet<>(); // No chatters found

        // Convert to TwitchUser
        HashSet<Integer> chatterIds = new HashSet<>();
        for (Chatter chatter : chatters) chatterIds.add(Integer.parseInt(chatter.getUserId()));
        HashMap<Integer, User> userMap = getUsersByIDsMap(chatterIds);
        HashSet<TwitchUser> fetchedChatters = new HashSet<>();
        for (var chatter : chatters) fetchedChatters.add(new TwitchUser(userMap.get(Integer.parseInt(chatter.getUserId()))));

        // Check if there are more moderators
        HelixPagination pagination = chattersList.getPagination();
        String nextCursor = pagination != null ? pagination.getCursor() : null;
        if (nextCursor != null) fetchedChatters.addAll(getChatters(channel, accessToken, nextCursor));

        // Return Chatters
        return fetchedChatters;
    }

    public HashSet<TwitchUser> getChatters(TwitchUser channel) {

        // Check Parameters
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");

        // Get Access Token
        var id = channel.getId();
        AuthToken authToken = tokenHandler.getAuthToken(id);

        // Check AuthToken
        if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");
        if (!authToken.hasScope(MODERATOR_READ_CHATTERS)) throw new IllegalArgumentException("AuthToken does not have the required scope: " + MODERATOR_READ_CHATTERS.getScope());

        // Get Chatters
        return getChatters(channel, authToken.getAccessToken(), null);
    }

    public HashSet<TwitchUser> getChatters(Integer channel) {
        return getChatters(new TwitchUser(getUser(channel)));
    }

    public HashSet<TwitchUser> getChatters(String channel) {
        return getChatters(new TwitchUser(getUser(channel)));
    }





    public void deleteChatMessages(TwitchUser channel, HashSet<Integer> messageIds) {

        // Check Parameters
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");
        if (messageIds == null || messageIds.isEmpty()) throw new IllegalArgumentException("Message IDs cannot be null or empty");
        for (var id : messageIds) if (id == null || id <= 0) throw new IllegalArgumentException("Invalid Message ID in Message IDs: " + id);

        // Get Access Token
        var id = channel.getId();
        AuthToken authToken = tokenHandler.getAuthToken(id);

        // Check AuthToken
        if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");
        if (!authToken.hasScope(MODERATOR_MANAGE_CHAT_MESSAGES)) throw new IllegalArgumentException("AuthToken does not have the required scope: " + MODERATOR_MANAGE_CHAT_MESSAGES.getScope());

        // Delete Messages
        String accessToken = authToken.getAccessToken();
        for (var message : messageIds) helix.deleteChatMessages(
                accessToken,        // Access Token
                id.toString(),      // Broadcaster ID
                id.toString(),      // Moderator ID
                message.toString()  // Message ID
        ).execute();
    }

    public void deleteChatMessages(Integer channel, HashSet<Integer> messageIds) {
        deleteChatMessages(new TwitchUser(getUser(channel)), messageIds);
    }

    public void deleteChatMessages(String channel, HashSet<Integer> messageIds) {
        deleteChatMessages(new TwitchUser(getUser(channel)), messageIds);
    }

    public void deleteChatMessage(TwitchUser channel, Integer messageId) {
        deleteChatMessages(channel, new HashSet<>(Collections.singleton(messageId)));
    }

    public void deleteChatMessage(Integer channel, Integer messageId) {
        deleteChatMessage(new TwitchUser(getUser(channel)), messageId);
    }

    public void deleteChatMessage(String channel, Integer messageId) {
        deleteChatMessage(new TwitchUser(getUser(channel)), messageId);
    }
}