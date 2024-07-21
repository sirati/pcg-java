package de.edu.lmu.pcg;

import de.edu.lmu.pcg.services.PCGCtorService;

// 64 bit state to 32 bit output
public class PCG_XSH_RR implements PCGInt, SeedTypeMarker<Long> {
    public static class CtorService implements PCGCtorService.SeedU64<PCG_XSH_RR> {
        @Override
        public PCG_XSH_RR create(long seed) {
            return new PCG_XSH_RR(seed);
        }
    }

    private long state;

    public static PCG_XSH_RR createFromNumber(Number seed) {
        return new PCG_XSH_RR(seed.longValue());
    }

    public PCG_XSH_RR(long seed) {
        this.state = Util.newLongState(0) + seed;
        newState();
    }

    @Override
    public void newState() {
        this.state = Util.newLongState(this.state);
    }

    @Override
    public void skipLong(long ulong) {
        this.state = Util.skipLong(this.state, ulong);
    }

    @Override
    public void skip(int uint) {
        this.skipLong((long) uint);
    }

    @Override
    public int nextInt() {
        // permutation
        int shiftedInt = (int) ((this.state ^ (this.state >>> 18)) >>> 27);
        int rotationDistance = (int) (this.state >>> 59);
        newState();
        return Integer.rotateRight(shiftedInt, rotationDistance);
    }
}
