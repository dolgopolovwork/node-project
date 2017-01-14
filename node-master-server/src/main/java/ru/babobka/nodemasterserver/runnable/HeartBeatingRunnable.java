package ru.babobka.nodemasterserver.runnable;

import java.io.IOException;
import java.util.List;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodemasterserver.slave.Slave;
import ru.babobka.nodemasterserver.slave.SlavesStorage;

public class HeartBeatingRunnable implements Runnable {

	private static final int HEARTBEAT_TIMEOUT_MILLIS = 30000;

	private final SimpleLogger logger = Container.getInstance()
			.get(SimpleLogger.class);

	private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);

	@Override
	public void run() {
		logger.log("Start HeartBeatingRunnable");
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(HEARTBEAT_TIMEOUT_MILLIS);
				List<Slave> clientThreads = slavesStorage.getFullList();
				for (Slave clientThread : clientThreads) {
					if (!Thread.currentThread().isInterrupted()) {
						try {
							clientThread.sendHeartBeating();
						} catch (IOException e) {
							logger.log(e);
						}
					}
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				logger.log("HeartBeatingRunnable is done");
				break;
			}

		}
	}

}
