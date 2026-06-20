import de.MCmoderSD.helix.handler.*;
import de.MCmoderSD.helix.objects.*;

import examples.AuthExample;

import java.util.HashSet;
import java.util.List;

void main() {

    // Init Handlers
    var helixHandler = AuthExample.initHelix();
    var userHandler = helixHandler.getUserHandler();
    var roleHandler = helixHandler.getRoleHandler();

    // Example Users
    var channel = userHandler.getTwitchUser("MCmoderSD");           // Your Channel Here (needs to be authenticated)
    var exampleMod = userHandler.getTwitchUser("LangerLanzenLeo");  // Example Moderator
    var exampleEditor = userHandler.getTwitchUser("r4kunnn");       // Example Editor
    var exampleVIP = userHandler.getTwitchUser("RebixDev");         // Example VIP
    var exampleSubscriber = userHandler.getTwitchUser("seybiiiii"); // Example Subscriber
    var exampleFollower = userHandler.getTwitchUser("RedSmileTV");  // Example Follower
    var exampleUser = userHandler.getTwitchUser("ModersEsel");      // Example User


    // Promote VIP to Moderator
    IO.println("\nPromoting " + exampleVIP.getDisplayName() + " from VIP to Moderator...");
    IO.println(" - Before Promotion: Moderator: " + roleHandler.isModerator(exampleVIP, channel) + " | Is VIP: " + roleHandler.isVIP(exampleVIP, channel));
    IO.println(" - Success: " + roleHandler.addModerator(exampleVIP, channel));
    IO.println(" - After Promotion: Moderator: " + roleHandler.isModerator(exampleVIP, channel) + " | Is VIP: " + roleHandler.isVIP(exampleVIP, channel));

    // Remove Moderator
    IO.println("\nRemoving " + exampleMod.getDisplayName() + " from Moderator role...");
    IO.println(" - Before Removal: Is Moderator: " + roleHandler.isModerator(exampleMod, channel) + " | Is VIP: " + roleHandler.isVIP(exampleMod, channel));
    IO.println(" - Success: " + roleHandler.removeModerator(exampleMod, channel));
    IO.println(" - After Removal: Is Moderator: " + roleHandler.isModerator(exampleMod, channel) + " | Is VIP: " + roleHandler.isVIP(exampleMod, channel));

    // Make user VIP
    IO.println("\nAdding " + exampleUser.getDisplayName() + " to VIPs...");
    IO.println(" - Before: Is VIP: " + roleHandler.isVIP(exampleUser, channel) + " | Is Moderator: " + roleHandler.isModerator(exampleUser, channel));
    IO.println(" - Success: " + roleHandler.addVIP(exampleUser, channel));
    IO.println(" - After: Is VIP: " + roleHandler.isVIP(exampleUser, channel) + " | Is Moderator: " + roleHandler.isModerator(exampleUser, channel));

    // Reset to original state
    IO.println("\nResetting roles to original state...");
    IO.println(" - Re-adding " + exampleMod.getDisplayName() + " to Moderators: " + roleHandler.addModerator(exampleMod, channel));     // Re-add Moderator
    IO.println(" - Demoting " + exampleVIP.getDisplayName() + " from Moderator to VIP: " + roleHandler.addVIP(exampleVIP, channel));    // Demote to VIP from Moderator
    IO.println(" - Removing " + exampleUser.getDisplayName() + " from VIPs: " + roleHandler.removeVIP(exampleUser, channel));           // Remove VIP
    IO.println("\n\n--------------------------------------\n\n");


    // Check Roles for Example Users
    IO.println("Checking roles for example users:");
    IO.println(" - " + exampleMod.getDisplayName() + " is moderator: " + roleHandler.isModerator(exampleMod, channel));
    IO.println(" - " + exampleEditor.getDisplayName() + " is editor: " + roleHandler.isEditor(exampleEditor, channel));
    IO.println(" - " + exampleVIP.getDisplayName() + " is VIP: " + roleHandler.isVIP(exampleVIP, channel));
    IO.println(" - " + exampleSubscriber.getDisplayName() + " is subscriber: " + roleHandler.isSubscriber(exampleSubscriber, channel));
    IO.println(" - " + exampleFollower.getDisplayName() + " is follower: " + roleHandler.isFollower(exampleFollower, channel));
    IO.println("\n\n--------------------------------------\n\n");


    // Get Roles Lists
    var moderators = roleHandler.getModerators(channel);    // Get all moderators
    var editors = roleHandler.getEditors(channel);          // Get all editors
    var vips = roleHandler.getVIPs(channel);                // Get all VIPs
    var subscribers = roleHandler.getSubscribers(channel);  // Get all subscribers
    var followers = roleHandler.getFollowers(channel);      // Get all followers

    // Print Sizes
    IO.println("Found Roles for " + channel.getDisplayName() + ":");
    IO.println(" - Moderators: " + moderators.size());
    IO.println(" - Editors: " + editors.size());
    IO.println(" - VIPs: " + vips.size());
    IO.println(" - Subscribers: " + subscribers.size());
    IO.println(" - Followers: " + followers.size());
    IO.println("\n\n--------------------------------------\n\n");


    // Check Roles for Multiple Users
    var twitchUsers = new HashSet<>(List.of(new TwitchUser[]{ channel, exampleMod, exampleEditor, exampleVIP, exampleSubscriber, exampleFollower, exampleUser }));
    var modMap = roleHandler.checkModerators(twitchUsers, channel);         // Check Moderators
    var editorMap = roleHandler.checkEditors(twitchUsers, channel);         // Check Editors
    var vipMap = roleHandler.checkVIPs(twitchUsers, channel);               // Check VIPs
    var subscriberMap = roleHandler.checkSubscribers(twitchUsers, channel); // Check Subscribers
    var followerMap = roleHandler.checkFollowers(twitchUsers, channel);     // Check Followers

    // Print positive checks
    IO.println("\nModerator Check:");
    for (var entry : modMap.entrySet()) if (entry.getValue()) IO.println(" - " + entry.getKey().getDisplayName() + " is a moderator");

    IO.println("\nEditor Check:");
    for (var entry : editorMap.entrySet()) if (entry.getValue()) IO.println(" - " + entry.getKey().getDisplayName() + " is an editor");

    IO.println("\nVIP Check:");
    for (var entry : vipMap.entrySet()) if (entry.getValue()) IO.println(" - " + entry.getKey().getDisplayName() + " is a VIP");

    IO.println("\nSubscriber Check:");
    for (var entry : subscriberMap.entrySet()) if (entry.getValue()) IO.println(" - " + entry.getKey().getDisplayName() + " is a subscriber");

    IO.println("\nFollower Check:");
    for (var entry : followerMap.entrySet()) if (entry.getValue()) IO.println(" - " + entry.getKey().getDisplayName() + " is a follower");

    // Exit
    System.exit(0);
}