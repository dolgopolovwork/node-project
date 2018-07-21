package ru.babobka.dlp;

import org.junit.Test;
import ru.babobka.dlp.model.regular.DlpTask;
import ru.babobka.dlp.service.regular.DlpService;
import ru.babobka.nodeutils.math.Fp;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 27.01.2018.
 */
public class DlpServiceTest {

    private DlpService dlpService = new DlpService() {
        @Override
        protected BigInteger dlpImpl(DlpTask task) {
            return null;
        }
    };

    @Test(expected = IllegalArgumentException.class)
    public void testDlpNullTask() {
        dlpService.dlp(null);
    }

    @Test
    public void testDlpMultNeutral() {
        Fp gen = new Fp(BigInteger.valueOf(2), BigInteger.TEN);
        Fp y = new Fp(BigInteger.ONE, BigInteger.TEN);
        assertEquals(BigInteger.ZERO, dlpService.dlp(new DlpTask(gen, y)));
    }

    @Test
    public void testDlpSameValues() {
        Fp gen = new Fp(BigInteger.valueOf(2), BigInteger.TEN);
        Fp y = new Fp(BigInteger.valueOf(2), BigInteger.TEN);
        assertEquals(BigInteger.ONE, dlpService.dlp(new DlpTask(gen, y)));
    }
}
