import java.math.BigInteger;
/**
 * 06/04/2021
 * TCSS 487 A Sp 21: Cryptography
 * Group Project –cryptographic library & app
 * Anna Blanton, Caleb Chang and Taehong Kim
 *
 * EPoint class to represent a elliptic point
 */
public class ECPoint {
    public final BigInteger x;
    public final BigInteger y;
    public static final BigInteger p = BigInteger.TWO.pow(521).subtract(BigInteger.ONE);
    public static final BigInteger d = BigInteger.valueOf(-376014);
    public static final BigInteger r = BigInteger.TWO.pow(519).subtract(new BigInteger("337554763258501705789107630418782636071904961214051226618635150085779108655765"));
    public static final ECPoint g = new ECPoint(BigInteger.valueOf(4), false);
    public ECPoint() {
        x = BigInteger.ZERO;
        y = BigInteger.ONE;
    }

    /**
     * Constructor for ECPoint object
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public ECPoint(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructor for ECPoint object
     * @param x x-coordinate
     * @param lsb
     */
    public ECPoint(BigInteger x, boolean lsb) {
        this.x = x;
        BigInteger num = BigInteger.ONE.subtract(x.multiply(x));
        BigInteger den = BigInteger.ONE.add(d.negate().multiply(x).multiply(x)).modInverse(p);
        this.y = sqrt(num.multiply(den).mod(p), p, lsb).mod(p);
    }

    /**
     * Override equal to compare ec point
     * @param other ecpoint to compare
     * @return return boolean result for comparing
     */
    @Override
    public boolean equals(Object other) {
        if (this.getClass().equals(other.getClass())) {
            ECPoint o = (ECPoint) other;
            return this.x.equals(o.x) && this.y.equals(o.y);
        }
        return false;
    }

    /**
     * setting the negate for ecpoint
     * @return return negate value for ecpoint
     */
    public ECPoint negate() {
        return new ECPoint(this.p.subtract(this.x), this.y);
    }

    /**
     * Add another ECPoint to get a new ECPoint
     * @param other Another ECPoint
     * @return new ECPoint
     */
    public ECPoint add(ECPoint other) {
        BigInteger x1y2 = this.x.multiply(other.y);
        BigInteger y1x2 = this.y.multiply(other.x);
        BigInteger xNumerator = x1y2.add(y1x2);

        BigInteger x1x2 = this.x.multiply(other.x);
        BigInteger y1y2 = this.y.multiply(other.y);
        BigInteger yNumerator = y1y2.subtract(x1x2);

        BigInteger xDenominator = BigInteger.ONE.add(d.multiply(x1x2).multiply(y1y2)).modInverse(p);
        BigInteger yDenominator = BigInteger.ONE.subtract(d.multiply(x1x2).multiply(y1y2)).modInverse(p);

        BigInteger newX = xNumerator.multiply(xDenominator).mod(p);
        BigInteger newY = yNumerator.multiply(yDenominator).mod(p);
        return new ECPoint(newX, newY);
    }

    /**
     * Multiply a ECPoint by a BigInteger
     * @param s BigInteger
     * @return new ECPoint
     */
    public ECPoint multiply(BigInteger s) {
        if (s.bitLength() > 0) {
            ECPoint V = this;
            for (int i = s.bitLength() - 2; i >= 0; i--) {
                V = V.add(V);
                if (s.testBit(i)) {
                    V = V.add(this);
                }
            }
            return V;
        }
        return new ECPoint();
    }

    /**
     * to string to print out ec point
     * @return x,y of ec point
     */
    public String toString() {
        return x + ", " + y;
    }

    /**
     * method to calculate sqrt big integer
     * @param v big integer for v
     * @param p big integer for p
     * @param lsb boolean to distinguish subtract r from p
     * @return return with sqrt big integer or null if result is 0
     */
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