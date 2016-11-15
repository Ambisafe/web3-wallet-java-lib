package co.ambisafe.etoken.imports;

import co.ambisafe.etoken.exceptions.CryptoException;
import org.spongycastle.crypto.engines.AESFastEngine;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class AesCbcCrypto {

    private PaddedBufferedBlockCipher encryptCipher = null;
    private PaddedBufferedBlockCipher decryptCipher = null;

    // Buffer used to transport the bytes from one stream to another
    private byte[] buf = new byte[16];              //input buffer
    private byte[] obuf = new byte[512];            //output buffer
    // The key
    private byte[] key = null;
    // The initialization vector needed by the CBC mode
    private byte[] IV = null;

    // The default block size
    public static int blockSize = 16;

    public AesCbcCrypto() {
    }

    public void initCiphers() {
        //create the ciphers
        // AES block cipher in CBC mode with padding
        encryptCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));

        decryptCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));

        //create the IV parameter
        ParametersWithIV parameterIV = new ParametersWithIV(new KeyParameter(key), IV);

        encryptCipher.init(true, parameterIV);
        decryptCipher.init(false, parameterIV);
    }

    public void resetCiphers() {
        if (encryptCipher != null) {
            encryptCipher.reset();
        }
        if (decryptCipher != null) {
            decryptCipher.reset();
        }
    }

    public byte[] encrypt(byte[] input) throws CryptoException {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(input);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            int noBytesRead = 0;        //number of bytes read from input
            int noBytesProcessed = 0;   //number of bytes processed

            while ((noBytesRead = in.read(buf)) >= 0) {
                noBytesProcessed = encryptCipher.processBytes(buf, 0, noBytesRead, obuf, 0);
                out.write(obuf, 0, noBytesProcessed);
            }
            noBytesProcessed = encryptCipher.doFinal(obuf, 0);

            out.write(obuf, 0, noBytesProcessed);
            out.flush();
            in.close();
            out.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    public byte[] decrypt(byte[] input) throws CryptoException {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(input);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            int noBytesRead = 0;        //number of bytes read from input
            int noBytesProcessed = 0;   //number of bytes processed

            while ((noBytesRead = in.read(buf)) >= 0) {
                noBytesProcessed = decryptCipher.processBytes(buf, 0, noBytesRead, obuf, 0);
                out.write(obuf, 0, noBytesProcessed);
            }
            noBytesProcessed = decryptCipher.doFinal(obuf, 0);

            out.write(obuf, 0, noBytesProcessed);
            out.flush();
            in.close();
            out.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public void setIV(byte[] IV) {
        this.IV = IV;
    }

}
