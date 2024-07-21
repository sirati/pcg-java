package de.edu.lmu.pcg.bench;

import de.edu.lmu.pcg.PCG_RXS_M_XS_32;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class NaiveToArray {

    private int[] array;
    private Random seed_init;

    @Setup(Level.Trial)
    public void setUpOnce() {
        // Pre-create a 32MB array, we do this to avoid GC pressure during the benchmark
        array = new int[Util.MB32 / 4];
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
    @Benchmark
    public int[] bench() {
        //init
        var pcg = new PCG_RXS_M_XS_32(seed_init.nextInt());
        var bits = pcg.bitsPerIteration();
        if (bits >> Integer.numberOfTrailingZeros(bits) != 1) {
            throw new RuntimeException("Bites per iteration must be a power of 2");
        }
        int advance = bits / 32; //int:= i32
        //benchmark
        for (int i = 0; i < array.length; i += advance) {
            pcg.fillOnceInto(array, 0, array.length);
        }

        //to make sure the jit does not realise that array is never read
        return array;
    }
}
