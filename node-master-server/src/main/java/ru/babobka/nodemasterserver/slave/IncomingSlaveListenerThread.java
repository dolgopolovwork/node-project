package ru.babobka.nodemasterserver.slave;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;

/**
 * Created by dolgopolov.a on 27.07.15.
 */
public class IncomingSlaveListenerThread extends Thread {

    private final ServerSocket ss;

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

    public IncomingSlaveListenerThread(int port) throws IOException {
	ss = new ServerSocket(port);
    }

    @Override
    public void run() {
	try {
	    logger.info("Start InputListenerThread");
	    while (!Thread.currentThread().isInterrupted()) {
		try {
		    Socket socket = ss.accept();
		    new Slave(socket).start();
		} catch (Exception e) {
		    if (!ss.isClosed() || !Thread.currentThread().isInterrupted()) {
			logger.error(e);
		    }
		}
	    }
	} finally {

	    try {
		ss.close();
	    } catch (IOException e) {
		logger.error(e);
	    }

	}
	logger.info("InputListenerThread is done");
    }

    @Override
    public void interrupt() {
	super.interrupt();
	try {
	    ss.close();
	} catch (IOException e) {
	    logger.error(e);
	}

    }
}