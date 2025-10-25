package de.MCmoderSD.helix.handler;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.User;

import de.MCmoderSD.helix.core.TokenHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Arrays;

@SuppressWarnings("unused")
public abstract class Handler {

    // Constants
    protected static final Integer LIMIT = 100; // Max 100 per request (Default 20)

    // Associations
    protected final TwitchHelix helix;
    protected final TokenHandler tokenHandler;

    // Constructor
    public Handler(TwitchHelix helix, TokenHandler tokenHandler) {

        // Check Parameters
        if (helix == null) throw new IllegalArgumentException("TwitchHelix cannot be null");
        if (tokenHandler == null) throw new IllegalArgumentException("TokenHandler cannot be null");

        // Set Associations
        this.helix = helix;
        this.tokenHandler = tokenHandler;
    }

    // Get user with ID
    public User getUser(Integer id) {

        // Check Parameters
        if (id == null || id < 1) throw new IllegalArgumentException("ID cannot be null or less than 1");

        // Get user ID
        var userList = helix.getUsers(null, Collections.singletonList(id.toString()), null).execute();

        // Check Response
        if (userList == null) throw new IllegalStateException("Failed to get user with ID: " + id);
        var users = userList.getUsers();
        if (users == null) throw new IllegalStateException("Failed to get user with ID: " + id);
        if (users.isEmpty()) throw new IllegalStateException("No user found with ID: " + id);
        if (users.size() != 1) throw new IllegalStateException("Multiple users found with ID: " + id);

        // Return user
        return users.getFirst();
    }

    // Get user with name
    public User getUser(String username) {

        // Check Parameters
        if (username == null || username.isBlank() || username.contains(" ")) throw new IllegalStateException("Invalid username: " + username);

        // Get user ID
        var userList = helix.getUsers(null, null, Collections.singletonList(username.toLowerCase())).execute();

        // Check Response
        if (userList == null) throw new IllegalStateException("Failed to get user: " + username);
        var users = userList.getUsers();
        if (users == null) throw new IllegalStateException("Failed to get user: " + username);
        if (users.isEmpty()) throw new IllegalStateException("No user found: " + username);
        if (users.size() != 1) throw new IllegalStateException("Multiple users found: " + username);

        // Return user
        return users.getFirst();
    }

    // Get Users with IDs
    public HashSet<User> getUsersByIDs(HashSet<Integer> ids) {

        // Check Parameters
        if (ids == null || ids.isEmpty()) throw new IllegalArgumentException("IDs cannot be empty");
        for (var id : ids) if (id == null || id < 1) throw new IllegalArgumentException("ID cannot be null or less than 1");

        // Check size and chunk
        var size = ids.size();
        if (size > LIMIT) {
            HashSet<User> users = new HashSet<>();
            for (var i = 0; i < size; i += LIMIT) users.addAll(getUsersByIDs(new HashSet<>(ids.stream().toList().subList(i, Math.min(i + LIMIT, size)))));
            return users;
        }

        // Get user ID
        var userList = helix.getUsers(null, ids.stream().map(String::valueOf).toList(), null).execute();

        // Check Response
        if (userList == null) throw new IllegalStateException("Failed to get users with IDs: " + Arrays.toString(ids.toArray()));
        var users = userList.getUsers();
        if (users == null) throw new IllegalStateException("Failed to get users with IDs: " + Arrays.toString(ids.toArray()));
        if (users.isEmpty()) throw new IllegalStateException("No users found with IDs: " + Arrays.toString(ids.toArray()));

        // Return users
        return new HashSet<>(users);
    }

    // Get Users with names
    public HashSet<User> getUsersByName(HashSet<String> usernames) {

        // Check Parameters
        if (usernames == null || usernames.isEmpty()) throw new IllegalArgumentException("Usernames cannot be empty");
        for (var username : usernames) if (username == null || username.isBlank() || username.contains(" ")) throw new IllegalStateException("Invalid username: " + username);

        // Check size and chunk
        var size = usernames.size();
        if (size > LIMIT) {
            HashSet<User> users = new HashSet<>();
            for (var i = 0; i < size; i += LIMIT) users.addAll(getUsersByName(new HashSet<>(usernames.stream().toList().subList(i, Math.min(i + LIMIT, size)))));
            return users;
        }

        // Get user ID
        var userList = helix.getUsers(null, null, usernames.stream().map(String::toLowerCase).toList()).execute();

        // Check Response
        if (userList == null) throw new IllegalStateException("Failed to get users with names: " + Arrays.toString(usernames.toArray()));
        var users = userList.getUsers();
        if (users == null) throw new IllegalStateException("Failed to get users with names: " + Arrays.toString(usernames.toArray()));
        if (users.isEmpty()) throw new IllegalStateException("No users found with names: " + Arrays.toString(usernames.toArray()));

        // Return users
        return new HashSet<>(users);
    }

    // Get User Map by IDs
    public HashMap<Integer, User> getUsersByIDsMap(HashSet<Integer> ids) {
        HashSet<User> users = getUsersByIDs(ids);
        HashMap<Integer, User> userMap = new HashMap<>();
        for (var user : users) userMap.put(Integer.parseInt(user.getId()), user);
        return userMap;
    }

    // Get User Map by names
    public HashMap<String, User> getUsersByNameMap(HashSet<String> usernames) {
        HashSet<User> users = getUsersByName(usernames);
        HashMap<String, User> userMap = new HashMap<>();
        for (var user : users) userMap.put(user.getLogin(), user);
        return userMap;
    }
}