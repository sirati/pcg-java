package de.edu.lmu.pcg.impl.vector.preview21;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorSpecies;


public final class Util {
    private Util() {
    }

    public static final VectorSpecies<Long> LONG_SPECIES = LongVector.SPECIES_PREFERRED;
    public static final byte LONG_COUNT = (byte) LONG_SPECIES.length();
    public static final short LONG_SIZE = (short) (LONG_COUNT * (short) 8);
    public static final VectorMask<Integer> L2I_MASK = IntVector.SPECIES_PREFERRED.indexInRange(0, LONG_COUNT);


    public static final VectorSpecies<Integer> INT_SPECIES = IntVector.SPECIES_PREFERRED;
    public static final byte INT_COUNT = (byte) INT_SPECIES.length();
    public static final short INT_SIZE = (short) (INT_COUNT * (short) 4);
    public static final byte L2I_REDUCTION_FACTOR = (byte) (INT_COUNT / LONG_COUNT);

}
