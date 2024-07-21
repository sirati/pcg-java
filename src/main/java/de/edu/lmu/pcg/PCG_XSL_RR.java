package de.edu.lmu.pcg;
import de.edu.lmu.pcg.services.PCGCtorService;

import java.math.BigInteger;

import static de.edu.lmu.pcg.Util.MASK_64;
import static de.edu.lmu.pcg.Util.MASK_128;

public class PCG_XSL_RR implements PCGLong, SeedTypeMarker<U128> {
    public static class CtorService implements PCGCtorService.SeedU128<PCG_XSL_RR> {
        @Override
        public PCG_XSL_RR create(U128 seed) {
            return new PCG_XSL_RR(seed);
        }
    }

    protected long stateLower = Util.u128IncrementLow;
    protected long stateUpper = Util.u128IncrementHigh;



    public static PCG_XSL_RR createFromNumber(Number seed) {
        return new PCG_XSL_RR(BigInteger.valueOf(seed.longValue()));
    }

    public PCG_XSL_RR(BigInteger seed) {
        this(seed.and(MASK_64).longValue(), seed.shiftRight(64).and(MASK_64).longValue());
    }

    public PCG_XSL_RR(U128 seed) {
        this(seed.lo, seed.hi);
    }


    public PCG_XSL_RR(long seedLower, long seedUpper) {
        increment(seedUpper, seedLower);
        newState();
    }
    
    private void increment(long incrementHigh, long incrementLow) {
        long resultLower = this.stateLower + incrementLow;
        long carry = (this.stateLower & 0xFFFFFFFFL) + (incrementLow & 0xFFFFFFFFL) >>> 32;
        carry = ((this.stateLower >>> 32) + (incrementLow >>> 32) + carry) >>> 32;
        this.stateUpper = this.stateUpper + incrementHigh + carry;
        this.stateLower = resultLower;
    }



    private void multiply(long multiplierHigh, long multiplierLow) {
        long resultLower = this.stateLower * multiplierLow;
        this.stateUpper = Math.unsignedMultiplyHigh(this.stateLower , multiplierLow)
                + this.stateLower * multiplierHigh
                + this.stateUpper * multiplierLow;
        this.stateLower = resultLower;
    }


    @Override
    public void newState() {
        multiply(Util.u128MultiplierHigh, Util.u128MultiplierLow);
        increment(Util.u128IncrementHigh, Util.u128IncrementLow);
    }

    @Override
    public void skipLong(long ulong) {
        //todo this.state = Util.skip128(this.state, ulong).and(MASK_128); // Ensure skipped state is 128-bit
    }

    @Override
    public void skip(int uint) {
        this.skipLong((long) uint);
    }

    @Override
    public long nextLong() {
        // XOR the upper and lower parts
        long shiftedLong = this.stateLower ^ this.stateUpper;
        int rotationDistance = (int) (this.stateLower >>> 58); //== all >> 122  this.state.shiftRight(122).intValue();
        newState();
        // return permutation
        return Long.rotateRight(shiftedLong, rotationDistance);
    }
}
