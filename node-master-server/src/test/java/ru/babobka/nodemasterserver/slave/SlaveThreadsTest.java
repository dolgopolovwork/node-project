package ru.babobka.nodemasterserver.slave;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SlaveThreadsTest {

	static final int N = 1000;
	static final int MAX_THREADS = 10;
	Slaves slaves;
	final SlaveThread slaveThreadMock = mock(SlaveThread.class);

	@Before
	public void setUp() {
		slaves = new Slaves(N);
	}

	@After
	public void tearDown() {
		slaves.clear();
	}

	@Test
	public void testEmpty() {
		assertTrue(slaves.isEmpty());
	}

	@Test
	public void testMaxSize() {

		for (int i = 0; i < N; i++) {
			assertTrue(slaves.add(slaveThreadMock));
		}
		assertFalse(slaves.add(slaveThreadMock));
	}

	@Test
	public void testAdd() {

		assertTrue(slaves.add(slaveThreadMock));
	}

	@Test
	public void testClear() {
		slaves.add(slaveThreadMock);
		slaves.clear();
		assertTrue(slaves.isEmpty());
	}

	@Test
	public void testAddNull() {
		assertFalse(slaves.add(null));
	}

	@Test
	public void testRemoveNull() {
		assertFalse(slaves.remove(null));
	}

	@Test
	public void testRemove() {
		slaves.add(slaveThreadMock);
		assertFalse(slaves.isEmpty());
		assertTrue(slaves.remove(slaveThreadMock));
		assertTrue(slaves.isEmpty());
	}

	@Test
	public void testAddParallel() throws InterruptedException {

		Thread[] addThreads = new Thread[MAX_THREADS];
		final AtomicInteger succededAdds = new AtomicInteger();
		for (int i = 0; i < addThreads.length; i++) {
			addThreads[i] = new Thread(new Runnable() {

				@Override
				public void run() {
					for (int i = 0; i < N; i++) {
						if (slaves.add(slaveThreadMock)) {
							succededAdds.incrementAndGet();
						}
					}
				}
			});
		}
		for (Thread addThread : addThreads) {
			addThread.start();
		}

		for (Thread addThread : addThreads) {
			addThread.join();
		}
		assertEquals(succededAdds.intValue(), N);
		assertEquals(slaves.getClusterSize(), N);
		assertEquals(slaves.getFullList().size(), N);
	}

	@Test
	public void testRemoveParallel() throws InterruptedException {
		for (int i = 0; i < N; i++) {
			slaves.add(slaveThreadMock);
		}
		Thread[] removeThreads = new Thread[MAX_THREADS];
		final AtomicInteger succededRemoves = new AtomicInteger();
		for (int i = 0; i < removeThreads.length; i++) {
			removeThreads[i] = new Thread(new Runnable() {

				@Override
				public void run() {
					for (int i = 0; i < N; i++) {
						if (slaves.remove(slaveThreadMock)) {
							succededRemoves.incrementAndGet();
						}
					}
				}
			});
		}
		for (Thread removeThread : removeThreads) {
			removeThread.start();
		}

		for (Thread removeThread : removeThreads) {
			removeThread.join();
		}
		assertEquals(succededRemoves.intValue(), N);
		assertEquals(slaves.getClusterSize(), 0);
		assertTrue(slaves.isEmpty());
		assertTrue(slaves.getFullList().isEmpty());
	}

}