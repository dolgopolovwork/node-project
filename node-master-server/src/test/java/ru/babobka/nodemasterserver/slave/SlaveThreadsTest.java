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
	SlavesStorage slavesStorage;
	final Slave slaveThreadMock = mock(Slave.class);

	@Before
	public void setUp() {
		slavesStorage = new SlavesStorage(N);
	}

	@After
	public void tearDown() {
		slavesStorage.clear();
	}

	@Test
	public void testEmpty() {
		assertTrue(slavesStorage.isEmpty());
	}

	@Test
	public void testMaxSize() {

		for (int i = 0; i < N; i++) {
			assertTrue(slavesStorage.add(slaveThreadMock));
		}
		assertFalse(slavesStorage.add(slaveThreadMock));
	}

	@Test
	public void testAdd() {

		assertTrue(slavesStorage.add(slaveThreadMock));
	}

	@Test
	public void testClear() {
		slavesStorage.add(slaveThreadMock);
		slavesStorage.clear();
		assertTrue(slavesStorage.isEmpty());
	}

	@Test
	public void testAddNull() {
		assertFalse(slavesStorage.add(null));
	}

	@Test
	public void testRemoveNull() {
		assertFalse(slavesStorage.remove(null));
	}

	@Test
	public void testRemove() {
		slavesStorage.add(slaveThreadMock);
		assertFalse(slavesStorage.isEmpty());
		assertTrue(slavesStorage.remove(slaveThreadMock));
		assertTrue(slavesStorage.isEmpty());
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
						if (slavesStorage.add(slaveThreadMock)) {
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
		assertEquals(slavesStorage.getClusterSize(), N);
		assertEquals(slavesStorage.getFullList().size(), N);
	}

	@Test
	public void testRemoveParallel() throws InterruptedException {
		for (int i = 0; i < N; i++) {
			slavesStorage.add(slaveThreadMock);
		}
		Thread[] removeThreads = new Thread[MAX_THREADS];
		final AtomicInteger succededRemoves = new AtomicInteger();
		for (int i = 0; i < removeThreads.length; i++) {
			removeThreads[i] = new Thread(new Runnable() {

				@Override
				public void run() {
					for (int i = 0; i < N; i++) {
						if (slavesStorage.remove(slaveThreadMock)) {
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
		assertEquals(slavesStorage.getClusterSize(), 0);
		assertTrue(slavesStorage.isEmpty());
		assertTrue(slavesStorage.getFullList().isEmpty());
	}

}