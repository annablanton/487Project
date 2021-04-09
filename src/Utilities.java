import java.util.Arrays;

public class Utilities {
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
            byte x_i = (byte) ((x & mask) >> (8 * (n - i - 1)));
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
            byte x_i = (byte) ((x & mask) >> (8 * (n - i - 1)));
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
                b >>= 1;
            } else {
                ret.append("0");
            }
        }

        return ret.toString();
    }
}
