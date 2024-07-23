package de.edu.lmu.pcg.bench;

import de.edu.lmu.pcg.PCGBuilder;
import de.edu.lmu.pcg.PCG_XSH_RS;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static de.edu.lmu.pcg.PCGImplementationVariant.JavaPrimitive;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@OperationsPerInvocation(4)
@State(Scope.Thread)
public class ThroughputPrimitive_PCG_XSH_RS {

    private Random seed_init;
    private PCG_XSH_RS pcg;
    private PCGBuilder<PCG_XSH_RS, Long>.StateConfigured builder;


    @Setup(Level.Trial)
    public void setUpOnce() {
        //this needs to be per iteration as otherwise changing iteration count may create inconsistent results
        seed_init = new Random(0x5EED_5EEDL);
        builder = new PCGBuilder<>().preferred_variant(JavaPrimitive).type(PCG_XSH_RS.class);
    }

    @Setup(Level.Iteration)
    public void setUp() {
        //init
        pcg = builder.seed(seed_init.nextLong()).build();
        if (pcg.getImplementationVariant() != JavaPrimitive)
            throw new AssertionError("Implementation variant is not JavaPrimitive");
    }

    /**
     * Benchmarks the PCG algorithm
     * this is very biased towards bad results as we have to loop and call the fillInto method which is not optimised
     */
    @Benchmark
    public int bench() {
        return pcg.nextInt();
    }
}
