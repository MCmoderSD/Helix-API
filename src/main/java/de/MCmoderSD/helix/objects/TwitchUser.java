package de.MCmoderSD.helix.objects;

import com.github.twitch4j.helix.domain.User;

import java.io.Serializable;

import java.time.Instant;
import java.util.Objects;

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
        displayName = user.getDisplayName();                                                        // Display name (upper and lowercase)
        description = user.getDescription().isBlank() ? null : user.getDescription();               // Description
        profileImageUrl = user.getProfileImageUrl().isBlank() ? null : user.getProfileImageUrl();   // Profile image URL
        offlineImageUrl = user.getOfflineImageUrl().isBlank() ? null : user.getOfflineImageUrl();   // Offline image URL

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
        boolean isEqual = id.equals(user.id);
        isEqual &= username.equalsIgnoreCase(user.username);
        isEqual &= createdAt.equals(user.createdAt);
        isEqual &= Objects.equals(displayName, user.displayName);
        isEqual &= Objects.equals(description, user.description);
        isEqual &= Objects.equals(profileImageUrl, user.profileImageUrl);
        isEqual &= Objects.equals(offlineImageUrl, user.offlineImageUrl);
        isEqual &= broadcasterType == user.broadcasterType;
        isEqual &= type == user.type;
        return isEqual;
    }
}