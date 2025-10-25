package de.MCmoderSD.helix.objects;

import com.github.twitch4j.helix.domain.Subscription;
import com.github.twitch4j.helix.domain.User;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@SuppressWarnings("ALL")
public class ChannelSubscriber extends TwitchUser implements Serializable {

    // Attributes
    private final TwitchUser channel;

    // Subscription information
    private final Tier tier;
    private final String planName;

    // Gifter information
    private final boolean isGift;
    private final TwitchUser gifter;

    // Constructor
    public ChannelSubscriber(Subscription subscription, User user, User channel, @Nullable User gifter) {

        // Call super constructor
        super(user);

        // Channel where this user is a subscriber
        this.channel = new TwitchUser(channel);

        // Subscription information
        tier = Tier.fromString(subscription.getTier());                 // Subscription tier
        planName = subscription.getPlanName();                          // Subscription plan name
        isGift = subscription.getIsGift();                              // Is this subscription a gift?
        this.gifter = gifter != null ? new TwitchUser(gifter) : null;   // Gifter (TwitchUser)
    }

    public ChannelSubscriber(Subscription subscription, TwitchUser user, User channel, @Nullable User gifter) {

        // Call super constructor
        super(user);

        // Channel where this user is a subscriber
        this.channel = new TwitchUser(channel);

        // Subscription information
        tier = Tier.fromString(subscription.getTier());                 // Subscription tier
        planName = subscription.getPlanName();                          // Subscription plan name
        isGift = subscription.getIsGift();                              // Is this subscription a gift?
        this.gifter = gifter != null ? new TwitchUser(gifter) : null;   // Gifter (TwitchUser)
    }

    public ChannelSubscriber(Subscription subscription, User user, TwitchUser channel, @Nullable User gifter) {

        // Call super constructor
        super(user);

        // Channel where this user is a subscriber
        this.channel = channel;

        // Subscription information
        tier = Tier.fromString(subscription.getTier());                 // Subscription tier
        planName = subscription.getPlanName();                          // Subscription plan name
        isGift = subscription.getIsGift();                              // Is this subscription a gift?
        this.gifter = gifter != null ? new TwitchUser(gifter) : null;   // Gifter (TwitchUser)
    }

    public ChannelSubscriber(Subscription subscription, User user, User channel, @Nullable TwitchUser gifter) {

        // Call super constructor
        super(user);

        // Channel where this user is a subscriber
        this.channel = new TwitchUser(channel);

        // Subscription information
        tier = Tier.fromString(subscription.getTier());                 // Subscription tier
        planName = subscription.getPlanName();                          // Subscription plan name
        isGift = subscription.getIsGift();                              // Is this subscription a gift?
        this.gifter = gifter;                                           // Gifter (TwitchUser)
    }

    public ChannelSubscriber(Subscription subscription, User user, TwitchUser channel, @Nullable TwitchUser gifter) {

        // Call super constructor
        super(user);

        // Channel where this user is a subscriber
        this.channel = channel;

        // Subscription information
        tier = Tier.fromString(subscription.getTier());                 // Subscription tier
        planName = subscription.getPlanName();                          // Subscription plan name
        isGift = subscription.getIsGift();                              // Is this subscription a gift?
        this.gifter = gifter;                                           // Gifter (TwitchUser)
    }

    public ChannelSubscriber(Subscription subscription, TwitchUser user, User channel, @Nullable TwitchUser gifter) {

        // Call super constructor
        super(user);

        // Channel where this user is a subscriber
        this.channel = new TwitchUser(channel);

        // Subscription information
        tier = Tier.fromString(subscription.getTier());                 // Subscription tier
        planName = subscription.getPlanName();                          // Subscription plan name
        isGift = subscription.getIsGift();                              // Is this subscription a gift?
        this.gifter = gifter;                                           // Gifter (TwitchUser)
    }

    public ChannelSubscriber(Subscription subscription, TwitchUser user, TwitchUser channel, @Nullable User gifter) {

        // Call super constructor
        super(user);

        // Channel where this user is a subscriber
        this.channel = channel;

        // Subscription information
        tier = Tier.fromString(subscription.getTier());                 // Subscription tier
        planName = subscription.getPlanName();                          // Subscription plan name
        isGift = subscription.getIsGift();                              // Is this subscription a gift?
        this.gifter = gifter != null ? new TwitchUser(gifter) : null;   // Gifter (TwitchUser)
    }

    public ChannelSubscriber(Subscription subscription, TwitchUser user, TwitchUser channel, @Nullable TwitchUser gifter) {

        // Call super constructor
        super(user);

        // Channel where this user is a subscriber
        this.channel = channel;

        // Subscription information
        tier = Tier.fromString(subscription.getTier());                 // Subscription tier
        planName = subscription.getPlanName();                          // Subscription plan name
        isGift = subscription.getIsGift();                              // Is this subscription a gift?
        this.gifter = gifter;                                           // Gifter (TwitchUser)
    }

    // Getter
    public TwitchUser getChannel() {
        return channel;
    }

    public Tier getTier() {
        return tier;
    }

    public String getPlanName() {
        return planName;
    }

    public boolean isGift() {
        return isGift;
    }

    public TwitchUser getGifter() {
        return gifter;
    }

    // Enum
    public enum Tier implements Serializable {

        // Values
        TIER_1, TIER_2, TIER_3;

        // Methods
        public static Tier fromString(String string) {
            return fromInt(Integer.parseInt(string));
        }

        public static Tier fromInt(Integer value) {
            return switch (value) {
                case 1000 -> TIER_1;
                case 2000 -> TIER_2;
                case 3000 -> TIER_3;
                default -> null;
            };
        }
    }

    // Methods
    public boolean equals(ChannelSubscriber subscriber) {
        if (subscriber == null) return false;
        boolean isEqual = super.equals(subscriber);
        isEqual &= channel.equals(subscriber.channel);
        isEqual &= tier == subscriber.tier;
        isEqual &= planName.equals(subscriber.planName);
        isEqual &= isGift == subscriber.isGift;
        if (gifter == null && subscriber.gifter == null) isEqual &= true;
        else if (gifter != null && subscriber.gifter != null) isEqual &= gifter.equals(subscriber.gifter);
        else isEqual &= false;
        return isEqual;
    }
}