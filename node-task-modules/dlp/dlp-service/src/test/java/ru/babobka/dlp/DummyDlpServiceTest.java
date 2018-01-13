package ru.babobka.dlp;

import org.junit.Test;
import ru.babobka.dlp.dummy.DummyDlpService;
import ru.babobka.nodeutils.math.Fp;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 08.01.2018.
 */
public class DummyDlpServiceTest {
    private DlpService dlpService = new DummyDlpService();

    @Test
    public void testDlp() {
        int intMod = 659;
        BigInteger mod = BigInteger.valueOf(intMod);
        Fp gen = new Fp(BigInteger.valueOf(2L), mod);
        for (int i = 1; i < intMod; i++) {
            Fp y = new Fp(BigInteger.valueOf(i), mod);
            DlpTask dlpTask = new DlpTask(gen, y);
            assertEquals(y, gen.pow(dlpService.dlp(dlpTask)));
        }
    }

}
