package co.ambisafe.etoken.imports.cryptohash;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

public abstract class DigestEngine implements Digest {
    private int digestLen;
    private int blockLen;
    private int inputLen;
    private byte[] inputBuf;
    private byte[] outputBuf;
    private long blockCount;

    protected abstract void engineReset();

    protected abstract void processBlock(byte[] var1);

    protected abstract void doPadding(byte[] var1, int var2);

    protected abstract void doInit();

    public DigestEngine() {
        this.doInit();
        this.digestLen = this.getDigestLength();
        this.blockLen = this.getInternalBlockLength();
        this.inputBuf = new byte[this.blockLen];
        this.outputBuf = new byte[this.digestLen];
        this.inputLen = 0;
        this.blockCount = 0L;
    }

    private void adjustDigestLen() {
        if(this.digestLen == 0) {
            this.digestLen = this.getDigestLength();
            this.outputBuf = new byte[this.digestLen];
        }

    }

    public byte[] digest() {
        this.adjustDigestLen();
        byte[] result = new byte[this.digestLen];
        this.digest(result, 0, this.digestLen);
        return result;
    }

    public byte[] digest(byte[] input) {
        this.update(input, 0, input.length);
        return this.digest();
    }

    public int digest(byte[] buf, int offset, int len) {
        this.adjustDigestLen();
        if(len >= this.digestLen) {
            this.doPadding(buf, offset);
            this.reset();
            return this.digestLen;
        } else {
            this.doPadding(this.outputBuf, 0);
            System.arraycopy(this.outputBuf, 0, buf, offset, len);
            this.reset();
            return len;
        }
    }

    public void reset() {
        this.engineReset();
        this.inputLen = 0;
        this.blockCount = 0L;
    }

    public void update(byte input) {
        this.inputBuf[this.inputLen++] = input;
        if(this.inputLen == this.blockLen) {
            this.processBlock(this.inputBuf);
            ++this.blockCount;
            this.inputLen = 0;
        }

    }

    public void update(byte[] input) {
        this.update(input, 0, input.length);
    }

    public void update(byte[] input, int offset, int len) {
        while(len > 0) {
            int copyLen = this.blockLen - this.inputLen;
            if(copyLen > len) {
                copyLen = len;
            }

            System.arraycopy(input, offset, this.inputBuf, this.inputLen, copyLen);
            offset += copyLen;
            this.inputLen += copyLen;
            len -= copyLen;
            if(this.inputLen == this.blockLen) {
                this.processBlock(this.inputBuf);
                ++this.blockCount;
                this.inputLen = 0;
            }
        }

    }

    protected int getInternalBlockLength() {
        return this.getBlockLength();
    }

    protected final int flush() {
        return this.inputLen;
    }

    protected final byte[] getBlockBuffer() {
        return this.inputBuf;
    }

    protected long getBlockCount() {
        return this.blockCount;
    }

    protected Digest copyState(DigestEngine dest) {
        dest.inputLen = this.inputLen;
        dest.blockCount = this.blockCount;
        System.arraycopy(this.inputBuf, 0, dest.inputBuf, 0, this.inputBuf.length);
        this.adjustDigestLen();
        dest.adjustDigestLen();
        System.arraycopy(this.outputBuf, 0, dest.outputBuf, 0, this.outputBuf.length);
        return dest;
    }
}
