public class SHA3 {
  //SHA-3 implementation converted from C to Java from https://github.com/mjosaarinen/tiny_sha3
    public static final int KECCAKF_ROUNDS = 24;
    // state context
    private byte[] b = new byte[200]; 
    private int pt, rsiz, mdlen;
    public long [/*25*/] st;


    // constants
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

    public static long ROTL64(long x, int y) {
        return (x << y) | (x >>> (64 - (y)));
    }

    private static void sha3_keccakf(byte[/*200*/] v){
        long[] st = new long[25]; // 64-bit words
        long[] bc = new long[5];

        // variables
        int i, j, r;
        long t;

        // #if __BYTE_ORDER__ != __ORDER_LITTLE_ENDIAN__
        //     uint8_t *v;
            // endianess conversion. this is redundant on little-endian targets
            for (i = 0, j =0; i < 25; i++, j +=8) {
                // v = (uint8_t *) &st[i];
                st[i] =  (((long)v[j + 0] & 0xFFL)) | (((long)v[j + 1] & 0xFFL) <<  8) |
                (((long)v[j + 2] & 0xFFL) << 16) | (((long)v[j + 3] & 0xFFL) << 24) |
                (((long)v[j + 4] & 0xFFL) << 32) | (((long)v[j + 5] & 0xFFL) << 40) |
                (((long)v[j + 6] & 0xFFL) << 48) | (((long)v[j + 7] & 0xFFL) << 56);
    }
        // #endif

        // actual iteration
        for (r = 0; r < KECCAKF_ROUNDS; r++) {

            // Theta
            for (i = 0; i < 5; i++){
                //q[i] = q[i] ^ q[i + 5] ^ q[i + 10] ^ q[i + 15] ^ q[i + 20];
                bc[i] = st[i] ^ st[i + 5] ^ st[i + 10] ^ st[i + 15] ^ st[i + 20];

            }

            for (i = 0; i < 5; i++) {
                t = bc[(i + 4) % 5] ^ ROTL64(bc[(i + 1) % 5], 1);
                for (j = 0; j < 25; j += 5){
                    st[j + i] ^= t;
                }
            }

            // Rho Pi
            t = st[1];
            for (i = 0; i < 24; i++) {
                j = keccakf_piln[i];
                bc[0] = st[j];
                st[j] = ROTL64(t, keccakf_rotc[i]);
                t = bc[0];
            }

            //  Chi
            for (j = 0; j < 25; j += 5) {
                for (i = 0; i < 5; i++){
                    bc[i] = st[j + i];
                }
                for (i = 0; i < 5; i++){
                    st[j + i] ^= (~bc[(i + 1) % 5]) & bc[(i + 2) % 5];
                }
            }

            //  Iota
            st[0] ^= keccakf_rndc[r];
        }

            // #if __BYTE_ORDER__ != __ORDER_LITTLE_ENDIAN__
            // endianess conversion. this is redundant on little-endian targets
            for (i = 0, j = 0 ; i < 25; i++, j+=8) {
                // v = (uint8_t *) &st[i];
                //t = st[i];
                t = st[i];
                v[0] = (byte)(t & 0xFF);
                v[1] = (byte)((t >>> 8) & 0xFF);
                v[2] = (byte)((t >>> 16) & 0xFF);
                v[3] = (byte)((t >>> 24) & 0xFF);
                v[4] = (byte)((t >>> 32) & 0xFF);
                v[5] = (byte)((t >>> 40) & 0xFF);
                v[6] = (byte)((t >>> 48) & 0xFF);
                v[7] = (byte)((t >>> 56) & 0xFF);
            }
        // #endif
    }
    
    
    // Initialize the context for SHA3

    void sha3_init(int _mdlen)
    {
        for (int i = 0; i < 25; i++){
            st[i] = 0;
        }
        mdlen = _mdlen;
        rsiz = 200 - 2 * mdlen;
        pt = 0;
    }

    // update state with more data

    public void sha3_update(byte[] data, int len) {
        int j = this.pt;
        for (int i = 0; i < len; i++) {
            b[j++] ^= data[i];
            if (j >= this.rsiz) {
                sha3_keccakf(b);
                j = 0;
            }
        }
        this.pt = j;
    }

    // finalize and output a hash

/**Need to double check **/
    public byte[] sha3_final(byte[] md)
    {
        b[pt] ^= 0x06;
        b[rsiz - 1] ^= 0x80;
        sha3_keccakf(b);

        for (int i = 0; i < mdlen; i++) {
            md[i] = b[i];
        }

        return md;
    }

    // compute a SHA-3 hash (md) of given byte length from "in"

    public byte[] sha3(byte[] in, int inlen, byte[] md, int mdlen)
    {

        sha3_init(mdlen);
        sha3_update(in, inlen);
        md = sha3_final(md);

        return md;
    }

    // SHAKE128 and SHAKE256 extensible-output functionality


    /**
 * Need to double check
 */
    // the (binary) cSHAKE suffix is 00, while the (binary) SHAKE suffix is 1111
    // 00 -> 0000 01 00 -> 0000 0100 => 0x04
    // 00001 1111 =  0x1f
    public void shake_xof()
    {
        this.b[this.pt] ^= (byte)0x1F;
        this.b[this.rsiz - 1] ^= (byte)0x80;
        sha3_keccakf(b);
        this.pt = 0;
    }

    public void shake_out(byte[] out, int len)
    {
        int j = pt;
        for (int i = 0; i < len; i++) {
            if (j >= rsiz) {
                // sha3_keccakf(c->st.q);
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
