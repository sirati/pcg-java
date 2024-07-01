package de.edu.lmu.pcg;
import de.edu.lmu.pcg.services.PCGCtorService;

import java.math.BigInteger;

public class PCG_XSL_RR implements PCGLong {
    public static class CtorService implements PCGCtorService<PCG_XSL_RR, BigInteger> {
        @Override
        public PCG_XSL_RR create(BigInteger seed) {
            return new PCG_XSL_RR(seed);
        }
    }
    private static final BigInteger MASK_128 = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);
    private static final BigInteger MASK_64 = new BigInteger("FFFFFFFFFFFFFFFF", 16);
    private BigInteger state;



    public static PCG_XSL_RR createFromNumber(Number seed) {
        return new PCG_XSL_RR(BigInteger.valueOf(seed.longValue()));
    }

    public PCG_XSL_RR(BigInteger seed) {
        this.state = seed.and(MASK_128); // Ensure initial state is 128-bit
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
