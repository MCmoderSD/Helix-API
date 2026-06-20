import de.MCmoderSD.helix.handler.*;

import examples.AuthExample;

void main() {

    // Init HelixHandler
    var helixHandler = AuthExample.initHelix();
    var userHandler = helixHandler.getUserHandler();
    var streamHandler = helixHandler.getStreamHandler();

    // Get Channels
    var user = userHandler.getTwitchUser("MCmoderSD");      // Channel to send shoutout to      (target)
    var channel = userHandler.getTwitchUser("ModersEsel");  // Channel to send shoutout from    (source)

    // Send Shoutout
    streamHandler.sendShoutout(user, channel);

    // Exit
    System.exit(0);
}