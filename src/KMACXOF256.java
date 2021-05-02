public class KMACXOF256 extends cSHAKE256 {
    public byte[] kmacxof256(byte[] k, byte[] in, int inlen, byte[] md, int mdlen, byte[] s) {
        byte[] newX = bytepad(encode_string(k), 136);
        newX = concat(newX, in);
        newX = concat(newX, right_encode(0));
        return cshake256(newX, newX.length, md, mdlen, "KMAC".getBytes(), s);
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
