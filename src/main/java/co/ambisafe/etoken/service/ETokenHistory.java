package co.ambisafe.etoken.service;

import co.ambisafe.etoken.exception.ETokenException;
import co.ambisafe.etoken.utils.RestClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;

import static co.ambisafe.etoken.utils.Utils.check0x;

public class ETokenHistory {

    private static final String BASE_URL ="https://etoken-history.ambisafe.co/";

    public static TxList getTxList(String recipient) {
        recipient = check0x(recipient);

        String url = BASE_URL + "/tx/" + recipient;
        RestClient.Response response = RestClient.get(url);

        JsonNode body = response.getBody();
        long total = body.path("total").asLong();

        TxList txList = new TxList(total, body.path("nextRequest").asText());

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


    }

    public static void main(String[] args) {
        getTxList("0x60dda47483288e673dc0d522a715ae8eed5d60fd");
    }
}
