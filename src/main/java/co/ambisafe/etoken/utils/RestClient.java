package co.ambisafe.etoken.utils;

import co.ambisafe.etoken.exception.RestClientException;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import static co.ambisafe.etoken.utils.Utils.readTree;

public class RestClient {

    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    public static Response get(String url) throws RestClientException {
        try {
            HttpGet httpGet = new HttpGet(url);

            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity responseEntity = response.getEntity();

            String body = EntityUtils.toString(responseEntity);
            JsonNode bodyJson = readTree(body);

            return new Response(response.getStatusLine(), bodyJson);
        } catch (IOException e) {
            throw new RestClientException(e);
        }
    }

    public static Response post(String url, String json) throws RestClientException {
        try {
            StringEntity entity = new StringEntity(json);
            entity.setContentType("application/json");

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(entity);

            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            String body = EntityUtils.toString(responseEntity);
            JsonNode bodyJson = readTree(body);

            return new Response(response.getStatusLine(), bodyJson);
        } catch (IOException e) {
            throw new RestClientException(e);
        }
    }

    public static class Response {
        private StatusLine status;
        private JsonNode body;

        public Response(StatusLine status, JsonNode body) {
            this.status = status;
            this.body = body;
        }

        public StatusLine getStatus() {
            return status;
        }

        public JsonNode getBody() {
            return body;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "status=" + status +
                    ", body=" + body +
                    '}';
        }
    }
}
