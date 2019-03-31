package ru.babobka.factor.model.ec.multprovider;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.factor.model.ec.EllipticCurvePoint;
import ru.babobka.nodeutils.container.Container;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 04.10.2017.
 */
public class BinaryMultiplicationProviderTest {

    private static final BinaryMultiplicationProvider BINARY_MULTIPLICATION_PROVIDER = new BinaryMultiplicationProvider();
    private static final DummyMultiplicationProvider dummyMultiplicationProvider = new DummyMultiplicationProvider();

    @BeforeClass
    public static void setUp() {
        Container.getInstance().put(BINARY_MULTIPLICATION_PROVIDER);
    }

    @Test
    public void testMultDummyMultEquality() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            EllipticCurvePoint point = EllipticCurvePoint
                    .generateRandomPoint(BigInteger.probablePrime(32, new Random()));
            int mult = random.nextInt(100) + 1;
            assertEquals(dummyMultiplicationProvider.mult(point, mult), BINARY_MULTIPLICATION_PROVIDER.mult(point, mult));
        }
    }
}
