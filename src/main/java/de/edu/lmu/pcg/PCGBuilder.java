package de.edu.lmu.pcg;

import de.edu.lmu.pcg.services.PCGCtorService;

import java.util.HashMap;
import java.util.Map;

import static de.edu.lmu.pcg.services.PCGCtorService.AVAILABLE_PCGS_PRIO;

public class PCGBuilder<T_PCG extends PCG & SeedTypeMarker<T_Seed>, T_Seed extends Number> {
    private final static Map<Class<? extends PCG>, PCGImplementationVariant.PrioMap> ctors_by_class = AVAILABLE_PCGS_PRIO;
    private final static Map<String, PCGImplementationVariant.PrioMap> ctors_by_name;

    static {
        ctors_by_name = ctors_by_class.values().stream().collect(
                HashMap::new,
                (map, desc) -> map.put(desc.get(PCGImplementationVariant.JavaPrimitive).getName(), desc),
                Map::putAll
        );
    }


    @SuppressWarnings("unchecked")
    private static <T_PCG extends PCG & SeedTypeMarker<T_Seed>, T_Seed extends Number>
    PCGCtorService.PCGCtorServiceDescriptor<T_PCG, T_Seed> find(Class<T_PCG> cls_PCG, PCGImplementationVariant preferred_variant) {
        return (PCGCtorService.PCGCtorServiceDescriptor<T_PCG, T_Seed>) ctors_by_class.get(cls_PCG).get(preferred_variant);
    }

    @SuppressWarnings("unchecked")
    private static <T_PCG extends PCG & SeedTypeMarker<T_Seed>, T_Seed extends Number>
    PCGCtorService.PCGCtorServiceDescriptor<T_PCG, T_Seed> find(String pcg_name, PCGImplementationVariant preferred_variant) {
        return (PCGCtorService.PCGCtorServiceDescriptor<T_PCG, T_Seed>) ctors_by_name.get(pcg_name).get(preferred_variant);
    }
    @SuppressWarnings("unchecked")
    private <T_PCG_NEW extends PCG & SeedTypeMarker<T_Seed_NEW>, T_Seed_NEW extends Number, T_Builder extends PCGBuilder<T_PCG_NEW, T_Seed_NEW>> T_Builder magic() {
        return (T_Builder) this;
    }

    private PCGCtorService.PCGCtorServiceDescriptor<T_PCG, T_Seed> descriptor;
    private T_Seed seed;
    private PCGImplementationVariant preferred_variant = null;

    //technically as it is now this could just be the constructor and everything would be fine as well
    public <T_PCG_NEW extends PCG & SeedTypeMarker<T_Seed_NEW>, T_Seed_NEW extends Number>
    PCGBuilder<T_PCG_NEW, T_Seed_NEW>.StateConfigured type(Class<T_PCG_NEW> cls_PCG) {
        PCGBuilder<T_PCG_NEW, T_Seed_NEW> result = magic();
        result.descriptor = find(cls_PCG, preferred_variant);
        if (this.descriptor == null) {
            throw new RuntimeException("No ctor service found for class " + cls_PCG + ".\n\tavailable: \n\t\t"
                    + ctors_by_class.keySet().stream().map(Class::getName).reduce((a, b) -> a + ", " + b).orElse("none"));
        }
        return result.new StateConfigured();
    }

    //this however would not
    public <T_PCG_NEW extends PCG & SeedTypeMarker<T_Seed_NEW>, T_Seed_NEW extends Number>
    PCGBuilder<T_PCG_NEW, T_Seed_NEW>.StateConfigured type(String pcg_name) {
        PCGBuilder<T_PCG_NEW, T_Seed_NEW> result = magic();
        result.descriptor = find(pcg_name, preferred_variant);
        if (this.descriptor == null) {
            throw new RuntimeException("No ctor service found for class " + pcg_name + ".\n\tavailable: \n\t\t"
                    + ctors_by_class.keySet().stream().map(Class::getName).reduce((a, b) -> a + ", " + b).orElse("none"));
        }
        return result.new StateConfigured();
    }

    public PCGBuilder<T_PCG, T_Seed> preferred_variant(PCGImplementationVariant variant) {
        this.preferred_variant = variant;
        return this;
    }

    public class StateConfigured {
        public  PCGBuilder<T_PCG, T_Seed>.StateConfigured seed(T_Seed seed) {
            PCGBuilder.this.seed = seed;
            return this;
        }


        public PCGBuilder<T_PCG, T_Seed>.StateConfigured preferred_variant(PCGImplementationVariant variant) {
            preferred_variant = variant;
            //we need to update our descriptor, as it might be different now
            descriptor = find(descriptor.cls_PCG(), preferred_variant);
            return this;
        }

        public  PCGBuilder<T_PCG, T_Seed>.StateConfigured seedFromU128(U128 seed) {
            var ma = magic();
            ma.seed = seed;
            return this;
        }

        public T_PCG build() {
            if (descriptor.cls_Seed() == seed.getClass()
                    || (descriptor.cls_Seed().isPrimitive() && seed.getClass() == Util.toWrapperClass(descriptor.cls_Seed()))) {
                return descriptor.service().create(seed);
            } else if (seed.getClass() == U128.class) {
                return descriptor.service().create((U128) seed);
            } else {
                throw new RuntimeException("Seed type mismatch: " + seed.getClass().getName() + " != " + descriptor.cls_Seed().getName());
            }
        }
    }
}
