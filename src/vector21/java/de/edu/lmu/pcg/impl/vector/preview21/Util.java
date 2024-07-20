package de.edu.lmu.pcg.impl.vector.preview21;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorSpecies;


public final class Util {
    private Util() {}

    /*
    public static final long u128MultiplierHigh = 2549297995355413924L;
    public static final long u128MultiplierLow = 4865540595714422341L;
    public static final BigInteger u128Multiplier = BigInteger.valueOf(u128MultiplierHigh).shiftLeft(64)
            .or(BigInteger.valueOf(u128MultiplierLow));
    public static final long u128IncrementHigh = 6364136223846793005L;
    public static final long u128IncrementLow = 1442695040888963407L;
    public static final BigInteger u128Increment = BigInteger.valueOf(u128IncrementHigh).shiftLeft(64)
            .or(BigInteger.valueOf(u128IncrementLow));
    public static final BigInteger bigModulus = BigInteger.ONE.shiftLeft(127);

    public static final long longMultiplier = 6364136223846793005L;
    public static final long longIncrement = 1442695040888963407L;
    public static final long longMod = 1L << 63;

    public static final int intMultiplier = 1664525;
    public static final int intIncrement = 1013904223;
    public static final int intMod = 1 << 31;
     */

    public static final VectorSpecies<Long> LONG_SPECIES = LongVector.SPECIES_PREFERRED;
    public static final int LONG_COUNT = LONG_SPECIES.length();
    public static final int LONG_SIZE =  LONG_COUNT * 8;
    public static final VectorMask<Integer> L2I = IntVector.SPECIES_PREFERRED.indexInRange(0, LONG_COUNT);

    public static final VectorSpecies<Integer> INT_SPECIES = IntVector.SPECIES_PREFERRED;
    public static final int INT_COUNT = INT_SPECIES.length();
    public static final int INT_SIZE =  INT_COUNT * 4;


    public static final LongVector u128MultiplierLow = LongVector.broadcast(LONG_SPECIES, de.edu.lmu.pcg.Util.u128MultiplierLow);
    public static final LongVector u128MultiplierHigh = LongVector.broadcast(LONG_SPECIES, de.edu.lmu.pcg.Util.u128MultiplierHigh);
    public static final LongVector u128IncrementLow = LongVector.broadcast(LONG_SPECIES, de.edu.lmu.pcg.Util.u128IncrementLow);
    public static final LongVector u128IncrementHigh = LongVector.broadcast(LONG_SPECIES, de.edu.lmu.pcg.Util.u128IncrementHigh);


    public static final LongVector longMultiplier = LongVector.broadcast(LONG_SPECIES, de.edu.lmu.pcg.Util.longMultiplier);
    public static final LongVector longIncrement = LongVector.broadcast(LONG_SPECIES, de.edu.lmu.pcg.Util.longIncrement);
    public static final LongVector longMod = LongVector.broadcast(LONG_SPECIES, de.edu.lmu.pcg.Util.longMod);
    public static final IntVector intMultiplier = IntVector.broadcast(INT_SPECIES, de.edu.lmu.pcg.Util.intMultiplier);
    public static final IntVector intIncrement = IntVector.broadcast(INT_SPECIES, de.edu.lmu.pcg.Util.intIncrement);
    public static final IntVector intMod = IntVector.broadcast(INT_SPECIES, de.edu.lmu.pcg.Util.intMod);

}
