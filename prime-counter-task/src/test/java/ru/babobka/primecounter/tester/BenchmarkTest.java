package ru.babobka.primecounter.tester;

import ru.babobka.primecounter.tester.DummyPrimeTester;
import ru.babobka.primecounter.tester.PrimeTester;

public class BenchmarkTest {

	public static void main(String[] args) {

		BenchmarkTest benchMark = new BenchmarkTest();
		PrimeTester tester = new DummyPrimeTester();
		benchMark.test("Dummy tester", tester);
		

	}

	public void test(String name, PrimeTester tester) {
		int tests = 7;
		int mult = 10;
		int numbers = 10;
		for (int i = 0; i < tests; i++) {
			Timer timer = new Timer(name + " for " + numbers + " numbers");
			for (int j = 0; j < numbers; j++) {
				tester.isPrime(j);
			}
			System.out.println(timer);
			numbers *= mult;
		}

	}

	private static class Timer {
		private long time;
		private String title;

		Timer(String title) {
			this.time = System.currentTimeMillis();
			this.title = title;
		}

		public String toString() {
			return title + " takes " + (System.currentTimeMillis() - time);
		}

	}


}
