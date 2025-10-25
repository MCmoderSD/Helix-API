import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.objects.TwitchUser;
import de.MCmoderSD.helix.handler.*;

@SuppressWarnings("ALL")
public class ShoutoutExample {

    /**
     * <p>Preconditions for sending a shoutout:</p>
     * <ul>
     *   <li>The source channel must be live and have at least one viewer.</li>
     *   <li>The calling credentials must include the {@code MODERATOR_MANAGE_SHOUTOUTS} scope for the source channel.</li>
     * </ul>
     */
    public static void main(String[] args) {

        // Init HelixHandler
        HelixHandler helixHandler = AuthExample.initHelix();
        ChannelHandler channelHandler = helixHandler.getChannelHandler();
        UserHandler userHandler = helixHandler.getUserHandler();

        // Get Channels
        TwitchUser user = userHandler.getTwitchUser("MCmoderSD");       // Channel to send shoutout to      (target)
        TwitchUser channel = userHandler.getTwitchUser("Modersesel");   // Channel to send shoutout from    (source)

        // Send Shoutout
        channelHandler.sendShoutout(user, channel);

        // Exit
        System.exit(0);
    }
}