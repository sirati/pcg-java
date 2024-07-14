package de.edu.lmu.pcg.test;

import de.edu.lmu.pcg.PCG;
import de.edu.lmu.pcg.PCGBuilder;
import de.edu.lmu.pcg.U128;
import de.edu.lmu.pcg.services.PCGCtorService;

import java.util.stream.Stream;

public class Util {
    public static Stream<TestConstructor<?, U128>> rngCtorProvider() {
        return PCGCtorService.AVAILABLE_PCGS.values().stream()
                .map(desc -> new PCGBuilder<>().type(desc.cls_PCG()))
                .map(builder -> (TestConstructor<PCG, U128>) number
                        -> builder.seedFromU128(number).build());
    }
}
