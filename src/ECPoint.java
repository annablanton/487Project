import java.math.BigInteger;

public class ECPoint {
    public BigInteger x;
    public BigInteger y;
    public static final BigInteger p = BigInteger.TWO.pow(521).subtract(BigInteger.ONE);
    public ECPoint() {
        x = BigInteger.ZERO;
        y = BigInteger.ONE;
    }

    public ECPoint(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
    }

    public ECPoint(BigInteger x, boolean lsb) {
        //TODO write ECPoint constructor that takes x coord and lsb of y coord
        throw new UnsupportedOperationException("LSB constructor not yet implemented");
    }

    public boolean equals(ECPoint other) {
        return this.x.equals(other.x) && this.y.equals(other.y);
    }

    public ECPoint negate() {
        return new ECPoint(this.x.negate().mod(p), this.y);
    }

    public ECPoint add(ECPoint other) {
        //TODO write elliptic curve addition method
        throw new UnsupportedOperationException("Add not yet implemented");
    }

    public ECPoint multiply(BigInteger k) {
        //TODO write elliptic curve multiplication method
        throw new UnsupportedOperationException("Multiply not yet implemented");
    }
}
