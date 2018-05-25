package ru.babobka.nodesecurity.service;

import org.junit.Test;
import ru.babobka.nodesecurity.rsa.RSAConfig;
import ru.babobka.nodesecurity.rsa.RSAConfigFactory;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by 123 on 20.05.2018.
 */
public class RSAServiceTest {
    private RSAService rsaService = new RSAService();

    @Test
    public void testEncryptDecryptLittleModulus() {
        testEncryptDecrypt(64);
    }

    @Test
    public void testEncryptDecryptMediumModulus() {
        testEncryptDecrypt(128);
    }

    @Test
    public void testEncryptDecryptLargeModulus() {
        testEncryptDecrypt(256);
    }

    @Test
    public void testEncryptDecryptProductionModulus() {
        testEncryptDecrypt(512);
    }

    private void testEncryptDecrypt(int modulusBits) {
        RSAConfig rsaConfig = RSAConfigFactory.create(modulusBits);
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            BigInteger m = new BigInteger(random.nextInt(modulusBits), new Random()).mod(rsaConfig.getPublicKey().getN());
            BigInteger c = rsaService.encrypt(m, rsaConfig.getPublicKey());
            if (m.compareTo(BigInteger.ONE) > 0) {
                assertNotEquals(m, c);
                assertEquals(rsaService.decrypt(c, rsaConfig.getPrivateKey()), m);
            }
        }
    }
}
