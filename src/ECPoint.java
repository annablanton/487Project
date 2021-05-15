import java.math.BigInteger;

public class ECPoint {
    public BigInteger x;
    public BigInteger y;
    public static final BigInteger p = BigInteger.TWO.pow(521).subtract(BigInteger.ONE);
    public static final BigInteger d = BigInteger.valueOf(-376014);
    public ECPoint() {
        x = BigInteger.ZERO;
        y = BigInteger.ONE;
    }

    public ECPoint(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
    }

    public ECPoint(BigInteger x, boolean lsb) {
        this.x = x;
        this.y = sqrt(BigInteger.ONE.subtract(x.multiply(x)).multiply(BigInteger.ONE.add(d.multiply(x).multiply(x))), p, lsb);
    }

    public boolean equals(ECPoint other) {
        return this.x.equals(other.x) && this.y.equals(other.y);
    }

    public ECPoint negate() {
        return new ECPoint(this.x.negate().mod(p), this.y);
    }

    public ECPoint add(ECPoint other) {
        BigInteger x1y2 = this.x.multiply(other.y);
        BigInteger y1x2 = this.y.multiply(other.x);
        BigInteger x1x2 = this.x.multiply(other.x);
        BigInteger y1y2 = this.y.multiply(other.y);

        BigInteger newX = x1y2.add(y1x2).multiply(BigInteger.ONE.add(d.multiply(x1x2).multiply(y1y2)).modInverse(p));
        BigInteger newY = y1y2.subtract(x1x2).multiply(BigInteger.ONE.subtract(d.multiply(x1x2).multiply(y1y2)).modInverse(p));
        return new ECPoint(newX, newY);
    }

    public ECPoint multiply(BigInteger s) {
        ECPoint V = this;
        for (int i = 0; i < s.bitCount(); i++) {
            V = V.add(V);
            if (s.testBit(i)) {
                V = V.add(this);
            }
        }
        return V;
    }

    private BigInteger sqrt(BigInteger v, BigInteger p, boolean lsb) {
        assert(p.testBit(0) && p.testBit(1));
        if (v.signum() == 0) {
            return BigInteger.ZERO;
        }
        BigInteger r = v.modPow(p.shiftRight(2).add(BigInteger.ONE), p);
        if (r.testBit(0) != lsb) {
            r = p.subtract(r);
        }
        return (r.multiply(r).subtract(v).mod(p).signum() == 0) ? r : null;
    }
}
