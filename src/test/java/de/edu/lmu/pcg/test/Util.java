package de.edu.lmu.pcg.test;

import de.edu.lmu.pcg.PCG;
import de.edu.lmu.pcg.PCGBuilder;
import de.edu.lmu.pcg.services.PCGCtorService;

import java.util.stream.Stream;

public class Util {
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Stream<TestConstructor<?, ?>> rngCtorProvider() {
        return PCGCtorService.AVAILABLE_PCGS.values().stream()
                .map(desc -> new PCGBuilder<>().type(desc.cls_PCG()))
                .map(builder -> (TestConstructor<PCG, Number>) number
                        -> ((PCGBuilder.StateConfigured) builder).seed(number).build());
    }
}
