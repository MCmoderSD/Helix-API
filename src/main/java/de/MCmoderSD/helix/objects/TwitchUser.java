package de.MCmoderSD.helix.objects;

import com.github.twitch4j.helix.domain.User;
import de.MCmoderSD.imageloader.ImageLoader;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.time.Instant;

@SuppressWarnings("unused")
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

    // Images
    protected final BufferedImage profileImage;
    protected final BufferedImage offlineImage;

    // Enums
    protected final BroadcasterType broadcasterType;
    protected final Type type;

    // Constructor
    public TwitchUser(User user) {

        // Init Image Loader
        ImageLoader imageLoader = new ImageLoader();

        // Temp Variables
        BufferedImage profileImage = null;
        BufferedImage offlineImage = null;

        // Attributes
        id = Integer.parseInt(user.getId());            // User ID
        username = user.getLogin();                     // Username (lowercase)
        createdAt = user.getCreatedAt();                // Created at

        // Custom Attributes
        displayName = user.getDisplayName();            // Display name (upper and lowercase)
        description = user.getDescription();            // Description
        profileImageUrl = user.getProfileImageUrl();    // Profile image URL
        offlineImageUrl = user.getOfflineImageUrl();    // Offline image URL

        try {

            // Try to load images
            profileImage = imageLoader.load(profileImageUrl); // Profile image
            offlineImage = imageLoader.load(offlineImageUrl); // Offline image

        } catch (IOException | URISyntaxException e) {
            System.err.println("Failed to load images: " + e.getMessage());
        }

        // Set images
        this.profileImage = profileImage; // Profile image
        this.offlineImage = offlineImage; // Offline image

        // Set enums
        broadcasterType = BroadcasterType.fromString(user.getBroadcasterType());    // Broadcaster type
        type = Type.fromString(user.getType());                                     // User type
    }

    // Methods
    public void print() {
        System.out.println("------------------------------");
        System.out.println("User ID: " + id);
        System.out.println("Username: " + username);
        System.out.println("Created at: " + createdAt);
        System.out.println("Display name: " + displayName);
        System.out.println("Description: " + description);
        System.out.println("Profile image URL: " + profileImageUrl + (profileImage != null ? " | Loaded" : " | Failed to load"));
        System.out.println("Offline image URL: " + offlineImageUrl + (offlineImage != null ? " | Loaded" : " | Failed to load"));
        System.out.println("Broadcaster type: " + broadcasterType);
        System.out.println("User type: " + type);
        System.out.println("------------------------------");
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
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

    public BufferedImage getProfileImage() {
        return profileImage;
    }

    public BufferedImage getOfflineImage() {
        return offlineImage;
    }

    public BroadcasterType getBroadcasterType() {
        return broadcasterType;
    }

    public Type getType() {
        return type;
    }

    // Enums
    public enum BroadcasterType {

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

    public enum Type {

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
}