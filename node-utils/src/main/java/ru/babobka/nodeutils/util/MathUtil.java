package ru.babobka.nodeutils.util;

import ru.babobka.nodeutils.math.SafePrime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dolgopolov.a on 23.11.15.
 */
public class MathUtil {

    private static final boolean[] PRIMES = {false, false, true, true, false, true, false, true, false, false, false, true, false, true, false, false, false, true, false, true, false, false, false, true, false, false, false, false, false, true, false, true, false, false, false, false, false, true, false, false, false, true, false, true, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, true, false, true, false, false, false, false, false, true, false, false, false, true, false, true, false, false, false, false, false, true, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false, true, false, false, false, true, false, true, false, false, false, true, false, true, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, true, false, false, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false, true, false, true, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false, true, false, true, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, true, false, true, false, false, false, true, false, false, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, true, false, true, false, false, false, false, false, true, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, true, false, true, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, true, false, true, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false, true, false, false, false, true, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false, true, false, true, false, false, false, false, false, true, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false, true, false, false, false, true, false, true, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, true, false, false, false, true, false, false, false, false, false, false, false, true, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, true, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, true, false, true, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, true, false, true, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, true, false, false, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, true, false, false, false, false, false, false, false, true, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false, true, false, true, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, true, false, true, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, true, false, true, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, true, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, true, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false, true, false, false, false, false, false, true, false, false};
    private static final BigDecimal THREE_D = BigDecimal.valueOf(3);
    private static final BigInteger TWO_BIG = BigInteger.valueOf(2L);
    private static final int MIN_NONCE_BITS = 3;
    private static final int UP = BigDecimal.ROUND_HALF_UP;

    private MathUtil() {
    }

    public static BigInteger cbrt(BigInteger n) {
        if (n == null) {
            throw new IllegalArgumentException("n is null");
        }
        BigDecimal m = new BigDecimal(n); // BigDecimal copy
        BigInteger r = BigInteger.ZERO.setBit(n.bitLength() / 3); // initial
        // estimate
        for (BigDecimal s = BigDecimal.ZERO; // different from r
             !r.equals(s.toBigInteger()); // loop test: does r=s?
             s = new BigDecimal(r), r = new BigDecimal(r.shiftLeft(1)) // Convert
                     // to
                     // BigDecimal,
                     .add(m.divide(s.multiply(s), UP)) // do the tricky
                     // division,
                     .divide(THREE_D, UP).toBigInteger())
            ;// and convert back.
        return r; // return the value
    }


    public static long log(long stage, long number) {
        return (long) (Math.log(number) / Math.log(stage));
    }

    public static boolean isPrime(BigInteger number) {
        if (number == null) {
            throw new IllegalArgumentException("number is null");
        }
        if (number.bitLength() < 32) {
            return isPrime(number.intValue());
        } else {
            return number.isProbablePrime(100);
        }
    }

    public static boolean isPrime(long a) {
        if (a < 0) {
            a = -a;
        }
        return isPrime(BigInteger.valueOf(a));
    }

    public static long ternaryToDecimal(List<Integer> ternary) {
        long result = 0;
        int curValue;
        for (int i = 0; i < ternary.size(); i++) {
            curValue = ternary.get(i);
            if (curValue == 1) {
                result += Math.pow(2, ternary.size() - i - 1);
            } else if (curValue == -1) {
                result -= Math.pow(2, ternary.size() - i - 1);
            } else if (curValue != 0) {
                throw new IllegalArgumentException("cannot transform from ternary to decimal due to invalid non-ternary value " + curValue);
            }
        }
        return result;
    }

    public static char[] toBinary(long n) {
        return Long.toBinaryString(n).toCharArray();
    }

    public static List<Integer> toTernary(long n) {
        if (n < 0) {
            throw new IllegalArgumentException("cannot transform negative number to ternary");
        }
        LinkedList<Integer> ternary = new LinkedList<>();
        if (n == 0) {
            ternary.add(0);
            return ternary;
        }
        char[] binary = toBinary(n);
        int lastIndex = binary.length - 1;
        while (true) {
            lastIndex = toTernary(binary, lastIndex, ternary);
            if (lastIndex < 0) {
                break;
            }
        }
        return ternary;
    }

    private static int toTernary(char[] binary, int beginIndex, LinkedList<Integer> ternary) {
        if (beginIndex >= binary.length) {
            throw new IllegalArgumentException("cannot transform binary to ternary. beginIndex " + beginIndex + " is bigger than binary array length");
        } else if (beginIndex < 0) {
            throw new IllegalArgumentException("cannot transform binary to ternary. beginIndex is negative");
        }
        int terms = 0;
        int lastIndex = beginIndex;
        for (; lastIndex >= 0; lastIndex--) {
            if (binary[lastIndex] == '0') {
                break;
            }
            terms++;
        }
        if (terms == 0) {
            ternary.addFirst(0);
        } else if (terms == 1) {
            ternary.addFirst(1);
            if (lastIndex > 0) {
                ternary.addFirst(0);
            }
            return lastIndex - 1;
        } else {
            ternary.addFirst(-1);
            for (int i = 0; i <= terms - 2; i++) {
                ternary.addFirst(0);
            }
            ternary.addFirst(1);
        }
        return lastIndex - 1;
    }

    public static boolean isPrime(int a) {
        if (a < 0) {
            a = -a;
        }
        if (a < 2 || (a > 2 && a % 2 == 0)) {
            return false;
        } else if (a < 1000) {
            return PRIMES[a];
        }
        int sqr = (int) Math.sqrt(a);
        for (int i = 3; i <= sqr; i += 2) {
            if (a % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static BigInteger sqrtBig(BigInteger x) {
        BigInteger div = BigInteger.ZERO.setBit(x.bitLength() / 2);
        BigInteger div2 = div;
        // Loop until we hit the same value twice in a row, or wind
        // up alternating.
        for (; ; ) {
            BigInteger y = div.add(x.divide(div)).shiftRight(1);
            if (y.equals(div) || y.equals(div2))
                return y;
            div2 = div;
            div = y;
        }
    }

    public static BigInteger getGenerator(SafePrime safePrime) {
        BigInteger gen = BigInteger.ONE;
        while (!isGenerator(safePrime, gen)) {
            gen = gen.add(BigInteger.ONE);
        }
        return gen;
    }

    public static boolean isSafePrime(BigInteger number) {
        if (number == null) {
            throw new IllegalArgumentException("number is null");
        } else if (!isPrime(number)) {
            return false;
        }
        BigInteger sophieNumber = number.subtract(BigInteger.ONE).divide(TWO_BIG);
        return isPrime(sophieNumber);
    }

    public static boolean inRange(int number, int begin, int end) {
        if (begin >= end) {
            throw new IllegalArgumentException("end must be bigger than begin [" + begin + ":" + end + "]");
        }
        return number >= begin && number <= end;
    }

    public static BigIntEuclidean eea(BigInteger a, BigInteger b) {
        BigInteger x = BigInteger.ZERO;
        BigInteger lastX = BigInteger.ONE;
        BigInteger y = BigInteger.ONE;
        BigInteger lastY = BigInteger.ZERO;
        while (!b.equals(BigInteger.ZERO)) {
            BigInteger[] quotientAndRemainder = a.divideAndRemainder(b);
            BigInteger quotient = quotientAndRemainder[0];
            BigInteger temp;
            a = b;
            b = quotientAndRemainder[1];
            temp = x;
            x = lastX.subtract(quotient.multiply(x));
            lastX = temp;
            temp = y;
            y = lastY.subtract(quotient.multiply(y));
            lastY = temp;
        }
        return new BigIntEuclidean(lastX, lastY, a);
    }

    private static boolean isGenerator(SafePrime safePrime, BigInteger gen) {
        return !gen.modPow(TWO_BIG, safePrime.getPrime()).equals(BigInteger.ONE) && !gen.modPow(safePrime.getSophieNumber(), safePrime.getPrime()).equals(BigInteger.ONE);
    }

    public static long dummyFactor(long n) {
        if (n < 0) {
            throw new IllegalArgumentException("cannot factor negative number");
        } else if (n == 0) {
            throw new IllegalArgumentException("cannot factor zero");
        }
        if (n != 2 && n % 2 == 0) {
            return 2;
        }
        long sqrt = (long) Math.sqrt(n);
        for (long i = 3; i <= sqrt; i += 2) {
            if (n % i == 0) {
                return i;
            }
        }
        return 1;
    }

    public static List<Integer> getFirstBigTermsPrimes(int primesToGenerate) {
        if (primesToGenerate < 0) {
            throw new IllegalArgumentException("cannot create negative number of prime numbers");
        }
        List<Integer> primes = new ArrayList<>();
        int probablePrime = 3;
        while (primes.size() != primesToGenerate) {
            if (isPrime(probablePrime)) {
                char[] binary = toBinary(probablePrime);
                if (TextUtil.getLongestRepeats(binary, '1') >= (binary.length / 3)) {
                    primes.add(probablePrime);
                }
            }
            probablePrime += 2;
        }
        return primes;
    }

    public static boolean makesSenseToUseTernary(long number) {
        return number == 31 || number == 127 || number == 255 || number == 8191;
    }

    public static BigInteger getRelativePrime(BigInteger n) {
        if (n == null) {
            throw new IllegalArgumentException("n is null");
        }
        SecureRandom random = new SecureRandom();
        BigInteger relativePrime = BigInteger.probablePrime(n.bitLength(), random);
        while (!relativePrime.gcd(n).equals(BigInteger.ONE)) {
            relativePrime = BigInteger.probablePrime(n.bitLength(), random);
        }
        return relativePrime;
    }

    public static BigInteger createNonce(int bits) {
        if (bits < MIN_NONCE_BITS) {
            throw new IllegalArgumentException("must be at least " + MIN_NONCE_BITS + " bits to create nonce");
        }
        SecureRandom random = new SecureRandom();
        BigInteger nonce = new BigInteger(random.nextInt(bits), random);
        while (nonce.compareTo(BigInteger.ONE) <= 0) {
            nonce = new BigInteger(random.nextInt(bits), random);
        }
        return nonce;
    }

    public static class BigIntEuclidean {
        private final BigInteger x;
        private final BigInteger y;
        private final BigInteger gcd;

        public BigIntEuclidean(BigInteger x, BigInteger y, BigInteger gcd) {
            this.x = x;
            this.y = y;
            this.gcd = gcd;
        }

        public BigInteger getX() {
            return x;
        }

        public BigInteger getY() {
            return y;
        }

        public BigInteger getGcd() {
            return gcd;
        }

        @Override
        public String toString() {
            return "BigIntEuclidean{" +
                    "x=" + x +
                    ", y=" + y +
                    ", gcd=" + gcd +
                    '}';
        }
    }

    protected static int getBinaryTerms(char[] binary) {
        int terms = 0;
        for (char b : binary) {
            if (b != '0') {
                terms++;
            }
        }
        return terms;
    }

    protected static int getTernaryTerms(List<Integer> ternary) {
        int terms = 0;
        for (Integer t : ternary) {
            if (t != 0) {
                terms++;
            }
        }
        return terms;
    }

}
