package co.ambisafe.etoken.service;

import co.ambisafe.etoken.exceptions.ETokenException;
import co.ambisafe.etoken.exceptions.InsufficientFundsException;
import co.ambisafe.etoken.exceptions.RestClientException;
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

import static co.ambisafe.etoken.utils.Utils.assure0x;
import static co.ambisafe.etoken.utils.Utils.writeObjectAsString;
import static org.ethereum.util.ByteUtil.bigIntegerToBytes;
import static org.ethereum.util.ByteUtil.longToBytesNoLeadZeroes;

public class AmbisafeNode {

    private static String NODE_URL = "https://9t1f4yor7v5t521m4ra9xh24ggd2q4-node.ambisafe.co/";
    private static byte[] GAS_PRICE = longToBytesNoLeadZeroes(21000000000L);
    private static byte[] GAS_LIMIT = longToBytesNoLeadZeroes(250000L);

    // setup
    public static void setNodeUrl(String nodeUrl) {
        if (!nodeUrl.endsWith("/")) nodeUrl += "/";
        NODE_URL = nodeUrl;
    }

    public static void setGasPrice(long gasPrice) {
        GAS_PRICE = longToBytesNoLeadZeroes(gasPrice);
    }

    public static void setGasLimit(long gasLimit) {
        GAS_LIMIT = longToBytesNoLeadZeroes(gasLimit);
    }

    /**
     * EToken Assets
     */
    public static class EToken {
        // EToken contract
        private static final String abi = "[{\"constant\":true,\"inputs\":[{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"owner\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_eventsHistory\",\"type\":\"address\"}],\"name\":\"setupEventsHistory\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_holder\",\"type\":\"address\"}],\"name\":\"getHolderId\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_stackDepthLib\",\"type\":\"address\"}],\"name\":\"setupStackDepthLib\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_spender\",\"type\":\"address\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"allowance\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_address\",\"type\":\"address\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"setCosignerAddress\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"isCreated\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"},{\"name\":\"_reference\",\"type\":\"string\"}],\"name\":\"transferFromWithReference\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"transferFrom\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"}],\"name\":\"trust\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"bytes32\"}],\"name\":\"cosigners\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_holder\",\"type\":\"address\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"balanceOf\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"transfer\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"pendingContractOwner\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_to\",\"type\":\"address\"}],\"name\":\"recover\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_to\",\"type\":\"address\"}],\"name\":\"isTrusted\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"name\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"bytes32\"}],\"name\":\"proxies\",\"outputs\":[{\"name\":\"proxy\",\"type\":\"address\"},{\"name\":\"onlyProxy\",\"type\":\"bool\"},{\"name\":\"throwOnFailedEmit\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"holdersCount\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"},{\"name\":\"_reference\",\"type\":\"string\"}],\"name\":\"transferWithReference\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"},{\"name\":\"_data\",\"type\":\"bytes\"}],\"name\":\"forwardCall\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_icap\",\"type\":\"bytes32\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"transferToICAP\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_registryICAP\",\"type\":\"address\"}],\"name\":\"setupRegistryICAP\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_icap\",\"type\":\"bytes32\"},{\"name\":\"_value\",\"type\":\"uint256\"},{\"name\":\"_reference\",\"type\":\"string\"}],\"name\":\"transferToICAPWithReference\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_spender\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"approve\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"bytes32\"}],\"name\":\"assets\",\"outputs\":[{\"name\":\"owner\",\"type\":\"uint256\"},{\"name\":\"totalSupply\",\"type\":\"uint256\"},{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"description\",\"type\":\"string\"},{\"name\":\"isReissuable\",\"type\":\"bool\"},{\"name\":\"baseUnit\",\"type\":\"uint8\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"registryICAP\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_icap\",\"type\":\"bytes32\"},{\"name\":\"_value\",\"type\":\"uint256\"},{\"name\":\"_reference\",\"type\":\"string\"}],\"name\":\"transferFromToICAPWithReference\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_icap\",\"type\":\"bytes32\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"transferFromToICAP\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"bytes32\"}],\"name\":\"switches\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"totalSupply\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"distrustAll\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"description\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"isReissuable\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_address\",\"type\":\"address\"}],\"name\":\"setCosignerAddressForUser\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"contractOwner\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"baseUnit\",\"outputs\":[{\"name\":\"\",\"type\":\"uint8\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_owner\",\"type\":\"address\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"isOwner\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_onlyThroughProxy\",\"type\":\"bool\"},{\"name\":\"_throwOnFailedEmit\",\"type\":\"bool\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"setProxyConf\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"eventsHistory\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"}],\"name\":\"distrust\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_symbol\",\"type\":\"bytes32\"},{\"name\":\"_newOwner\",\"type\":\"address\"}],\"name\":\"changeOwnership\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"}]";
        private static final String contractAddress = "0x68c769478002b2e2db64fe3be55c943fe4fbd6b1";
        private static final CallTransaction.Contract contract = new CallTransaction.Contract(abi);
        // Functions
        private static final CallTransaction.Function balanceOf = contract.getByName("balanceOf");
        private static final CallTransaction.Function transferWithReference = contract.getByName("transferWithReference");
        private static final CallTransaction.Function baseUnit = contract.getByName("baseUnit");
        private static final CallTransaction.Function trust = contract.getByName("trust");
        private static final CallTransaction.Function isTrusted = contract.getByName("isTrusted");

        public static String enableRecovery(String recoveryContractAddress, byte[] privateKey) {
            ECKey key = ECKey.fromPrivate(privateKey);
            String senderAddress = Hex.toHexString(key.getAddress());
            BigInteger nonce = getTransactionsCount(senderAddress);

            byte[] encodedData = trust.encode(assure0x(recoveryContractAddress));

            simulateTransaction(senderAddress, encodedData, contractAddress);

            Transaction tx = new Transaction(
                    longToBytesNoLeadZeroes(nonce.longValueExact()),
                    GAS_PRICE,
                    GAS_LIMIT,
                    Hex.decode(contractAddress.substring(2)),
                    longToBytesNoLeadZeroes(0),
                    encodedData
            );

            tx.sign(key);

            return sendTransaction(tx);
        }

        public static boolean isRecoveryEnabled(String address, String recoveryContractAddress) {
            String json = prepareContractCall(isTrusted, contractAddress, assure0x(address), assure0x(recoveryContractAddress));

            RestClient.Response response = RestClient.post(NODE_URL, json);
            JsonNode body = response.getBody();

            if (!body.path("error").isMissingNode()) {
                throw new RestClientException(body.path("error").path("message").asText());
            }

            return body.path("result").asText().endsWith("1");
        }

        public static BigDecimal getBalance(String address, String symbol) {
            String json = prepareContractCall(balanceOf, contractAddress, assure0x(address), symbol.toUpperCase());

            RestClient.Response response = RestClient.post(NODE_URL, json);
            JsonNode body = response.getBody();

            if (!body.path("error").isMissingNode()) {
                throw new RestClientException(body.path("error").path("message").asText());
            }

            BigDecimal baseUnit = new BigDecimal("10").pow(getBaseUnit(symbol).intValue());
            BigInteger balance = new BigInteger(body.path("result").asText().substring(2), 16);

            return new BigDecimal(balance).divide(baseUnit);
        }

        public static String transfer(String recipient, String amount, String symbol, String reference, byte[] privateKey)
                throws RestClientException {
            ECKey key = ECKey.fromPrivate(privateKey);
            String senderAddress = Hex.toHexString(key.getAddress());
            BigInteger nonce = getTransactionsCount(senderAddress);

            BigDecimal baseUnit = new BigDecimal("10").pow(getBaseUnit(symbol).intValue());
            BigInteger newAmount = new BigDecimal(amount).multiply(baseUnit).toBigIntegerExact();

            if (reference == null) reference = "";

            byte[] encodedData = transferWithReference.encode(assure0x(recipient), newAmount, symbol.toUpperCase(), reference);

            // check balance
            BigDecimal currentBalance = getBalance(senderAddress, symbol);
            if (currentBalance.compareTo(new BigDecimal(amount)) == -1) {
                throw new InsufficientFundsException("Insufficient balance: " + currentBalance);
            }

            simulateTransaction(senderAddress, encodedData, contractAddress);

            Transaction tx = new Transaction(
                    longToBytesNoLeadZeroes(nonce.longValueExact()),
                    GAS_PRICE,
                    GAS_LIMIT,
                    Hex.decode(contractAddress.substring(2)),
                    longToBytesNoLeadZeroes(0),
                    encodedData
            );

            tx.sign(key);

            return sendTransaction(tx);
        }

        public static String transfer(String recipient, String amount, String symbol, byte[] privateKey) throws RestClientException {
            return transfer(recipient, amount, symbol, "", privateKey);
        }

        public static BigInteger getBaseUnit(String symbol) {
            String json = prepareContractCall(baseUnit, contractAddress, symbol.toUpperCase());

            RestClient.Response response = RestClient.post(NODE_URL, json);
            JsonNode body = response.getBody();

            if (!body.path("error").isMissingNode()) {
                throw new RestClientException(body.path("error").path("message").asText());
            }

            return new BigInteger(body.path("result").asText().substring(2), 16);
        }
    }

    /**
     * ETokenETH
     */
    public static class ETokenETH {
        // ETokenETH
        private static final String abi = "[{\"constant\":true,\"inputs\":[],\"name\":\"multiAsset\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"name\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_spender\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"approve\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"totalSupply\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_icap\",\"type\":\"bytes32\"}],\"name\":\"isAutoDepositICAP\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_spender\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"emitApprove\",\"outputs\":[],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"transferFrom\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"emitTransfer\",\"outputs\":[],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_multiAsset\",\"type\":\"address\"},{\"name\":\"_symbol\",\"type\":\"bytes32\"}],\"name\":\"init\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"decimals\",\"outputs\":[{\"name\":\"\",\"type\":\"uint8\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"autoTopupThreshold\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"},{\"name\":\"_reference\",\"type\":\"string\"}],\"name\":\"transferFromWithReference\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_enabled\",\"type\":\"bool\"}],\"name\":\"setAutoTopup\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_owner\",\"type\":\"address\"}],\"name\":\"balanceOf\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_icap\",\"type\":\"bytes32\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"transferToICAP\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_icap\",\"type\":\"bytes32\"},{\"name\":\"_value\",\"type\":\"uint256\"},{\"name\":\"_reference\",\"type\":\"string\"}],\"name\":\"transferToICAPWithReference\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_cosigner\",\"type\":\"address\"}],\"name\":\"setCosignerAddress\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"}],\"name\":\"isAutoTopup\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_enabled\",\"type\":\"bool\"}],\"name\":\"setAutoDeposit\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"autoTopupAmount\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"symbol\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes32\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_icap\",\"type\":\"bytes32\"},{\"name\":\"_value\",\"type\":\"uint256\"},{\"name\":\"_reference\",\"type\":\"string\"}],\"name\":\"transferFromToICAPWithReference\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_icap\",\"type\":\"bytes32\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"transferFromToICAP\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"transfer\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"routerICAP\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"},{\"name\":\"_reference\",\"type\":\"string\"}],\"name\":\"transferWithReference\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"}],\"name\":\"isAutoDeposit\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_spender\",\"type\":\"address\"}],\"name\":\"allowance\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"}],\"name\":\"deposit\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_routerICAP\",\"type\":\"address\"}],\"name\":\"setupRouterICAP\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"from\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"to\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"uint256\"}],\"name\":\"Transfer\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"from\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"spender\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"uint256\"}],\"name\":\"Approve\",\"type\":\"event\"}]";
        private static final String contractAddress = "0x7660727d3cb947e807acead927ef3ede24c4a18d";
        private static final int baseUnit = 18;
        private static final CallTransaction.Contract contract = new CallTransaction.Contract(abi);
        // Functions
        private static final CallTransaction.Function setAutoDeposit = contract.getByName("setAutoDeposit");
        private static final CallTransaction.Function balanceOf = contract.getByName("balanceOf");
        private static final CallTransaction.Function transferWithReference = contract.getByName("transferWithReference");
        private static final CallTransaction.Function isAutoDeposit = contract.getByName("isAutoDeposit");

        public static String activateAccount(byte[] privateKey) {
            ECKey key = ECKey.fromPrivate(privateKey);
            String senderAddress = Hex.toHexString(key.getAddress());
            BigInteger nonce = getTransactionsCount(senderAddress);

            byte[] encodedData = setAutoDeposit.encode(true);

            simulateTransaction(senderAddress, encodedData, contractAddress);

            Transaction tx = new Transaction(
                    longToBytesNoLeadZeroes(nonce.longValueExact()),
                    GAS_PRICE,
                    GAS_LIMIT,
                    Hex.decode(contractAddress.substring(2)),
                    longToBytesNoLeadZeroes(0),
                    encodedData
            );

            tx.sign(key);

            return sendTransaction(tx);
        }

        public static boolean isActivated(String address) {
            String json = prepareContractCall(isAutoDeposit, contractAddress, assure0x(address));

            RestClient.Response response = RestClient.post(NODE_URL, json);
            JsonNode body = response.getBody();

            if (!body.path("error").isMissingNode()) {
                throw new RestClientException(body.path("error").path("message").asText());
            }

            return body.path("result").asText().endsWith("1");
        }

        public static BigDecimal getBalance(String address) {
            String json = prepareContractCall(balanceOf, contractAddress, assure0x(address));

            RestClient.Response response = RestClient.post(NODE_URL, json);
            JsonNode body = response.getBody();

            if (!body.path("error").isMissingNode()) {
                throw new RestClientException(body.path("error").path("message").asText());
            }

            BigDecimal bUnit = new BigDecimal("10").pow(baseUnit);
            BigInteger balance = new BigInteger(body.path("result").asText().substring(2), 16);

            return new BigDecimal(balance).divide(bUnit);
        }

        public static String transfer(String recipient, String amount, String reference, byte[] privateKey)
                throws RestClientException {
            ECKey key = ECKey.fromPrivate(privateKey);
            String senderAddress = Hex.toHexString(key.getAddress());
            BigInteger nonce = getTransactionsCount(senderAddress);

            BigDecimal baseUnit = new BigDecimal("10").pow(ETokenETH.baseUnit);
            BigInteger newAmount = new BigDecimal(amount).multiply(baseUnit).toBigIntegerExact();

            if (reference == null) reference = "";

            byte[] encodedData = transferWithReference.encode(assure0x(recipient), newAmount, reference);

            // check balance
            BigDecimal currentBalance = getBalance(senderAddress);
            if (currentBalance.compareTo(new BigDecimal(amount)) == -1) {
                throw new InsufficientFundsException("Insufficient balance: " + currentBalance);
            }

            simulateTransaction(senderAddress, encodedData, contractAddress);

            Transaction tx = new Transaction(
                    longToBytesNoLeadZeroes(nonce.longValueExact()),
                    GAS_PRICE,
                    GAS_LIMIT,
                    Hex.decode(contractAddress.substring(2)),
                    longToBytesNoLeadZeroes(0),
                    encodedData
            );

            tx.sign(key);

            return sendTransaction(tx);
        }

        public static String transfer(String recipient, String amount, byte[] privateKey) throws RestClientException {
            return transfer(recipient, amount, "", privateKey);
        }
    }

    // Ethereum
    public static class Eth {
        private static final int baseUnit = 18;

        public static BigDecimal getBalance(String address) {
            String json = writeObjectAsString(prepareRpcCall("eth_getBalance", assure0x(address), "pending"));

            RestClient.Response response = RestClient.post(NODE_URL, json);
            JsonNode body = response.getBody();

            if (!body.path("error").isMissingNode()) {
                throw new RestClientException(body.path("error").path("message").asText());
            }

            BigDecimal bUnit = new BigDecimal("10").pow(baseUnit);
            BigInteger balance = new BigInteger(body.path("result").asText().substring(2), 16);

            return new BigDecimal(balance).divide(bUnit);
        }

        public static String transfer(String recipient, String amount, byte[] privateKey)
                throws RestClientException {
            recipient = assure0x(recipient);

            ECKey key = ECKey.fromPrivate(privateKey);
            String senderAddress = Hex.toHexString(key.getAddress());
            BigInteger nonce = getTransactionsCount(senderAddress);

            BigDecimal baseUnit = new BigDecimal("10").pow(Eth.baseUnit);
            BigInteger newAmount = new BigDecimal(amount).multiply(baseUnit).toBigIntegerExact();

            Transaction tx = new Transaction(
                    longToBytesNoLeadZeroes(nonce.longValueExact()),
                    GAS_PRICE,
                    GAS_LIMIT,
                    Hex.decode(recipient.substring(2)),
                    bigIntegerToBytes(newAmount),
                    null
            );

            tx.sign(key);

            return sendTransaction(tx);
        }
    }

    // Common
    public static BigInteger getTransactionsCount(String address) {
        String json = writeObjectAsString(prepareRpcCall("eth_getTransactionCount", assure0x(address), "pending"));

        RestClient.Response response = RestClient.post(NODE_URL, json);
        JsonNode body = response.getBody();

        if (!body.path("error").isMissingNode()) {
            throw new RestClientException(body.path("error").path("message").asText());
        }

        return new BigInteger(body.path("result").asText().substring(2), 16);
    }

    private static void simulateTransaction(String senderAddress, byte[] encodedData, String contractAddress) {
        Map<String, Object> params = new HashMap<>();
        params.put("from", assure0x(senderAddress));
        params.put("to", contractAddress);
        params.put("gas", assure0x(new BigInteger(GAS_LIMIT).toString(16)));
        params.put("gasPrice", assure0x(new BigInteger(GAS_PRICE).toString(16)));
        params.put("value", assure0x(new BigInteger("0").toString(16)));
        params.put("data", assure0x(Hex.toHexString(encodedData)));

        String simulateJson = writeObjectAsString(prepareRpcCall("eth_call", params));

        RestClient.Response simulateResponse = RestClient.post(NODE_URL, simulateJson);
        JsonNode simulateBody = simulateResponse.getBody();

        if (!simulateBody.path("error").isMissingNode()) {
            throw new RestClientException(simulateBody.path("error").path("message").asText());
        }

        String result = simulateBody.path("result").asText();
        if (result.endsWith("0")) {
            // tx will fail
            throw new ETokenException("Simulation failed. Json: " + simulateJson);
        }
    }

    private static String sendTransaction(Transaction tx) {
        String rawHex = assure0x(Hex.toHexString(tx.getEncoded()));
        String json = writeObjectAsString(prepareRpcCall("eth_sendRawTransaction", rawHex));

        // make request
        RestClient.Response response = RestClient.post(NODE_URL, json);
        JsonNode body = response.getBody();

        if (!body.path("error").isMissingNode()) {
            throw new RestClientException(body.path("error").path("message").asText());
        }

        String txHash = body.path("result").asText();

        return txHash;
    }

    private static String prepareContractCall(CallTransaction.Function function, String contractAddress, Object... args) {
        byte[] encodedData = function.encode(args);
        String data = assure0x(Hex.toHexString(encodedData));

        Map<String, Object> params = new HashMap<>();
        params.put("to", assure0x(contractAddress));
        params.put("data", data);

        return writeObjectAsString(prepareRpcCall("eth_call", params, "pending"));
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
