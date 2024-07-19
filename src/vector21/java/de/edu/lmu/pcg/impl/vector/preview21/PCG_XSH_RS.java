package de.edu.lmu.pcg.impl.vector.preview21;
import jdk.incubator.vector.*;
import de.edu.lmu.pcg.PCGImplementationVariant;
import de.edu.lmu.pcg.services.PCGCtorService;

import static de.edu.lmu.pcg.Util.longIncrement;
import static de.edu.lmu.pcg.Util.longMod;
import static de.edu.lmu.pcg.Util.longMultiplier;

public class PCG_XSH_RS extends de.edu.lmu.pcg.PCG_XSH_RS {
    public static class CtorService implements PCGCtorService.SeedU64<PCG_XSH_RS> {
        @Override
        public PCG_XSH_RS create(long seed) {
            return new PCG_XSH_RS(seed);
        }
        @Override
        public PCGImplementationVariant getImplementationVariant() {
            return PCGImplementationVariant.JavaVectoring21;
        }
    }

    public PCG_XSH_RS(long seed) {
        super(seed);
    }

    // Vector species for long vectors, assuming a platform-specific best choice
    private static final VectorSpecies<Long> LONG_SPECIES = LongVector.SPECIES_PREFERRED;
    private static final VectorSpecies<Integer> INT_SPECIES = IntVector.SPECIES_PREFERRED;


    private static LongVector newState(LongVector stateVector) {
        LongVector multiplierVector = LongVector.broadcast(LONG_SPECIES, longMultiplier);
        LongVector incrementVector = LongVector.broadcast(LONG_SPECIES, longIncrement);
        LongVector modVector = LongVector.broadcast(LONG_SPECIES, longMod);

        LongVector multiplied = stateVector.mul(multiplierVector);
        LongVector added = multiplied.add(incrementVector);

        return added.remainder(modVector);
    }

//this is all completely wrong. todo fix
    public void nextIntShift(IntVector result, LongVector stateVector) {
        assert stateVector.length() == LONG_SPECIES.length();
        LongVector xorshiftVector = stateVector.xor(stateVector.lshr(22));
        LongVector randomshiftVector = stateVector.lshr(61);

        // Generate the next states
        stateVector = newState(stateVector);

        IntVector xorshift = xorshiftVector.convert(VectorOperators.L2I, 0);
        IntVector randomshift = randomshiftVector.convert(VectorOperators.L2I, 0);

        for (int i = 0; i < INT_SPECIES.length(); i++) {
            result.set(i, (int) (xorshift.lane(i) >> (22 + randomshift.lane(i))));
        }
    }
}
