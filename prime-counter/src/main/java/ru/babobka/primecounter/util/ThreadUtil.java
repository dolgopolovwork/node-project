package ru.babobka.primecounter.util;

import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by dolgopolov.a on 02.10.15.
 */
public interface ThreadUtil {

	public static void interruptBatch(AtomicReferenceArray<Thread> localThreads) {
		if (localThreads != null) {
			Thread tempThread;
			for (int i = 0; i < localThreads.length(); i++) {

				while ((tempThread = localThreads.get(i)) == null) {
					Thread.yield();
				}
				if (tempThread.isAlive() && !tempThread.isInterrupted()) {
					tempThread.interrupt();
				}
			}
		}
	}
}
