package ru.babobka.nodeclient.factor;

import ru.babobka.nodeutils.util.TextUtil;

import java.math.BigInteger;

/**
 * Created by 123 on 16.12.2017.
 */
class FactorData {
    private final String host;
    private final int port;
    private final BigInteger number;

    FactorData(String[] args) {
        if (args == null || args.length < 3) {
            throw new IllegalArgumentException("You must specify 3 arguments: host, port and a number to factor");
        }
        String host = args[0];
        int port = TextUtil.tryParseInt(args[1], -1);
        if (!TextUtil.isValidPort(port)) {
            throw new IllegalArgumentException("Invalid port " + args[1]);
        } else if (TextUtil.isEmpty(host)) {
            throw new IllegalArgumentException("Host was not specified");
        }
        BigInteger number;
        try {
            number = new BigInteger(args[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format " + args[2], e);
        }
        this.port = port;
        this.host = host;
        this.number = number;
    }

    String getHost() {
        return host;
    }

    int getPort() {
        return port;
    }

    BigInteger getNumber() {
        return number;
    }
}
