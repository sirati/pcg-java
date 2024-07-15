package de.edu.lmu.pcg.bench;

import de.edu.lmu.pcg.PCG_RXS_M_XS_32;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class NaiveConsume {

    private Random seed_init;

    @Setup(Level.Iteration)
    public void setUp() {
        //this needs to be per iteration as otherwise changing iteration count may create inconsistent results
        seed_init = new Random(0x5EED_5EEDL);
    }

    /**
     * Benchmarks the PCG algorithm
     * this is very biased towards bad results as we have to loop and call the fillInto method which is not optimised
     */
    @Benchmark
    public void bench(Blackhole bh) {
        //init
        var pcg = new PCG_RXS_M_XS_32(seed_init.nextInt());
        var bits = pcg.bitesPerIteration();
        if (bits >> Integer.numberOfTrailingZeros(bits) != 1) {
            throw new RuntimeException("Bites per iteration must be a power of 2");
        }
        int advance = bits / 8; //int:= i32, so for each i32 advance 4 bytes
        //benchmark
        for (int i = 0; i < Util.MB32; i+=advance) {
            bh.consume(pcg.nextInt());
        }
    }
}
