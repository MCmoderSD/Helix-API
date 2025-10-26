package examples;

import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.objects.TwitchUser;
import de.MCmoderSD.helix.handler.*;

@SuppressWarnings("ALL")
public class ShoutoutExample {

    public static void main(String[] args) {

        // Init HelixHandler
        HelixHandler helixHandler = AuthExample.initHelix();
        UserHandler userHandler = helixHandler.getUserHandler();
        StreamHandler streamHandler = helixHandler.getStreamHandler();

        // Get Channels
        TwitchUser user = userHandler.getTwitchUser("MCmoderSD");       // Channel to send shoutout to      (target)
        TwitchUser channel = userHandler.getTwitchUser("ModersEsel");   // Channel to send shoutout from    (source)

        // Send Shoutout
        streamHandler.sendShoutout(user, channel);

        // Exit
        System.exit(0);
    }
}