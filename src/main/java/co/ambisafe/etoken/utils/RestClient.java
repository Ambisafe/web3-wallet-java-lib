package co.ambisafe.etoken.utils;

import co.ambisafe.etoken.Account;
import co.ambisafe.etoken.exceptions.RestClientException;
import co.ambisafe.etoken.service.Keystore;
import co.ambisafe.etoken.service.Tenant;
import com.fasterxml.jackson.databind.JsonNode;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static co.ambisafe.etoken.utils.Utils.readTree;

public class RestClient {

    private static final String USER_AGENT = "Mozilla/5.0";

    public static Response get(String url, String... headers) throws RestClientException {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");

            // add request header
            if (headers.length % 2 != 0) {
                throw new RestClientException("Wrong headers structure");
            }
            for (int i = 0; i < headers.length; i +=2 ) {
                String key = headers[i];
                String value = headers[i+1];
                con.setRequestProperty(key, value);
            }

            // default header
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonNode bodyJson = readTree(response.toString());

            return new Response(responseCode, bodyJson);
        } catch (IOException e) {
            throw new RestClientException(e);
        }
    }

    public static Response post(String url, String json, String... headers) throws RestClientException {
        try {
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            // add request header
            if (headers.length % 2 != 0) {
                throw new RestClientException("Wrong headers structure");
            }
            for (int i = 0; i < headers.length; i +=2 ) {
                String key = headers[i];
                String value = headers[i+1];
                con.setRequestProperty(key, value);
            }

            // default headers
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type", "application/json");

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(json);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonNode bodyJson = readTree(response.toString());

            return new Response(responseCode, bodyJson);
        } catch (IOException e) {
            throw new RestClientException(e);
        }
    }

    public static class Response {
        private int status;
        private JsonNode body;

        public Response(int status, JsonNode body) {
            this.status = status;
            this.body = body;
        }

        public int getStatus() {
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
