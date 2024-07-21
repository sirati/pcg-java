package de.edu.lmu.pcg;
import de.edu.lmu.pcg.services.PCGCtorService;

import java.math.BigInteger;

import static de.edu.lmu.pcg.Util.MASK_64;
import static de.edu.lmu.pcg.Util.MASK_128;

public class PCG_XSL_RR implements PCGLong, SeedTypeMarker<U128> {
    public static class CtorService implements PCGCtorService.SeedU128<PCG_XSL_RR> {
        @Override
        public PCG_XSL_RR create(U128 seed) {
            return new PCG_XSL_RR(seed.toBigInteger());
        }
    }

    protected BigInteger state;



    public static PCG_XSL_RR createFromNumber(Number seed) {
        return new PCG_XSL_RR(BigInteger.valueOf(seed.longValue()));
    }

    public PCG_XSL_RR(BigInteger seed) {
        this.state = Util.new128State(BigInteger.valueOf(0)).add(seed.and(MASK_128)); // Ensure initial state is 128-bit
         newState();
    }

    @Override
    public void newState() {
        this.state = Util.new128State(this.state).and(MASK_128); // Ensure new state is 128-bit
    }

    @Override
    public void skipLong(long ulong) {
        this.state = Util.skip128(this.state, ulong).and(MASK_128); // Ensure skipped state is 128-bit
    }

    @Override
    public void skip(int uint) {
        this.skipLong((long) uint);
    }

    @Override
    public long nextLong() {
        // Extract lower and upper 64 bits
        long lower64 = this.state.and(MASK_64).longValue();
        long upper64 = this.state.shiftRight(64).and(MASK_64).longValue();
        // XOR the upper and lower parts
        long shiftedLong = lower64 ^ upper64;
        int rotationDistance = this.state.shiftRight(122).intValue();
        newState();
        // return permutation
        return Long.rotateRight(shiftedLong, rotationDistance);
    }
}
