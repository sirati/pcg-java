package de.edu.lmu.pcg;

import de.edu.lmu.pcg.services.PCGCtorService;

import java.util.Map;

import static de.edu.lmu.pcg.services.PCGCtorService.AVAILABLE_PCGS;

public class PCGBuilder<T_PCG extends PCG & SeedMarker<T_Seed>, T_Seed extends Number> {
    private final static Map<Class<? extends PCG>, PCGCtorService.PCGCtorServiceDescriptor<?, ?>> ctors = AVAILABLE_PCGS;

    @SuppressWarnings("unchecked")
    private <T_PCG_NEW extends PCG & SeedMarker<T_Seed_NEW>, T_Seed_NEW extends Number, T_Builder extends PCGBuilder<T_PCG_NEW, T_Seed_NEW>> T_Builder magic() {
        return (T_Builder) this;
    }

    private PCGCtorService.PCGCtorServiceDescriptor<T_PCG, T_Seed> descriptor;
    private T_Seed seed;

    //technically as it is now this could just be the constructor and everything would be fine as well
    public <T_PCG_NEW extends PCG & SeedMarker<T_Seed_NEW>, T_Seed_NEW extends Number> PCGBuilder<T_PCG_NEW, T_Seed_NEW>.StateConfigured type(Class<T_PCG_NEW> cls_PCG) {
        PCGBuilder<T_PCG_NEW, T_Seed_NEW> result = magic();
        result.descriptor = (PCGCtorService.PCGCtorServiceDescriptor<T_PCG_NEW, T_Seed_NEW>) ctors.get(cls_PCG);
        if (this.descriptor == null) {
            throw new RuntimeException("No ctor service found for class " + cls_PCG + ".\n\tavailable: \n\t\t"
                    + ctors.keySet().stream().map(Class::getName).reduce((a, b) -> a + ", " + b).orElse("none"));
        }
        return result.new StateConfigured();
    }

    public class StateConfigured {
        public  PCGBuilder<T_PCG, T_Seed>.StateConfigured seed(T_Seed seed) {
            PCGBuilder.this.seed = seed;
            return this;
        }
        public T_PCG build() {
            return descriptor.service().create(seed);
        }
    }
}
