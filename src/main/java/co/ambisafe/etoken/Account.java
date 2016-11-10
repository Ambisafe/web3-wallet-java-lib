package co.ambisafe.etoken;

import co.ambisafe.etoken.exception.CryptoException;
import co.ambisafe.etoken.utils.CryptoUtils;
import org.ethereum.crypto.ECKey;
import org.spongycastle.util.encoders.Hex;

public class Account {

    private String address;
    private Container container;
    private String id;
    private int version;

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

    public Account(String address, Container container, String id, int version) {
        this.address = address;
        this.container = container;
        this.id = id;
        this.version = version;
    }

    public String getAddress() {
        return address;
    }

    public byte[] getPublicKey() {
        return container.getPublicKey();
    }

    public String getPublicKeyHex() {
        return Hex.toHexString(container.getPublicKey());
    }

    public Container getContainer() {
        return container;
    }

    public String getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public byte[] getPrivateKey(String password) throws CryptoException {
        return CryptoUtils.decryptData(container, password);
    }

    public String getPrivateKeyHex(String password) throws CryptoException {
        return Hex.toHexString(getPrivateKey(password));
    }

    public void changePassword(String oldPassword, String newPassword) throws CryptoException {
        byte[] privateKey = CryptoUtils.decryptData(container, oldPassword);

        String salt = CryptoUtils.getUuid();
        byte[] iv = CryptoUtils.getRandomIv();
        byte[] encryptedPrivateKey = CryptoUtils.encryptData(privateKey, iv, salt, newPassword);

        container = new Container(encryptedPrivateKey, iv, container.getPublicKey(), salt);
        version += 1;
        id = CryptoUtils.getUuid();
    }

    @Override
    public String toString() {
        return "{" +
                "\"address\":\"" + address + '\"' +
                ", \"container\":" + container +
                ", \"id\":\"" + id + '\"' +
                ", \"version\":\"" + version + '\"' +
                '}';
    }
}
