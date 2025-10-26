package examples;

import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.objects.TwitchUser;
import de.MCmoderSD.helix.handler.*;

import java.util.HashSet;

@SuppressWarnings("ALL")
public class ChatExample {

    public static void main(String[] args) {

        // Init HelixHandler
        HelixHandler helixHandler = AuthExample.initHelix();
        UserHandler userHandler = helixHandler.getUserHandler();
        ChatHandler chatHandler = helixHandler.getChatHandler();

        // Example Variables
        TwitchUser channel = userHandler.getTwitchUser("MCmoderSD");

        // Get Chatters
        HashSet<TwitchUser> chatters = chatHandler.getChatters(channel);
        System.out.println("Chatters in " + channel.getDisplayName() + "'s channel: " + chatters.size());
        for (TwitchUser chatter : chatters) System.out.println(" - " + chatter.getDisplayName() + " (ID: " + chatter.getId() + ")");

        // Exit
        System.exit(0);
    }
}