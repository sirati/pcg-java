package de.edu.lmu.pcg;

import de.edu.lmu.pcg.services.PCGCtorService;

public class PCG_RXS_M_XS_32 implements PCGInt {
    public static class CtorService implements PCGCtorService<PCG_RXS_M_XS_32, Integer> {
        @Override
        public PCG_RXS_M_XS_32 create(Integer seed) {
            return new PCG_RXS_M_XS_32(seed);
        }
    }

    private static final int MCG_MULTIPLIER = 277803737;

    private int state;

    public PCG_RXS_M_XS_32(int seed) { this.state = seed; }

    @Override
    public void newState() {
        this.state = Util.newIntState(this.state);
    }

    @Override
    public void skipLong(long ulong) {
        this.state = Util.skip(this.state, (int) ulong);
    }

    @Override
    public void skip(int uint) {
        this.skipLong((long) uint);
    }

    @Override
    public int nextInt() {
        int mask = (1 << 4) - 1;
        int upper4Bits = (this.state >> (32-4) ) & mask;

        // permutation
        int xorshifted = this.state ^ (this.state >> 4+upper4Bits);
        int multiplied = xorshifted * MCG_MULTIPLIER;
        newState();
        return multiplied ^ (multiplied >> 22);

    }
}
