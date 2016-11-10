package co.ambisafe.etoken.imports.cryptohash;

public class Keccak256 extends KeccakCore {
    public Keccak256() {
    }

    public Digest copy() {
        return this.copyState(new Keccak256());
    }

    public int getDigestLength() {
        return 32;
    }
}
