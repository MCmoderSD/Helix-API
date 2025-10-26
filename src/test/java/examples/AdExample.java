package examples;

import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.objects.TwitchUser;
import de.MCmoderSD.helix.handler.*;

import static de.MCmoderSD.helix.handler.StreamHandler.CommercialLength.LENGTH_30;

@SuppressWarnings("ALL")
public class AdExample {

    public static void main(String[] args) {

        // Init HelixHandler
        HelixHandler helixHandler = AuthExample.initHelix();
        UserHandler userHandler = helixHandler.getUserHandler();
        StreamHandler streamHandler = helixHandler.getStreamHandler();

        // Get Channel
        TwitchUser channel = userHandler.getTwitchUser("MCmoderSD");    // Channel to run ad on

        // Run Ad
        streamHandler.runCommercial(channel, LENGTH_30);

        // Exit
        System.exit(0);
    }
}