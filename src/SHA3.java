/**
 * 06/04/2021
 * TCSS 487 A Sp 21: Cryptography
 * Group Project –cryptographic library & app
 * Anna Blanton, Caleb Chang and Taehong Kim
 *
 * References:
 * Based on NIST Special Publication 800-185 document
 * SHA-3 Derived Functions by John Kelsey, Shu-jen Chang, Ray Perlner
 * cSHAKE256
 * https://dx.doi.org/10.6028/NIST.SP.800-185
 */
public class SHA3 {
    public static final int KECCAKF_ROUNDS = 24;

    // state context
    byte[] b = new byte[200];
    int pt, rsiz, mdlen;
    boolean ext = false;

    public static long ROTL64(long x, long y) {
        return (((x) << (y)) | ((x) >>> (64 - (y))));
    }

    private static final long[/*24*/] keccakf_rndc = {
            0x0000000000000001L, 0x0000000000008082L, 0x800000000000808aL,
            0x8000000080008000L, 0x000000000000808bL, 0x0000000080000001L,
            0x8000000080008081L, 0x8000000000008009L, 0x000000000000008aL,
            0x0000000000000088L, 0x0000000080008009L, 0x000000008000000aL,
            0x000000008000808bL, 0x800000000000008bL, 0x8000000000008089L,
            0x8000000000008003L, 0x8000000000008002L, 0x8000000000000080L,
            0x000000000000800aL, 0x800000008000000aL, 0x8000000080008081L,
            0x8000000000008080L, 0x0000000080000001L, 0x8000000080008008L
    };
    private static final int[/*24*/] keccakf_rotc = {
            1,  3,  6,  10, 15, 21, 28, 36, 45, 55, 2,  14,
            27, 41, 56, 8,  25, 43, 62, 18, 39, 61, 20, 44
    };
    private static final int[/*24*/] keccakf_piln = {
            10, 7,  11, 17, 18, 3, 5,  16, 8,  21, 24, 4,
            15, 23, 19, 13, 12, 2, 20, 14, 22, 9,  6,  1
    };

    /**
     * Rotate the 64-bit long value x by y positions to the left
     * @param x 64-bit long value
     * @param y left rotation displacement
     * @return 64-bit long value x left-rotated by y position
     */
    public static long ROTL64(long x, int y) {
        return (x << y) | (x >>> (64 - (y)));
    }

    /**
     * Apply the Keccak-f permutation to the byte-oriented state buffer v.
     * @param v
     */
    protected static void sha3_keccakf(byte[/*200*/] v){
        long[] q = new long[25];
        long[] bc = new long[5];

        long t;

        // map from bytes (in v[]) to longs (in q[]).
        for (int i = 0, j = 0; i < 25; i++, j += 8) {
            q[i] =  (((long)v[j    ] & 0xFFL)      ) | (((long)v[j + 1] & 0xFFL) <<  8) |
                    (((long)v[j + 2] & 0xFFL) << 16) | (((long)v[j + 3] & 0xFFL) << 24) |
                    (((long)v[j + 4] & 0xFFL) << 32) | (((long)v[j + 5] & 0xFFL) << 40) |
                    (((long)v[j + 6] & 0xFFL) << 48) | (((long)v[j + 7] & 0xFFL) << 56);
        }


        // actual iteration
        for (int r = 0; r < KECCAKF_ROUNDS; r++) {

            // Theta
            for (int i = 0; i < 5; i++)
                bc[i] = q[i] ^ q[i + 5] ^ q[i + 10] ^ q[i + 15] ^ q[i + 20];

            for (int i = 0; i < 5; i++) {
                t = bc[(i + 4) % 5] ^ ROTL64(bc[(i + 1) % 5], 1);
                for (int j = 0; j < 25; j += 5)
                    q[j + i] ^= t;
            }

            // Rho Pi
            t = q[1];
            for (int i = 0; i < 24; i++) {
                int j = keccakf_piln[i];
                bc[0] = q[j];
                q[j] = ROTL64(t, keccakf_rotc[i]);
                t = bc[0];
            }

            //  Chi
            for (int j = 0; j < 25; j += 5) {
                for (int i = 0; i < 5; i++)
                    bc[i] = q[j + i];
                for (int i = 0; i < 5; i++)
                    q[j + i] ^= (~bc[(i + 1) % 5]) & bc[(i + 2) % 5];
            }

            //  Iota
            q[0] ^= keccakf_rndc[r];
        }

        // Mapping from longs (in q[]) to bytes (in v[])
        for (int i = 0, j = 0; i < 25; i++, j += 8) {
            t = q[i];
            v[j    ] = (byte)(t & 0xFF);
            v[j + 1] = (byte)((t >> 8) & 0xFF);
            v[j + 2] = (byte)((t >> 16) & 0xFF);
            v[j + 3] = (byte)((t >> 24) & 0xFF);
            v[j + 4] = (byte)((t >> 32) & 0xFF);
            v[j + 5] = (byte)((t >> 40) & 0xFF);
            v[j + 6] = (byte)((t >> 48) & 0xFF);
            v[j + 7] = (byte)((t >> 56) & 0xFF);
        }
    }

    // Initialize the context for SHA3
    void sha3_init(int mdlen)
    {
        int i;

        for (i = 0; i < 200; i++)
            b[i] = 0;
        this.mdlen = mdlen;
        rsiz = 200 - 2 * mdlen;
        pt = 0;
    }

    // update state with more data
    void sha3_update(byte[] data, int len)
    {
        int i;
        int j;

        j = pt;
        for (i = 0; i < len; i++) {
            b[j++] ^= data[i];
            if (j >= rsiz) {
                sha3_keccakf(b);
                j = 0;
            }
        }
        pt = j;
    }

    // finalize and output a hash
    void sha3_final(byte[] md)
    {
        int i;

        b[pt] ^= 0x06;
        b[rsiz - 1] ^= 0x80;
        sha3_keccakf(b);

        for (i = 0; i < mdlen; i++) {
            md[i] = b[i];
        }
    }

    // compute a SHA-3 hash (md) of given byte length from "in"
    byte[] sha3(byte[] in, int inlen, byte[] md, int mdlen)
    {

        sha3_init(mdlen);
        sha3_update(in, inlen);
        sha3_final(md);

        return md;
    }

    public static byte[] right_encode(long x) {
        byte n;
        if (x == 0) {
            n = 1;
        } else {
            double nGreaterThan = Math.log10(x) / Math.log10(2) / 8;
            if (nGreaterThan == (int) nGreaterThan) {
                n = (byte) (nGreaterThan + 1);
            } else {
                n = (byte) Math.ceil(nGreaterThan);
            }
        }

        byte[] xArr = new byte[n+1];

        for (int i = 0; i < n; i++) {
            long mask = 0xFF << (8 * (n - i - 1));
            byte x_i = (byte) ((x & mask) >>> (8 * (n - i - 1)));
            xArr[i] = x_i;
        }
        xArr[n] = n;
        return xArr;
    }

    public static byte[] left_encode(long x) {
        byte n;
        if (x == 0) {
            n = 1;
        } else {
            double nGreaterThan = Math.log10(x) / Math.log10(2) / 8;
            if (nGreaterThan == (int) nGreaterThan) {
                n = (byte) (nGreaterThan + 1);
            } else {
                n = (byte) Math.ceil(nGreaterThan);
            }
        }

        byte[] xArr = new byte[n+1];
        xArr[0] = n;

        for (int i = 0; i < n; i++) {
            long mask = 0xFF << (8 * (n - i - 1));
            byte x_i = (byte) ((x & mask) >>> (8 * (n - i - 1)));
            xArr[i+1] = x_i;
        }

        return xArr;
    }

    public static byte[] encode_string(byte[] S) {
        return concat(left_encode(S.length * 8), S);
    }

    /**
     * Apply the NIST bytepad primitive to a byte array X with encoding factor w.
     * @param X the byte array to bytepad
     * @param w the encoding factor (the output length must be a multiple of w)
     * @return the byte-padded byte array X with encoding factor w.
     */
    public static byte[] bytepad(byte[] X, int w) {
        byte[] wenc = left_encode(w);
        byte[] z = new byte[wenc.length + X.length + w - ((wenc.length + X.length) % w)];
        for (int i = 0; i < wenc.length; i++) {
            z[i] = wenc[i];
        }
        for (int i = wenc.length, j = 0; i < wenc.length + X.length; i++, j++) {
            z[i] = X[j];
        }

        return z;
    }

    /**
     * Concatenate two byte array
     * @param a byte array
     * @param b byte array
     * @return new concatenated byte array
     */
    private static byte[] concat(byte[] a, byte[] b) {
        byte[] ret = new byte[a.length + b.length];
        int i;
        for (i = 0; i < a.length; i++) {
            ret[i] = a[i];
        }

        for (int j = 0; j < b.length; j++) {
            ret[j + i] = b[j];
        }
        return ret;
    }
}
