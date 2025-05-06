package de.MCmoderSD.helix.objects;

import java.io.Serializable;

@SuppressWarnings("unused")
public class Game implements Serializable {

    // Attributes
    private final String id;
    private final String name;
    private final String boxArtUrl;

    // Constructor
    public Game(com.github.twitch4j.helix.domain.Game game) {
        id = game.getId();
        name = game.getName();
        boxArtUrl = game.getBoxArtUrl();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBoxArtUrl() {
        return boxArtUrl;
    }
}
