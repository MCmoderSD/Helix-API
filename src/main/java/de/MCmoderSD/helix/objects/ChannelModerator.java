package de.MCmoderSD.helix.objects;

import com.github.twitch4j.helix.domain.User;

import java.io.Serializable;

@SuppressWarnings("unused")
public class ChannelModerator extends TwitchUser implements Serializable {

    // Attributes
    private final TwitchUser channel;

    // Constructor
    public ChannelModerator(User user, User channel) {

        // Call super constructor
        super(user);

        // Channel which is moderated by this user
        this.channel = new TwitchUser(channel);
    }

    // Getter
    public TwitchUser getChannel() {
        return channel;
    }
}