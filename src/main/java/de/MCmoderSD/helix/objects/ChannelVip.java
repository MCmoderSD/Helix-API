package de.MCmoderSD.helix.objects;

import com.github.twitch4j.helix.domain.User;

import java.io.Serializable;

@SuppressWarnings("unused")
public class ChannelVip extends TwitchUser implements Serializable {

    // Attributes
    private final TwitchUser channel;

    // Constructor
    public ChannelVip(User user, User channel) {

        // Call super constructor
        super(user);

        // Channel where this user is a VIP
        this.channel = new TwitchUser(channel);
    }

    // Getter
    public TwitchUser getChannel() {
        return channel;
    }
}