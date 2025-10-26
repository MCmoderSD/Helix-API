package de.MCmoderSD.helix.handler;

import com.github.twitch4j.helix.TwitchHelix;

import de.MCmoderSD.helix.core.TokenHandler;
import de.MCmoderSD.helix.enums.Scope;
import de.MCmoderSD.helix.objects.AuthToken;
import de.MCmoderSD.helix.objects.TwitchUser;

import static de.MCmoderSD.helix.enums.Scope.CHANNEL_EDIT_COMMERCIAL;
import static de.MCmoderSD.helix.enums.Scope.CHANNEL_MANAGE_RAIDS;
import static de.MCmoderSD.helix.enums.Scope.MODERATOR_MANAGE_SHOUTOUTS;

@SuppressWarnings("unused")
public class StreamHandler extends Handler {

    // Constants
    public static final Scope[] REQUIRED_SCOPES = {
            CHANNEL_EDIT_COMMERCIAL,    // Edit commercials
            CHANNEL_MANAGE_RAIDS,       // Manage raids
            MODERATOR_MANAGE_SHOUTOUTS  // Send shoutouts
    };

    // Constructor
    public StreamHandler(TwitchHelix helix, TokenHandler tokenHandler) {
        super(helix, tokenHandler);
    }





    // Run Commercial
    public void runCommercial(TwitchUser channel, CommercialLength length) {

        // Check Parameters
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");
        if (length == null) throw new IllegalArgumentException("Length cannot be null");

        // Get AuthToken
        AuthToken authToken = tokenHandler.getAuthToken(channel.getId());

        // Check AuthToken
        if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");
        if (!authToken.hasScope(CHANNEL_EDIT_COMMERCIAL)) throw new IllegalArgumentException("AuthToken does not have the required scope: " + CHANNEL_EDIT_COMMERCIAL.getScope());

        // Start commercial
        helix.startCommercial(
                authToken.getAccessToken(),     // Access Token of the channel
                channel.getId().toString(),     // Broadcaster ID
                length.getSeconds()             // Length in seconds
        ).execute();
    }

    public void runCommercial(Integer channel, CommercialLength length) {
        runCommercial(new TwitchUser(getUser(channel)), length);
    }

    public void runCommercial(String channel, CommercialLength length) {
        runCommercial(new TwitchUser(getUser(channel)), length);
    }





    // Start Raid
    public void startRaid(TwitchUser user, TwitchUser channel) {

        // Check Parameters
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");

        // Get AuthToken
        AuthToken authToken = tokenHandler.getAuthToken(channel.getId());

        // Check AuthToken
        if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");
        if (!authToken.hasScope(CHANNEL_MANAGE_RAIDS)) throw new IllegalArgumentException("AuthToken does not have the required scope: " + CHANNEL_MANAGE_RAIDS.getScope());

        // Start raid
        helix.startRaid(
                authToken.getAccessToken(),         // Access Token of the channel
                channel.getId().toString(),         // Source Channel ID
                user.getId().toString()             // Target Channel ID
        ).execute();
    }

    public void startRaid(Integer user, Integer channel) {
        startRaid(new TwitchUser(getUser(user)), new TwitchUser(getUser(channel)));
    }

    public void startRaid(String user, String channel) {
        startRaid(new TwitchUser(getUser(user)), new TwitchUser(getUser(channel)));
    }

    public void startRaid(Integer user, String channel) {
        startRaid(new TwitchUser(getUser(user)), new TwitchUser(getUser(channel)));
    }

    public void startRaid(String user, Integer channel) {
        startRaid(new TwitchUser(getUser(user)), new TwitchUser(getUser(channel)));
    }

    public void startRaid(TwitchUser user, Integer channel) {
        startRaid(user, new TwitchUser(getUser(channel)));
    }

    public void startRaid(TwitchUser user, String channel) {
        startRaid(user, new TwitchUser(getUser(channel)));
    }

    public void startRaid(Integer user, TwitchUser channel) {
        startRaid(new TwitchUser(getUser(user)), channel);
    }

    public void startRaid(String user, TwitchUser channel) {
        startRaid(new TwitchUser(getUser(user)), channel);
    }





    // Cancel Raid
    public void cancelRaid(TwitchUser channel) {

        // Check Parameters
        if (channel == null) throw new IllegalArgumentException("Channel cannot be null");

        // Get AuthToken
        AuthToken authToken = tokenHandler.getAuthToken(channel.getId());

        // Check AuthToken
        if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");
        if (!authToken.hasScope(CHANNEL_MANAGE_RAIDS)) throw new IllegalArgumentException("AuthToken does not have the required scope: " + CHANNEL_MANAGE_RAIDS.getScope());

        // Cancel raid
        helix.cancelRaid(
                authToken.getAccessToken(),     // Access Token of the channel
                channel.getId().toString()      // Channel ID
        ).execute();
    }

    public void cancelRaid(Integer channel) {
        cancelRaid(new TwitchUser(getUser(channel)));
    }

    public void cancelRaid(String channel) {
        cancelRaid(new TwitchUser(getUser(channel)));
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





    public enum CommercialLength {

        // Values
        LENGTH_30(30),
        LENGTH_60(60),
        LENGTH_90(90),
        LENGTH_120(120),
        LENGTH_150(150),
        LENGTH_180(180);

        // Attributes
        private final int seconds;

        // Constructor
        CommercialLength(int seconds) {
            this.seconds = seconds;
        }

        // Getter
        public int getSeconds() {
            return seconds;
        }
    }
}