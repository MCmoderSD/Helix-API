package de.MCmoderSD.helix.objects;

import com.github.twitch4j.helix.domain.User;

import java.io.Serializable;

@SuppressWarnings("ALL")
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

    public ChannelEditor(User user, TwitchUser channel) {

        // Call super constructor
        super(user);

        // Channel which is edited by this user
        this.channel = channel;
    }

    public ChannelEditor(TwitchUser user, User channel) {

        // Call super constructor
        super(user);

        // Channel which is edited by this user
        this.channel = new TwitchUser(channel);
    }

    public ChannelEditor(TwitchUser user, TwitchUser channel) {

        // Call super constructor
        super(user);

        // Channel which is edited by this user
        this.channel = channel;
    }

    // Getter
    public TwitchUser getChannel() {
        return channel;
    }

    // Methods
    public boolean equals(ChannelEditor editor) {
        if (editor == null) return false;
        return super.equals(editor) && channel.equals(editor.channel);
    }
}