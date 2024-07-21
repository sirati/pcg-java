package de.edu.lmu.pcg.impl.vector.preview21;
import jdk.incubator.vector.*;
import de.edu.lmu.pcg.services.PCGCtorService;

import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static de.edu.lmu.pcg.Util.*;
import static de.edu.lmu.pcg.impl.vector.preview21.Util.*;

@SuppressWarnings("preview")
public class PCG_XSH_RS extends de.edu.lmu.pcg.PCG_XSH_RS implements PCGVector21.U32 {
    public static class CtorService implements PCGCtorService.SeedU64<PCG_XSH_RS>,
            Marker<PCG_XSH_RS, Long> {
        @Override
        public PCG_XSH_RS create(long seed) {
            return new PCG_XSH_RS(seed);
        }
    }

    public PCG_XSH_RS(long seed) {
        super(seed);
    }


    @Override
    public void fillSegment(MemorySegment into, ByteOrder order) {
        variantFast(into, order);
    }

    private void variantFast(MemorySegment into, ByteOrder order) {
        final long LOOP_SIZE = INT_SIZE / 2; //todo: calculate this

        long alignment = into.address() % LOOP_SIZE;
        long start = (LOOP_SIZE - alignment) % LOOP_SIZE;//todo fix alignment in all other methods
        long max = (into.byteSize() - alignment) / LOOP_SIZE * LOOP_SIZE + alignment;

        if (alignment != 0) {
            PCG_XSH_RS.super.fill(into.asSlice(0, start).asByteBuffer());
        }

        /*this is placed into its own scope so that we do not forget to save state into this.state*/
        {
            var internalStateArr = new long[LONG_COUNT];
            var state = this.state;
            for (long segment = start; segment < max; segment += LOOP_SIZE) {


                //because there is internal dependency in the state calculation, we need to calculate unvectorized
                //we need to displace the first element calculation to the end because we start this method with the state already updated
                internalStateArr[0] = state;
                for (int i = 1; i < internalStateArr.length; i++) {
                    state = newLongState(state);
                    internalStateArr[i] = state;
                }
                state = newLongState(state);

                //now we vectorize the output function
                var internalState = LONG_SPECIES.fromArray(internalStateArr, 0);
                var afterXorshift = internalState.lanewise(VectorOperators.XOR, internalState.lanewise(VectorOperators.LSHR, 22));
                var randomshift = afterXorshift.lanewise(VectorOperators.LSHR, 61)
                        .lanewise(VectorOperators.ADD, 22);
                //problem is converting will leave half as 0 maybe castShape will fix it but has cost
                //or we use parts and then or mask them together
                var result = afterXorshift.lanewise(VectorOperators.LSHR, randomshift).convert(VectorOperators.L2I, 0);



                result.intoMemorySegment(into, segment, order, INT_SPECIES.indexInRange(0, LONG_COUNT));
            }
            this.state = state;
        }

        //do post alignment processing
        if (max != into.byteSize()) {
            PCG_XSH_RS.super.fill(into.asSlice(max, into.byteSize() - max).asByteBuffer());
        }
    }

    private void variantNaiveSlow(MemorySegment into, ByteOrder order) {
        long alignment = into.address() % LONG_SIZE;
        long max = (into.byteSize() - alignment) / INT_SIZE * INT_SIZE + alignment;

        if (alignment != 0) {
            PCG_XSH_RS.super.fill(into.asSlice(0, alignment).asByteBuffer());
        }

        /*this is placed into its own scope so that we do not forget to save state into this.state*/
        {
            var internalStateArr = new long[LONG_COUNT];
            var state = this.state;
            for (long segment = alignment; segment < max; segment += INT_SIZE) {

                Vector<Integer> result = null;
                for (int part = 0; part < INT_COUNT/LONG_COUNT; part++) {

                    //because there is internal dependency in the state calculation, we need to calculate unvectorized
                    //we need to displace the first element calculation to the end because we start this method with the state already updated
                    internalStateArr[0] = state;
                    for (int i = 1; i < LONG_COUNT; i++) {
                        state = newLongState(state);
                        internalStateArr[i] = state;
                    }
                    state = newLongState(state);

                    //now we vectorize the output function
                    var internalState = LONG_SPECIES.fromArray(internalStateArr, 0);
                    var afterXorshift = internalState.lanewise(VectorOperators.XOR, internalState.lanewise(VectorOperators.LSHR, 22));
                    var randomshift = afterXorshift.lanewise(VectorOperators.LSHR, 61)
                            .lanewise(VectorOperators.ADD, 22);
                    //problem is converting will leave half as 0 maybe castShape will fix it but has cost
                    //or we use parts and then or mask them together
                    var result_part = afterXorshift.lanewise(VectorOperators.LSHR, randomshift).convert(VectorOperators.L2I, -part);
                    if (part == 0) {
                        result = result_part;
                    } else {
                        //this ought to be blend() not OR / XOR!
                        result = result.lanewise(VectorOperators.OR, result_part);
                    }
                }

                result.intoMemorySegment(into, segment, order);
            }
            this.state = state;
        }

        //do post alignment processing
        if (max != into.byteSize()) {
            PCG_XSH_RS.super.fill(into.asSlice(max, into.byteSize() - max).asByteBuffer());
        }
    }
}
