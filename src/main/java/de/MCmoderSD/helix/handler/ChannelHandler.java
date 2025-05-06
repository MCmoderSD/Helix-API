package de.MCmoderSD.helix.handler;

import com.github.twitch4j.helix.domain.User;

import de.MCmoderSD.helix.core.Client;
import de.MCmoderSD.helix.enums.Scope;
import de.MCmoderSD.helix.objects.ChannelInformation;

import java.util.Collections;
import java.util.HashSet;

@SuppressWarnings("unused")
public class ChannelHandler extends Handler {

    // Constants
    public static final Scope[] REQUIRED_SCOPES = {
            Scope.MODERATOR_MANAGE_SHOUTOUTS // Send shoutouts
    };

    // Constructor
    public ChannelHandler(Client client) {
        super(client);
    }

    public void sendShoutout(Integer raider, Integer channel) {

        // Check Parameters
        if (raider == null || raider > 0) throw new IllegalArgumentException("Raider ID cannot be null or less than 1");
        if (channel == null || channel > 0) throw new IllegalArgumentException("Channel ID cannot be null or less than 1");

        // Get access token
        String accessToken = manager.getToken(channel, Scope.MODERATOR_MANAGE_SHOUTOUTS);

        // Null check
        if (accessToken == null || accessToken.isBlank()) throw new IllegalArgumentException("Access token cannot be null or empty");

        // Send shoutout
        helix.sendShoutout(accessToken, String.valueOf(channel), String.valueOf(raider), String.valueOf(channel)).execute();
    }

    public HashSet<ChannelInformation> getChannelInformation(HashSet<Integer> ids) {

        // Check Parameters
        if (ids == null || ids.isEmpty()) throw new IllegalArgumentException("IDs cannot be null or empty");
        if (ids.stream().anyMatch(id -> id == null || id < 1)) throw new IllegalArgumentException("ID cannot be null or less than 1");

        // Get channel information
        var information = helix.getChannelInformation(null, ids.stream().map(String::valueOf).toList()).execute();

        // Null check
        if (information == null) {
            System.err.println("Channel information could not be found");
            return null;
        }

        // Return channel information
        HashSet<ChannelInformation> channelInformation = new HashSet<>();
        HashSet<User> users = getUsersByIDs(ids);
        for (User user : users) {
            for (var info : information.getChannels()) {
                if (user.getId().equals(info.getBroadcasterId())) {
                    channelInformation.add(new ChannelInformation(info, user));
                }
            }
        }

        // Return channel information
        return channelInformation;
    }

    public ChannelInformation getChannelInformation(Integer id) {
        return getChannelInformation(new HashSet<>(Collections.singletonList(id))).iterator().next();
    }
}