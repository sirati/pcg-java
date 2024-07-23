package de.edu.lmu.pcg.bench;

import de.edu.lmu.pcg.PCGBuilder;
import de.edu.lmu.pcg.PCG_XSH_RS;
import org.openjdk.jmh.annotations.*;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static de.edu.lmu.pcg.PCGImplementationVariant.JavaVectoring;



@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@OperationsPerInvocation(512)
@State(Scope.Thread)
public class ThroughputVectorized_PCG_XSH_RS {

    private ByteBuffer buff;
    private Random seed_init;
    private PCG_XSH_RS pcg;
    private PCGBuilder<PCG_XSH_RS, Long>.StateConfigured builder;


    @Setup(Level.Trial)
    public void setUpOnce() {
        //this needs to be per iteration as otherwise changing iteration count may create inconsistent results
        seed_init = new Random(0x5EED_5EEDL);
        buff = ByteBuffer.allocateDirect(512);
        //assume 64 = one cache line
        //assume 32 = 84 long are one lane
        builder = new PCGBuilder<>().preferred_variant(JavaVectoring).type(PCG_XSH_RS.class);
    }

    @Setup(Level.Iteration)
    public void setUp() {
        //init
        pcg = builder.seed(seed_init.nextLong()).build();
        if (pcg.getImplementationVariant() != JavaVectoring)
            throw new AssertionError("Implementation variant is not JavaVectoring");
    }

    /**
     * Benchmarks the PCG algorithm
     * this is very biased towards bad results as we have to loop and call the fillInto method which is not optimised
     */
    @Fork(jvmArgsPrepend = {"--enable-preview", "--add-modules=jdk.incubator.vector"})
    @Benchmark
    public ByteBuffer bench() {
        pcg.fill(buff);
        return buff;
    }
}
