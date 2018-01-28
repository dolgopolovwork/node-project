package ru.babobka.factor.service;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.factor.model.FactoringResult;
import ru.babobka.factor.model.ec.multprovider.FastMultiplicationProvider;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class EllipticCurveFactorServiceTest {

    @BeforeClass
    public static void setUp() {
        Container.getInstance().put(mock(SimpleLogger.class));
        Container.getInstance().put(new FastMultiplicationProvider());
    }

    @Test
    public void testLittleNumbers() throws InterruptedException {
        int tests = 150;
        int factorBits = 16;
        generateTests(tests, factorBits);
    }

    @Test
    public void testLargeNumbers() throws InterruptedException {
        int tests = 10;
        int factorBits = 35;
        generateTests(tests, factorBits);
    }

    private void generateTests(int tests, int factorBits) {
        for (int i = 0; i < tests; i++) {
            BigInteger number = BigInteger.probablePrime(factorBits, new Random())
                    .multiply(BigInteger.probablePrime(factorBits, new Random()));
            EllipticCurveFactorService service = new EllipticCurveFactorService(Runtime.getRuntime().availableProcessors());
            FactoringResult result = service.execute(number);
            assertEquals(number.mod(result.getFactor()), BigInteger.ZERO);
        }
    }
}
