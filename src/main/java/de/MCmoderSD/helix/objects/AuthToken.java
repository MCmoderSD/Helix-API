package de.MCmoderSD.helix.objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.MCmoderSD.helix.core.TokenManager;
import de.MCmoderSD.helix.enums.Scope;

import java.io.Serializable;
import java.sql.Timestamp;

import java.util.Arrays;

import static de.MCmoderSD.helix.enums.Scope.format;

@SuppressWarnings("unused")
public class AuthToken implements Serializable {

    // Attributes
    private final Integer id;
    private final String accessToken;
    private final String refreshToken;
    private final Scope[] scopes;
    private final int expiresIn;
    private final Timestamp timestamp;

    // JSON Constructor
    public AuthToken(TokenManager manager, String responseBody) throws JsonProcessingException {

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

    public Scope[] getScopes() {
        return scopes;
    }

    public String getScopesAsString() {
        return Arrays.stream(scopes).map(Scope::getScope).reduce((s1, s2) -> s1 + "+" + s2).orElse("");
    }

    public boolean hasScope(Scope... scopes) {
        return Arrays.stream(scopes).allMatch(scope -> Arrays.asList(this.scopes).contains(scope));
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
