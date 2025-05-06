import com.fasterxml.jackson.databind.JsonNode;
import de.MCmoderSD.helix.core.Client;
import de.MCmoderSD.helix.enums.Scope;
import de.MCmoderSD.helix.objects.ChannelFollower;
import de.MCmoderSD.json.JsonUtility;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {

        // Load Config
        JsonNode config = JsonUtility.loadJson("/config.json", false);

        // Credentials
        String clientId = config.get("clientId").asText();
        String clientSecret = config.get("clientSecret").asText();

        // Initialize API Client
        Client client = new Client(clientId, clientSecret);
        System.out.println(client.getTokenManager().getAuthorizationUrl(Scope.values()));

        var userHandler = client.getUserHandler();
        var roleHandler = client.getRoleHandler();

        // Get UserID
        var user = userHandler.getTwitchUser("r4kunnn");
        System.out.println("User: " + user.getDisplayName() + " (" + user.getId() + ")");

        // Get Moderators
        var moderators = roleHandler.getModerators(user.getId(), null);
        System.out.println("\nModerators: " + moderators.size());
        for (var moderator : moderators) System.out.println(String.format(" - %s (%d) - %s",
                moderator.getDisplayName(),
                moderator.getId(),
                moderator.getBroadcasterType()
        ));

        // Get Editors
        var editors = roleHandler.getEditors(user.getId());
        System.out.println("\nEditors: " + editors.size());
        for (var editor : editors) System.out.println(String.format(" - %s (%d) - %s",
                editor.getDisplayName(),
                editor.getId(),
                editor.getBroadcasterType()
        ));

        // Get Vips
        var vips = roleHandler.getVIPs(user.getId(), null);
        System.out.println("\nVIPs: " + vips.size());
        for (var vip : vips) System.out.println(String.format(" - %s (%d) - %s",
                vip.getDisplayName(),
                vip.getId(),
                vip.getBroadcasterType()
        ));


        // Get Subscriber
        var subscriber = roleHandler.getSubscribers(user.getId(), null);
        System.out.println("\nSubscriber: " + subscriber.size());
        for (var sub : subscriber) sub.print();


        // Get Follower
        var followers = roleHandler.getFollowers(user.getId(), null);
        var sortedFollowers = new ArrayList<>(followers);
        sortedFollowers.sort((o1, o2) -> {
            if (o1.getFollowedAt() == null || o2.getFollowedAt() == null) return 0;
            return o1.getFollowedAt().compareTo(o2.getFollowedAt());
        });
        System.out.println("\nFollowers: " + followers.size());
        sortedFollowers.forEach(ChannelFollower::print);
    }
}