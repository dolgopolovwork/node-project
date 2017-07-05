package ru.babobka.nodecrypto;

import ru.babobka.nodecrypto.hash.HashService;
import ru.babobka.nodecrypto.hash.SCryptHashService;
import ru.babobka.nodecrypto.hash.SHA512HashService;
import ru.babobka.nodecrypto.util.ArrayUtil;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by 123 on 03.07.2017.
 */
public final class CryptoClient {

    private static final HashService hashService = new SHA512HashService();

    private static final HashService secureHashService = new SCryptHashService();

    private final CryptoParams cryptoParams;

    private final BigInteger secret;

    private final String identity;

    private final byte[] salt;

    private final BigInteger a;

    private final BigInteger expA;

    private final BigInteger serverSalt;

    private final byte[] key;

    private CryptoClient(CryptoParams cryptoParams, BigInteger secret, String identity, byte[] salt, BigInteger a, BigInteger expA, BigInteger serverSalt, byte[] key) {
        this.cryptoParams = cryptoParams;
        this.secret = secret;
        this.identity = identity;
        this.salt = salt;
        this.a = a;
        this.expA = expA;
        this.serverSalt = serverSalt;
        this.key = key;
    }

    public byte[] getKeyProof(BigInteger proofSalt) {
        if (proofSalt == null) {
            throw new IllegalArgumentException("proofSalt is null");
        }
        return hashService.hash(ArrayUtil.concat(key, proofSalt.toByteArray()));
    }

    public BigInteger getServerSalt() {
        return serverSalt;
    }

    public CryptoParams getCryptoParams() {
        return cryptoParams;
    }

    public BigInteger getSecret() {
        return secret;
    }

    public String getIdentity() {
        return identity;
    }

    public BigInteger getA() {
        return a;
    }

    public byte[] getSalt() {
        return salt.clone();
    }

    public BigInteger getExpA() {
        return expA;
    }

    public byte[] getKey() {
        return key.clone();
    }

    static class Builder {

        private CryptoParams cryptoParams;

        private BigInteger secret;

        private String identity;

        private final byte[] salt = ArrayUtil.randomArray(CryptoPolitics.MIN_SALT_BIT_LENGTH / 8);

        private final BigInteger a = new BigInteger(CryptoPolitics.MIN_EXP_BIT_LENGTH, new SecureRandom());

        private BigInteger expA;

        private BigInteger expB;

        private BigInteger serverSalt;

        private byte[] key;

        public synchronized Builder setCryptoParams(CryptoParams cryptoParams) {
            if (cryptoParams == null) {
                throw new IllegalArgumentException("cryptoParams are null");
            }
            this.cryptoParams = cryptoParams;
            this.expA = cryptoParams.getGenerator().modPow(a, cryptoParams.getSafePrime().getPrime());
            return this;
        }

        public synchronized Builder setSecret(BigInteger secret) {
            if (secret == null) {
                throw new IllegalArgumentException("secret is null");
            }
            this.secret = secret;
            return this;
        }

        public synchronized Builder setIdentity(String identity) {
            if (identity == null) {
                throw new IllegalArgumentException("identity is null");
            }
            this.identity = identity;
            return this;
        }

        public synchronized Builder setServerSalt(BigInteger serverSalt) {
            if (serverSalt == null) {
                throw new IllegalArgumentException("serverSalt is null");
            }
            this.serverSalt = serverSalt;
            return this;
        }

        public synchronized Builder setExpB(BigInteger expB) {
            if (expB == null) {
                throw new IllegalArgumentException("expB is null");
            }
            this.expB = expB;
            return this;
        }

        private synchronized void generateKey() {
            byte[] hashPower = hashService.hash(ArrayUtil.concat(serverSalt.toByteArray(), secret.toByteArray()));
            BigInteger power = new BigInteger(hashPower);
            power = power.add(a);
            BigInteger generatedKey = expB.modPow(power, cryptoParams.getSafePrime().getPrime());
            this.key = secureHashService.hash(generatedKey.toByteArray(), salt);
        }

        public synchronized CryptoClient build() {
            if (serverSalt == null) {
                throw new IllegalStateException("serverSalt was not set");
            } else if (secret == null) {
                throw new IllegalStateException("secret was not set");
            } else if (expB == null) {
                throw new IllegalStateException("expB was not set");
            } else if (cryptoParams == null) {
                throw new IllegalStateException("cryptoParams was not set");
            }
            generateKey();
            return new CryptoClient(cryptoParams, secret, identity, salt, a, expA, serverSalt, key);
        }
    }
}
