package co.ambisafe.etoken;

import co.ambisafe.etoken.exception.RestClientException;
import co.ambisafe.etoken.imports.ECKey;
import co.ambisafe.etoken.service.AmbisafeNode;
import com.auth0.jwt.internal.org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import org.springframework.util.Assert;

import java.math.BigInteger;

public class AmbisafeNodeETokenTest {

    @Test
    public void getBalance() {
//        String address = "0x182c44e3afd39811947d344082ec5fd9e6c0a6b7";
        String address = "a085e2f5b4d6e8e611853ad585a1b6c444116ce2";
        String symbol = "CC";

        BigInteger balance = AmbisafeNode.EToken.getBalance(address, symbol);
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
//        byte[] privateKey = account.getPrivateKey("test");
        byte[] privateKey = ECKey.fromPrivate(Hex.decode("afcccf3e84e9164099e861c0f627f552ec5d152e3c2f0a11cb3efe4a401064c2")).getPrivKeyBytes();

        try {
            String txHash = AmbisafeNode.EToken.transfer(recipient, amount, symbol, privateKey);
            System.out.println("Hash: " + txHash);
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
            String txHash = AmbisafeNode.Eth.transfer(recipient, amount, privateKey);
        } catch (RestClientException e) {
            System.out.println("Error: " + e.getMessage());
            Assert.hasText("Insufficient funds", e.getMessage());
        }
    }

    @Test
    public void getBaseUnit() {
        String symbol = "CC";

        System.out.println(AmbisafeNode.EToken.getBaseUnit(symbol));
    }

    @Test
    public void activation() {
        Account account = Account.generate("test");

        AmbisafeNode.ETokenETH.activateAccount(account.getPrivateKey("test"));
    }
}
