package de.MCmoderSD.helix.objects;

import com.github.twitch4j.helix.domain.User;

import java.io.Serializable;

@SuppressWarnings("ALL")
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

    public ChannelModerator(User user, TwitchUser channel) {

        // Call super constructor
        super(user);

        // Channel which is moderated by this user
        this.channel = channel;
    }

    public ChannelModerator(TwitchUser user, User channel) {

        // Call super constructor
        super(user);

        // Channel which is moderated by this user
        this.channel = new TwitchUser(channel);
    }

    public ChannelModerator(TwitchUser user, TwitchUser channel) {

        // Call super constructor
        super(user);

        // Channel which is moderated by this user
        this.channel = channel;
    }

    // Getter
    public TwitchUser getChannel() {
        return channel;
    }

    // Methods
    public boolean equals(ChannelModerator moderator) {
        if (moderator == null) return false;
        return super.equals(moderator) && channel.equals(moderator.channel);
    }
}