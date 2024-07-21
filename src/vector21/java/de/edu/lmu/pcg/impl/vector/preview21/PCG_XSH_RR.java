package de.edu.lmu.pcg.impl.vector.preview21;

import de.edu.lmu.pcg.PCGImplementationVariant;
import de.edu.lmu.pcg.PCGNative;
import de.edu.lmu.pcg.services.PCGCtorService;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorOperators;

import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static de.edu.lmu.pcg.Util.newLongState;
import static de.edu.lmu.pcg.impl.vector.preview21.Util.*;

@SuppressWarnings("preview")
public class PCG_XSH_RR extends de.edu.lmu.pcg.PCG_XSH_RR implements PCGVector21.U32 {
    public static class CtorService implements PCGCtorService.SeedU64<PCG_XSH_RR>,
            PCGVector21.Marker<PCG_XSH_RR, Long> {
        @Override
        public PCG_XSH_RR create(long seed) {
            return new PCG_XSH_RR(seed);
        }
    }

    public PCG_XSH_RR(long seed) {
        super(seed);
    }

    @Override
    public void fillSegment(MemorySegment memorySegment, ByteOrder order) {
        variantU32X2_EQ_X65_LANE_SIZE(memorySegment, order);
    }

    /**
     * here I am making an important assumption that may not be true for all platforms
     * namely that a lane of u32 can keep twice as much as a lane of u64
     * this can probably be solved by static dispatching to the correct method
     */

    private void variantU32X2_EQ_X65_LANE_SIZE(MemorySegment into, ByteOrder order) {
        long alignment = into.address() % LONG_SIZE;
        long max = (into.byteSize() - alignment) / INT_SIZE * INT_SIZE + alignment;

        if (alignment != 0) {
            PCG_XSH_RR.super.fill(into.asSlice(0, alignment).asByteBuffer());
        }

        /*this is placed into its own scope so that we do not forget to save state into this.state*/
        {
            var internalStateArr = new long[LONG_COUNT * 2]; //here we need 2 lanes because we have to rotate on u32 and that's twice as compact as u64
            var state = this.state;
            for (long segment = alignment; segment < max; segment += INT_SIZE) {


                //because there is internal dependency in the state calculation, we need to calculate unvectorized
                //we need to displace the first element calculation to the end because we start this method with the state already updated
                internalStateArr[0] = state;
                for (int i = 1; i < internalStateArr.length; i++) {
                    state = newLongState(state);
                    internalStateArr[i] = state;
                }
                state = newLongState(state);

                /*
                    int shiftedInt = (int) ((this.state ^ (this.state >>> 18)) >>> 27);
                    int rotationDistance = (int) (this.state >>> 59);
                    newState();
                    return Integer.rotateRight(shiftedInt, rotationDistance);
                 */

                //now we vectorize the output function
                var internalState1 = LONG_SPECIES.fromArray(internalStateArr, 0);
                var internalState2 = LONG_SPECIES.fromArray(internalStateArr, LONG_COUNT);
                var shiftedInt = internalState1.lanewise(VectorOperators.XOR, internalState1.lanewise(VectorOperators.LSHR, 18))
                        .lanewise(VectorOperators.LSHR, 27)
                        .convert(VectorOperators.L2I, 0);
                shiftedInt = shiftedInt.blend(
                        internalState2.lanewise(VectorOperators.XOR, internalState2.lanewise(VectorOperators.LSHR, 18))
                                .lanewise(VectorOperators.LSHR, 27)
                                .convert(VectorOperators.L2I, -1)
                        , INT_SPECIES.indexInRange(LONG_COUNT, LONG_COUNT));

                var rotationDistance = internalState1
                        .lanewise(VectorOperators.LSHR, 59)
                        .convert(VectorOperators.L2I, 0)
                        .blend(internalState2
                                        .lanewise(VectorOperators.LSHR, 59)
                                        .convert(VectorOperators.L2I, -1)
                                , INT_SPECIES.indexInRange(LONG_COUNT, LONG_COUNT));
                //problem is converting will leave half as 0 maybe castShape will fix it but has cost
                //or we use parts and then or mask them together
                var result = shiftedInt.lanewise(VectorOperators.ROR, rotationDistance);


                result.intoMemorySegment(into, segment, order);
            }
            this.state = state;
        }

        //do post alignment processing
        if (max != into.byteSize()) {
            PCG_XSH_RR.super.fill(into.asSlice(max, into.byteSize() - max).asByteBuffer());
        }
    }
}
