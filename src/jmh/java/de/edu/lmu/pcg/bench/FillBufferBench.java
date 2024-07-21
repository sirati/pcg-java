package de.edu.lmu.pcg.bench;

import de.edu.lmu.pcg.PCGBuilder;
import de.edu.lmu.pcg.PCG_XSH_RS;
import org.openjdk.jmh.annotations.*;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static de.edu.lmu.pcg.PCGImplementationVariant.JavaPrimitive;
import static de.edu.lmu.pcg.PCGImplementationVariant.JavaVectoring;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class FillBufferBench {

    private ByteBuffer buff;
    private Random seed_init;
    private PCGBuilder<PCG_XSH_RS, Long>.StateConfigured builder;

    @Setup(Level.Trial)
    public void setUpOnce() {
        // Pre-create a 32MB array, we do this to avoid GC pressure during the benchmark
        buff = ByteBuffer.allocate(Util.MB32);
        builder = new PCGBuilder<>().preferred_variant(JavaVectoring).type(PCG_XSH_RS.class);
    }

    @Setup(Level.Iteration)
    public void setUp() {
        //this needs to be per iteration as otherwise changing iteration count may create inconsistent results
        seed_init = new Random(0x5EED_5EEDL);
    }

    /**
     * Benchmarks the PCG algorithm
     * this is very biased towards bad results as we have to loop and call the fillInto method which is not optimised
     */
    @Fork(value = 1, jvmArgsPrepend = {"--enable-preview", "--add-modules=jdk.incubator.vector"})
    @Benchmark
    public ByteBuffer bench() {
        //init
        var pcg = builder.seed(seed_init.nextLong()).build();
        if (pcg.getImplementationVariant() != JavaVectoring)
            throw new AssertionError("Implementation variant is not JavaVectoring");
        pcg.fill(buff);

        //to make sure the jit does not realise that array is never read
        return buff;
    }
}
