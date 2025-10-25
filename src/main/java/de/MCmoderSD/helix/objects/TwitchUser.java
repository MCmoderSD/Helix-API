package de.MCmoderSD.helix.objects;

import com.github.twitch4j.helix.domain.User;

import java.io.Serializable;

import java.time.Instant;

public class TwitchUser implements Serializable {

    // Attributes
    protected final Integer id;
    protected final String  username;
    protected final Instant createdAt;

    // Custom Attributes
    protected final String displayName;
    protected final String description;
    protected final String profileImageUrl;
    protected final String offlineImageUrl;

    // Enums
    protected final BroadcasterType broadcasterType;
    protected final Type type;

    // Constructor
    public TwitchUser(User user) {

        // Attributes
        id = Integer.parseInt(user.getId());            // User ID
        username = user.getLogin().toLowerCase();       // Username (lowercase)
        createdAt = user.getCreatedAt();                // Created at

        // Custom Attributes
        displayName = user.getDisplayName();            // Display name (upper and lowercase)
        description = user.getDescription();            // Description
        profileImageUrl = user.getProfileImageUrl();    // Profile image URL
        offlineImageUrl = user.getOfflineImageUrl();    // Offline image URL

        // Set enums
        broadcasterType = BroadcasterType.fromString(user.getBroadcasterType());    // Broadcaster type
        type = Type.fromString(user.getType());                                     // User type
    }

    public TwitchUser(TwitchUser user) {

        // Attributes
        id = user.id;                                   // User ID
        username = user.username;                       // Username (lowercase)
        createdAt = user.createdAt;                     // Created at

        // Custom Attributes
        displayName = user.displayName;                 // Display name (upper and lowercase)
        description = user.description;                 // Description
        profileImageUrl = user.profileImageUrl;         // Profile image URL
        offlineImageUrl = user.offlineImageUrl;         // Offline image URL

        // Set enums
        broadcasterType = user.broadcasterType;         // Broadcaster type
        type = user.type;                               // User type
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username.toLowerCase();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getOfflineImageUrl() {
        return offlineImageUrl;
    }

    public BroadcasterType getBroadcasterType() {
        return broadcasterType;
    }

    public Type getType() {
        return type;
    }

    // Enums
    public enum BroadcasterType implements Serializable {

        // Values
        PARTNER,
        AFFILIATE,
        NONE;

        // Get enum from string
        public static BroadcasterType fromString(String type) {
            return switch (type.toUpperCase()) {
                case "PARTNER" -> PARTNER;
                case "AFFILIATE" -> AFFILIATE;
                default -> NONE;
            };
        }
    }

    public enum Type implements Serializable {

        // Values
        ADMIN,
        GLOBAL_MOD,
        STAFF,
        USER;

        // Get enum from string
        public static Type fromString(String type) {
            return switch (type.toUpperCase()) {
                case "ADMIN" -> ADMIN;
                case "GLOBAL_MOD" -> GLOBAL_MOD;
                case "STAFF" -> STAFF;
                default -> USER;
            };
        }
    }

    // Methods
    public boolean equals(TwitchUser user) {
        if (user == null) return false;
        return id.equals(user.id) && username.equals(user.username) && createdAt.equals(user.createdAt) && displayName.equals(user.displayName) && description.equals(user.description) && profileImageUrl.equals(user.profileImageUrl) && offlineImageUrl.equals(user.offlineImageUrl) && broadcasterType == user.broadcasterType && type == user.type;
    }
}