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

    protected long stateLower;
    protected long stateUpper;



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
        this.stateLower = seedLower;
        this.stateUpper = seedUpper;
        //todo SEED init is not done!!!! need to do newState etc!
    }


    @Override
    public void newState() {
        long lower = this.stateLower * Util.u128MultiplierLow;
        long upper = Math.unsignedMultiplyHigh(this.stateLower , Util.u128MultiplierLow)
                + this.stateLower * Util.u128MultiplierHigh
                + this.stateUpper * Util.u128MultiplierLow;

        this.stateLower = lower + Util.u128IncrementLow;
        long carry = (lower & 0xFFFFFFFFL) + (Util.u128IncrementLow & 0xFFFFFFFFL) >>> 32;
        carry = ((lower >>> 32) + (Util.u128IncrementLow >>> 32) + carry) >>> 32;
        this.stateUpper = upper + Util.u128IncrementHigh + carry;
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
        // XOR the upper and lower parts
        long shiftedLong = this.stateLower ^ this.stateUpper;
        int rotationDistance = (int) (this.stateLower >>> 58); //== all >> 122  this.state.shiftRight(122).intValue();
        newState();
        // return permutation
        return Long.rotateRight(shiftedLong, rotationDistance);
    }
}
