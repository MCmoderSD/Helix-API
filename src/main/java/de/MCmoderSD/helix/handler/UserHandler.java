package de.MCmoderSD.helix.handler;

import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import de.MCmoderSD.helix.core.Client;
import de.MCmoderSD.helix.core.TokenManager;
import java.util.Collections;

public class UserHandler extends Handler {

    // Constructor
    public UserHandler(Client client, TokenManager tokenManager) {
        super(client, tokenManager);
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
}
