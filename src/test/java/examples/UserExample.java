import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.handler.UserHandler;
import de.MCmoderSD.helix.objects.TwitchUser;

import examples.AuthExample;

void main() {

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
    IO.println("Users are the same: " + user.equals(userByName));

    // Print User Info
    IO.println("\n------- Twitch User Info -------");
    IO.println("User ID: " + user.getId());
    IO.println("Username: " + user.getUsername());
    IO.println("Created At: " + user.getCreatedAt());
    IO.println("Display Name: " + user.getDisplayName());
    IO.println("Description: " + user.getDescription());
    IO.println("Profile Image URL: " + user.getProfileImageUrl());
    IO.println("Offline Image URL: " + user.getOfflineImageUrl());
    IO.println("Broadcaster Type: " + user.getBroadcasterType());
    IO.println("User Type: " + user.getType());
    IO.println("-----------------------------------\n");

    // Get User Email (requires USER_READ_EMAIL scope)
    String email = userHandler.getUserMail(user);                               // By TwitchUser
    IO.println("Email: " + email);

    // Get User Email by ID and Username
    IO.println("ID: " + userHandler.getUserMail(exampleId));            // By ID
    IO.println("Name: " + userHandler.getUserMail(exampleUsername));    // By Username

    // Exit
    System.exit(0);
}