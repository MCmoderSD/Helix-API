import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.handler.RoleHandler;
import de.MCmoderSD.helix.handler.UserHandler;
import de.MCmoderSD.helix.objects.*;

import java.util.HashSet;

public class RoleCheck {

    public static void main(String[] args) {

        // Init HelixHandler
        HelixHandler helixHandler = AuthExample.initHelix();
        UserHandler userHandler = helixHandler.getUserHandler();
        RoleHandler roleHandler = helixHandler.getRoleHandler();

        String channelNameA = "mcmodersd";
        String channelNameB = "modersesel";

        TwitchUser channelA = new TwitchUser(userHandler.getUser(channelNameA));
        TwitchUser channelB = new TwitchUser(userHandler.getUser(channelNameB));

        modCheck(channelA, channelB, roleHandler);
        System.out.println("\n----------------------------------------");
        editorCheck(channelA, channelB, roleHandler);
        System.out.println("\n----------------------------------------");
        vipCheck(channelA, channelB, roleHandler);
        System.out.println("\n----------------------------------------");
        subscriberCheck(channelA, channelB, roleHandler);
        System.out.println("\n----------------------------------------");
        followerCheck(channelA, channelB, roleHandler);
    }

    private static void modCheck(TwitchUser channelA, TwitchUser channelB, RoleHandler roleHandler) {
        HashSet<ChannelModerator> modsA = roleHandler.getModerators(channelA);
        HashSet<ChannelModerator> modsB = roleHandler.getModerators(channelB);

        System.out.println("Moderators in: " + channelA.getDisplayName() + " (" + channelA.getId() + ")");
        for (ChannelModerator mod : modsA) System.out.println(" - " + mod.getDisplayName());

        System.out.println("\nModerators in: " + channelB.getDisplayName() + " (" + channelB.getId() + ")");
        for (ChannelModerator mod : modsB) System.out.println(" - " + mod.getDisplayName());
    }

    private static void editorCheck(TwitchUser channelA, TwitchUser channelB, RoleHandler roleHandler) {
        HashSet<ChannelEditor> editorsA = roleHandler.getEditors(channelA);
        HashSet<ChannelEditor> editorsB = roleHandler.getEditors(channelB);

        System.out.println("\nEditors in: " + channelA.getDisplayName() + " (" + channelA.getId() + ")");
        for (ChannelEditor editor : editorsA) System.out.println(" - " + editor.getDisplayName());

        System.out.println("\nEditors in: " + channelB.getDisplayName() + " (" + channelB.getId() + ")");
        for (ChannelEditor editor : editorsB) System.out.println(" - " + editor.getDisplayName());
    }

    private static void vipCheck(TwitchUser channelA, TwitchUser channelB, RoleHandler roleHandler) {
        HashSet<ChannelVip> vipsA = roleHandler.getVIPs(channelA);
        HashSet<ChannelVip> vipsB = roleHandler.getVIPs(channelB);

        System.out.println("\nVIPs in: " + channelA.getDisplayName() + " (" + channelA.getId() + ")");
        for (TwitchUser vip : vipsA) System.out.println(" - " + vip.getDisplayName());

        System.out.println("\nVIPs in: " + channelB.getDisplayName() + " (" + channelB.getId() + ")");
        for (TwitchUser vip : vipsB) System.out.println(" - " + vip.getDisplayName());
    }

    private static void subscriberCheck(TwitchUser channelA, TwitchUser channelB, RoleHandler roleHandler) {
        HashSet<ChannelSubscriber> subscribersA = roleHandler.getSubscribers(channelA);
        HashSet<ChannelSubscriber> subscribersB = roleHandler.getSubscribers(channelB);

        System.out.println("\nSubscribers in: " + channelA.getDisplayName() + " (" + channelA.getId() + ")");
        for (ChannelSubscriber sub : subscribersA) System.out.println(" - " + sub.getDisplayName() + " (" + sub.getTier() + ")");

        System.out.println("\nSubscribers in: " + channelB.getDisplayName() + " (" + channelB.getId() + ")");
        for (ChannelSubscriber sub : subscribersB) System.out.println(" - " + sub.getDisplayName() + " (" + sub.getTier() + ")");
    }

    private static void followerCheck(TwitchUser channelA, TwitchUser channelB, RoleHandler roleHandler) {
        HashSet<ChannelFollower> followersA = roleHandler.getFollowers(channelA);
        HashSet<ChannelFollower> followersB = roleHandler.getFollowers(channelB);

        System.out.println("\nFollowers in: " + channelA.getDisplayName() + " (" + channelA.getId() + ")");
        for (ChannelFollower follower : followersA) System.out.println(" - " + follower.getDisplayName() + " (since " + follower.getFollowedAt() + ")");

        System.out.println("\nFollowers in: " + channelB.getDisplayName() + " (" + channelB.getId() + ")");
        for (ChannelFollower follower : followersB) System.out.println(" - " + follower.getDisplayName() + " (since " + follower.getFollowedAt() + ")");
    }
}