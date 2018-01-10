package ru.babobka.primecounter.tester;

import ru.babobka.nodeutils.util.MathUtil;

/**
 * Created by dolgopolov.a on 06.07.15.
 */
public class DummyPrimeTester implements PrimeTester {

    @Override
    public boolean isPrime(long n) {
        return MathUtil.isPrime(n);
    }
}
