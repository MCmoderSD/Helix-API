package de.MCmoderSD.helix.objects;

import com.github.twitch4j.helix.domain.User;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), channel);
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == getClass() && hashCode() == obj.hashCode();
    }
}