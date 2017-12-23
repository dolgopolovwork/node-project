package ru.babobka.node.factorbenchmark;

import ru.babobka.nodeutils.util.TextUtil;

/**
 * Created by 123 on 16.12.2017.
 */
class FactorBenchmarkData {
    private static final int DEFAULT_INVALID_INT_VALUE = -1;
    private static final int MIN_BIT_LENGTH = 16;
    private final String host;
    private final int port;
    private final int numberBitLength;
    private final int iterations;

    FactorBenchmarkData(String[] args) {
        if (args == null || args.length < 4) {
            throw new IllegalArgumentException("You must specify 4 arguments: host, port, bit length of number to factor and total number of requests to benchmark");
        }
        String host = args[0];
        int port = TextUtil.tryParseInt(args[1], DEFAULT_INVALID_INT_VALUE);
        if (!TextUtil.isValidPort(port)) {
            throw new IllegalArgumentException("Invalid port " + args[1]);
        } else if (TextUtil.isEmpty(host)) {
            throw new IllegalArgumentException("Host was not specified");
        }
        int numberBitLength = TextUtil.tryParseInt(args[2], DEFAULT_INVALID_INT_VALUE);
        if (numberBitLength <= 0) {
            throw new IllegalArgumentException("Invalid bit length");
        } else if (numberBitLength < MIN_BIT_LENGTH) {
            throw new IllegalArgumentException("Bit length must be at least " + MIN_BIT_LENGTH);
        }
        int iterations = TextUtil.tryParseInt(args[3], DEFAULT_INVALID_INT_VALUE);
        if (iterations <= 0) {
            throw new IllegalArgumentException("Invalid number of requests to benchmark");
        }
        this.port = port;
        this.host = host;
        this.numberBitLength = numberBitLength;
        this.iterations = iterations;
    }

    String getHost() {
        return host;
    }

    int getPort() {
        return port;
    }

    int getNumberBitLength() {
        return numberBitLength;
    }

    int getIterations() {
        return iterations;
    }
}
