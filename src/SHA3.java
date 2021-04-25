public class SHA3 {
    //SHA-3 implementation converted from C to Java from https://github.com/mjosaarinen/tiny_sha3
    public static final int KECCAKF_ROUNDS = 24;

    // state context
    public static byte[] b = new byte[200];
    static int pt, rsiz, mdlen;

    public static long ROTL64(long x, long y) {
        return (((x) << (y)) | ((x) >>> (64 - (y))));
    }

    static void sha3_keccakf(byte[/*200*/] v)
    {
        // constants
        long[/*24*/] keccakf_rndc = {
                0x0000000000000001L, 0x0000000000008082L, 0x800000000000808aL,
                0x8000000080008000L, 0x000000000000808bL, 0x0000000080000001L,
                0x8000000080008081L, 0x8000000000008009L, 0x000000000000008aL,
                0x0000000000000088L, 0x0000000080008009L, 0x000000008000000aL,
                0x000000008000808bL, 0x800000000000008bL, 0x8000000000008089L,
                0x8000000000008003L, 0x8000000000008002L, 0x8000000000000080L,
                0x000000000000800aL, 0x800000008000000aL, 0x8000000080008081L,
                0x8000000000008080L, 0x0000000080000001L, 0x8000000080008008L
        };
        int[/*24*/] keccakf_rotc = {
                1,  3,  6,  10, 15, 21, 28, 36, 45, 55, 2,  14,
                27, 41, 56, 8,  25, 43, 62, 18, 39, 61, 20, 44
        };
        int[/*24*/] keccakf_piln = {
                10, 7,  11, 17, 18, 3, 5,  16, 8,  21, 24, 4,
                15, 23, 19, 13, 12, 2, 20, 14, 22, 9,  6,  1
        };

        // variables
//        int i, j, r;
        long t;
        long[] q = new long[25];
        long[/*5*/] bc = new long[5];

//        #if __BYTE_ORDER__ != __ORDER_LITTLE_ENDIAN__
//            uint8_t *v;
        // map from bytes (in v[]) to longs (in q[])
        for (int i = 0, j = 0; i < 25; i++, j += 8) {
            q[i] =  (((long)v[j    ] & 0xFFL)      ) | (((long)v[j + 1] & 0xFFL) <<  8) |
                    (((long)v[j + 2] & 0xFFL) << 16) | (((long)v[j + 3] & 0xFFL) << 24) |
                    (((long)v[j + 4] & 0xFFL) << 32) | (((long)v[j + 5] & 0xFFL) << 40) |
                    (((long)v[j + 6] & 0xFFL) << 48) | (((long)v[j + 7] & 0xFFL) << 56);
        }
        // endianess conversion. this is redundant on little-endian targets
//            for (i = 0; i < 25; i++) {
//                v = (uint8_t *) &st[i];
//                st[i] = ((uint64_t) v[0])     | (((uint64_t) v[1]) << 8) |
//                        (((uint64_t) v[2]) << 16) | (((uint64_t) v[3]) << 24) |
//                        (((uint64_t) v[4]) << 32) | (((uint64_t) v[5]) << 40) |
//                        (((uint64_t) v[6]) << 48) | (((uint64_t) v[7]) << 56);
//            }
////        #endif

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

//        #if __BYTE_ORDER__ != __ORDER_LITTLE_ENDIAN__

        // endianess conversion. this is redundant on little-endian targets
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
//        #endif
    }

    // Initialize the context for SHA3

    static void sha3_init(int mdlen)
    {
        int i;

        for (i = 0; i < 200; i++)
            b[i] = 0;
        mdlen = mdlen;
        rsiz = 200 - 2 * mdlen;
        pt = 0;
    }

    // update state with more data

    static void sha3_update(byte[] data, int len)
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

    static byte[] sha3_final(byte[] md)
    {
        int i;

        b[pt] ^= 0x06;
        b[rsiz - 1] ^= 0x80;
        sha3_keccakf(b);

        for (i = 0; i < mdlen; i++) {
            md[i] = b[i];
        }

        return md;
    }

    // compute a SHA-3 hash (md) of given byte length from "in"

    static byte[] sha3(byte[] in, int inlen, byte[] md, int mdlen)
    {

        sha3_init(mdlen);
        sha3_update(in, inlen);
        md = sha3_final(md);

        return md;
    }

    // SHAKE128 and SHAKE256 extensible-output functionality

    static void shake_xof()
    {
        b[pt] ^= 0x1F;
        b[rsiz - 1] ^= 0x80;
        sha3_keccakf(b);
        pt = 0;
    }

    static void shake_out(byte[] out, int len)
    {
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



    public static String right_encode(long x) {
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

        byte[] xArr = new byte[n];

        for (int i = 0; i < n; i++) {
            long mask = 0xFF << (8 * (n - i - 1));
            byte x_i = (byte) ((x & mask) >>> (8 * (n - i - 1)));
            xArr[i] = x_i;
        }

        String[] oArr = new String[n+1];

        for (int i = 0; i < n; i++) {
            oArr[i] = enc8(xArr[i]);
        }

        oArr[n] = enc8(n);
        String O = "";
        for (int i = 0; i < n+1; i++) {
            O = O.concat(oArr[i]);
        }

        return O;

    }

    public static String left_encode(long x) {
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

        byte[] xArr = new byte[n];

        for (int i = 0; i < n; i++) {
            long mask = 0xFF << (8 * (n - i - 1));
            byte x_i = (byte) ((x & mask) >>> (8 * (n - i - 1)));
            xArr[i] = x_i;
        }

        String[] oArr = new String[n+1];

        oArr[0] = enc8(n);
        for (int i = 0; i < n; i++) {
            oArr[i+1] = enc8(xArr[i]);
        }

        String O = "";
        for (int i = 0; i < n+1; i++) {
            O = O.concat(oArr[i]);
        }

        return O;
    }

    public static String encode_string(String S) {
        return left_encode(S.length()).concat(S);
    }

    public static String bytepad(String X, long w) {
        String z = left_encode(w).concat(X);
        while (z.length() % 8 != 0) {
            z = z.concat("0");
        }

        while ((z.length() / 8) % w != 0) {
            z = z.concat("00000000");
        }

        return z;
    }

    private static String enc8(byte b) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            if (b != 0) {
                ret.append(Integer.toString(Math.abs(b % 2)));
                b >>>= 1;
            } else {
                ret.append("0");
            }
        }

        return ret.toString();
    }
}
