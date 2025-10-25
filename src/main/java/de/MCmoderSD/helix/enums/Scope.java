package de.MCmoderSD.helix.enums;

import java.io.Serializable;

/**
 * Represents the available <a href="https://dev.twitch.tv/docs/authentication/scopes/">
 * Twitch API OAuth scopes</a> used for authenticating and authorizing access
 * to specific Twitch API resources.
 * <p>
 * Each {@code Scope} corresponds to a permission string defined by Twitch,
 * which determines what operations an application can perform on behalf
 * of a user or broadcaster.
 * <p>
 * Scopes are grouped by their associated functionality (e.g., Channel,
 * Moderation, User) and can be used to request granular access tokens
 * during the OAuth authorization process.
 *
 * @see <a href="https://dev.twitch.tv/docs/authentication/scopes/">Twitch Developer Documentation: Authentication Scopes</a>
 */
public enum Scope implements Serializable {

    // Analytics
    ANALYTICS_READ_EXTENSIONS(              "analytics:read:extensions"             ),
    ANALYTICS_READ_GAMES(                   "analytics:read:games"                  ),

    // Bits
    BITS_READ(                              "bits:read"                             ),

    // Channel
    CHANNEL_BOT(                            "channel:bot"                           ),
    CHANNEL_MANAGE_ADS(                     "channel:manage:ads"                    ),
    CHANNEL_READ_ADS(                       "channel:read:ads"                      ),
    CHANNEL_MANAGE_BROADCAST(               "channel:manage:broadcast"              ),
    CHANNEL_READ_CHARITY(                   "channel:read:charity"                  ),
    CHANNEL_MANAGE_CLIPS(                   "channel:manage:clips"                  ),
    CHANNEL_EDIT_COMMERCIAL(                "channel:edit:commercial"               ),
    CHANNEL_READ_EDITORS(                   "channel:read:editors"                  ),
    CHANNEL_MANAGE_EXTENSIONS(              "channel:manage:extensions"             ),
    CHANNEL_READ_GOALS(                     "channel:read:goals"                    ),
    CHANNEL_READ_GUEST_STAR(                "channel:read:guest_star"               ),
    CHANNEL_MANAGE_GUEST_STAR(              "channel:manage:guest_star"             ),
    CHANNEL_READ_HYPE_TRAIN(                "channel:read:hype_train"               ),
    CHANNEL_MANAGE_MODERATORS(              "channel:manage:moderators"             ),
    CHANNEL_READ_POLLS(                     "channel:read:polls"                    ),
    CHANNEL_MANAGE_POLLS(                   "channel:manage:polls"                  ),
    CHANNEL_READ_PREDICTIONS(               "channel:read:predictions"              ),
    CHANNEL_MANAGE_PREDICTIONS(             "channel:manage:predictions"            ),
    CHANNEL_MANAGE_RAIDS(                   "channel:manage:raids"                  ),
    CHANNEL_READ_REDEMPTIONS(               "channel:read:redemptions"              ),
    CHANNEL_MANAGE_REDEMPTIONS(             "channel:manage:redemptions"            ),
    CHANNEL_MANAGE_SCHEDULE(                "channel:manage:schedule"               ),
    CHANNEL_READ_STREAM_KEY(                "channel:read:stream_key"               ),
    CHANNEL_READ_SUBSCRIPTIONS(             "channel:read:subscriptions"            ),
    CHANNEL_MANAGE_VIDEOS(                  "channel:manage:videos"                 ),
    CHANNEL_READ_VIPS(                      "channel:read:vips"                     ),
    CHANNEL_MANAGE_VIPS(                    "channel:manage:vips"                   ),
    CHANNEL_MODERATE(                       "channel:moderate"                      ),

    // Clips
    CLIPS_EDIT(                             "clips:edit"                            ),

    // Editor
    EDITOR_MANAGE_CLIPS(                    "editor:manage:clips"                   ),

    // Moderation
    MODERATION_READ(                        "moderation:read"                       ),
    MODERATOR_MANAGE_ANNOUNCEMENTS(         "moderator:manage:announcements"        ),
    MODERATOR_MANAGE_AUTOMOD(               "moderator:manage:automod"              ),
    MODERATOR_READ_AUTOMOD_SETTINGS(        "moderator:read:automod_settings"       ),
    MODERATOR_MANAGE_AUTOMOD_SETTINGS(      "moderator:manage:automod_settings"     ),
    MODERATOR_READ_BANNED_USERS(            "moderator:read:banned_users"           ),
    MODERATOR_MANAGE_BANNED_USERS(          "moderator:manage:banned_users"         ),
    MODERATOR_READ_BLOCKED_TERMS(           "moderator:read:blocked_terms"          ),
    MODERATOR_READ_CHAT_MESSAGES(           "moderator:read:chat_messages"          ),
    MODERATOR_MANAGE_BLOCKED_TERMS(         "moderator:manage:blocked_terms"        ),
    MODERATOR_MANAGE_CHAT_MESSAGES(         "moderator:manage:chat_messages"        ),
    MODERATOR_READ_CHAT_SETTINGS(           "moderator:read:chat_settings"          ),
    MODERATOR_MANAGE_CHAT_SETTINGS(         "moderator:manage:chat_settings"        ),
    MODERATOR_READ_CHATTERS(                "moderator:read:chatters"               ),
    MODERATOR_READ_FOLLOWERS(               "moderator:read:followers"              ),
    MODERATOR_READ_GUEST_STAR(              "moderator:read:guest_star"             ),
    MODERATOR_MANAGE_GUEST_STAR(            "moderator:manage:guest_star"           ),
    MODERATOR_READ_MODERATORS(              "moderator:read:moderators"             ),
    MODERATOR_READ_SHIELD_MODE(             "moderator:read:shield_mode"            ),
    MODERATOR_MANAGE_SHIELD_MODE(           "moderator:manage:shield_mode"          ),
    MODERATOR_READ_SHOUTOUTS(               "moderator:read:shoutouts"              ),
    MODERATOR_MANAGE_SHOUTOUTS(             "moderator:manage:shoutouts"            ),
    MODERATOR_READ_SUSPICIOUS_USERS(        "moderator:read:suspicious_users"       ),
    MODERATOR_READ_UNBAN_REQUESTS(          "moderator:read:unban_requests"         ),
    MODERATOR_MANAGE_UNBAN_REQUESTS(        "moderator:manage:unban_requests"       ),
    MODERATOR_READ_VIPS(                    "moderator:read:vips"                   ),
    MODERATOR_READ_WARNINGS(                "moderator:read:warnings"               ),
    MODERATOR_MANAGE_WARNINGS(              "moderator:manage:warnings"             ),

    // User
    USER_BOT(                               "user:bot"                              ),
    USER_EDIT(                              "user:edit"                             ),
    USER_EDIT_BROADCAST(                    "user:edit:broadcast"                   ),
    USER_READ_BLOCKED_USERS(                "user:read:blocked_users"               ),
    USER_MANAGE_BLOCKED_USERS(              "user:manage:blocked_users"             ),
    USER_READ_BROADCAST(                    "user:read:broadcast"                   ),
    USER_READ_CHAT(                         "user:read:chat"                        ),
    USER_MANAGE_CHAT_COLOR(                 "user:manage:chat_color"                ),
    USER_READ_EMAIL(                        "user:read:email"                       ),
    USER_READ_EMOTES(                       "user:read:emotes"                      ),
    USER_READ_FOLLOWS(                      "user:read:follows"                     ),
    USER_READ_MODERATED_CHANNELS(           "user:read:moderated_channels"          ),
    USER_READ_SUBSCRIPTIONS(                "user:read:subscriptions"               ),
    USER_READ_WHISPERS(                     "user:read:whispers"                    ),
    USER_MANAGE_WHISPERS(                   "user:manage:whispers"                  ),
    USER_WRITE_CHAT(                        "user:write:chat"                       ),

    // IRC Chat Scopes
    CHAT_EDIT(                              "chat:edit"                             ),
    CHAT_READ(                              "chat:read"                             ),

    // PubSub-specific Chat Scopes
    WHISPERS_READ(                          "whispers:read"                         );

    // Attributes
    private final String scope;

    // Constructor
    Scope(String scope) {
        this.scope = scope;
    }

    // Static Getter
    public static Scope getScope(String scope) {
        if (scope == null || scope.isBlank()) throw new IllegalArgumentException("Scope cannot be null or blank");
        for (var value : Scope.values()) if (value.getScope().equals(scope)) return value;
        throw new IllegalArgumentException("Invalid scope: " + scope);
    }

    // Getters
    public String getScope() {
        return scope;
    }
}