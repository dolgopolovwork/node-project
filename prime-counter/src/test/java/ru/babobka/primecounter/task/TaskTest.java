package ru.babobka.primecounter.task;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.primecounter.task.PrimeCounterTask;
import ru.babobka.subtask.model.SubTask;

public class TaskTest {

	public SubTask task;

	private NodeRequest tenPrimesRequest;

	private NodeRequest thousandPrimesRequest;

	private NodeRequest tenThousandPrimesRequest;

	private NodeRequest millionPrimesRequest;

	@Before
	public void init() {
		task = new PrimeCounterTask();
		Map<String, Serializable> additionMap = new HashMap<>();
		additionMap.put("begin", 0L);
		additionMap.put("end", 15_485_863L);
		millionPrimesRequest = new NodeRequest(UUID.randomUUID(), UUID.randomUUID(), "millerPrimeCounter", additionMap,
				false, false);

		additionMap = new HashMap<>();
		additionMap.put("begin", 0L);
		additionMap.put("end", 7919L);
		thousandPrimesRequest = new NodeRequest(UUID.randomUUID(), UUID.randomUUID(), "millerPrimeCounter", additionMap,
				false, false);

		additionMap = new HashMap<>();
		additionMap.put("begin", 0L);
		additionMap.put("end", 104729L);
		tenThousandPrimesRequest = new NodeRequest(UUID.randomUUID(), UUID.randomUUID(), "millerPrimeCounter",
				additionMap, false, false);

		additionMap = new HashMap<>();
		additionMap.put("begin", 0L);
		additionMap.put("end", 29L);
		tenPrimesRequest = new NodeRequest(UUID.randomUUID(), UUID.randomUUID(), "millerPrimeCounter", additionMap,
				false, false);
	}

	

	@Test
	public void testMillionPrimes() {
		assertEquals(task.execute(millionPrimesRequest).getResultMap().get("primeCount"), 1_000_000);
	}

	@Test
	public void testStop() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					task.stopTask();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}).start();
		assertEquals(task.execute(millionPrimesRequest).getResultMap().get("primeCount"), 0);
		assertTrue(task.isStopped());
	}

	@Test
	public void testTenThousandPrimes() {
		assertEquals(task.execute(tenThousandPrimesRequest).getResultMap().get("primeCount"), 10000);
	}

	@Test
	public void testTenPrimes() {
		assertEquals(task.execute(tenPrimesRequest).getResultMap().get("primeCount"), 10);
	}

	@Test
	public void testThousandPrimes() {
		assertEquals(task.execute(thousandPrimesRequest).getResultMap().get("primeCount"), 1000);
	}

}
