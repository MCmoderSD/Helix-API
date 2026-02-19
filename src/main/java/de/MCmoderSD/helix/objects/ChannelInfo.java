package de.MCmoderSD.helix.objects;

import com.github.twitch4j.helix.domain.ChannelInformation;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;

public class ChannelInfo extends TwitchUser implements Serializable {

    // Attributes
    private final String title;
    private final HashSet<String> tags;
    private final String language;
    private final Integer gameId;
    private final String gameName;
    private final boolean brandedContent;

    // Constructor
    public ChannelInfo(ChannelInformation info, TwitchUser channel) {

        // Call super constructor
        super(channel);

        // Channel info
        title = info.getTitle().isBlank() ? null : info.getTitle();
        tags = new HashSet<>(info.getTags());
        language = info.getBroadcasterLanguage().isBlank() ? null : info.getBroadcasterLanguage();
        gameId = info.getGameId().isBlank() ? null : Integer.parseInt(info.getGameId());
        gameName = info.getGameName().isBlank() ? null : info.getGameName();
        brandedContent = info.isBrandedContent();
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

    public Integer getGameId() {
        return gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public boolean isBrandedContent() {
        return brandedContent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, tags, language, gameId, gameName, brandedContent);
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == getClass() && hashCode() == obj.hashCode();
    }
}