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

package ru.babobka.dlp;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;
import ru.babobka.dlp.model.DlpTask;
import ru.babobka.dlp.service.pollard.PollardCollisionService;
import ru.babobka.dlp.service.pollard.parallel.ParallelPollardDlpService;
import ru.babobka.dlp.service.pollard.parallel.PrimeDistinguishable;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.key.UtilKey;

import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.math.SafePrime;
import ru.babobka.nodeutils.thread.ThreadPoolService;
import ru.babobka.nodeutils.util.MathUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;

public class DlpBenchmark {
    @State(Scope.Thread)
    public static class MyState {

        @Param({"30", "31", "32", "33"})
        int orderBits;

        @Setup(Level.Trial)
        public void doSetup() {
            executorService = ThreadPoolService.createDaemonPool(Runtime.getRuntime().availableProcessors());
            Container.getInstance().put(container -> {
                container.put(UtilKey.SERVICE_THREAD_POOL, executorService);
                container.put(new PollardCollisionService());
                container.put(new PrimeDistinguishable());
            });
            parallelPollardDlpService = new ParallelPollardDlpService(Runtime.getRuntime().availableProcessors());
        }

        @Setup(Level.Iteration)
        public void initNumber() {
            SafePrime safePrime = SafePrime.random(orderBits - 1);
            BigInteger gen = MathUtil.getGenerator(safePrime);
            BigInteger mod = safePrime.getPrime();
            BigInteger y = createNumber(mod);
            task = new DlpTask(new Fp(gen, mod), new Fp(y, mod));
        }

        private BigInteger createNumber(BigInteger mod) {
            BigInteger number = BigInteger.valueOf(new Random().nextInt()).mod(mod);
            while (number.equals(BigInteger.ZERO)) {
                number = BigInteger.valueOf(new Random().nextInt()).mod(mod);
            }
            return number;
        }

        @TearDown(Level.Trial)
        public void doTearDown() throws InterruptedException {
            Container.getInstance().clear();
            executorService.shutdownNow();
            executorService.awaitTermination(15, TimeUnit.SECONDS);
        }

        DlpTask task;
        ExecutorService executorService;
        ParallelPollardDlpService parallelPollardDlpService;

    }

    @Benchmark
    @Warmup(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public BigInteger testMethod(MyState state) {
        return state.parallelPollardDlpService.execute(state.task);
    }

    public static void main(String[] args) throws IOException, RunnerException {
        Main.main(args);
    }

}
