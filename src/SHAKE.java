/**
 * 06/04/2021
 * TCSS 487 A Sp 21: Cryptography
 * Group Project –cryptographic library & app
 * Anna Blanton, Caleb Chang and Taehong Kim
 *
 * References: The shake from the tiny-sha3 implementation
 * by Dr. Markku-Juhani O. Saarinen
 * https://github.com/mjosaarinen/tiny_sha3
 *
 */

public class SHAKE extends SHA3 {
    /**
     * Initialize the SHAKE128 sponge.
     */
    void shake128_init() {
        sha3_init(16);
    }

    /**
     * Initialize the SHAKE256 sponge.
     */
    void shake256_init() {
        sha3_init(32);
    }

    /**
     * Update the SHAKE256 sponge with a byte-oriented data chunk.
     * @param data  byte-oriented data buffer
     * @param len   byte count on the buffer (starting at index 0)
     */
    void shake_update(byte[] data, int len) {
        sha3_update(data, len);
    }
    void shake_xof() {
        b[pt] ^= (byte) (this.ext ? 0x04 : 0x1F);
        b[rsiz - 1] ^=  0x80;
        sha3_keccakf(b);
        pt = 0;
    }

    /**
     * Squeeze a chunk of hashed bytes from the sponge.
     * Repeat as many times as needed to extract the total desired number of bytes.
     * @param out   hash value buffer
     * @param len   squeezed byte count
     */
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

    /**
     * Compute a SHAKE128 hash (md) of given byte length from "in"
     */
    byte[] shake128(byte[] in, int inlen, byte[] md, int mdlen) {
        shake128_init();
        shake_update(in, inlen);
        shake_xof();
        shake_out(md, mdlen);
        return md;
    }

    /**
     * Compute a SHAKE128 hash (md) of given byte length from "in"
     */
    byte[] shake256(byte[] in, int inlen, byte[] md, int mdlen) {
        shake256_init();
        shake_update(in, inlen);
        shake_xof();
        shake_out(md, mdlen);
        return md;
    }
}