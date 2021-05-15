import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class ECPointTest {
    ECPoint g;
    @Before
    public void setUp() {
        g = new ECPoint(BigInteger.valueOf(4), BigInteger.valueOf(2));
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

    }

    @Test
    public void testMultiplicationVsAddition() {
        assertEquals(g.multiply(BigInteger.TWO), g.add(g));
    }

    @Test
    public void testAssociativity() {

    }

    @Test
    public void testFourTimesG() {

    }

    @Test
    public void testRTimesG() {

    }
}
