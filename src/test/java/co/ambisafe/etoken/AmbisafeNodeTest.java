package co.ambisafe.etoken;

import co.ambisafe.etoken.exception.RestClientException;
import co.ambisafe.etoken.service.AmbisafeNode;
import org.junit.Test;
import org.springframework.util.Assert;

import java.math.BigInteger;

public class AmbisafeNodeTest {

    @Test
    public void getBalance() {
//        String address = "0x182c44e3afd39811947d344082ec5fd9e6c0a6b7";
        String address = "a085e2f5b4d6e8e611853ad585a1b6c444116ce2";
        String symbol = "CC";

        BigInteger balance = AmbisafeNode.getBalance(address, symbol);
        System.out.println("Balance CC: " + balance);
    }

    @Test
    public void getTransactionsCount() {
        String address = "0x182c44e3afd39811947d344082ec5fd9e6c0a6b7";

        BigInteger txCount = AmbisafeNode.getTransactionsCount(address);
        System.out.println(txCount);
    }

    @Test
    public void transfer() {
        Account account = Account.generate("test");

        String recipient = "0x182c44e3afd39811947d344082ec5fd9e6c0a6b7";
        String amount = "0.000001";
        String symbol = "CC";
        byte[] privateKey = account.getPrivateKey("test");

        try {
            String txHash = AmbisafeNode.transfer(recipient, amount, symbol, privateKey);
        } catch (RestClientException e) {
            System.out.println("Error: " + e.getMessage());
            Assert.hasText("Insufficient funds", e.getMessage());
        }
    }

    @Test
    public void transferEth() {
        Account account = Account.generate("test");

        String recipient = "0x182c44e3afd39811947d344082ec5fd9e6c0a6b7";
        long amount = 1000;
        byte[] privateKey = account.getPrivateKey("test");

        try {
            String txHash = AmbisafeNode.transferEth(recipient, amount, privateKey);
        } catch (RestClientException e) {
            System.out.println("Error: " + e.getMessage());
            Assert.hasText("Insufficient funds", e.getMessage());
        }
    }

    @Test
    public void getBaseUnit() {
        String symbol = "CC";

        System.out.println(AmbisafeNode.getBaseUnit(symbol));
    }
}
