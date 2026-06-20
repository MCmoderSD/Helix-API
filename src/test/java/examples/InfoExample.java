import de.MCmoderSD.helix.handler.*;

import examples.AuthExample;

import java.util.Arrays;

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
    IO.println("ChannelInfos are the same: " + infoById.equals(infoByName));

    // Print Channel Info
    IO.println("\n------- Channel Info -------");
    IO.println("Channel ID: " + info.getId());
    IO.println("Display Name: " + info.getDisplayName());
    IO.println("Title: " + info.getTitle());
    IO.println("Game ID: " + info.getGameId());
    IO.println("Game Name: " + info.getGameName());
    IO.println("Language: " + info.getLanguage());
    IO.println("Tags: " + Arrays.toString(info.getTags().toArray()));
    IO.println("Branded Content: " + info.isBrandedContent());
    IO.println("-----------------------------------\n");

    // Exit
    System.exit(0);
}