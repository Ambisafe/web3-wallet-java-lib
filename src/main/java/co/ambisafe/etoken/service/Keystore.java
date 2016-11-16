package co.ambisafe.etoken.service;

import co.ambisafe.etoken.Account;
import co.ambisafe.etoken.Container;
import co.ambisafe.etoken.exceptions.RestClientException;
import com.auth0.jwt.internal.org.bouncycastle.util.encoders.Hex;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static co.ambisafe.etoken.utils.Utils.writeObjectAsString;

public class Keystore {

    private static String BASE_URL = "https://keystore.ambisafe.co/keystore/";
    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }

    public static void saveAccount(String jwtToken, Account account) throws RestClientException {
        String url = BASE_URL + account.getId();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Authorization", jwtToken);

        try {
            String json = getAccountJson(account);

            StringEntity entity = new StringEntity(json);
            entity.setContentType("application/json");
            httpPost.setEntity(entity);

            CloseableHttpResponse response = httpClient.execute(httpPost);

            HttpEntity responseEntity = response.getEntity();

            String body = EntityUtils.toString(responseEntity);
        } catch (IOException e) {
            throw new RestClientException(e);
        }
    }

    public static Account getAccount(String accountId) throws RestClientException {
        String url = BASE_URL + accountId;

        HttpGet httpGet = new HttpGet(url);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);

            HttpEntity entity = response.getEntity();
            String body = EntityUtils.toString(entity);

            JsonNode json = new ObjectMapper().readTree(body);

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
        } catch (IOException e) {
            e.printStackTrace();
            throw new RestClientException(e);
        }
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
