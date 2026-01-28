import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.objects.TwitchUser;
import de.MCmoderSD.helix.handler.*;

import examples.AuthExample;

import java.util.HashSet;

void main() {

    // Init HelixHandler
    HelixHandler helixHandler = AuthExample.initHelix();
    UserHandler userHandler = helixHandler.getUserHandler();
    ChatHandler chatHandler = helixHandler.getChatHandler();

    // Example Variables
    TwitchUser channel = userHandler.getTwitchUser("MCmoderSD");

    // Get Chatters
    HashSet<TwitchUser> chatters = chatHandler.getChatters(channel);
    IO.println("Chatters in " + channel.getDisplayName() + "'s channel: " + chatters.size());
    for (TwitchUser chatter : chatters) IO.println(" - " + chatter.getDisplayName() + " (ID: " + chatter.getId() + ")");

    // Exit
    System.exit(0);
}