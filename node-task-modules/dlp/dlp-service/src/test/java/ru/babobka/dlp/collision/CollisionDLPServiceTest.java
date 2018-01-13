package ru.babobka.dlp.collision;

import org.junit.Test;
import ru.babobka.dlp.DlpService;
import ru.babobka.dlp.DlpTask;
import ru.babobka.nodeutils.math.Fp;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 08.01.2018.
 */
public class CollisionDLPServiceTest {

    @Test
    public void testDlp() {
        DlpService dlpService = new CollisionDLPService();
        int intMod = 659;
        BigInteger mod = BigInteger.valueOf(intMod);
        Fp gen = new Fp(BigInteger.valueOf(2L), mod);
        for (int i = 1; i < intMod; i++) {
            Fp y = new Fp(BigInteger.valueOf(i), mod);
            DlpTask dlpTask = new DlpTask(gen, y);
            assertEquals(y, gen.pow(dlpService.dlp(dlpTask)));
        }
    }

    @Test
    public void testDlpNullResult() {
        CollisionDLPService dlpService = spy(new CollisionDLPService());
        doReturn(null).when(dlpService).dlpComputation(any(Fp.class), any(Fp.class));
        int intMod = 659;
        BigInteger mod = BigInteger.valueOf(intMod);
        Fp gen = new Fp(BigInteger.valueOf(2L), mod);
        Fp y = new Fp(BigInteger.valueOf(2L), mod);
        DlpTask dlpTask = new DlpTask(gen, y);
        assertNull(dlpService.dlp(dlpTask));
        verify(dlpService, times(5)).dlpComputation(gen, y);
    }
}