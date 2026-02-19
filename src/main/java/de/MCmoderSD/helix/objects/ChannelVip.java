package de.MCmoderSD.helix.objects;

import com.github.twitch4j.helix.domain.User;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("ALL")
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

    public ChannelVip(User user, TwitchUser channel) {

        // Call super constructor
        super(user);

        // Channel where this user is a VIP
        this.channel = channel;
    }

    public ChannelVip(TwitchUser user, User channel) {

        // Call super constructor
        super(user);

        // Channel where this user is a VIP
        this.channel = new TwitchUser(channel);
    }

    public ChannelVip(TwitchUser user, TwitchUser channel) {

        // Call super constructor
        super(user);

        // Channel where this user is a VIP
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