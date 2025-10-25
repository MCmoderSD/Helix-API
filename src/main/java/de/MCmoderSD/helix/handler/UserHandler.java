package de.MCmoderSD.helix.handler;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;

import de.MCmoderSD.helix.core.TokenHandler;
import de.MCmoderSD.helix.enums.Scope;
import de.MCmoderSD.helix.objects.AuthToken;
import de.MCmoderSD.helix.objects.TwitchUser;

import java.util.Collections;
import java.util.HashSet;

import static de.MCmoderSD.helix.enums.Scope.USER_READ_EMAIL;

@SuppressWarnings("ALL")
public class UserHandler extends Handler {

    // Constants
    public static final Scope[] REQUIRED_SCOPES = {
            USER_READ_EMAIL   // User email
    };

    // Constructor
    public UserHandler(TwitchHelix helix, TokenHandler tokenHandler) {
        super(helix, tokenHandler);
    }

    public TwitchUser getTwitchUser(Integer id) {
        return new TwitchUser(getUser(id));
    }

    public TwitchUser getTwitchUser(String username) {
        return new TwitchUser(getUser(username));
    }

    public HashSet<TwitchUser> getTwitchUsers(HashSet<Integer> ids) {

        // Variables
        HashSet<User> users = getUsersByIDs(ids);
        HashSet<TwitchUser> twitchUsers = new HashSet<>();

        // Convert IDs to String
        for (var user : users) twitchUsers.add(new TwitchUser(user));

        // Return users
        return twitchUsers;
    }

    public HashSet<TwitchUser> getTwitchUsersByName(HashSet<String> usernames) {

        // Variables
        HashSet<User> users = getUsersByName(usernames);
        HashSet<TwitchUser> twitchUsers = new HashSet<>();

        // Convert IDs to String
        for (var user : users) twitchUsers.add(new TwitchUser(user));

        // Return users
        return twitchUsers;
    }

    public String getUserMail(TwitchUser twitchUser) {

        // Null check
        if (twitchUser == null) throw new IllegalArgumentException("TwitchUser cannot be null");

        // Get AuthToken
        AuthToken authToken = tokenHandler.getAuthToken(twitchUser.getId());

        // Check AuthToken
        if (authToken == null) throw new IllegalArgumentException("AuthToken cannot be null");
        if (!authToken.hasScope(USER_READ_EMAIL)) throw new IllegalArgumentException("AuthToken does not have the required scope: " + USER_READ_EMAIL.getScope());

        // Get user ID
        UserList userList = helix.getUsers(
                authToken.getAccessToken(),                                 // Access Token
                Collections.singletonList(twitchUser.getId().toString()),   // ID
                Collections.singletonList(twitchUser.getUsername())         // Username
        ).execute();

        // Null check
        if (userList == null) throw new IllegalStateException("Failed to get user email");
        var users = userList.getUsers();
        if (users == null || users.isEmpty()) throw new IllegalStateException("Failed to get user email");
        if (users.size() > 1) throw new IllegalStateException("Multiple users found when getting email for user: " + twitchUser.getId() + " / " + twitchUser.getUsername());

        // Return email
        return users.getFirst().getEmail();
    }

    public String getUserMail(Integer id) {
        return getUserMail(getTwitchUser(id));
    }

    public String getUserMail(String username) {
        return getUserMail(getTwitchUser(username));
    }
}