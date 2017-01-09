package ru.babobka.nodemasterserver.slave;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.util.StreamUtil;

/**
 * Created by dolgopolov.a on 27.07.15.
 */
public class IncomingSlaveListenerThread extends Thread {

	private final ServerSocket ss;

	private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

	private final Slaves slaves = Container.getInstance().get(Slaves.class);

	public IncomingSlaveListenerThread(int port) throws IOException {
		ss = new ServerSocket(port);
	}

	private boolean fit(SlaveThread slaveThread) throws IOException {
		synchronized (SlaveThread.class) {
			boolean fittable = slaves.add(slaveThread);
			StreamUtil.sendObject(fittable, slaveThread.getSocket());
			return fittable;
		}
	}

	@Override
	public void run() {
		try {
			logger.log("Start InputListenerThread");
			while (!Thread.currentThread().isInterrupted()) {
				try {
					Socket socket = ss.accept();
					SlaveThread slaveThread = new SlaveThread(socket);
					boolean fittable = fit(slaveThread);
					if (fittable) {
						slaveThread.start();
					} else {
						logger.log(Level.WARNING, "Can not add new slave due to max connection limit " + socket);
						socket.close();

					}

				} catch (Exception e) {
					if (!ss.isClosed() || !Thread.currentThread().isInterrupted()) {
						logger.log(e);
					}

				}

			}
		} finally {

			try {
				ss.close();
			} catch (IOException e) {
				logger.log(e);
			}

		}
		logger.log("InputListenerThread is done");
	}

	@Override
	public void interrupt() {
		super.interrupt();
		try {
			ss.close();
		} catch (IOException e) {
			logger.log(e);
		}

	}
}