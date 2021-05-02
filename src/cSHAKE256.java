import java.util.Arrays;

public class cSHAKE256 extends SHAKE {
    byte[] cshake256(byte[] in, int inlen, byte[] md, int mdlen, byte[] n, byte[] s) {
        if (n.length == 0 && s.length == 0) {
            return shake256(in, inlen, md, mdlen);
        }
        byte[] newX = bytepad(concat(encode_string(n), encode_string(s)), 136);
        newX = concat(newX, in);
        final byte mask = (byte) 0xC0;
        ext = true;
        shake256_init();
        shake_update(newX, newX.length);
        shake_xof();
        shake_out(md, mdlen);
        return md;
    }

    private byte[] concat(byte[] a, byte[] b) {
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
