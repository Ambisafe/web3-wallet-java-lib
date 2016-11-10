package co.ambisafe.etoken.imports;

import co.ambisafe.etoken.imports.cryptohash.Keccak256;
import co.ambisafe.etoken.utils.CryptoUtils;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.RIPEMD160Digest;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static co.ambisafe.etoken.imports.ByteUtil.EMPTY_BYTE_ARRAY;
import static java.util.Arrays.copyOfRange;

public class HashUtil {

    private static final int MAX_ENTRIES = 100; // Should contain most commonly hashed values
    public static final byte[] EMPTY_DATA_HASH = sha3(EMPTY_BYTE_ARRAY);

    private static final MessageDigest sha256digest;

    static {
        try {
            sha256digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);  // Can't happen.
        }
    }

    /**
     * @param input - data for hashing
     * @return - sha256 hash of the data
     */
    public static byte[] sha256(byte[] input) {
        return sha256digest.digest(input);
    }

    public static byte[] sha3(byte[] input) {
        Keccak256 digest =  new Keccak256();
        digest.update(input);
        return digest.digest();
    }

    /**
     * hashing chunk of the data
     * @param input - data for hash
     * @param start - start of hashing chunk
     * @param length - length of hashing chunk
     * @return - sha3 hash of the chunk
     */
    public static byte[] sha3(byte[] input, int start, int length) {
        return SHA3Helper.sha3(input, start, length);
    }


    /**
     * @param data - message to hash
     * @return - reipmd160 hash of the message
     */
    public static byte[] ripemd160(byte[] data) {
        Digest digest = new RIPEMD160Digest();
        if (data != null) {
            byte[] resBuf = new byte[digest.getDigestSize()];
            digest.update(data, 0, data.length);
            digest.doFinal(resBuf, 0);
            return resBuf;
        }
        throw new NullPointerException("Can't hash a NULL value");
    }


    /**
     * Calculates RIGTMOST160(SHA3(input)). This is used in address calculations.
     * *
     * @param input - data
     * @return - 20 right bytes of the hash sha3 of the data
     */
    public static byte[] sha3omit12(byte[] input) {
        byte[] hash = sha3(input);
        return copyOfRange(hash, 12, hash.length);
    }

    /**
     * @see #doubleDigest(byte[], int, int)
     *
     * @param input -
     * @return -
     */
    public static byte[] doubleDigest(byte[] input) {
        return doubleDigest(input, 0, input.length);
    }

    /**
     * Calculates the SHA-256 hash of the given byte range, and then hashes the resulting hash again. This is
     * standard procedure in Bitcoin. The resulting hash is in big endian form.
     *
     * @param input -
     * @param offset -
     * @param length -
     * @return -
     */
    public static byte[] doubleDigest(byte[] input, int offset, int length) {
        synchronized (sha256digest) {
            sha256digest.reset();
            sha256digest.update(input, offset, length);
            byte[] first = sha256digest.digest();
            return sha256digest.digest(first);
        }
    }

    /**
     * @return generates random peer id for the HelloMessage
     */
    public static byte[] randomPeerId() {

        byte[] peerIdBytes = new BigInteger(512, CryptoUtils.getRandom()).toByteArray();

        final String peerId;
        if (peerIdBytes.length > 64)
            peerId = Hex.toHexString(peerIdBytes, 1, 64);
        else
            peerId = Hex.toHexString(peerIdBytes);

        return Hex.decode(peerId);
    }

    /**
     * @return - generate random 32 byte hash
     */
    public static byte[] randomHash(){

        byte[] randomHash = new byte[32];
        Random random = new Random();
        random.nextBytes(randomHash);
        return randomHash;
    }

    public static String shortHash(byte[] hash){
        return Hex.toHexString(hash).substring(0, 6);
    }
}
