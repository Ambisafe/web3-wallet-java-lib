package co.ambisafe.etoken.imports.cryptohash;

public interface Digest {
    void update(byte var1);

    void update(byte[] var1);

    void update(byte[] var1, int var2, int var3);

    byte[] digest();

    byte[] digest(byte[] var1);

    int digest(byte[] var1, int var2, int var3);

    int getDigestLength();

    void reset();

    Digest copy();

    int getBlockLength();

    String toString();
}

