package co.ambisafe.etoken.service;

import co.ambisafe.etoken.Account;
import co.ambisafe.etoken.Container;
import co.ambisafe.etoken.exceptions.RestClientException;
import co.ambisafe.etoken.utils.RestClient;
import com.auth0.jwt.internal.org.bouncycastle.util.encoders.Hex;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static co.ambisafe.etoken.utils.Utils.writeObjectAsString;

public class Keystore {

    private static String BASE_URL = "https://keystore.ambisafe.co/";

    public static void setBaseUrl(String baseUrl) {
        if (!baseUrl.endsWith("/")) baseUrl += "/";
        BASE_URL = baseUrl;
    }

    public static void saveAccount(String jwtToken, Account account) throws RestClientException {
        String url = BASE_URL + "keystore/" + account.getId();

        try {
            String json = getAccountJson(account);

            RestClient.Response response = RestClient.post(url, json, "Authorization", jwtToken);
            if (response.getStatus() != 200) {
                throw new RestClientException("ERROR: " + response.getStatus() + ", body: " + response.getBody());
            }
        } catch (IOException e) {
            throw new RestClientException(e);
        }
    }

    public static Account getAccount(String accountId) throws RestClientException {
        String url = BASE_URL + "keystore/" + accountId;

        RestClient.Response response = RestClient.get(url);

        JsonNode json = response.getBody();

        JsonNode crypto = json.path("crypto");
        Container container = new Container(
                Hex.decode(crypto.path("data").asText()),
                Hex.decode(crypto.path("iv").asText()),
                Hex.decode(crypto.path("public_key").asText()),
                crypto.path("salt").asText());

        Account account = new Account(
                json.path("address").asText(),
                container,
                json.path("id").asText(),
                json.path("version").asInt());

        return account;
    }

    private static String getAccountJson(Account account) throws JsonProcessingException {
        Container container = account.getContainer();
        Map<String, Object> crypto = new HashMap<>();
        crypto.put("data", container.getDataHex());
        crypto.put("iv", container.getIvHex());
        crypto.put("public_key", container.getPublicKeyHex());
        crypto.put("salt", container.getSalt());

        Map<String, Object> json = new HashMap<>();
        json.put("address", account.getAddress());
        json.put("crypto", crypto);
        json.put("id", account.getId());
        json.put("version", account.getVersion());

        return writeObjectAsString(json);
    }
}
