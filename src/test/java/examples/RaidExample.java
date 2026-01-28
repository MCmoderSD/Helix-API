import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.objects.TwitchUser;
import de.MCmoderSD.helix.handler.*;

import examples.AuthExample;

void main() {

    // Init HelixHandler
    HelixHandler helixHandler = AuthExample.initHelix();
    UserHandler userHandler = helixHandler.getUserHandler();
    StreamHandler streamHandler = helixHandler.getStreamHandler();

    // Get Channels
    TwitchUser user = userHandler.getTwitchUser("MCmoderSD");       // Channel to raid to      (target)
    TwitchUser channel = userHandler.getTwitchUser("ModersEsel");   // Channel to raid from    (source)

    // Cancel Raid
    streamHandler.cancelRaid(channel);

    // Start Raid
    streamHandler.startRaid(user, channel);

    // Exit
    System.exit(0);
}