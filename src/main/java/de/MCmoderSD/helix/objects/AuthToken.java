package de.MCmoderSD.helix.objects;

import de.MCmoderSD.helix.core.TokenHandler;
import de.MCmoderSD.helix.enums.Scope;

import com.github.twitch4j.helix.TwitchHelix;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

@SuppressWarnings("unused")
public class AuthToken implements Serializable {

    // Attributes
    private final Integer id;
    private final String accessToken;
    private final String refreshToken;
    private final HashSet<Scope> scopes;
    private final Integer expiresIn;
    private final Timestamp timestamp;
    private final Timestamp nextRefresh;

    // Constructor
    public AuthToken(String responseBody, TwitchHelix helix, TokenHandler tokenHandler) {

        // Init Timestamp
        timestamp = new Timestamp(System.currentTimeMillis());

        // Validate input
        if (responseBody == null || responseBody.isBlank()) throw new IllegalArgumentException("Response body cannot be null or empty");

        // Parse JSON
        JsonNode response = new ObjectMapper().readTree(responseBody);

        // Validate JSON
        if (!response.has("access_token") || response.get("access_token").isNull() || !response.get("access_token").isString()) throw new IllegalArgumentException("Response body does not contain access_token");
        if (!response.has("refresh_token") || response.get("refresh_token").isNull() || !response.get("refresh_token").isString()) throw new IllegalArgumentException("Response body does not contain refresh_token");
        if (!response.has("expires_in") || response.get("expires_in").isNull() || !response.get("expires_in").isNumber()) throw new IllegalArgumentException("Response body does not contain expires_in");

        // Extract data
        accessToken = response.get("access_token").asString();
        refreshToken = response.get("refresh_token").asString();
        expiresIn = response.get("expires_in").asInt();

        // Validate data
        if (accessToken.isBlank()) throw new IllegalArgumentException("Access token cannot be empty");
        if (refreshToken.isBlank()) throw new IllegalArgumentException("Refresh token cannot be empty");
        if (expiresIn < 1) throw new IllegalArgumentException("Expires in must be greater than 0");

        // Parse scopes
        scopes = new HashSet<>();
        if (response.has("scope") && !response.get("scope").isNull() && response.get("scope").isArray() && !response.get("scope").isEmpty()) {
            var scopeArray = response.get("scope");
            for (var scope : scopeArray) scopes.add(Scope.getScope(scope.asString()));
        }

        // Get user ID
        id = Integer.parseInt(helix.getUsers(accessToken, null, null).execute().getUsers().getFirst().getId());

        // Calculate next refresh time
        nextRefresh = new Timestamp(timestamp.getTime() + (expiresIn * 1000L));

        // Set next refresh
        new Thread(() -> {

            // Sleep until the token expires
            try {
                var sleepTime = getNextRefresh().getTime() - System.currentTimeMillis();
                if (sleepTime > 1000) Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException("Token refresh thread interrupted: " + e.getMessage(), e);
            }

            // Refresh the token
            tokenHandler.refreshToken(this);

        }).start();
    }

    public AuthToken(Integer id, String refreshToken, TokenHandler tokenHandler) {

        // Validate input
        if (id == null || id < 1) throw new IllegalArgumentException("ID cannot be null or less than 1");
        if (refreshToken == null || refreshToken.isBlank()) throw new IllegalArgumentException("Refresh token cannot be null or empty");
        if (tokenHandler == null) throw new IllegalArgumentException("TokenHandler cannot be null");

        // Set attributes
        this.id = id;
        this.accessToken = null;
        this.refreshToken = refreshToken;
        this.scopes = null;
        this.expiresIn = null;
        this.timestamp = null;
        this.nextRefresh = null;

        // Refresh the token
        tokenHandler.refreshToken(this);
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public HashSet<Scope> getScopes() {
        return scopes;
    }

    public boolean hasScope(Scope... scopes) {
        return Arrays.stream(scopes).allMatch(this.scopes::contains);
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Timestamp getNextRefresh() {
        return nextRefresh;
    }

    // Methods
    public boolean equals(AuthToken token) {
        if (token == null) return false;
        boolean isEqual = id.equals(token.id);
        isEqual &= Objects.equals(accessToken, token.accessToken);
        isEqual &= Objects.equals(refreshToken, token.refreshToken);
        isEqual &= Objects.equals(expiresIn, token.expiresIn);
        isEqual &= Objects.equals(timestamp, token.timestamp);
        isEqual &= Objects.equals(nextRefresh, token.nextRefresh);
        isEqual &= scopes.containsAll(token.scopes) && token.scopes.containsAll(scopes);
        return isEqual;
    }
}