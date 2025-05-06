package de.MCmoderSD.helix.objects;

import com.github.twitch4j.helix.domain.User;

import java.io.Serializable;

@SuppressWarnings("unused")
public class ChannelEditor extends TwitchUser implements Serializable {

    // Attributes
    private final TwitchUser channel;

    // Constructor
    public ChannelEditor(User user, User channel) {

        // Call super constructor
        super(user);

        // Channel which is edited by this user
        this.channel = new TwitchUser(channel);
    }

    // Getter
    public TwitchUser getChannel() {
        return channel;
    }
}