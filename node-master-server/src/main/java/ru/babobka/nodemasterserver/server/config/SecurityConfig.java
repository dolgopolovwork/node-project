package ru.babobka.nodemasterserver.server.config;

import ru.babobka.nodeutils.math.SafePrime;

import java.io.Serializable;

/**
 * Created by 123 on 13.05.2018.
 */
public class SecurityConfig implements Serializable {
    private static final long serialVersionUID = -1315130732356373527L;
    private SafePrime bigSafePrime;
    private int challengeBytes;

    public SafePrime getBigSafePrime() {
        return bigSafePrime;
    }

    public void setBigSafePrime(SafePrime bigSafePrime) {
        this.bigSafePrime = bigSafePrime;
    }

    public int getChallengeBytes() {
        return challengeBytes;
    }

    public void setChallengeBytes(int challengeBytes) {
        this.challengeBytes = challengeBytes;
    }
}
