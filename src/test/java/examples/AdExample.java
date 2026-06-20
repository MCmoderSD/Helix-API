import de.MCmoderSD.helix.handler.*;

import examples.AuthExample;

import static de.MCmoderSD.helix.handler.StreamHandler.CommercialLength.LENGTH_30;

void main() {

    // Init HelixHandler
    var helixHandler = AuthExample.initHelix();
    var userHandler = helixHandler.getUserHandler();
    var streamHandler = helixHandler.getStreamHandler();

    // Get Channel
    var channel = userHandler.getTwitchUser("MCmoderSD"); // Channel to run ad on

    // Run Ad
    streamHandler.runCommercial(channel, LENGTH_30);

    // Exit
    System.exit(0);
}