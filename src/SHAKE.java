public class SHAKE extends SHA3 {

    void shake128_init() {
        sha3_init(16);
    }

    void shake256_init() {
        sha3_init(32);
    }

    void shake_update(byte[] data, int len) {
        sha3_update(data, len);
    }
    void shake_xof() {
        b[pt] ^= (byte) (this.ext ? 0x04 : 0x1F);
        b[rsiz - 1] ^=  0x80;
        sha3_keccakf(b);
        pt = 0;
    }

    void shake_out(byte[] out, int len) {
        int i;
        int j;

        j = pt;
        for (i = 0; i < len; i++) {
            if (j >= rsiz) {
                sha3_keccakf(b);
                j = 0;
            }
            out[i] = b[j++];
        }
        pt = j;
    }

    byte[] shake128(byte[] in, int inlen, byte[] md, int mdlen) {
        shake128_init();
        shake_update(in, inlen);
        shake_xof();
        shake_out(md, mdlen);

        return md;
    }

    byte[] shake256(byte[] in, int inlen, byte[] md, int mdlen) {
        shake256_init();
        shake_update(in, inlen);
        shake_xof();
        shake_out(md, mdlen);
        return md;
    }
}