package de.MCmoderSD.helix.handler;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;

import de.MCmoderSD.helix.core.Client;
import de.MCmoderSD.helix.core.TokenManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

@SuppressWarnings("unused")
public abstract class Handler {

    // Constants
    protected static final Integer LIMIT = 100; // Max 100 per request (Default 20)

    // Associations
    protected final Client client;
    protected final TwitchHelix helix;
    protected final TokenManager manager;

    // Constructor
    public Handler(Client client) {
        this.client = client;
        this.helix = client.getHelix();
        this.manager = client.getTokenManager();
    }

    // Get user with ID
    public User getUser(Integer id) {

        // Check Parameters
        if (id == null || id < 1) throw new IllegalArgumentException("ID cannot be null or less than 1");

        // Get user ID
        UserList userList = helix.getUsers(null, Collections.singletonList(String.valueOf(id)), null).execute();

        // Null check
        if (userList == null || userList.getUsers() == null || userList.getUsers().isEmpty()) {
            System.err.println("Failed to get user with ID: " + id);
            return null;
        }

        // Return user
        return userList.getUsers().getFirst();
    }

    // Get user with name
    public User getUser(String username) {

        // Check Parameters
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Username cannot be empty");

        // Get user ID
        UserList userList = helix.getUsers(null, null, Collections.singletonList(username)).execute();

        // Null check
        if (userList == null || userList.getUsers() == null || userList.getUsers().isEmpty()) {
            System.err.println("Failed to get user with name: " + username);
            return null;
        }

        // Return user
        return userList.getUsers().getFirst();
    }

    // Get user with ID and name
    public User getUser(Integer id, String username) {

        // Check Parameters
        if (id == null || id < 1) throw new IllegalArgumentException("ID cannot be null or less than 1");
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Username cannot be empty");

        // Get user ID
        UserList userList = helix.getUsers(null, Collections.singletonList(String.valueOf(id)), Collections.singletonList(username)).execute();

        // Null check
        if (userList == null || userList.getUsers() == null || userList.getUsers().isEmpty()) {
            System.err.println("Failed to get user with ID: " + id + " and name: " + username);
            return null;
        }

        // Return user
        return userList.getUsers().getFirst();
    }

    // Get Users with IDs
    public HashSet<User> getUsersByIDs(HashSet<Integer> ids) {

        // Check Parameters
        if (ids == null || ids.isEmpty()) throw new IllegalArgumentException("IDs cannot be empty");
        for (Integer id : ids) if (id == null || id < 1) throw new IllegalArgumentException("ID cannot be null or less than 1");

        // Check size and chunk
        if (ids.size() > LIMIT) {
            ArrayList<Integer> idList = new ArrayList<>(ids);
            HashSet<User> users = new HashSet<>();
            for (var i = 0; i < idList.size(); i += LIMIT) {
                ArrayList<Integer> chunk = new ArrayList<>(idList.subList(i, Math.min(i + LIMIT, idList.size())));
                users.addAll(getUsersByIDs(new HashSet<>(chunk)));
            }
            return users;
        }

        // Get user ID
        UserList userList = helix.getUsers(null, ids.stream().map(String::valueOf).toList(), null).execute();

        // Null check
        if (userList == null || userList.getUsers() == null || userList.getUsers().isEmpty()) {
            System.err.println("Failed to get users with IDs: " + ids);
            return null;
        }

        // Return users
        return new HashSet<>(userList.getUsers());
    }

    // Get Users with names
    public HashSet<User> getUsersByName(HashSet<String> usernames) {

        // Check Parameters
        if (usernames == null || usernames.isEmpty()) throw new IllegalArgumentException("Usernames cannot be empty");
        for (String username : usernames) if (username == null || username.isBlank()) throw new IllegalArgumentException("Username cannot be empty");

        // Check size and chunk
        if (usernames.size() > LIMIT) {
            ArrayList<String> nameList = new ArrayList<>(usernames);
            HashSet<User> users = new HashSet<>();
            for (var i = 0; i < nameList.size(); i += LIMIT) {
                ArrayList<String> chunk = new ArrayList<>(nameList.subList(i, Math.min(i + LIMIT, nameList.size())));
                users.addAll(getUsersByName(new HashSet<>(chunk)));
            }
            return users;
        }

        // Get user ID
        UserList userList = helix.getUsers(null, null, usernames.stream().toList()).execute();

        // Null check
        if (userList == null || userList.getUsers() == null || userList.getUsers().isEmpty()) {
            System.err.println("Failed to get users with names: " + usernames);
            return null;
        }

        // Return users
        return new HashSet<>(userList.getUsers());
    }
}