import com.fasterxml.jackson.databind.JsonNode;
import de.MCmoderSD.helix.core.Client;
import de.MCmoderSD.json.JsonUtility;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {

        // Load Config
        JsonNode config = JsonUtility.loadJson("/config.json", false);

        // Credentials
        String clientId = config.get("clientId").asText();
        String clientSecret = config.get("clientSecret").asText();

        // Initialize API Client
        Client client = new Client(clientId, clientSecret);
    }
}