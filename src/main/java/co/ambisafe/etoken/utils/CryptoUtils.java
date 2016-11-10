package co.ambisafe.etoken.utils;

import co.ambisafe.etoken.exception.CryptoException;
import co.ambisafe.etoken.imports.AesCbcCrypto;
import co.ambisafe.etoken.imports.PBKDF2SHA512;
import co.ambisafe.etoken.model.Container;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

public class CryptoUtils {

    private static SecureRandom secureRandom = new SecureRandom();
    private static final int keyLength = 32;
    private static final int iterations = 1000;

    public static Random getRandom() {
        return secureRandom;
    }

    public static byte[] getRandomIv() {
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        return iv;
    }

    public static String getUuid() {
        return UUID.randomUUID().toString();
    }

    public static byte[] encryptData(byte[] data, byte[] iv, String salt, String password) throws CryptoException {
        AesCbcCrypto aesCbcCrypto = new AesCbcCrypto();
        prepare(aesCbcCrypto, iv, salt, password);
        return aesCbcCrypto.encrypt(data);
    }

    public static byte[] decryptData(byte[] data, byte[] iv, String salt, String password) throws CryptoException {
        AesCbcCrypto aesCbcCrypto = new AesCbcCrypto();
        prepare(aesCbcCrypto, iv, salt, password);
        return aesCbcCrypto.decrypt(data);
    }

    public static byte[] decryptData(Container container, String password) throws CryptoException {
        return decryptData(container.getData(), container.getIv(), container.getSalt(), password);
    }

//    public static byte[] sha3(byte[] input) {
//        Keccak256 digest = new Keccak256();
//        digest.update(input);
//        return digest.digest();
//    }

    private static void prepare(AesCbcCrypto aesCbcCrypto, byte[] iv, String salt, String password) {
        aesCbcCrypto.resetCiphers();
        aesCbcCrypto.setKey(PBKDF2SHA512.derive(password, salt, iterations, keyLength));
        aesCbcCrypto.setIV(iv);
        aesCbcCrypto.initCiphers();
    }
}
