import de.MCmoderSD.helix.handler.*;
import de.MCmoderSD.helix.objects.*;

import examples.AuthExample;

import java.util.HashSet;
import java.util.List;

import static java.lang.IO.println;

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
    println("\nPromoting " + exampleVIP.getDisplayName() + " from VIP to Moderator...");
    println(" - Before Promotion: Moderator: " + roleHandler.isModerator(exampleVIP, channel) + " | Is VIP: " + roleHandler.isVIP(exampleVIP, channel));
    println(" - Success: " + roleHandler.addModerator(exampleVIP, channel));
    println(" - After Promotion: Moderator: " + roleHandler.isModerator(exampleVIP, channel) + " | Is VIP: " + roleHandler.isVIP(exampleVIP, channel));

    // Remove Moderator
    println("\nRemoving " + exampleMod.getDisplayName() + " from Moderator role...");
    println(" - Before Removal: Is Moderator: " + roleHandler.isModerator(exampleMod, channel) + " | Is VIP: " + roleHandler.isVIP(exampleMod, channel));
    println(" - Success: " + roleHandler.removeModerator(exampleMod, channel));
    println(" - After Removal: Is Moderator: " + roleHandler.isModerator(exampleMod, channel) + " | Is VIP: " + roleHandler.isVIP(exampleMod, channel));

    // Make user VIP
    println("\nAdding " + exampleUser.getDisplayName() + " to VIPs...");
    println(" - Before: Is VIP: " + roleHandler.isVIP(exampleUser, channel) + " | Is Moderator: " + roleHandler.isModerator(exampleUser, channel));
    println(" - Success: " + roleHandler.addVIP(exampleUser, channel));
    println(" - After: Is VIP: " + roleHandler.isVIP(exampleUser, channel) + " | Is Moderator: " + roleHandler.isModerator(exampleUser, channel));

    // Reset to original state
    println("\nResetting roles to original state...");
    println(" - Re-adding " + exampleMod.getDisplayName() + " to Moderators: " + roleHandler.addModerator(exampleMod, channel));     // Re-add Moderator
    println(" - Demoting " + exampleVIP.getDisplayName() + " from Moderator to VIP: " + roleHandler.addVIP(exampleVIP, channel));    // Demote to VIP from Moderator
    println(" - Removing " + exampleUser.getDisplayName() + " from VIPs: " + roleHandler.removeVIP(exampleUser, channel));           // Remove VIP
    println("\n\n--------------------------------------\n\n");


    // Check Roles for Example Users
    println("Checking roles for example users:");
    println(" - " + exampleMod.getDisplayName() + " is moderator: " + roleHandler.isModerator(exampleMod, channel));
    println(" - " + exampleEditor.getDisplayName() + " is editor: " + roleHandler.isEditor(exampleEditor, channel));
    println(" - " + exampleVIP.getDisplayName() + " is VIP: " + roleHandler.isVIP(exampleVIP, channel));
    println(" - " + exampleSubscriber.getDisplayName() + " is subscriber: " + roleHandler.isSubscriber(exampleSubscriber, channel));
    println(" - " + exampleFollower.getDisplayName() + " is follower: " + roleHandler.isFollower(exampleFollower, channel));
    println("\n\n--------------------------------------\n\n");


    // Get Roles Lists
    var moderators = roleHandler.getModerators(channel);    // Get all moderators
    var editors = roleHandler.getEditors(channel);          // Get all editors
    var vips = roleHandler.getVIPs(channel);                // Get all VIPs
    var subscribers = roleHandler.getSubscribers(channel);  // Get all subscribers
    var followers = roleHandler.getFollowers(channel);      // Get all followers

    // Print Sizes
    println("Found Roles for " + channel.getDisplayName() + ":");
    println(" - Moderators: " + moderators.size());
    println(" - Editors: " + editors.size());
    println(" - VIPs: " + vips.size());
    println(" - Subscribers: " + subscribers.size());
    println(" - Followers: " + followers.size());
    println("\n\n--------------------------------------\n\n");


    // Check Roles for Multiple Users
    var twitchUsers = new HashSet<>(List.of(new TwitchUser[]{ channel, exampleMod, exampleEditor, exampleVIP, exampleSubscriber, exampleFollower, exampleUser }));
    var modMap = roleHandler.checkModerators(twitchUsers, channel);         // Check Moderators
    var editorMap = roleHandler.checkEditors(twitchUsers, channel);         // Check Editors
    var vipMap = roleHandler.checkVIPs(twitchUsers, channel);               // Check VIPs
    var subscriberMap = roleHandler.checkSubscribers(twitchUsers, channel); // Check Subscribers
    var followerMap = roleHandler.checkFollowers(twitchUsers, channel);     // Check Followers

    // Print positive checks
    println("\nModerator Check:");
    for (var entry : modMap.entrySet()) if (entry.getValue()) println(" - " + entry.getKey().getDisplayName() + " is a moderator");

    println("\nEditor Check:");
    for (var entry : editorMap.entrySet()) if (entry.getValue()) println(" - " + entry.getKey().getDisplayName() + " is an editor");

    println("\nVIP Check:");
    for (var entry : vipMap.entrySet()) if (entry.getValue()) println(" - " + entry.getKey().getDisplayName() + " is a VIP");

    println("\nSubscriber Check:");
    for (var entry : subscriberMap.entrySet()) if (entry.getValue()) println(" - " + entry.getKey().getDisplayName() + " is a subscriber");

    println("\nFollower Check:");
    for (var entry : followerMap.entrySet()) if (entry.getValue()) println(" - " + entry.getKey().getDisplayName() + " is a follower");

    // Exit
    System.exit(0);
}