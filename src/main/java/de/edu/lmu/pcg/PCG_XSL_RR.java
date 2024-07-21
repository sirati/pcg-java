package de.edu.lmu.pcg;
import de.edu.lmu.pcg.services.PCGCtorService;

import java.math.BigInteger;

import static de.edu.lmu.pcg.Util.MASK_64;

public class PCG_XSL_RR implements PCGLong, SeedTypeMarker<U128> {
    public static class CtorService implements PCGCtorService.SeedU128<PCG_XSL_RR> {
        @Override
        public PCG_XSL_RR create(U128 seed) {
            return new PCG_XSL_RR(seed);
        }
    }




    public static PCG_XSL_RR createFromNumber(Number seed) {
        return new PCG_XSL_RR(BigInteger.valueOf(seed.longValue()));
    }

    public PCG_XSL_RR(BigInteger seed) {
        this(seed.and(MASK_64).longValue(), seed.shiftRight(64).and(MASK_64).longValue());
    }

    public PCG_XSL_RR(U128 seed) {
        this(seed.lo, seed.hi);
    }

    protected long stateLower = Util.u128IncrementLow;
    protected long stateUpper = Util.u128IncrementHigh;
    public PCG_XSL_RR(long seedLower, long seedUpper) {
        add(seedUpper, seedLower);
        newState();
    }

    private void add(long incrementHigh, long incrementLow) {
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
        add(Util.u128IncrementHigh, Util.u128IncrementLow);
    }

    @Override
    public void skipLong(long ulong) {
        var result = Util.skip128(new U128(this.stateUpper, this.stateLower), ulong);
        this.stateLower = result.lo;
        this.stateUpper = result.hi;
    }

    @Override
    public void skip(int uint) {
        this.skipLong((long) uint);
    }

    @Override
    public long nextLong() {
        //System.out.println(new U128(this.stateUpper, this.stateLower).toHexString());
        // XOR the upper and lower parts
        long shiftedLong = this.stateLower ^ this.stateUpper;
        int rotationDistance = (int) (this.stateUpper >>> 58); //== all >> 122  this.state.shiftRight(122).intValue();
        newState();
        // return permutation
        return Long.rotateRight(shiftedLong, rotationDistance);
    }
}
