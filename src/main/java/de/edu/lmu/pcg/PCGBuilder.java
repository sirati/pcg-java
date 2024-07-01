package de.edu.lmu.pcg;

import de.edu.lmu.pcg.services.PCGCtorService;

import java.util.Map;

public class PCGBuilder<T_PCG extends PCG, T_Seed extends Number, T_Builder extends PCGBuilder<T_PCG, T_Seed, T_Builder>> {
    //singleton
    private final static Map<Class<? extends PCG>, PCGCtorService.PCGCtorServiceDescriptor<?, ?>> ctors = PCGCtorService.load_services();

    private <T_PCG extends PCG, T_Seed extends Number, T_Builder extends PCGBuilder<T_PCG, T_Seed, T_Builder>> T_Builder magic() {
        return (T_Builder) this;
    }

    private PCGCtorService.PCGCtorServiceDescriptor<T_PCG, T_Seed> descriptor;
    private T_Seed seed;
    public <T_PCG extends PCG> PCGBuilder<T_PCG, T_Seed, ?>.Step1 type(Class<T_PCG> cls_PCG) {
        PCGBuilder<T_PCG, T_Seed, ?> result = magic();
        result.descriptor = (PCGCtorService.PCGCtorServiceDescriptor<T_PCG, T_Seed>) ctors.get(cls_PCG);
        if (this.descriptor == null) {
            throw new RuntimeException("No ctor service found for class " + cls_PCG + ".\n\tavailable: \n\t\t"
                    + ctors.keySet().stream().map(Class::getName).reduce((a, b) -> a + ", " + b).orElse("none"));
        }
        return result.new Step1();
    }

    public class Step1 {
        public <T_Seed extends Number> PCGBuilder<T_PCG, T_Seed, ?>.Step2 seed(T_Seed seed) {
            PCGBuilder<T_PCG, T_Seed, ?> result = magic();
            result.seed = seed;
            return result.new Step2();
        }
    }

    public class Step2 {
        public T_PCG build() {
            return descriptor.service().create(seed);
        }
    }
}
