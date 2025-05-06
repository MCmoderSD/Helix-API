package de.MCmoderSD.helix.objects;

import com.github.twitch4j.helix.domain.User;

import java.io.Serializable;
import java.util.HashSet;

@SuppressWarnings("unused")
public class ChannelInformation extends TwitchUser implements Serializable {

    // Attributes
    private final String title;
    private final HashSet<String> tags;
    private final String language;
    private final String gameId;
    private final String gameName;

    // Constructor
    public ChannelInformation(com.github.twitch4j.helix.domain.ChannelInformation information, User channel) {

        // Call super constructor
        super(channel);

        // Channel information
        title = information.getTitle();
        tags = new HashSet<>(information.getTags());
        language = information.getBroadcasterLanguage();
        gameId = information.getGameId();
        gameName = information.getGameName();
    }

    // Methods
    public void print() {
        System.out.println("------------------------------");
        System.out.println("Channel Information: " + getDisplayName());
        System.out.println("Title: " + title);
        System.out.println("Tags: " + tags);
        System.out.println("Language: " + language);
        System.out.println("Game ID: " + gameId);
        System.out.println("Game Name: " + gameName);
        System.out.println("------------------------------");
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public HashSet<String> getTags() {
        return tags;
    }

    public String getLanguage() {
        return language;
    }

    public String getGameId() {
        return gameId;
    }

    public String getGameName() {
        return gameName;
    }
}