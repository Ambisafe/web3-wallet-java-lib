package co.ambisafe.etoken.service;

import co.ambisafe.etoken.exception.ETokenException;
import co.ambisafe.etoken.exception.RestClientException;
import co.ambisafe.etoken.utils.RestClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;

import static co.ambisafe.etoken.utils.Utils.check0x;

public class ETokenHistory {

    private static String BASE_URL ="https://etoken-history.ambisafe.co/";

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }

    public static TxList getTxList(String recipient, String... params) throws ETokenException {
        recipient = check0x(recipient);

        String paramsString = prepareParams(params);
        String url = BASE_URL + "/tx/" + recipient + paramsString;

        RestClient.Response response = RestClient.get(url);
        JsonNode body = response.getBody();
        long total = body.path("total").asLong();

        String nextRequest = body.path("nextRequest").asText();
        if (nextRequest.equals("null")) nextRequest = null;

        TxList txList = new TxList(total, nextRequest);

        ArrayNode result = (ArrayNode) body.path("result");
        for (JsonNode tx : result) {
            Tx temp = new Tx();
            temp.txHash = tx.path("transactionHash").asText();
            temp.timestamp = tx.path("timestamp").asLong();
            temp.blockNumber = tx.path("blockNumber").asLong();
            temp.eventName = tx.path("eventName").asText();
            temp.confirmations = tx.path("confirmations").asLong();

            JsonNode eventData = tx.path("eventData");
            temp.from = eventData.path("from").asText();
            temp.reference = eventData.path("reference").asText();
            temp.value = eventData.path("value").asText();
            temp.to = eventData.path("to").asText();

            if (!eventData.path("icap").isMissingNode()) {
                // TransferToICAP
                temp.icap = eventData.path("icap").asText();
            } else if (!eventData.path("symbol").isMissingNode()) {
                // Transfer
                temp.symbol = eventData.path("symbol").asText();
            } else {
                throw new ETokenException("ETokenHistory tx wrong type");
            }

            txList.add(temp);
        }

        return txList;
    }

    private static String prepareParams(String[] params) {
        int length = params.length;
        if (length == 0) return "";
        if (length % 2 != 0) throw new RestClientException("Query params wrong format");

        StringBuilder sb = new StringBuilder("?");
        for (int i = 0; i < length; i += 2) {
            String param = params[i];
            String value = params[i+1];
            sb.append(param).append('=').append(value).append('&');
        }

        return sb.toString();
    }

    public static class Tx {
        private String txHash;
        private long timestamp;
        private long blockNumber;
        private long confirmations;
        private String eventName;
        private String from;
        private String reference;
        private String value;
        private String to;
        private String icap;
        private String symbol;

        @Override
        public String toString() {
            return "{" +
                    "\"txHash\":\"" + txHash + "\"" +
                    ", \"timestamp\":\"" + timestamp + "\"" +
                    ", \"blockNumber\":\"" + blockNumber + "\"" +
                    ", \"confirmations\":\"" + confirmations + "\"" +
                    ", \"eventName\":\"" + eventName + "\"" +
                    ", \"from\":\"" + from + "\"" +
                    ", \"reference\":\"" + reference + "\"" +
                    ", \"value\":\"" + value + "\"" +
                    ", \"to\":\"" + to + "\"" +
                    ", \"icap\":\"" + icap + "\"" +
                    ", \"symbol\":\"" + symbol + "\"" +
                    "}";
        }
    }

    public static class TxList {
        private ArrayList<Tx> txList = new ArrayList<>();
        private long total;
        private String nextRequest;

        public TxList(long total, String nextRequest) {
            this.total = total;
            this.nextRequest = nextRequest;
        }

        public long getTotal() {
            return total;
        }

        public String getNextRequest() {
            return nextRequest;
        }

        public List<Tx> getTxList() {
            return txList;
        }

        public void add(Tx tx) {
            txList.add(tx);
        }

        public boolean hasNextPage() {
            return nextRequest != null;
        }

        public void requestNextPage() {
            if (nextRequest == null) return;

            int last = nextRequest.lastIndexOf('/');
            String recipientWithParams = nextRequest.substring(last + 1);

            TxList temp = ETokenHistory.getTxList(recipientWithParams);
            this.total = temp.total;
            this.txList = temp.txList;
            this.nextRequest = temp.nextRequest;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"total\":\"" + total + '"' +
                    ", \"txList\":" + txList +
                    ", \"nextRequest\":\"" + nextRequest + "\"" +
                    '}';
        }
    }
}
