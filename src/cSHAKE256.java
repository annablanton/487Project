public class cSHAKE256 extends SHAKE {
    byte[] cshake256(byte[] in, int inlen, byte[] md, int mdlen, byte[] n, byte[] s) {
        if (n.length == 0 && s.length == 0) {
            return shake256(in, inlen, md, mdlen);
        }
        byte[] newX = bytepad(concat(n, s), 136);
        newX = concat(newX, in);
        final byte mask = (byte) 0xC0;

        byte prevTwoBits = 0x00;
        for (int i = newX.length - 1; i >= 0; i--) {
            byte temp = (byte) ((newX[i] & mask) >>> 6);
            newX[i] = (byte) ((newX[i] << 2) + prevTwoBits);
            prevTwoBits = temp;
        }
        if (prevTwoBits != 0) {
            byte[] temp = new byte[newX.length + 1];
            temp[0] = prevTwoBits;
            for (int i = 0; i < newX.length; i++) {
                temp[i+1] = newX[i];
            }
            newX = temp;
        }
        shake256_init();
        sha3_keccakf(newX);
        return newX;
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
