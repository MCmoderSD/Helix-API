import de.MCmoderSD.helix.handler.*;

import examples.AuthExample;

void main() {

    // Init HelixHandler
    var helixHandler = AuthExample.initHelix();
    var userHandler = helixHandler.getUserHandler();
    var chatHandler = helixHandler.getChatHandler();

    // Example Variables
    var channel = userHandler.getTwitchUser("MCmoderSD");

    // Get Chatters
    var chatters = chatHandler.getChatters(channel);
    IO.println("Chatters in " + channel.getDisplayName() + "'s channel: " + chatters.size());
    for (var chatter : chatters) IO.println(" - " + chatter.getDisplayName() + " (ID: " + chatter.getId() + ")");

    // Exit
    System.exit(0);
}