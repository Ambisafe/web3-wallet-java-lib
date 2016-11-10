package co.ambisafe.etoken.imports.cryptohash;

abstract class KeccakCore extends DigestEngine {
    private long[] A;
    private byte[] tmpOut;
    private static final long[] RC = new long[]{1L, 32898L, -9223372036854742902L, -9223372034707259392L, 32907L, 2147483649L, -9223372034707259263L, -9223372036854743031L, 138L, 136L, 2147516425L, 2147483658L, 2147516555L, -9223372036854775669L, -9223372036854742903L, -9223372036854743037L, -9223372036854743038L, -9223372036854775680L, 32778L, -9223372034707292150L, -9223372034707259263L, -9223372036854742912L, 2147483649L, -9223372034707259384L};

    KeccakCore() {
    }

    private static final void encodeLELong(long val, byte[] buf, int off) {
        buf[off + 0] = (byte)((int)val);
        buf[off + 1] = (byte)((int)(val >>> 8));
        buf[off + 2] = (byte)((int)(val >>> 16));
        buf[off + 3] = (byte)((int)(val >>> 24));
        buf[off + 4] = (byte)((int)(val >>> 32));
        buf[off + 5] = (byte)((int)(val >>> 40));
        buf[off + 6] = (byte)((int)(val >>> 48));
        buf[off + 7] = (byte)((int)(val >>> 56));
    }

    private static final long decodeLELong(byte[] buf, int off) {
        return (long)buf[off + 0] & 255L | ((long)buf[off + 1] & 255L) << 8 | ((long)buf[off + 2] & 255L) << 16 | ((long)buf[off + 3] & 255L) << 24 | ((long)buf[off + 4] & 255L) << 32 | ((long)buf[off + 5] & 255L) << 40 | ((long)buf[off + 6] & 255L) << 48 | ((long)buf[off + 7] & 255L) << 56;
    }

    protected void engineReset() {
        this.doReset();
    }

    protected void processBlock(byte[] data) {
        for(int t0 = 0; t0 < data.length; t0 += 8) {
            this.A[t0 >>> 3] ^= decodeLELong(data, t0);
        }

        for(int j = 0; j < 24; j += 2) {
            long tt0 = this.A[1] ^ this.A[6];
            long tt1 = this.A[11] ^ this.A[16];
            tt0 ^= this.A[21] ^ tt1;
            tt0 = tt0 << 1 | tt0 >>> 63;
            long tt2 = this.A[4] ^ this.A[9];
            long tt3 = this.A[14] ^ this.A[19];
            tt0 ^= this.A[24];
            tt2 ^= tt3;
            long t01 = tt0 ^ tt2;
            tt0 = this.A[2] ^ this.A[7];
            tt1 = this.A[12] ^ this.A[17];
            tt0 ^= this.A[22] ^ tt1;
            tt0 = tt0 << 1 | tt0 >>> 63;
            tt2 = this.A[0] ^ this.A[5];
            tt3 = this.A[10] ^ this.A[15];
            tt0 ^= this.A[20];
            tt2 ^= tt3;
            long t1 = tt0 ^ tt2;
            tt0 = this.A[3] ^ this.A[8];
            tt1 = this.A[13] ^ this.A[18];
            tt0 ^= this.A[23] ^ tt1;
            tt0 = tt0 << 1 | tt0 >>> 63;
            tt2 = this.A[1] ^ this.A[6];
            tt3 = this.A[11] ^ this.A[16];
            tt0 ^= this.A[21];
            tt2 ^= tt3;
            long t2 = tt0 ^ tt2;
            tt0 = this.A[4] ^ this.A[9];
            tt1 = this.A[14] ^ this.A[19];
            tt0 ^= this.A[24] ^ tt1;
            tt0 = tt0 << 1 | tt0 >>> 63;
            tt2 = this.A[2] ^ this.A[7];
            tt3 = this.A[12] ^ this.A[17];
            tt0 ^= this.A[22];
            tt2 ^= tt3;
            long t3 = tt0 ^ tt2;
            tt0 = this.A[0] ^ this.A[5];
            tt1 = this.A[10] ^ this.A[15];
            tt0 ^= this.A[20] ^ tt1;
            tt0 = tt0 << 1 | tt0 >>> 63;
            tt2 = this.A[3] ^ this.A[8];
            tt3 = this.A[13] ^ this.A[18];
            tt0 ^= this.A[23];
            tt2 ^= tt3;
            long t4 = tt0 ^ tt2;
            this.A[0] ^= t01;
            this.A[5] ^= t01;
            this.A[10] ^= t01;
            this.A[15] ^= t01;
            this.A[20] ^= t01;
            this.A[1] ^= t1;
            this.A[6] ^= t1;
            this.A[11] ^= t1;
            this.A[16] ^= t1;
            this.A[21] ^= t1;
            this.A[2] ^= t2;
            this.A[7] ^= t2;
            this.A[12] ^= t2;
            this.A[17] ^= t2;
            this.A[22] ^= t2;
            this.A[3] ^= t3;
            this.A[8] ^= t3;
            this.A[13] ^= t3;
            this.A[18] ^= t3;
            this.A[23] ^= t3;
            this.A[4] ^= t4;
            this.A[9] ^= t4;
            this.A[14] ^= t4;
            this.A[19] ^= t4;
            this.A[24] ^= t4;
            this.A[5] = this.A[5] << 36 | this.A[5] >>> 28;
            this.A[10] = this.A[10] << 3 | this.A[10] >>> 61;
            this.A[15] = this.A[15] << 41 | this.A[15] >>> 23;
            this.A[20] = this.A[20] << 18 | this.A[20] >>> 46;
            this.A[1] = this.A[1] << 1 | this.A[1] >>> 63;
            this.A[6] = this.A[6] << 44 | this.A[6] >>> 20;
            this.A[11] = this.A[11] << 10 | this.A[11] >>> 54;
            this.A[16] = this.A[16] << 45 | this.A[16] >>> 19;
            this.A[21] = this.A[21] << 2 | this.A[21] >>> 62;
            this.A[2] = this.A[2] << 62 | this.A[2] >>> 2;
            this.A[7] = this.A[7] << 6 | this.A[7] >>> 58;
            this.A[12] = this.A[12] << 43 | this.A[12] >>> 21;
            this.A[17] = this.A[17] << 15 | this.A[17] >>> 49;
            this.A[22] = this.A[22] << 61 | this.A[22] >>> 3;
            this.A[3] = this.A[3] << 28 | this.A[3] >>> 36;
            this.A[8] = this.A[8] << 55 | this.A[8] >>> 9;
            this.A[13] = this.A[13] << 25 | this.A[13] >>> 39;
            this.A[18] = this.A[18] << 21 | this.A[18] >>> 43;
            this.A[23] = this.A[23] << 56 | this.A[23] >>> 8;
            this.A[4] = this.A[4] << 27 | this.A[4] >>> 37;
            this.A[9] = this.A[9] << 20 | this.A[9] >>> 44;
            this.A[14] = this.A[14] << 39 | this.A[14] >>> 25;
            this.A[19] = this.A[19] << 8 | this.A[19] >>> 56;
            this.A[24] = this.A[24] << 14 | this.A[24] >>> 50;
            long bnn = ~this.A[12];
            long kt = this.A[6] | this.A[12];
            long c0 = this.A[0] ^ kt;
            kt = bnn | this.A[18];
            long c1 = this.A[6] ^ kt;
            kt = this.A[18] & this.A[24];
            long c2 = this.A[12] ^ kt;
            kt = this.A[24] | this.A[0];
            long c3 = this.A[18] ^ kt;
            kt = this.A[0] & this.A[6];
            long c4 = this.A[24] ^ kt;
            this.A[0] = c0;
            this.A[6] = c1;
            this.A[12] = c2;
            this.A[18] = c3;
            this.A[24] = c4;
            bnn = ~this.A[22];
            kt = this.A[9] | this.A[10];
            c0 = this.A[3] ^ kt;
            kt = this.A[10] & this.A[16];
            c1 = this.A[9] ^ kt;
            kt = this.A[16] | bnn;
            c2 = this.A[10] ^ kt;
            kt = this.A[22] | this.A[3];
            c3 = this.A[16] ^ kt;
            kt = this.A[3] & this.A[9];
            c4 = this.A[22] ^ kt;
            this.A[3] = c0;
            this.A[9] = c1;
            this.A[10] = c2;
            this.A[16] = c3;
            this.A[22] = c4;
            bnn = ~this.A[19];
            kt = this.A[7] | this.A[13];
            c0 = this.A[1] ^ kt;
            kt = this.A[13] & this.A[19];
            c1 = this.A[7] ^ kt;
            kt = bnn & this.A[20];
            c2 = this.A[13] ^ kt;
            kt = this.A[20] | this.A[1];
            c3 = bnn ^ kt;
            kt = this.A[1] & this.A[7];
            c4 = this.A[20] ^ kt;
            this.A[1] = c0;
            this.A[7] = c1;
            this.A[13] = c2;
            this.A[19] = c3;
            this.A[20] = c4;
            bnn = ~this.A[17];
            kt = this.A[5] & this.A[11];
            c0 = this.A[4] ^ kt;
            kt = this.A[11] | this.A[17];
            c1 = this.A[5] ^ kt;
            kt = bnn | this.A[23];
            c2 = this.A[11] ^ kt;
            kt = this.A[23] & this.A[4];
            c3 = bnn ^ kt;
            kt = this.A[4] | this.A[5];
            c4 = this.A[23] ^ kt;
            this.A[4] = c0;
            this.A[5] = c1;
            this.A[11] = c2;
            this.A[17] = c3;
            this.A[23] = c4;
            bnn = ~this.A[8];
            kt = bnn & this.A[14];
            c0 = this.A[2] ^ kt;
            kt = this.A[14] | this.A[15];
            c1 = bnn ^ kt;
            kt = this.A[15] & this.A[21];
            c2 = this.A[14] ^ kt;
            kt = this.A[21] | this.A[2];
            c3 = this.A[15] ^ kt;
            kt = this.A[2] & this.A[8];
            c4 = this.A[21] ^ kt;
            this.A[2] = c0;
            this.A[8] = c1;
            this.A[14] = c2;
            this.A[15] = c3;
            this.A[21] = c4;
            this.A[0] ^= RC[j + 0];
            tt0 = this.A[6] ^ this.A[9];
            tt1 = this.A[7] ^ this.A[5];
            tt0 ^= this.A[8] ^ tt1;
            tt0 = tt0 << 1 | tt0 >>> 63;
            tt2 = this.A[24] ^ this.A[22];
            tt3 = this.A[20] ^ this.A[23];
            tt0 ^= this.A[21];
            tt2 ^= tt3;
            t01 = tt0 ^ tt2;
            tt0 = this.A[12] ^ this.A[10];
            tt1 = this.A[13] ^ this.A[11];
            tt0 ^= this.A[14] ^ tt1;
            tt0 = tt0 << 1 | tt0 >>> 63;
            tt2 = this.A[0] ^ this.A[3];
            tt3 = this.A[1] ^ this.A[4];
            tt0 ^= this.A[2];
            tt2 ^= tt3;
            t1 = tt0 ^ tt2;
            tt0 = this.A[18] ^ this.A[16];
            tt1 = this.A[19] ^ this.A[17];
            tt0 ^= this.A[15] ^ tt1;
            tt0 = tt0 << 1 | tt0 >>> 63;
            tt2 = this.A[6] ^ this.A[9];
            tt3 = this.A[7] ^ this.A[5];
            tt0 ^= this.A[8];
            tt2 ^= tt3;
            t2 = tt0 ^ tt2;
            tt0 = this.A[24] ^ this.A[22];
            tt1 = this.A[20] ^ this.A[23];
            tt0 ^= this.A[21] ^ tt1;
            tt0 = tt0 << 1 | tt0 >>> 63;
            tt2 = this.A[12] ^ this.A[10];
            tt3 = this.A[13] ^ this.A[11];
            tt0 ^= this.A[14];
            tt2 ^= tt3;
            t3 = tt0 ^ tt2;
            tt0 = this.A[0] ^ this.A[3];
            tt1 = this.A[1] ^ this.A[4];
            tt0 ^= this.A[2] ^ tt1;
            tt0 = tt0 << 1 | tt0 >>> 63;
            tt2 = this.A[18] ^ this.A[16];
            tt3 = this.A[19] ^ this.A[17];
            tt0 ^= this.A[15];
            tt2 ^= tt3;
            t4 = tt0 ^ tt2;
            this.A[0] ^= t01;
            this.A[3] ^= t01;
            this.A[1] ^= t01;
            this.A[4] ^= t01;
            this.A[2] ^= t01;
            this.A[6] ^= t1;
            this.A[9] ^= t1;
            this.A[7] ^= t1;
            this.A[5] ^= t1;
            this.A[8] ^= t1;
            this.A[12] ^= t2;
            this.A[10] ^= t2;
            this.A[13] ^= t2;
            this.A[11] ^= t2;
            this.A[14] ^= t2;
            this.A[18] ^= t3;
            this.A[16] ^= t3;
            this.A[19] ^= t3;
            this.A[17] ^= t3;
            this.A[15] ^= t3;
            this.A[24] ^= t4;
            this.A[22] ^= t4;
            this.A[20] ^= t4;
            this.A[23] ^= t4;
            this.A[21] ^= t4;
            this.A[3] = this.A[3] << 36 | this.A[3] >>> 28;
            this.A[1] = this.A[1] << 3 | this.A[1] >>> 61;
            this.A[4] = this.A[4] << 41 | this.A[4] >>> 23;
            this.A[2] = this.A[2] << 18 | this.A[2] >>> 46;
            this.A[6] = this.A[6] << 1 | this.A[6] >>> 63;
            this.A[9] = this.A[9] << 44 | this.A[9] >>> 20;
            this.A[7] = this.A[7] << 10 | this.A[7] >>> 54;
            this.A[5] = this.A[5] << 45 | this.A[5] >>> 19;
            this.A[8] = this.A[8] << 2 | this.A[8] >>> 62;
            this.A[12] = this.A[12] << 62 | this.A[12] >>> 2;
            this.A[10] = this.A[10] << 6 | this.A[10] >>> 58;
            this.A[13] = this.A[13] << 43 | this.A[13] >>> 21;
            this.A[11] = this.A[11] << 15 | this.A[11] >>> 49;
            this.A[14] = this.A[14] << 61 | this.A[14] >>> 3;
            this.A[18] = this.A[18] << 28 | this.A[18] >>> 36;
            this.A[16] = this.A[16] << 55 | this.A[16] >>> 9;
            this.A[19] = this.A[19] << 25 | this.A[19] >>> 39;
            this.A[17] = this.A[17] << 21 | this.A[17] >>> 43;
            this.A[15] = this.A[15] << 56 | this.A[15] >>> 8;
            this.A[24] = this.A[24] << 27 | this.A[24] >>> 37;
            this.A[22] = this.A[22] << 20 | this.A[22] >>> 44;
            this.A[20] = this.A[20] << 39 | this.A[20] >>> 25;
            this.A[23] = this.A[23] << 8 | this.A[23] >>> 56;
            this.A[21] = this.A[21] << 14 | this.A[21] >>> 50;
            bnn = ~this.A[13];
            kt = this.A[9] | this.A[13];
            c0 = this.A[0] ^ kt;
            kt = bnn | this.A[17];
            c1 = this.A[9] ^ kt;
            kt = this.A[17] & this.A[21];
            c2 = this.A[13] ^ kt;
            kt = this.A[21] | this.A[0];
            c3 = this.A[17] ^ kt;
            kt = this.A[0] & this.A[9];
            c4 = this.A[21] ^ kt;
            this.A[0] = c0;
            this.A[9] = c1;
            this.A[13] = c2;
            this.A[17] = c3;
            this.A[21] = c4;
            bnn = ~this.A[14];
            kt = this.A[22] | this.A[1];
            c0 = this.A[18] ^ kt;
            kt = this.A[1] & this.A[5];
            c1 = this.A[22] ^ kt;
            kt = this.A[5] | bnn;
            c2 = this.A[1] ^ kt;
            kt = this.A[14] | this.A[18];
            c3 = this.A[5] ^ kt;
            kt = this.A[18] & this.A[22];
            c4 = this.A[14] ^ kt;
            this.A[18] = c0;
            this.A[22] = c1;
            this.A[1] = c2;
            this.A[5] = c3;
            this.A[14] = c4;
            bnn = ~this.A[23];
            kt = this.A[10] | this.A[19];
            c0 = this.A[6] ^ kt;
            kt = this.A[19] & this.A[23];
            c1 = this.A[10] ^ kt;
            kt = bnn & this.A[2];
            c2 = this.A[19] ^ kt;
            kt = this.A[2] | this.A[6];
            c3 = bnn ^ kt;
            kt = this.A[6] & this.A[10];
            c4 = this.A[2] ^ kt;
            this.A[6] = c0;
            this.A[10] = c1;
            this.A[19] = c2;
            this.A[23] = c3;
            this.A[2] = c4;
            bnn = ~this.A[11];
            kt = this.A[3] & this.A[7];
            c0 = this.A[24] ^ kt;
            kt = this.A[7] | this.A[11];
            c1 = this.A[3] ^ kt;
            kt = bnn | this.A[15];
            c2 = this.A[7] ^ kt;
            kt = this.A[15] & this.A[24];
            c3 = bnn ^ kt;
            kt = this.A[24] | this.A[3];
            c4 = this.A[15] ^ kt;
            this.A[24] = c0;
            this.A[3] = c1;
            this.A[7] = c2;
            this.A[11] = c3;
            this.A[15] = c4;
            bnn = ~this.A[16];
            kt = bnn & this.A[20];
            c0 = this.A[12] ^ kt;
            kt = this.A[20] | this.A[4];
            c1 = bnn ^ kt;
            kt = this.A[4] & this.A[8];
            c2 = this.A[20] ^ kt;
            kt = this.A[8] | this.A[12];
            c3 = this.A[4] ^ kt;
            kt = this.A[12] & this.A[16];
            c4 = this.A[8] ^ kt;
            this.A[12] = c0;
            this.A[16] = c1;
            this.A[20] = c2;
            this.A[4] = c3;
            this.A[8] = c4;
            this.A[0] ^= RC[j + 1];
            long t = this.A[5];
            this.A[5] = this.A[18];
            this.A[18] = this.A[11];
            this.A[11] = this.A[10];
            this.A[10] = this.A[6];
            this.A[6] = this.A[22];
            this.A[22] = this.A[20];
            this.A[20] = this.A[12];
            this.A[12] = this.A[19];
            this.A[19] = this.A[15];
            this.A[15] = this.A[24];
            this.A[24] = this.A[8];
            this.A[8] = t;
            t = this.A[1];
            this.A[1] = this.A[9];
            this.A[9] = this.A[14];
            this.A[14] = this.A[2];
            this.A[2] = this.A[13];
            this.A[13] = this.A[23];
            this.A[23] = this.A[4];
            this.A[4] = this.A[21];
            this.A[21] = this.A[16];
            this.A[16] = this.A[3];
            this.A[3] = this.A[17];
            this.A[17] = this.A[7];
            this.A[7] = t;
        }

    }

    protected void doPadding(byte[] out, int off) {
        int ptr = this.flush();
        byte[] buf = this.getBlockBuffer();
        int dlen;
        if(ptr + 1 == buf.length) {
            buf[ptr] = -127;
        } else {
            buf[ptr] = 1;

            for(dlen = ptr + 1; dlen < buf.length - 1; ++dlen) {
                buf[dlen] = 0;
            }

            buf[buf.length - 1] = -128;
        }

        this.processBlock(buf);
        this.A[1] = ~this.A[1];
        this.A[2] = ~this.A[2];
        this.A[8] = ~this.A[8];
        this.A[12] = ~this.A[12];
        this.A[17] = ~this.A[17];
        this.A[20] = ~this.A[20];
        dlen = this.getDigestLength();

        for(int i = 0; i < dlen; i += 8) {
            encodeLELong(this.A[i >>> 3], this.tmpOut, i);
        }

        System.arraycopy(this.tmpOut, 0, out, off, dlen);
    }

    protected void doInit() {
        this.A = new long[25];
        this.tmpOut = new byte[this.getDigestLength() + 7 & -8];
        this.doReset();
    }

    public int getBlockLength() {
        return 200 - 2 * this.getDigestLength();
    }

    private final void doReset() {
        for(int i = 0; i < 25; ++i) {
            this.A[i] = 0L;
        }

        this.A[1] = -1L;
        this.A[2] = -1L;
        this.A[8] = -1L;
        this.A[12] = -1L;
        this.A[17] = -1L;
        this.A[20] = -1L;
    }

    protected Digest copyState(KeccakCore dst) {
        System.arraycopy(this.A, 0, dst.A, 0, 25);
        return super.copyState(dst);
    }

    public String toString() {
        return "Keccak-" + (this.getDigestLength() << 3);
    }
}

