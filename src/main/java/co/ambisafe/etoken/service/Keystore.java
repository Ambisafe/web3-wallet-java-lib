package co.ambisafe.etoken.service;

import co.ambisafe.etoken.exception.ETokenException;
import co.ambisafe.etoken.Account;
import co.ambisafe.etoken.Container;
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

    private static String BASE_URL = "https://t2kx2mb0fg.execute-api.eu-central-1.amazonaws.com/stage/keystore/";
    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    public static void saveAccount(String jwtToken, Account account) {
        String url = BASE_URL + account.getId();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Authorization", jwtToken);

        try {
            String json = getAccountJson(account);
            System.out.println("Json: " + json);

            StringEntity entity = new StringEntity(json);
            entity.setContentType("application/json");
            httpPost.setEntity(entity);

            CloseableHttpResponse response = httpClient.execute(httpPost);

            System.out.println(response.getStatusLine());
            HttpEntity responseEntity = response.getEntity();

            String body = EntityUtils.toString(responseEntity);
            System.out.println("Body: " + body);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Account getAccount(String accountId) {
        String url = BASE_URL + accountId;

        HttpGet httpGet = new HttpGet(url);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);

            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            String body = EntityUtils.toString(entity);
            System.out.println("GET Response: " + body);

            JsonNode json = new ObjectMapper().readTree(body);
            System.out.println("Json: " + json);

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
            throw new ETokenException(e);
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
