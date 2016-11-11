package co.ambisafe.etoken;

import co.ambisafe.etoken.exception.RestClientException;
import co.ambisafe.etoken.service.AmbisafeNode;
import org.junit.Test;

import java.math.BigInteger;

public class AmbisafeNodeTest {

    @Test
    public void getBalance() {
        String address = "0x182c44e3afd39811947d344082ec5fd9e6c0a6b7";
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

    @Test(expected = RestClientException.class)
    public void sendTransaction() {
        String recipient = "0x182c44e3afd39811947d344082ec5fd9e6c0a6b7";
//        ECKey key = ECKey.fromPrivate(Hex.decode("afcccf3e84e9164099e861c0f627f552ec5d152e3c2f0a11cb3efe4a401064c2"));

        String txHash = AmbisafeNode.transfer(recipient, 1000, "CC", new byte[]{});
    }
}
