import de.MCmoderSD.helix.handler.*;

import examples.AuthExample;

void main() {

    // Init HelixHandler
    var helixHandler = AuthExample.initHelix();
    var userHandler = helixHandler.getUserHandler();
    var streamHandler = helixHandler.getStreamHandler();

    // Get Channels
    var user = userHandler.getTwitchUser("MCmoderSD");      // Channel to raid to      (target)
    var channel = userHandler.getTwitchUser("ModersEsel");  // Channel to raid from    (source)

    // Cancel Raid
    streamHandler.cancelRaid(channel);

    // Start Raid
    streamHandler.startRaid(user, channel);

    // Exit
    System.exit(0);
}