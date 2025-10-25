import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.objects.ChannelInfo;
import de.MCmoderSD.helix.handler.*;
import java.util.Arrays;

@SuppressWarnings("ALL")
public class InfoExample {

    public static void main(String[] args) {

        // Init HelixHandler
        HelixHandler helixHandler = AuthExample.initHelix();
        ChannelHandler channelHandler = helixHandler.getChannelHandler();
        UserHandler userHandler = helixHandler.getUserHandler();

        // Example Variables
        var exampleId = 164284617;              // User ID
        var exampleUsername = "MCmoderSD";      // Username/Display Name

        // Get Channel Info
        ChannelInfo infoById = channelHandler.getChannelInfo(exampleId);                        // By ID
        ChannelInfo infoByName = channelHandler.getChannelInfo(exampleUsername);                // By Username/Display Name
        ChannelInfo info = channelHandler.getChannelInfo(userHandler.getTwitchUser(exampleId)); // By TwitchUser

        // Check both are the same
        System.out.println("ChannelInfos are the same: " + infoById.equals(infoByName));

        // Print Channel Info
        System.out.println("\n------- Channel Info -------");
        System.out.println("Channel ID: " + info.getId());
        System.out.println("Display Name: " + info.getDisplayName());
        System.out.println("Title: " + info.getTitle());
        System.out.println("Game ID: " + info.getGameId());
        System.out.println("Game Name: " + info.getGameName());
        System.out.println("Language: " + info.getLanguage());
        System.out.println("Tags: " + Arrays.toString(info.getTags().toArray()));
        System.out.println("Branded Content: " + info.isBrandedContent());
        System.out.println("-----------------------------------\n");

        // Exit
        System.exit(0);
    }
}