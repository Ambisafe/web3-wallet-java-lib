package co.ambisafe.etoken.service;

import co.ambisafe.etoken.exception.InsufficientFundsException;
import co.ambisafe.etoken.exception.RestClientException;
import co.ambisafe.etoken.utils.RestClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.ethereum.core.CallTransaction;
import org.ethereum.core.Transaction;
import org.ethereum.crypto.ECKey;
import org.spongycastle.util.encoders.Hex;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static co.ambisafe.etoken.utils.Utils.check0x;
import static co.ambisafe.etoken.utils.Utils.writeObjectAsString;
import static org.ethereum.util.ByteUtil.bigIntegerToBytes;
import static org.ethereum.util.ByteUtil.longToBytesNoLeadZeroes;

public class AmbisafeNode {

    private static final String NODE_URL = "http://node.ambisafe.co/";
    private static byte[] GAS_PRICE = longToBytesNoLeadZeroes(21000000000L);
    private static byte[] GAS_LIMIT = longToBytesNoLeadZeroes(250000L);

    // EToken contract
    private static final String ETOKEN_CONTRACT_ABI = "[{\"constant\":true,\"inputs\":[{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"owner\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_eventsHistory\",\"type\":\"address\"}],\"name\":\"setupEventsHistory\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_holder\",\"type\":\"address\"}],\"name\":\"getHolderId\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_stackDepthLib\",\"type\":\"address\"}],\"name\":\"setupStackDepthLib\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_spender\",\"type\":\"address\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"allowance\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_address\",\"type\":\"address\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"setCosignerAddress\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"isCreated\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"},{\"name\":\"_reference\",\"type\":\"string\"}],\"name\":\"transferFromWithReference\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"transferFrom\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"}],\"name\":\"trust\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"bytes32\"}],\"name\":\"cosigners\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_holder\",\"type\":\"address\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"balanceOf\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"transfer\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"pendingContractOwner\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_to\",\"type\":\"address\"}],\"name\":\"recover\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_to\",\"type\":\"address\"}],\"name\":\"isTrusted\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"name\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"bytes32\"}],\"name\":\"proxies\",\"outputs\":[{\"name\":\"proxy\",\"type\":\"address\"},{\"name\":\"onlyProxy\",\"type\":\"bool\"},{\"name\":\"throwOnFailedEmit\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"holdersCount\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"},{\"name\":\"_reference\",\"type\":\"string\"}],\"name\":\"transferWithReference\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"},{\"name\":\"_data\",\"type\":\"bytes\"}],\"name\":\"forwardCall\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_icap\",\"type\":\"bytes32\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"transferToICAP\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_registryICAP\",\"type\":\"address\"}],\"name\":\"setupRegistryICAP\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_icap\",\"type\":\"bytes32\"},{\"name\":\"_value\",\"type\":\"uint256\"},{\"name\":\"_reference\",\"type\":\"string\"}],\"name\":\"transferToICAPWithReference\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_spender\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"approve\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"bytes32\"}],\"name\":\"assets\",\"outputs\":[{\"name\":\"owner\",\"type\":\"uint256\"},{\"name\":\"totalSupply\",\"type\":\"uint256\"},{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"description\",\"type\":\"string\"},{\"name\":\"isReissuable\",\"type\":\"bool\"},{\"name\":\"baseUnit\",\"type\":\"uint8\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"registryICAP\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_icap\",\"type\":\"bytes32\"},{\"name\":\"_value\",\"type\":\"uint256\"},{\"name\":\"_reference\",\"type\":\"string\"}],\"name\":\"transferFromToICAPWithReference\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_icap\",\"type\":\"bytes32\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"transferFromToICAP\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"bytes32\"}],\"name\":\"switches\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"totalSupply\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"distrustAll\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"description\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"isReissuable\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_address\",\"type\":\"address\"}],\"name\":\"setCosignerAddressForUser\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"contractOwner\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"baseUnit\",\"outputs\":[{\"name\":\"\",\"type\":\"uint8\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_owner\",\"type\":\"address\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"isOwner\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_onlyThroughProxy\",\"type\":\"bool\"},{\"name\":\"_throwOnFailedEmit\",\"type\":\"bool\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"setProxyConf\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"eventsHistory\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"}],\"name\":\"distrust\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_symbol\",\"type\":\"bytes32\"},{\"name\":\"_newOwner\",\"type\":\"address\"}],\"name\":\"changeOwnership\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"}]";
    private static final String ETOKEN_CONTRACT_ADDRESS = "0x68c769478002b2e2db64fe3be55c943fe4fbd6b1";
    private static final CallTransaction.Contract ETOKEN_CONTRACT = new CallTransaction.Contract(ETOKEN_CONTRACT_ABI);
    private static final CallTransaction.Function ETOKEN_BALANCE_OF = ETOKEN_CONTRACT.getByName("balanceOf");
    private static final CallTransaction.Function ETOKEN_TRANSFER = ETOKEN_CONTRACT.getByName("transfer");
    private static final CallTransaction.Function ETOKEN_BASE_UNIT = ETOKEN_CONTRACT.getByName("baseUnit");

    // setup
    public static void setGasPrice(long gasPrice) {
        GAS_PRICE = longToBytesNoLeadZeroes(gasPrice);
    }

    public static void setGasLimit(long gasLimit) {
        GAS_LIMIT = longToBytesNoLeadZeroes(gasLimit);
    }

    public static BigInteger getTransactionsCount(String address) {
        String json = writeObjectAsString(prepareRpcCall("eth_getTransactionCount", check0x(address), "pending"));

        RestClient.Response response = RestClient.post(NODE_URL, json);
        JsonNode body = response.getBody();

        if (!body.path("error").isMissingNode()) {
            throw new RestClientException(body.path("error").path("message").asText());
        }

        return new BigInteger(body.path("result").asText().substring(2), 16);
    }

    public static BigInteger getBalance(String address, String symbol) {
        byte[] encodedData = ETOKEN_BALANCE_OF.encode(check0x(address), symbol.toUpperCase());
        String data = "0x" + Hex.toHexString(encodedData);

        Map<String, Object> params = new HashMap<>();
        params.put("to", ETOKEN_CONTRACT_ADDRESS);
        params.put("data", data);

        String json = writeObjectAsString(prepareRpcCall("eth_call", params, "pending"));
        System.out.println(json);

        RestClient.Response response = RestClient.post(NODE_URL, json);
        JsonNode body = response.getBody();

        if (!body.path("error").isMissingNode()) {
            throw new RestClientException(body.path("error").path("message").asText());
        }

        return new BigInteger(body.path("result").asText().substring(2), 16);
    }

    public static String transfer(String recipient, String amount, String symbol, byte[] privateKey)
            throws RestClientException {
        byte[] encodedData = ETOKEN_TRANSFER.encode(check0x(recipient), amount, symbol.toUpperCase());

        ECKey key = ECKey.fromPrivate(privateKey);
        String senderAddress = Hex.toHexString(key.getAddress());
        BigInteger nonce = getTransactionsCount(senderAddress);

        BigDecimal baseUnit = new BigDecimal("10").pow(getBaseUnit(symbol).intValue());
        BigInteger newAmount = new BigDecimal(amount).multiply(baseUnit).toBigIntegerExact();
        BigInteger currentBalance = getBalance(senderAddress, symbol);

        if (currentBalance.compareTo(newAmount) == -1) {
            throw new InsufficientFundsException("Insufficient balance: " + currentBalance);
        }

        Transaction tx = new Transaction(
                longToBytesNoLeadZeroes(nonce.longValueExact()),
                GAS_PRICE,
                GAS_LIMIT,
                Hex.decode(ETOKEN_CONTRACT_ADDRESS.substring(2)),
                longToBytesNoLeadZeroes(0),
                encodedData
        );

        tx.sign(key);

        String rawHex = "0x" + Hex.toHexString(tx.getEncoded());
        String json = writeObjectAsString(prepareRpcCall("eth_sendRawTransaction", rawHex));

        RestClient.Response response = RestClient.post(NODE_URL, json);
        JsonNode body = response.getBody();

        if (!body.path("error").isMissingNode()) {
            throw new RestClientException(body.path("error").path("message").asText());
        }

        String txHash = body.path("result").asText();
        System.out.println("Tx hash: " + txHash);

        return txHash;
    }

    public static String transfer(String recipient, long amount, String symbol, byte[] privateKey)
            throws RestClientException {
        return transfer(recipient, Long.toString(amount), symbol, privateKey);
    }

    public static String transferEth(String recipient, BigInteger amount, byte[] privateKey)
            throws RestClientException {
        recipient = check0x(recipient);

        ECKey key = ECKey.fromPrivate(privateKey);
        String senderAddress = Hex.toHexString(key.getAddress());
        BigInteger nonce = getTransactionsCount(senderAddress);

        Transaction tx = new Transaction(
                longToBytesNoLeadZeroes(nonce.longValueExact()),
                GAS_PRICE,
                GAS_LIMIT,
                Hex.decode(recipient.substring(2)),
                bigIntegerToBytes(amount),
                null
        );

        tx.sign(key);

        String rawHex = "0x" + Hex.toHexString(tx.getEncoded());
        String json = writeObjectAsString(prepareRpcCall("eth_sendRawTransaction", rawHex));

        RestClient.Response response = RestClient.post(NODE_URL, json);
        JsonNode body = response.getBody();

        if (!body.path("error").isMissingNode()) {
            throw new RestClientException(body.path("error").path("message").asText());
        }

        String txHash = body.path("result").asText();
        System.out.println("Tx hash: " + txHash);

        return txHash;
    }

    public static String transferEth(String recipient, long amount, byte[] privateKey)
            throws RestClientException {
        return transferEth(recipient, new BigInteger(Long.toString(amount)), privateKey);
    }

    public static BigInteger getBaseUnit(String symbol) {
        byte[] encodedData = ETOKEN_BASE_UNIT.encode(symbol.toUpperCase());
        String data = "0x" + Hex.toHexString(encodedData);

        Map<String, Object> params = new HashMap<>();
        params.put("to", ETOKEN_CONTRACT_ADDRESS);
        params.put("data", data);

        String json = writeObjectAsString(prepareRpcCall("eth_call", params));
        System.out.println(json);

        RestClient.Response response = RestClient.post(NODE_URL, json);
        JsonNode body = response.getBody();

        if (!body.path("error").isMissingNode()) {
            throw new RestClientException(body.path("error").path("message").asText());
        }

        return new BigInteger(body.path("result").asText().substring(2), 16);
    }

    private static Map<String, Object> prepareRpcCall(String method, Object... params) {
        Map<String, Object> map = new HashMap<>();
        map.put("jsonrpc", "2.0");
        map.put("method", method);
        map.put("params", params);
        map.put("id", 1);
        return map;
    }
}
