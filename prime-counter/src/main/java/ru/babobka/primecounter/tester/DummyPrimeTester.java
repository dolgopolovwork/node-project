package ru.babobka.primecounter.tester;

/**
 * Created by dolgopolov.a on 06.07.15.
 */
public class DummyPrimeTester implements PrimeTester {

	@Override
	public boolean isPrime(long n) {
		if (n < 2) {
			return false;
		}
		if (n != 2 && n % 2 == 0) {

			return false;
		}
		int sqrt = (int) Math.sqrt(n);
		for (int i = 3; i <= sqrt; i += 2) {
			if (n % i == 0) {

				return false;
			}
		}
		return true;
	}

}
