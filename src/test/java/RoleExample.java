import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.handler.*;
import de.MCmoderSD.helix.objects.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("ALL")
public class RoleExample {

    public static void main(String[] args) {

        // Init Handlers
        HelixHandler helixHandler = AuthExample.initHelix();
        UserHandler userHandler = helixHandler.getUserHandler();
        RoleHandler roleHandler = helixHandler.getRoleHandler();

        // Example Users
        TwitchUser channel = userHandler.getTwitchUser("MCmoderSD");            // Your Channel Here (needs to be authenticated)
        TwitchUser exampleMod = userHandler.getTwitchUser("LangerLanzenLeo");   // Example Moderator
        TwitchUser exampleEditor = userHandler.getTwitchUser("r4kunnn");        // Example Editor
        TwitchUser exampleVIP = userHandler.getTwitchUser("RebixDev");          // Example VIP
        TwitchUser exampleSubscriber = userHandler.getTwitchUser("seybiiiii");  // Example Subscriber
        TwitchUser exampleFollower = userHandler.getTwitchUser("RedSmileTV");   // Example Follower
        TwitchUser exampleUser = userHandler.getTwitchUser("ModersEsel");       // Example User


        // Promote VIP to Moderator
        System.out.println("\nPromoting " + exampleVIP.getDisplayName() + " from VIP to Moderator...");
        System.out.println(" - Before Promotion: Moderator: " + roleHandler.isModerator(exampleVIP, channel) + " | Is VIP: " + roleHandler.isVIP(exampleVIP, channel));
        System.out.println(" - Success: " + roleHandler.addModerator(exampleVIP, channel));
        System.out.println(" - After Promotion: Moderator: " + roleHandler.isModerator(exampleVIP, channel) + " | Is VIP: " + roleHandler.isVIP(exampleVIP, channel));

        // Remove Moderator
        System.out.println("\nRemoving " + exampleMod.getDisplayName() + " from Moderator role...");
        System.out.println(" - Before Removal: Is Moderator: " + roleHandler.isModerator(exampleMod, channel) + " | Is VIP: " + roleHandler.isVIP(exampleMod, channel));
        System.out.println(" - Success: " + roleHandler.removeModerator(exampleMod, channel));
        System.out.println(" - After Removal: Is Moderator: " + roleHandler.isModerator(exampleMod, channel) + " | Is VIP: " + roleHandler.isVIP(exampleMod, channel));

        // Make user VIP
        System.out.println("\nAdding " + exampleUser.getDisplayName() + " to VIPs...");
        System.out.println(" - Before: Is VIP: " + roleHandler.isVIP(exampleUser, channel) + " | Is Moderator: " + roleHandler.isModerator(exampleUser, channel));
        System.out.println(" - Success: " + roleHandler.addVIP(exampleUser, channel));
        System.out.println(" - After: Is VIP: " + roleHandler.isVIP(exampleUser, channel) + " | Is Moderator: " + roleHandler.isModerator(exampleUser, channel));

        // Reset to original state
        System.out.println("\nResetting roles to original state...");
        System.out.println(" - Re-adding " + exampleMod.getDisplayName() + " to Moderators: " + roleHandler.addModerator(exampleMod, channel));     // Re-add Moderator
        System.out.println(" - Demoting " + exampleVIP.getDisplayName() + " from Moderator to VIP: " + roleHandler.addVIP(exampleVIP, channel));    // Demote to VIP from Moderator
        System.out.println(" - Removing " + exampleUser.getDisplayName() + " from VIPs: " + roleHandler.removeVIP(exampleUser, channel));           // Remove VIP
        System.out.println("\n\n--------------------------------------\n\n");


        // Check Roles for Example Users
        System.out.println("Checking roles for example users:");
        System.out.println(" - " + exampleMod.getDisplayName() + " is moderator: " + roleHandler.isModerator(exampleMod, channel));
        System.out.println(" - " + exampleEditor.getDisplayName() + " is editor: " + roleHandler.isEditor(exampleEditor, channel));
        System.out.println(" - " + exampleVIP.getDisplayName() + " is VIP: " + roleHandler.isVIP(exampleVIP, channel));
        System.out.println(" - " + exampleSubscriber.getDisplayName() + " is subscriber: " + roleHandler.isSubscriber(exampleSubscriber, channel));
        System.out.println(" - " + exampleFollower.getDisplayName() + " is follower: " + roleHandler.isFollower(exampleFollower, channel));
        System.out.println("\n\n--------------------------------------\n\n");


        // Get Roles Lists
        HashSet<ChannelModerator> moderators = roleHandler.getModerators(channel);      // Get all moderators
        HashSet<ChannelEditor> editors = roleHandler.getEditors(channel);               // Get all editors
        HashSet<ChannelVip> vips = roleHandler.getVIPs(channel);                        // Get all VIPs
        HashSet<ChannelSubscriber> subscribers = roleHandler.getSubscribers(channel);   // Get all subscribers
        HashSet<ChannelFollower> followers = roleHandler.getFollowers(channel);         // Get all followers

        // Print Sizes
        System.out.println("Found Roles for " + channel.getDisplayName() + ":");
        System.out.println(" - Moderators: " + moderators.size());
        System.out.println(" - Editors: " + editors.size());
        System.out.println(" - VIPs: " + vips.size());
        System.out.println(" - Subscribers: " + subscribers.size());
        System.out.println(" - Followers: " + followers.size());
        System.out.println("\n\n--------------------------------------\n\n");


        // Check Roles for Multiple Users
        HashSet<TwitchUser> twitchUsers = new HashSet<>(List.of(new TwitchUser[]{ channel, exampleMod, exampleEditor, exampleVIP, exampleSubscriber, exampleFollower, exampleUser }));
        HashMap<TwitchUser, Boolean> modMap = roleHandler.checkModerators(twitchUsers, channel);            // Check Moderators
        HashMap<TwitchUser, Boolean> editorMap = roleHandler.checkEditors(twitchUsers, channel);            // Check Editors
        HashMap<TwitchUser, Boolean> vipMap = roleHandler.checkVIPs(twitchUsers, channel);                  // Check VIPs
        HashMap<TwitchUser, Boolean> subscriberMap = roleHandler.checkSubscribers(twitchUsers, channel);    // Check Subscribers
        HashMap<TwitchUser, Boolean> followerMap = roleHandler.checkFollowers(twitchUsers, channel);        // Check Followers

        // Print positive checks
        System.out.println("\nModerator Check:");
        for (var entry : modMap.entrySet()) if (entry.getValue()) System.out.println(" - " + entry.getKey().getDisplayName() + " is a moderator");
        System.out.println("\nEditor Check:");
        for (var entry : editorMap.entrySet()) if (entry.getValue()) System.out.println(" - " + entry.getKey().getDisplayName() + " is an editor");
        System.out.println("\nVIP Check:");
        for (var entry : vipMap.entrySet()) if (entry.getValue()) System.out.println(" - " + entry.getKey().getDisplayName() + " is a VIP");
        System.out.println("\nSubscriber Check:");
        for (var entry : subscriberMap.entrySet()) if (entry.getValue()) System.out.println(" - " + entry.getKey().getDisplayName() + " is a subscriber");
        System.out.println("\nFollower Check:");
        for (var entry : followerMap.entrySet()) if (entry.getValue()) System.out.println(" - " + entry.getKey().getDisplayName() + " is a follower");

        // Exit
        System.exit(0);
    }
}