import de.MCmoderSD.helix.handler.*;

import examples.AuthExample;

import static java.lang.IO.println;

void main() {

    // Init HelixHandler
    var helixHandler = AuthExample.initHelix();
    var userHandler = helixHandler.getUserHandler();
    var chatHandler = helixHandler.getChatHandler();

    // Example Variables
    var channel = userHandler.getTwitchUser("MCmoderSD");

    // Get Chatters
    var chatters = chatHandler.getChatters(channel);
    println("Chatters in " + channel.getDisplayName() + "'s channel: " + chatters.size());
    for (var chatter : chatters) println(" - " + chatter.getDisplayName() + " (ID: " + chatter.getId() + ")");

    // Exit
    System.exit(0);
}