import de.MCmoderSD.helix.handler.*;

import examples.AuthExample;

import java.util.Arrays;

import static java.lang.IO.println;

void main() {

    // Init HelixHandler
    var helixHandler = AuthExample.initHelix();
    var userHandler = helixHandler.getUserHandler();
    var channelHandler = helixHandler.getChannelHandler();

    // Example Variables
    var exampleId = 164284617;          // User ID
    var exampleUsername = "MCmoderSD";  // Username

    // Get Channel Info
    var infoById = channelHandler.getChannelInfo(exampleId);                        // By ID
    var infoByName = channelHandler.getChannelInfo(exampleUsername);                // By Username
    var info = channelHandler.getChannelInfo(userHandler.getTwitchUser(exampleId)); // By TwitchUser

    // Check both are the same
    println("ChannelInfos are the same: " + infoById.equals(infoByName));

    // Print Channel Info
    println("\n------- Channel Info -------");
    println("Channel ID: " + info.getId());
    println("Display Name: " + info.getDisplayName());
    println("Title: " + info.getTitle());
    println("Game ID: " + info.getGameId());
    println("Game Name: " + info.getGameName());
    println("Language: " + info.getLanguage());
    println("Tags: " + Arrays.toString(info.getTags().toArray()));
    println("Branded Content: " + info.isBrandedContent());
    println("-----------------------------------\n");

    // Exit
    System.exit(0);
}