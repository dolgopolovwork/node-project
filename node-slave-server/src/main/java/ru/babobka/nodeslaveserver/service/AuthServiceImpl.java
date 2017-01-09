package ru.babobka.nodeslaveserver.service;

import ru.babobka.nodeslaveserver.builder.AuthResponseBuilder;
import ru.babobka.nodeslaveserver.exception.MasterServerIsFullException;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeserials.crypto.PublicKey;
import ru.babobka.nodeserials.crypto.RSA;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

/**
 * Created by dolgopolov.a on 30.10.15.
 */
public class AuthServiceImpl implements AuthService {

	private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

	private final SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);

	@Override
	public boolean auth(Socket socket, String login, String password) {

		try {
			socket.setSoTimeout(slaveServerConfig.getAuthTimeoutMillis());
			boolean fittable = StreamUtil.receiveObject(socket);
			if (fittable) {
				PublicKey publicKey = StreamUtil.receiveObject(socket);
				StreamUtil.sendObject(AuthResponseBuilder.build(new RSA(null, publicKey), login, password), socket);
				return (Boolean) StreamUtil.receiveObject(socket);
			} else {
				logger.log(Level.WARNING, "Can not connect to master server due to connection limit");
				throw new MasterServerIsFullException();

			}
		} catch (IOException e) {
			logger.log(e);
			return false;
		}
	}
}
