package de.MCmoderSD.helix.objects;

import com.github.twitch4j.helix.domain.InboundFollow;
import com.github.twitch4j.helix.domain.User;

import java.io.Serializable;

import java.time.Instant;

@SuppressWarnings("ALL")
public class ChannelFollower extends TwitchUser implements Serializable {

    // Attributes
    private final TwitchUser channel;

    // InboundFollow information
    private final Instant followedAt;

    // Constructor
    public ChannelFollower(InboundFollow follow, User user, User channel) {

        // Call super constructor
        super(user);

        // Channel which is followed by this user
        this.channel = new TwitchUser(channel);

        // InboundFollow information
        followedAt = follow.getFollowedAt(); // Date when the user followed the channel
    }

    public ChannelFollower(InboundFollow follow, TwitchUser user, User channel) {

        // Call super constructor
        super(user);

        // Channel which is followed by this user
        this.channel = new TwitchUser(channel);

        // InboundFollow information
        followedAt = follow.getFollowedAt(); // Date when the user followed the channel
    }

    public ChannelFollower(InboundFollow follow, User user, TwitchUser channel) {

        // Call super constructor
        super(user);

        // Channel which is followed by this user
        this.channel = channel;

        // InboundFollow information
        followedAt = follow.getFollowedAt(); // Date when the user followed the channel
    }

    public ChannelFollower(InboundFollow follow, TwitchUser user, TwitchUser channel) {

        // Call super constructor
        super(user);

        // Channel which is followed by this user
        this.channel = channel;

        // InboundFollow information
        followedAt = follow.getFollowedAt(); // Date when the user followed the channel
    }

    // Getter
    public TwitchUser getChannel() {
        return channel;
    }

    public Instant getFollowedAt() {
        return followedAt;
    }

    // Methods
    public boolean equals(ChannelFollower follower) {
        if (follower == null) return false;
        return super.equals(follower) && channel.equals(follower.channel) && followedAt.equals(follower.followedAt);
    }
}