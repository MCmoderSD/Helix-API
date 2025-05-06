package de.MCmoderSD.helix.objects;

import com.github.twitch4j.helix.domain.InboundFollow;
import com.github.twitch4j.helix.domain.User;

import java.io.Serializable;

import java.time.Instant;

@SuppressWarnings("unused")
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

    // Methods
    public void print() {
        System.out.println("------------------------------");
        System.out.println("Channel Follower: " + getDisplayName());
        System.out.println("Channel: " + channel.getDisplayName());
        System.out.println("Followed at: " + followedAt);
        System.out.println("------------------------------");
    }

    // Getter
    public TwitchUser getChannel() {
        return channel;
    }

    public Instant getFollowedAt() {
        return followedAt;
    }
}