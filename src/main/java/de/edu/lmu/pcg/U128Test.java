package de.edu.lmu.pcg;

import java.math.BigInteger;
import java.util.Random;

import static de.edu.lmu.pcg.Util.MASK_64;

public class U128Test {

    long stateLower;
    long stateUpper;

    U128Test(long lower, long upper) {
        this.stateLower = lower;
        this.stateUpper = upper;
    }


    public void newState() {
        long lower = this.stateLower * Util.u128MultiplierLow;
        long upper = Math.unsignedMultiplyHigh(this.stateLower , Util.u128MultiplierLow)
                + this.stateLower * Util.u128MultiplierHigh
                + this.stateUpper * Util.u128MultiplierLow;

        this.stateLower = lower + Util.u128IncrementLow;
        long carry = (lower & 0xFFFFFFFFL) + (Util.u128IncrementLow & 0xFFFFFFFFL) >>> 32;
        carry = ((lower >>> 32) + (Util.u128IncrementLow >>> 32) + carry) >>> 32;
        this.stateUpper = upper + Util.u128IncrementHigh + carry;

       /* this.stateLower = lower;
        this.stateUpper = upper;*/
    }


    public static void main(String[] args) {
        Random random = new Random();

        for (long i = 0; i < 1024L*1024*1024*16; i++) {
            long stateLower = random.nextLong();
            long stateUpper = random.nextLong();

            U128Test state = new U128Test(stateLower, stateUpper);

            state.newState();
            long lowerResult = state.stateLower;
            long upperResult = state.stateUpper;

            BigInteger stateBig = new U128(stateUpper, stateLower).toBigInteger();

            BigInteger result = stateBig
                    .multiply(Util.u128Multiplier)
                    .and(Util.MASK_128)
                    .add(Util.u128Increment)
                    .and(Util.MASK_128);


            long lowerResult2 = result.and(MASK_64).longValue();
            long upperResult2 = result.shiftRight(64).and(MASK_64).longValue();

            if (lowerResult != lowerResult2 || upperResult != upperResult2) {
                System.err.println("Mismatch at iteration " + i);
                System.err.println("Expected: " + Long.toString(lowerResult2,  16) + " " + Long.toString(upperResult2, 16));
                System.err.println("Actual  : " + Long.toString(lowerResult, 16) + " " + Long.toString(upperResult, 16));
            }
        }

        System.out.println("All tests passed.");
    }
}

