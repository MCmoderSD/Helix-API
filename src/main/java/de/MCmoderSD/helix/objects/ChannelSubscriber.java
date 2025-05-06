package de.MCmoderSD.helix.objects;

import com.github.twitch4j.helix.domain.Subscription;
import com.github.twitch4j.helix.domain.User;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@SuppressWarnings("unused")
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

        print();
    }

    // Methods
    public void print() {
        System.out.println("------------------------------");
        System.out.println("Channel Subscriber: " + getDisplayName());
        System.out.println("Channel: " + channel.getDisplayName());
        System.out.println("Tier: " + tier);
        System.out.println("Plan Name: " + planName);
        System.out.println("Is Gift: " + isGift);
        if (isGift) System.out.println("Gifter: " + gifter.getDisplayName());
        System.out.println("------------------------------");
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
    public enum Tier {

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
}