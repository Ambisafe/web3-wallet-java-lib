package co.ambisafe.etoken.model;

import com.auth0.jwt.internal.org.bouncycastle.util.encoders.Hex;

public class Container {

    private byte[] data;
    private byte[] iv;
    private byte[] publicKey;
    private String salt;

    public Container(byte[] data, byte[] iv, byte[] publicKey, String salt) {
        this.data = data;
        this.iv = iv;
        this.publicKey = publicKey;
        this.salt = salt;
    }

    public byte[] getData() {
        return data;
    }

    public String getDataHex() {
        return Hex.toHexString(data);
    }

    public byte[] getIv() {
        return iv;
    }

    public String getIvHex() {
        return Hex.toHexString(iv);
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public String getPublicKeyHex() {
        return Hex.toHexString(publicKey);
    }

    public String getSalt() {
        return salt;
    }
}
