import examples.AuthExample;

import static java.lang.IO.println;

void main() {

    // Init HelixHandler
    var helixHandler = AuthExample.initHelix();
    var userHandler = helixHandler.getUserHandler();

    // Example Variables
    var exampleId = 164284617;          // User ID
    var exampleUsername = "MCmoderSD";  // Username

    // Get TwitchUser
    var user = userHandler.getTwitchUser(exampleId);                    // By ID
    var userByName = userHandler.getTwitchUser(exampleUsername);        // By Username

    // Check both are the same
    println("Users are the same: " + user.equals(userByName));

    // Print User Info
    println("\n------- Twitch User Info -------");
    println("User ID: " + user.getId());
    println("Username: " + user.getUsername());
    println("Created At: " + user.getCreatedAt());
    println("Display Name: " + user.getDisplayName());
    println("Description: " + user.getDescription());
    println("Profile Image URL: " + user.getProfileImageUrl());
    println("Offline Image URL: " + user.getOfflineImageUrl());
    println("Broadcaster Type: " + user.getBroadcasterType());
    println("User Type: " + user.getType());
    println("-----------------------------------\n");

    // Get User Email (requires USER_READ_EMAIL scope)
    var email = userHandler.getUserMail(user);                          // By TwitchUser
    println("Email: " + email);

    // Get User Email by ID and Username
    println("ID: " + userHandler.getUserMail(exampleId));            // By ID
    println("Name: " + userHandler.getUserMail(exampleUsername));    // By Username

    // Exit
    System.exit(0);
}