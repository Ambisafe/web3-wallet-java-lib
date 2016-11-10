package co.ambisafe.etoken.service;

import co.ambisafe.etoken.model.Account;
import co.ambisafe.etoken.model.Container;
import co.ambisafe.etoken.utils.CryptoUtils;
import org.ethereum.crypto.ECKey;
import org.spongycastle.util.encoders.Hex;

public class AccountService {

    public static Account generate(String password) {
        String salt = CryptoUtils.getUuid();
        String id = CryptoUtils.getUuid();
        byte[] iv = CryptoUtils.getRandomIv();

        ECKey ecKey = new ECKey();
        byte[] containerData = CryptoUtils.encryptData(ecKey.getPrivKeyBytes(), iv, salt, password);

        String address = Hex.toHexString(ecKey.getAddress());
        byte[] publicKey = ecKey.getPubKey();

        Container container = new Container(containerData, iv, publicKey, salt);

        return new Account(address, container, id, 0);
    }
}
