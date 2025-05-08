package de.MCmoderSD.helix.core;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import de.MCmoderSD.encryption.Encryption;

import de.MCmoderSD.helix.database.SQL;
import de.MCmoderSD.helix.enums.Scope;
import de.MCmoderSD.helix.objects.AuthToken;

import de.MCmoderSD.server.Server;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.HashMap;
import java.util.Set;

@SuppressWarnings("unused")
public class TokenHandler {

    // Constants
    private static final String AUTH_URL = "https://id.twitch.tv/oauth2/authorize";
    private static final String TOKEN_URL = "https://id.twitch.tv/oauth2/token";

    // Associations
    private final HelixHandler helixHandler;
    private final Server server;
    private final SQL sql;

    // Credentials
    private final String clientId;
    private final String clientSecret;

    // Attributes
    private final HashMap<Integer, AuthToken> authTokens;

    // Constructor
    public TokenHandler(HelixHandler helixHandler, Server server) {

        // Set Associations
        this.helixHandler = helixHandler;
        this.server = server;

        // Set Credentials
        clientId = helixHandler.getClientId();
        clientSecret = helixHandler.getClientSecret();

        sql = new SQL(new Encryption(clientSecret));

        // Initialize Auth Tokens
        authTokens = sql.getAuthTokens();

        // Refresh Auth Tokens
        for (AuthToken token : authTokens.values()) refreshToken(token);

        // Register Callback Handler
        server.getHttpsServer().createContext("/callback", new CallbackHandler(this, server));
    }

    // Create HTTP request
    private static HttpRequest createRequest(String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    // Send HTTP request
    private static HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to send request: " + e.getMessage());
            return null;
        }
    }

    // Parse token
    private boolean parseToken(HttpResponse<String> response, @Nullable Integer id) {

        // Null check
        if (response == null || response.body() == null || response.body().isEmpty() || response.body().isBlank()) {
            System.err.println("Failed to get response");
            return false;
        }

        // Check for invalid refresh token
        if (response.body().contains("{\"status\":400,\"message\":\"Invalid refresh token\"}") && id != null) {
            System.err.println("Invalid refresh token, please reauthorize the bot");
            System.err.println("Deleting auth token");
            authTokens.remove(id);
            sql.deleteAuthToken(id);
            return true;
        }

        // Check for error
        if (response.statusCode() != 200) {
            System.err.println("Failed to get token: " + response.body());
            return false;
        }

        // Create new token
        AuthToken token = new AuthToken(this, response.body());

        // Null check
        if (token.getAccessToken() == null) {
            System.err.println("Failed to get access token");
            return false;
        }

        // Add token
        authTokens.replace(token.getId(), token);

        // Add Credentials
        helixHandler.addCredential(token.getAccessToken());

        // Update tokens in the database
        sql.addAuthToken(token);

        // Return
        return true;
    }

    // Refresh token
    public void refreshToken(AuthToken token) {

        // Create body
        String body = String.format(
                "client_id=%s&client_secret=%s&refresh_token=%s&grant_type=refresh_token",
                clientId,
                clientSecret,
                token.getRefreshToken()
        );

        // Request token
        boolean success = parseToken(sendRequest(createRequest(body)), token.getId());

        // Error message
        if (!success) System.err.println("Failed to refresh token");
    }

    // Get authorization URL
    public String getAuthorizationUrl(Scope... scopes) {
        StringBuilder scopeBuilder = new StringBuilder();
        for (Scope scope : Set.of(scopes)) scopeBuilder.append(scope.getScope()).append("+");
        return String.format(
                "%s?client_id=%s&redirect_uri=https://%s:%d/callback&response_type=code&scope=%s",
                AUTH_URL,
                clientId,
                server.getHostname(),
                server.getPort(),
                scopeBuilder.substring(0, scopeBuilder.length() - 1)
        );
    }

    // Get helixHandler
    public HelixHandler getClient() {
        return helixHandler;
    }

    // Get token
    public AuthToken getAuthToken(Integer channelId) {
        return authTokens.get(channelId);
    }

    // Get tokens
    public HashMap<Integer, AuthToken> getAuthTokens() {
        return authTokens;
    }

    // Get token
    public String getToken(Integer channelId) {
        return authTokens.get(channelId).getAccessToken();
    }

    // Get token
    public String getToken(Integer channelId, Scope... scopes) {

        // Check Parameters
        if (channelId == null || channelId < 1) throw new IllegalArgumentException("Channel ID cannot be null or less than 1");
        if (scopes == null || scopes.length == 0) throw new IllegalArgumentException("Scopes cannot be null or empty");

        // Check if a token is available
        if (!authTokens.containsKey(channelId)) throw new IllegalArgumentException("No token found for channel ID: " + channelId);
        if (!authTokens.get(channelId).hasScope(scopes)) throw new IllegalArgumentException("Token does not have the required scopes");

        // Refresh token
        return authTokens.get(channelId).getAccessToken();
    }

    // Callback handler
    private class CallbackHandler implements HttpHandler {

        // Constants
        private static final String SUCCESS_MESSAGE = "Successfully authenticated! \nYou can close this tab now!";
        private static final String ERROR_MESSAGE = "Failed to authenticate, please try again";
        private static final String INVALID_CODE_MESSAGE = "Invalid code, please try again";

        // Attributes
        private final TokenHandler tokenHandler;
        private final Server server;

        // Constructor
        public CallbackHandler(TokenHandler tokenHandler, Server server) {
            this.tokenHandler = tokenHandler;
            this.server = server;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            // Variables
            String responseMessage = INVALID_CODE_MESSAGE;

            // Extract code and scopes
            String query = exchange.getRequestURI().getQuery();

            // Check if the query contains the code
            if (query != null && query.contains("code=")) {

                // Create body
                String body = String.format(
                        "client_id=%s&client_secret=%s&code=%s&grant_type=authorization_code&redirect_uri=https://%s:%d/callback",
                        clientId,
                        clientSecret,
                        query.split("code=")[1].split("&")[0],
                        server.getHostname(),
                        server.getPort()
                );

                // Parse token
                boolean success = tokenHandler.parseToken(sendRequest(createRequest(body)), null);

                // Print success message if parsing was successful
                responseMessage = success ? SUCCESS_MESSAGE : ERROR_MESSAGE;
            }

            // Send response
            exchange.sendResponseHeaders(200, responseMessage.length());
            exchange.getResponseBody().write(responseMessage.getBytes());
            exchange.getResponseBody().close();
        }
    }
}