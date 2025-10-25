package de.MCmoderSD.helix.handler;

import com.github.twitch4j.helix.TwitchHelix;

import de.MCmoderSD.helix.core.TokenHandler;
import de.MCmoderSD.helix.enums.Scope;
import de.MCmoderSD.helix.objects.AuthToken;
import de.MCmoderSD.helix.objects.ChannelInfo;
import de.MCmoderSD.helix.objects.TwitchUser;

import java.util.Collections;
import java.util.HashSet;

import static de.MCmoderSD.helix.enums.Scope.MODERATOR_MANAGE_SHOUTOUTS;

@SuppressWarnings("unused")
public class ChannelHandler extends Handler {

    // Constants
    public static final Scope[] REQUIRED_SCOPES = {
            MODERATOR_MANAGE_SHOUTOUTS // Send shoutouts
    };

    // Constructor
    public ChannelHandler(TwitchHelix helix, TokenHandler tokenHandler) {
        super(helix, tokenHandler);
    }





    // Shoutout
    public void sendShoutout(TwitchUser user, TwitchUser channel) {

        // Check Parameters
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");

        // Get AuthToken
        AuthToken authToken = tokenHandler.getAuthToken(channel.getId());

        // Check AuthToken
        if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");
        if (!authToken.hasScope(MODERATOR_MANAGE_SHOUTOUTS)) throw new IllegalArgumentException("AuthToken does not have the required scope: " + MODERATOR_MANAGE_SHOUTOUTS.getScope());

        // Send shoutout
        helix.sendShoutout(
                authToken.getAccessToken(),     // Access Token of the channel
                channel.getId().toString(),     // Source ID
                user.getId().toString(),        // Target ID
                channel.getId().toString()      // Moderator ID (same as Source ID)
        ).execute();
    }

    public void sendShoutout(Integer user, Integer channel) {
        sendShoutout(new TwitchUser(getUser(user)), new TwitchUser(getUser(channel)));
    }

    public void sendShoutout(String user, String channel) {
        sendShoutout(new TwitchUser(getUser(user)), new TwitchUser(getUser(channel)));
    }

    public void sendShoutout(Integer user, String channel) {
        sendShoutout(new TwitchUser(getUser(user)), new TwitchUser(getUser(channel)));
    }

    public void sendShoutout(String user, Integer channel) {
        sendShoutout(new TwitchUser(getUser(user)), new TwitchUser(getUser(channel)));
    }

    public void sendShoutout(TwitchUser user, Integer channel) {
        sendShoutout(user, new TwitchUser(getUser(channel)));
    }

    public void sendShoutout(TwitchUser user, String channel) {
        sendShoutout(user, new TwitchUser(getUser(channel)));
    }

    public void sendShoutout(Integer user, TwitchUser channel) {
        sendShoutout(new TwitchUser(getUser(user)), channel);
    }

    public void sendShoutout(String user, TwitchUser channel) {
        sendShoutout(new TwitchUser(getUser(user)), channel);
    }





    // Channel Information
    public HashSet<ChannelInfo> getChannelInfo(HashSet<TwitchUser> channels) {

        // Check Parameters
        if (channels == null || channels.isEmpty()) throw new IllegalArgumentException("Channels cannot be null or empty");

        // Check size and chunk
        var size = channels.size();
        if (channels.size() > LIMIT) {
            HashSet<ChannelInfo> channelInfo = new HashSet<>();
            for (var i = 0; i < size; i += LIMIT) channelInfo.addAll(getChannelInfo(new HashSet<>(channels.stream().toList().subList(i, Math.min(i + LIMIT, size)))));
            return channelInfo;
        }

        // Get channel information
        var information = helix.getChannelInformation(null, channels.stream().map(channel -> channel.getId().toString()).toList()).execute();

        // Null check
        if (information == null) throw new IllegalStateException("Failed to get channel information");
        var channelInformation = information.getChannels();
        if (channelInformation.isEmpty()) throw new IllegalStateException("Failed to get channel information");

        // Map channel information to ChannelInfo objects
        HashSet<ChannelInfo> channelInfo = new HashSet<>();
        for (var channel : channels)
            for (var info : channelInformation)
                if (channel.getId().toString().equals(info.getBroadcasterId()))
                    channelInfo.add(new ChannelInfo(info, channel));

        // Return channel info
        return channelInfo;
    }

    public ChannelInfo getChannelInfo(TwitchUser channel) {
        return getChannelInfo(new HashSet<>(Collections.singleton(channel))).iterator().next();
    }

    public ChannelInfo getChannelInfo(Integer channel) {
        return getChannelInfo(getChannelInfo(new TwitchUser(getUser(channel))));
    }

    public ChannelInfo getChannelInfo(String channel) {
        return getChannelInfo(getChannelInfo(new TwitchUser(getUser(channel))));
    }
}