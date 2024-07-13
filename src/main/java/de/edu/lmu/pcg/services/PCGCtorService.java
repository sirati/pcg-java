package de.edu.lmu.pcg.services;

import de.edu.lmu.pcg.PCG;
import de.edu.lmu.pcg.SeedMarker;
import de.edu.lmu.pcg.U128;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * This interface must be implemented, not inherit, only exactly one 'create' method directly
 */
public sealed interface PCGCtorService
        <T extends PCG & SeedMarker<Seed>, Seed extends Number>
        permits PCGCtorService.SeedU32, PCGCtorService.SeedU64, PCGCtorService.SeedU128, PCGCtorService.SeedCustom
{
    T create(Seed seed);
    //not needed as long at the contract that Ctor must directly impl ONE method with parameter type == Seed is kept
    //Class<Seed> getSeedClass();
    T create(U128 seed);


    record PCGCtorServiceDescriptor<T extends PCG & SeedMarker<Seed>, Seed extends Number>
            (Class<T> cls_PCG, Class<Seed> cls_Seed, PCGCtorService<T, Seed> service) {

        static <T extends PCG & SeedMarker<Seed>, Seed extends Number> PCGCtorServiceDescriptor<T, Seed>
        create(PCGCtorService<T, Seed> service, Class<T> cls_PCG, Class<Seed> cls_Seed) {
            return new PCGCtorServiceDescriptor<>(cls_PCG, cls_Seed, service);
        }

        public String getName() {
            return cls_PCG.getSimpleName();
        }
    }

    private static Map<Class<? extends PCG>, PCGCtorServiceDescriptor<?, ?>> load_services() {
        //we load all registered PCG which is located in META-INF/services/de.edu.lmu.pcg.services.PCGCtorService
        var serviceLoader = ServiceLoader.load(PCGCtorService.class);
        var result = new java.util.HashMap<Class<? extends PCG>, PCGCtorServiceDescriptor<?, ?>>();
        for (var pcg_ctor : serviceLoader) {
            //get method create of impl
            var opt_method_create = Arrays.stream(pcg_ctor.getClass().getMethods())
                    .filter(method -> method.getName().equals("create")
                            && method.getParameterCount() == 1
                            && method.getDeclaringClass().equals(pcg_ctor.getClass()))
                    .findAny();
            if (opt_method_create.isEmpty()) {
                throw new RuntimeException("Service must directly impl create method, not inherit it!");
            }
            var method_create = opt_method_create.get();
            var cls_PCG = method_create.getReturnType();
            var cls_Seed = method_create.getParameterTypes()[0];
            //we need to cast to ungeneric here due to java reflection limitations, that do not support generics
            //but still return a class<?>
            //noinspection unchecked,rawtypes
            var desc = PCGCtorServiceDescriptor.create(pcg_ctor, (Class)cls_PCG, (Class)cls_Seed);
            result.put(desc.cls_PCG, desc);
        }
        return result;
    }

    public Map<Class<? extends PCG>, PCGCtorService.PCGCtorServiceDescriptor<?, ?>> AVAILABLE_PCGS = PCGCtorService.load_services();

    //<editor-fold defaultstate="collapsed" desc="Just writing type to primitive adapters for generic impl">
    /**
     * {@inheritDoc}
     */
    non-sealed interface SeedU32<T extends PCG & SeedMarker<Integer>> extends PCGCtorService<T, Integer> {
        T create(int seed);

        @Override
        default T create(Integer seed) {
            return create(seed.intValue());
        }

        @Override
        default T create(U128 seed) {
            return create(seed.intValue());
        }

        /*@Override
        default Class<Integer> getSeedClass() {
            return int.class;
        }*/
    }
    /**
     * {@inheritDoc}
     */
    non-sealed interface SeedU64<T extends PCG & SeedMarker<Long>> extends PCGCtorService<T, Long> {
        T create(long seed);

        @Override
        default T create(Long seed) {
            return create(seed.longValue());
        }

        @Override
        default T create(U128 seed) {
            return create(seed.longValue());
        }

        /*@Override
        default Class<Long> getSeedClass() {
            return long.class;
        }*/
    }
    /**
     * {@inheritDoc}
     */
    non-sealed interface SeedU128<T extends PCG & SeedMarker<U128>> extends PCGCtorService<T, U128> {
        /*@Override
        default Class<U128> getSeedClass() {
            return U128.class;
        }*/
    }

    non-sealed interface SeedCustom<T extends PCG & SeedMarker<Seed>, Seed extends Number> extends PCGCtorService<T, Seed> {}
    // </editor-fold>
}
