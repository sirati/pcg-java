package de.edu.lmu.pcg.bench;

import de.edu.lmu.pcg.PCGBuilder;
import de.edu.lmu.pcg.PCG_RXS_M_XS_32;
import de.edu.lmu.pcg.PCG_XSH_RS;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static de.edu.lmu.pcg.PCGImplementationVariant.JavaVectoring;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class Throughput {

    private Random seed_init;
    private PCG_RXS_M_XS_32 pcg;


    @Setup(Level.Trial)
    public void setUpOnce() {
        //this needs to be per iteration as otherwise changing iteration count may create inconsistent results
        seed_init = new Random(0x5EED_5EEDL);
    }

    @Setup(Level.Iteration)
    public void setUp() {
        //init
        pcg = new PCG_RXS_M_XS_32(seed_init.nextInt());
    }

    /**
     * Benchmarks the PCG algorithm
     * this is very biased towards bad results as we have to loop and call the fillInto method which is not optimised
     */
    @Benchmark
    public int bench(Blackhole bh) {
        return pcg.nextInt();
    }
}
