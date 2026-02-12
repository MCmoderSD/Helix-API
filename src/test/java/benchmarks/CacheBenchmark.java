import de.MCmoderSD.helix.core.HelixHandler;
import de.MCmoderSD.helix.enums.Scope;
import de.MCmoderSD.helix.objects.TwitchUser;
import de.MCmoderSD.helix.handler.UserHandler;
import de.MCmoderSD.helix.handler.RoleHandler;
import de.MCmoderSD.json.JsonUtility;
import de.MCmoderSD.server.core.Server;

import tools.jackson.databind.JsonNode;

import static de.MCmoderSD.helix.enums.Scope.MODERATION_READ;
import static de.MCmoderSD.helix.enums.Scope.CHANNEL_READ_VIPS;
import static de.MCmoderSD.helix.enums.Scope.CHANNEL_READ_SUBSCRIPTIONS;
import static de.MCmoderSD.helix.enums.Scope.MODERATOR_READ_FOLLOWERS;

void main() {

    // Declare Benchmark Variables
    long configStartTime, configEndTime;        // Configuration Load Time
    long serverStartTime, serverEndTime;        // Server Initialization Time
    long helixInitStartTime, helixInitEndTime;  // HelixHandler Initialization Time

    // Load Config
    configStartTime = System.nanoTime();
    JsonNode config = JsonUtility.getInstance().loadResource("/config.json");

    // Load Config
    JsonNode applicationConfig = config.get("twitch").get("application");
    JsonNode databaseConfig = config.get("database");
    JsonNode serverConfig = config.get("server");
    configEndTime = System.nanoTime();


    // Init Server
    serverStartTime = System.nanoTime();
    Server server = new Server(serverConfig);
    server.start();
    serverEndTime = System.nanoTime();

    // Init HelixHandler
    helixInitStartTime = System.nanoTime();
    HelixHandler helixHandler = new HelixHandler(applicationConfig, databaseConfig, server);
    helixInitEndTime = System.nanoTime();

    // Print Initialization Times
    IO.println("Configuration Load Time: " + (configEndTime - configStartTime) / 1_000_000 + " ms");
    IO.println("Server Initialization Time: " + (serverEndTime - serverStartTime) / 1_000_000 + " ms");
    IO.println("HelixHandler Initialization Time: " + (helixInitEndTime - helixInitStartTime) / 1_000_000 + " ms");

    // Setup Scopes
    Scope[] scopes = new Scope[]{
            MODERATION_READ,
            CHANNEL_READ_VIPS,
            CHANNEL_READ_SUBSCRIPTIONS,
            MODERATOR_READ_FOLLOWERS
    };

    // Print Authorization URL
    IO.println("\nPlease authorize the application if you haven't done so already.\n" + helixHandler.getAuthorizationUrl(scopes) + "\n");

    // Get Handlers
    UserHandler userHandler = helixHandler.getUserHandler();
    RoleHandler roleHandler = helixHandler.getRoleHandler();

    // Get Example User
    TwitchUser channel = userHandler.getTwitchUser("r4kunnn");

    // Declare Benchmark Variables
    var rounds = 10; // Number of rounds to average
    long[] moderatorNonCacheStartTime, moderatorNonCacheEndTime;      // Moderator Non-Cache Time
    long[] moderatorCacheStartTime, moderatorCacheEndTime;            // Moderator Cache Time
    long[] vipNonCacheStartTime, vipNonCacheEndTime;                  // VIP Non-Cache Time
    long[] vipCacheStartTime, vipCacheEndTime;                        // VIP Cache Time
    long[] subscriberNonCacheStartTime, subscriberNonCacheEndTime;    // Subscriber Non-Cache Time
    long[] subscriberCacheStartTime, subscriberCacheEndTime;          // Subscriber Cache Time
    long[] followerNonCacheStartTime, followerNonCacheEndTime;        // Follower Non-Cache Time
    long[] followerCacheStartTime, followerCacheEndTime;              // Follower Cache Time

    // Initialize Arrays
    moderatorNonCacheStartTime = new long[rounds + 1];
    moderatorNonCacheEndTime = new long[rounds + 1];
    moderatorCacheStartTime = new long[rounds + 1];
    moderatorCacheEndTime = new long[rounds + 1];
    vipNonCacheStartTime = new long[rounds + 1];
    vipNonCacheEndTime = new long[rounds + 1];
    vipCacheStartTime = new long[rounds + 1];
    vipCacheEndTime = new long[rounds + 1];
    subscriberNonCacheStartTime = new long[rounds + 1];
    subscriberNonCacheEndTime = new long[rounds + 1];
    subscriberCacheStartTime = new long[rounds + 1];
    subscriberCacheEndTime = new long[rounds + 1];
    followerNonCacheStartTime = new long[rounds + 1];
    followerNonCacheEndTime = new long[rounds + 1];
    followerCacheStartTime = new long[rounds + 1];
    followerCacheEndTime = new long[rounds + 1];

    // Run Benchmarks
    IO.println("\nStarting Benchmark for Role Retrievals over " + rounds + " rounds...\n");
    for (var i = 0; i < rounds + 1; i++) {
        var start = System.nanoTime();

        // Clear all caches before each round
        roleHandler.clearCaches();

        // Benchmark Moderator Retrieval - Non-Cached
        roleHandler.clearModeratorCache();
        moderatorNonCacheStartTime[i] = System.nanoTime();
        roleHandler.getModerators(channel);
        moderatorNonCacheEndTime[i] = System.nanoTime();

        // Benchmark VIP Retrieval - Non-Cached
        roleHandler.clearVIPCache();
        vipNonCacheStartTime[i] = System.nanoTime();
        roleHandler.getVIPs(channel);
        vipNonCacheEndTime[i] = System.nanoTime();

        // Benchmark Subscriber Retrieval - Non-Cached
        roleHandler.clearSubscriberCache();
        subscriberNonCacheStartTime[i] = System.nanoTime();
        roleHandler.getSubscribers(channel);
        subscriberNonCacheEndTime[i] = System.nanoTime();

        // Benchmark Follower Retrieval - Non-Cached
        roleHandler.clearFollowerCache();
        followerNonCacheStartTime[i] = System.nanoTime();
        roleHandler.getFollowers(channel);
        followerNonCacheEndTime[i] = System.nanoTime();

        // Benchmark Moderator Retrieval - Cached
        moderatorCacheStartTime[i] = System.nanoTime();
        roleHandler.getModerators(channel);
        moderatorCacheEndTime[i] = System.nanoTime();

        // Benchmark VIP Retrieval - Cached
        vipCacheStartTime[i] = System.nanoTime();
        roleHandler.getVIPs(channel);
        vipCacheEndTime[i] = System.nanoTime();

        // Benchmark Subscriber Retrieval - Cached
        subscriberCacheStartTime[i] = System.nanoTime();
        roleHandler.getSubscribers(channel);
        subscriberCacheEndTime[i] = System.nanoTime();

        // Benchmark Follower Retrieval - Cached
        followerCacheStartTime[i] = System.nanoTime();
        roleHandler.getFollowers(channel);
        followerCacheEndTime[i] = System.nanoTime();

        if (i == 0) IO.println("Warm-up round completed... took " + (System.nanoTime() - start) / 1_000_000 + " ms");
        else
            IO.println("Completed benchmark round " + (i) + " of " + rounds + "... took " + (System.nanoTime() - start) / 1_000_000 + " ms");
    }


    // Get Sizes
    var modSize = roleHandler.getModerators(channel).size();
    var vipSize = roleHandler.getVIPs(channel).size();
    var subSize = roleHandler.getSubscribers(channel).size();
    var followerSize = roleHandler.getFollowers(channel).size();

    // Declare Average Times
    long moderatorNonCacheTime = 0;
    long moderatorCacheTime = 0;
    long vipNonCacheTime = 0;
    long vipCacheTime = 0;
    long subscriberNonCacheTime = 0;
    long subscriberCacheTime = 0;
    long followerNonCacheTime = 0;
    long followerCacheTime = 0;

    // Aggregate Times
    for (var i = 1; i < rounds + 1; i++) {
        moderatorNonCacheTime += (moderatorNonCacheEndTime[i] - moderatorNonCacheStartTime[i]) / 1_000_000;
        moderatorCacheTime += (moderatorCacheEndTime[i] - moderatorCacheStartTime[i]) / 1_000_000;
        vipNonCacheTime += (vipNonCacheEndTime[i] - vipNonCacheStartTime[i]) / 1_000_000;
        vipCacheTime += (vipCacheEndTime[i] - vipCacheStartTime[i]) / 1_000_000;
        subscriberNonCacheTime += (subscriberNonCacheEndTime[i] - subscriberNonCacheStartTime[i]) / 1_000_000;
        subscriberCacheTime += (subscriberCacheEndTime[i] - subscriberCacheStartTime[i]) / 1_000_000;
        followerNonCacheTime += (followerNonCacheEndTime[i] - followerNonCacheStartTime[i]) / 1_000_000;
        followerCacheTime += (followerCacheEndTime[i] - followerCacheStartTime[i]) / 1_000_000;
    }

    // Calculate Averages
    moderatorNonCacheTime /= rounds;
    moderatorCacheTime /= rounds;
    vipNonCacheTime /= rounds;
    vipCacheTime /= rounds;
    subscriberNonCacheTime /= rounds;
    subscriberCacheTime /= rounds;
    followerNonCacheTime /= rounds;
    followerCacheTime /= rounds;

    // Print Results
    IO.println("\nBenchmark Results for Channel: " + channel.getDisplayName() + " (ID: " + channel.getId() + ")\n");

    // Moderators
    IO.println("Moderators: " + modSize);
    IO.println(" - Non-Cached Retrieval Time: " + moderatorNonCacheTime + " ms");
    IO.println(" - Cached Retrieval Time: " + moderatorCacheTime + " ms");
    IO.println(" - Saving with Cache: " + (moderatorNonCacheTime - moderatorCacheTime) + " ms");
    IO.println(" - Speedup Factor: " + String.format("%.2f", (double) moderatorNonCacheTime / moderatorCacheTime) + "x\n");

    // VIPs
    IO.println("VIPs: " + vipSize);
    IO.println(" - Non-Cached Retrieval Time: " + vipNonCacheTime + " ms");
    IO.println(" - Cached Retrieval Time: " + vipCacheTime + " ms");
    IO.println(" - Saving with Cache: " + (vipNonCacheTime - vipCacheTime) + " ms");
    IO.println(" - Speedup Factor: " + String.format("%.2f", (double) vipNonCacheTime / vipCacheTime) + "x\n");

    // Subscribers
    IO.println("Subscribers: " + subSize);
    IO.println(" - Non-Cached Retrieval Time: " + subscriberNonCacheTime + " ms");
    IO.println(" - Cached Retrieval Time: " + subscriberCacheTime + " ms");
    IO.println(" - Saving with Cache: " + (subscriberNonCacheTime - subscriberCacheTime) + " ms");
    IO.println(" - Speedup Factor: " + String.format("%.2f", (double) subscriberNonCacheTime / subscriberCacheTime) + "x\n");

    // Followers
    IO.println("Followers: " + followerSize);
    IO.println(" - Non-Cached Retrieval Time: " + followerNonCacheTime + " ms");
    IO.println(" - Cached Retrieval Time: " + followerCacheTime + " ms");
    IO.println(" - Saving with Cache: " + (followerNonCacheTime - followerCacheTime) + " ms");
    IO.println(" - Speedup Factor: " + String.format("%.2f", (double) followerNonCacheTime / followerCacheTime) + "x\n");

    // Exit
    System.exit(0);
}