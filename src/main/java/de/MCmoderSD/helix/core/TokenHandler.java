package de.MCmoderSD.helix.core;

import com.fasterxml.jackson.databind.JsonNode;

import de.MCmoderSD.encryption.core.Encryption;
import de.MCmoderSD.helix.database.SQL;
import de.MCmoderSD.helix.enums.Scope;
import de.MCmoderSD.helix.objects.AuthToken;
import de.MCmoderSD.server.core.Server;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.io.IOException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import static de.MCmoderSD.encryption.enums.Hash.SHA3_256;
import static de.MCmoderSD.encryption.enums.Transformer.AES_ECB_PKCS5;
import static de.MCmoderSD.helix.utilities.ConfigValidator.*;

@SuppressWarnings("UnusedReturnValue")
public class TokenHandler {

    // Endpoints
    private static final String TOKEN_URL = "https://id.twitch.tv/oauth2/token";
    private static final String AUTH_URL = "https://id.twitch.tv/oauth2/authorize";

    // Associations
    private final HelixHandler helixHandler;
    private final SQL sql;

    // Configuration
    private final String clientId;
    private final String clientSecret;
    private final String redirectURL;

    // Attributes
    private final Builder requestBuilder;
    private final ConcurrentHashMap<Integer, AuthToken> authTokens;

    // Constructor
    public TokenHandler(JsonNode application, JsonNode database, Server server, HelixHandler helixHandler) {

        // Validate Config
        if (!validateApplicationConfig(application)) throw new IllegalArgumentException("Invalid Application config");
        if (!validateDatabaseConfig(database)) throw new IllegalArgumentException("Invalid Database config");

        // Check Associations
        if (server == null) throw new IllegalArgumentException("Server is null");
        if (helixHandler == null) throw new IllegalArgumentException("HelixHandler is null");

        // Set Associations
        this.helixHandler = helixHandler;

        // Load Application Config
        JsonNode credentials = application.get("credentials");
        clientId = credentials.get("clientId").asText();
        clientSecret = credentials.get("clientSecret").asText();
        redirectURL = application.get("oAuthRedirectURL").asText();

        // Initialize SQL
        sql = new SQL(database, new Encryption(clientSecret, SHA3_256, AES_ECB_PKCS5));

        // Initialize Request Builder
        requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded");

        // Load Tokens
        authTokens = new ConcurrentHashMap<>();
        var tokens = sql.getAuthTokens();
        for (var token : tokens) refreshToken(token);

        // Register Callback Handler
        server.registerExactPath(redirectURL.substring(redirectURL.lastIndexOf('/')), new CallbackHandler());
    }

    private AuthToken requestToken(HttpRequest.BodyPublisher requestBody) {

        // Validate Body
        if (requestBody == null) throw new IllegalArgumentException("Request body cannot be null");
        if (requestBody.contentLength() < 1) throw new IllegalArgumentException("Request body cannot be empty");

        // Create Request
        HttpRequest request = requestBuilder
                .POST(requestBody)
                .build();

        // Send Request
        HttpResponse<String> response;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to send request", e);
        }

        // Check Response
        if (response == null) throw new RuntimeException("Failed to get token! Response is null");
        if (response.statusCode() != 200) throw new RuntimeException("Failed to get token! Status Code: " + response.statusCode() + " Body: " + response.body());
        if (response.body() == null) throw new RuntimeException("Failed to get token! Body is null");
        if (response.body().isBlank()) throw new RuntimeException("Failed to get token! Body is empty");

        // Create new token
        return new AuthToken(response.body(), helixHandler.getHelix(), this);
    }

    // Setter
    public AuthToken refreshToken(AuthToken token) {

        // Create body
        HttpRequest.BodyPublisher requestBody = HttpRequest.BodyPublishers.ofString(String.format(
                "client_id=%s&client_secret=%s&refresh_token=%s&grant_type=refresh_token",
                clientId,
                clientSecret,
                token.getRefreshToken()
        ));

        try {

            // Request Token
            AuthToken refreshedToken = requestToken(requestBody);           // Request new token
            authTokens.put(refreshedToken.getId(), refreshedToken);         // Update in Memory
            helixHandler.addCredential(refreshedToken.getAccessToken());    // Update in Helix
            sql.addAuthToken(refreshedToken);                               // Update or add token in database
            return refreshedToken;                                          // Return refreshed token

        } catch (Exception e) {
            sql.deleteAuthToken(token.getId());
            throw new RuntimeException("Failed to refresh token: " + e.getMessage(), e);
        }
    }

    // Getter
    public AuthToken getAuthToken(Integer id) {
        return authTokens.get(id);
    }

    public ConcurrentHashMap<Integer, AuthToken> getAuthTokens() {
        return authTokens;
    }

    public String getAuthorizationUrl(Scope... scopes) {

        // Build Scopes
        StringBuilder scopeBuilder = new StringBuilder();
        HashSet<Scope> scopeSet = new HashSet<>(Arrays.asList(scopes));
        for (var scope : scopeSet) scopeBuilder.append(scope.getScope()).append("+");

        // Return URL
        return String.format(
                "%s?client_id=%s&redirect_uri=%s&response_type=code&scope=%s",
                AUTH_URL,
                clientId,
                redirectURL,
                scopeBuilder.isEmpty() ? "" : scopeBuilder.substring(0, scopeBuilder.length() - 1)
        );
    }

    // Callback handler
    private class CallbackHandler implements HttpHandler {

        // Constants
        private static final String SUCCESS_MESSAGE = "Successfully authenticated! \nYou can close this tab now!";
        private static final String ERROR_MESSAGE = "Failed to authenticate, please try again";
        private static final String INVALID_CODE_MESSAGE = "Invalid code, please try again";

        @Override
        public void handleRequest(HttpServerExchange exchange) {

            // Get query
            String query = exchange.getQueryString();

            // Check if the query contains the code
            if (query != null && query.contains("code=")) {

                // Create Request Body
                HttpRequest.BodyPublisher requestBody = HttpRequest.BodyPublishers.ofString(String.format(
                        "client_id=%s&client_secret=%s&code=%s&grant_type=authorization_code&redirect_uri=%s",
                        clientId,
                        clientSecret,
                        query.split("code=")[1].split("&")[0],
                        redirectURL
                ));

                try {

                    // Request Token
                    AuthToken token = requestToken(requestBody);            // Request new token
                    authTokens.put(token.getId(), token);                   // Add to Memory
                    helixHandler.addCredential(token.getAccessToken());     // Add to Helix
                    sql.addAuthToken(token);                                // Add to database

                } catch (Exception e) {
                    System.err.println("Failed to handle callback: " + e.getMessage());
                    exchange.getResponseSender().send(ERROR_MESSAGE);
                } finally {
                    exchange.getResponseSender().send(SUCCESS_MESSAGE);
                }
            } else exchange.getResponseSender().send(INVALID_CODE_MESSAGE);
        }
    }
}