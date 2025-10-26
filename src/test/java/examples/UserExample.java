package examples;

import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.handler.UserHandler;
import de.MCmoderSD.helix.objects.TwitchUser;

@SuppressWarnings("ALL")
public class UserExample {

    public static void main(String[] args) {

        // Init HelixHandler
        HelixHandler helixHandler = AuthExample.initHelix();
        UserHandler userHandler = helixHandler.getUserHandler();

        // Example Variables
        var exampleId = 164284617;              // User ID
        var exampleUsername = "MCmoderSD";      // Username

        // Get TwitchUser
        TwitchUser user = userHandler.getTwitchUser(exampleId);                 // By ID
        TwitchUser userByName = userHandler.getTwitchUser(exampleUsername);     // By Username

        // Check both are the same
        System.out.println("Users are the same: " + user.equals(userByName));

        // Print User Info
        System.out.println("\n------- Twitch User Info -------");
        System.out.println("User ID: " + user.getId());
        System.out.println("Username: " + user.getUsername());
        System.out.println("Created At: " + user.getCreatedAt());
        System.out.println("Display Name: " + user.getDisplayName());
        System.out.println("Description: " + user.getDescription());
        System.out.println("Profile Image URL: " + user.getProfileImageUrl());
        System.out.println("Offline Image URL: " + user.getOfflineImageUrl());
        System.out.println("Broadcaster Type: " + user.getBroadcasterType());
        System.out.println("User Type: " + user.getType());
        System.out.println("-----------------------------------\n");

        // Get User Email (requires USER_READ_EMAIL scope)
        String email = userHandler.getUserMail(user);                               // By TwitchUser
        System.out.println("Email: " + email);

        // Get User Email by ID and Username
        System.out.println("ID: " + userHandler.getUserMail(exampleId));            // By ID
        System.out.println("Name: " + userHandler.getUserMail(exampleUsername));    // By Username

        // Exit
        System.exit(0);
    }
}