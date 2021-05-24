import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ECPointTest {
    ECPoint g;
    Random rand = new Random();
    @Before
    public void setUp() {
        g = new ECPoint(BigInteger.valueOf(4), false);
    }

    @Test
    public void testMultiplicationByNeutral() {
        assertEquals(g.multiply(BigInteger.ZERO), new ECPoint());
    }

    @Test
    public void testMultiplicationByOne() {
        assertEquals(g.multiply(BigInteger.ONE), g);
    }

    @Test
    public void testAdditionByNegation() {
        assertEquals(g.add(g.negate()), new ECPoint());
    }

    @Test
    public void testMultiplicationVsAddition() {
        assertEquals(g.multiply(BigInteger.TWO), g.add(g));
    }

    @Test
    public void testAssociativity() {
        assertEquals(g.multiply(BigInteger.valueOf(4)), g.multiply(BigInteger.TWO).multiply(BigInteger.TWO));
    }

    @Test
    public void testRTimesG() {
        assertEquals(new ECPoint(), g.multiply(ECPoint.r));
    }

    @Test
    public void testRandomMultiplicationModR() {
        for (int i = 0; i < 1000; i++) {
            BigInteger k = BigInteger.valueOf(rand.nextInt()).mod(ECPoint.p);
            assertEquals(g.multiply(k), g.multiply(k.mod(ECPoint.r)));
        }

    }

    @Test
    public void testRandomMultiplicationAgainstAddition() {
        for (int i = 0; i < 1000; i++) {
            BigInteger k = BigInteger.valueOf(rand.nextInt()).mod(ECPoint.p);
            ECPoint val1 = g.multiply(k.add(BigInteger.ONE));
            ECPoint val2 = g.multiply(k).add(g);
            assertEquals(val1, val2);
        }
    }

    @Test
    public void testSimpleCase() {
        ECPoint val1 = g.multiply(new BigInteger("6"));
        ECPoint val2 = g.add(g).add(g).add(g).add(g).add(g);
        assertEquals(val1, val2);
    }

    @Test
    public void testRandomIntDistribution() {
        for (int i = 0; i < 1000; i++) {
            BigInteger k = BigInteger.valueOf(rand.nextInt()).mod(ECPoint.p);
            BigInteger t = BigInteger.valueOf(rand.nextInt()).mod(ECPoint.p);
            assertEquals(g.multiply(k.add(t)), g.multiply(k).add(g.multiply(t)));
        }
    }

    @Test
    public void testPointGeneration() {
        for (int i = 0; i < 1000; i++) {
            BigInteger k = BigInteger.valueOf(rand.nextInt()).mod(ECPoint.p);
            BigInteger t = BigInteger.valueOf(rand.nextInt()).mod(ECPoint.p);
            ECPoint p = g.multiply(k);
            assertEquals(g.multiply(k).multiply(t), g.multiply(k.multiply(t).mod(ECPoint.r)));
            assertEquals(p.multiply(t), g.multiply(k.multiply(t).mod(ECPoint.r)));
        }
    }
}
