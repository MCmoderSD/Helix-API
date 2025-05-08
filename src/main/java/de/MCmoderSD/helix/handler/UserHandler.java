package de.MCmoderSD.helix.handler;

import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;

import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.enums.Scope;
import de.MCmoderSD.helix.objects.TwitchUser;

import java.util.Collections;
import java.util.HashSet;

@SuppressWarnings("unused")
public class UserHandler extends Handler {

    // Constants
    public static final Scope[] REQUIRED_SCOPES = {
            Scope.USER_READ_EMAIL   // User email
    };

    // Constructor
    public UserHandler(HelixHandler helixHandler) {
        super(helixHandler);
    }

    public TwitchUser getTwitchUser(Integer id) {
        return new TwitchUser(getUser(id));
    }

    public TwitchUser getTwitchUser(String username) {
        return new TwitchUser(getUser(username));
    }

    public TwitchUser getTwitchUser(Integer id, String username) {
        return new TwitchUser(getUser(id, username));
    }

    public HashSet<TwitchUser> getTwitchUsers(HashSet<Integer> ids) {

        // Variables
        HashSet<User> users = getUsersByIDs(ids);
        HashSet<TwitchUser> twitchUsers = new HashSet<>();

        // Convert IDs to String
        for (User user : users) twitchUsers.add(new TwitchUser(user));

        // Return users
        return twitchUsers;
    }

    public HashSet<TwitchUser> getTwitchUsersByName(HashSet<String> usernames) {

        // Variables
        HashSet<User> users = getUsersByName(usernames);
        HashSet<TwitchUser> twitchUsers = new HashSet<>();

        // Convert IDs to String
        for (User user : users) twitchUsers.add(new TwitchUser(user));

        // Return users
        return twitchUsers;
    }

    public String getUserMail(Integer id) {

        // Check Parameters
        if (id == null || id < 1) throw new IllegalArgumentException("ID cannot be null or less than 1");

        // Get access token
        String accessToken = tokenHandler.getToken(id, Scope.USER_READ_EMAIL);

        // Null check
        if (accessToken == null || accessToken.isBlank()) {
            System.err.println("Failed to get access token");
            return null;
        }

        // Get user ID
        UserList userList = helix.getUsers(accessToken, Collections.singletonList(String.valueOf(id)), null).execute();

        // Null check
        if (userList == null || userList.getUsers() == null || userList.getUsers().isEmpty()) {
            System.err.println("Failed to get user with ID: " + id);
            return null;
        }

        // Return user
        return userList.getUsers().getFirst().getEmail();
    }
}