package co.ambisafe.etoken;

import co.ambisafe.etoken.service.ETokenHistory;
import org.junit.Test;

public class ETokenHistoryTest {

    @Test
    public void getTxList() {
        String recipient = "0x60dda47483288e673dc0d522a715ae8eed5d60fd";

        ETokenHistory.TxList txList = ETokenHistory.getTxList(recipient);
        System.out.println(txList);
    }

    @Test
    public void getTxList_withParams() {
        String recipient = "0x60dda47483288e673dc0d522a715ae8eed5d60fd";
        String param1 = "max", value1 = "2", param2 = "skip", value2 = "2";

        ETokenHistory.TxList txList = ETokenHistory.getTxList(recipient, param1, value1, param2, value2);
        System.out.println(txList);
    }
}
