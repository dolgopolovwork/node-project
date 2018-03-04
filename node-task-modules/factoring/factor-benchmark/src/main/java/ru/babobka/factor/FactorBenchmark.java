/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package ru.babobka.factor;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;
import ru.babobka.factor.model.FactoringResult;
import ru.babobka.factor.model.ec.multprovider.FastMultiplicationProvider;
import ru.babobka.factor.service.EllipticCurveFactorService;
import ru.babobka.factor.service.EllipticCurveFactorServiceFactory;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.thread.ThreadPoolService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;

public class FactorBenchmark {

    @State(Scope.Thread)
    public static class MyState {

        @Param({"32", "33", "34", "35", "36"})
        int factorBits;

        @Setup(Level.Trial)
        public void doSetup() {
            executorService = ThreadPoolService.createDaemonPool(Runtime.getRuntime().availableProcessors());
            Container.getInstance().put("service-thread-pool", executorService);
            Container.getInstance().put(mock(SimpleLogger.class));
            Container.getInstance().put(new FastMultiplicationProvider());
            ellipticCurveFactorService = new EllipticCurveFactorServiceFactory().get();
        }

        @Setup(Level.Iteration)
        public void initNumber() {
            number = BigInteger.probablePrime(factorBits, new Random())
                    .multiply(BigInteger.probablePrime(factorBits, new Random()));
        }

        @TearDown(Level.Trial)
        public void doTearDown() throws InterruptedException {
            Container.getInstance().clear();
            executorService.shutdownNow();
            executorService.awaitTermination(15, TimeUnit.SECONDS);
        }


        private ExecutorService executorService;
        private EllipticCurveFactorService ellipticCurveFactorService;
        private BigInteger number;


    }

    @Benchmark
    @Warmup(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public FactoringResult factor(MyState state) {
        return state.ellipticCurveFactorService.execute(state.number);
    }

    public static void main(String[] args) throws IOException, RunnerException {
        Main.main(args);
    }

}
