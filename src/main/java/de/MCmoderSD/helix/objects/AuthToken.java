package de.MCmoderSD.helix.objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.MCmoderSD.helix.core.TokenManager;
import de.MCmoderSD.helix.enums.Scope;

import java.io.Serializable;
import java.sql.Timestamp;

import java.util.Arrays;
import java.util.HashSet;

import static de.MCmoderSD.helix.enums.Scope.format;

@SuppressWarnings("unused")
public class AuthToken implements Serializable {

    // Attributes
    private final Integer id;
    private final String accessToken;
    private final String refreshToken;
    private final HashSet<Scope> scopes;
    private final Integer expiresIn;
    private final Timestamp timestamp;

    // Constructor
    public AuthToken(TokenManager manager, String responseBody) {
        try {

            // Parse JSON
            JsonNode jsonNode = new ObjectMapper().readTree(responseBody);

            // Extract data
            accessToken = jsonNode.get("access_token").asText();
            refreshToken = jsonNode.get("refresh_token").asText();
            scopes = Scope.getScopes(format(jsonNode.get("scope")));
            expiresIn = jsonNode.get("expires_in").asInt();
            timestamp = new Timestamp(System.currentTimeMillis());

            // Get user ID
            id = Integer.parseInt(manager.getClient().getHelix().getUsers(accessToken, null, null).execute().getUsers().getFirst().getId());

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AuthToken: " + e.getMessage(), e);
        }

        // Set next refresh
        new Thread(() -> {

            // Sleep until the token expires
            try {
                var sleepTime = expiresIn * 1000L - (System.currentTimeMillis() - timestamp.getTime());
                if (sleepTime > 1000) Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Refresh the token
            manager.refreshToken(this);

        }).start();
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

    public String getScopesAsString() {
        return String.join("+", scopes.stream().map(Scope::getScope).toArray(String[]::new));
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
}