package de.edu.lmu.pcg.services;

import de.edu.lmu.pcg.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * This interface must be implemented, not inherit, only exactly one 'create' method directly
 */
public sealed interface PCGCtorService
        <T extends PCG & SeedTypeMarker<Seed>, Seed extends Number>
        permits PCGCtorService.NativeProvidedImpl, PCGCtorService.SeedCustom, PCGCtorService.SeedU128, PCGCtorService.SeedU32, PCGCtorService.SeedU64, PCGCtorService.Vectorized {
    T create(Seed seed);

    Class<Seed> getSeedClass();

    T create(U128 seed);


    default PCGImplementationVariant getImplementationVariant() {
        return PCGImplementationVariant.JavaPrimitive;
    }

    record PCGCtorServiceDescriptor<T extends PCG & SeedTypeMarker<Seed>, Seed extends Number>
            (Class<T> cls_PCG, Class<Seed> cls_Seed, PCGCtorService<T, Seed> service) {

        static <T extends PCG & SeedTypeMarker<Seed>, Seed extends Number> PCGCtorServiceDescriptor<T, Seed>
        create(PCGCtorService<T, Seed> service, Class<T> cls_PCG, Class<Seed> cls_Seed) {
            return new PCGCtorServiceDescriptor<>(cls_PCG, cls_Seed, service);
        }

        public String getName() {
            return cls_PCG.getSimpleName();
        }
    }

    private static Map<Class<? extends PCG>, PCGImplementationVariant.PrioMap> load_services() {

        ClassLoader service_class_loader;
        try {
            service_class_loader = JDKRequirement.load_version_specific();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        //we load all registered PCG which is located in META-INF/services/de.edu.lmu.pcg.services.PCGCtorService
        var serviceLoader = ServiceLoader.load(PCGCtorService.class, service_class_loader);


        var result = new java.util.HashMap<Class<? extends PCG>, PCGImplementationVariant.PrioMap>();
        for (var pcg_ctor : serviceLoader) {
            //get method create of impl
            var opt_method_create = Arrays.stream(pcg_ctor.getClass().getMethods())
                    .filter(method -> method.getName().equals("create")
                            && method.getParameterCount() == 1
                            && !method.getReturnType().isInterface()
                            && method.getDeclaringClass().equals(pcg_ctor.getClass()))
                    .toList();
            if (opt_method_create.isEmpty()) {
                throw new RuntimeException("Service must directly impl create method, not inherit it!");
            }
            if (opt_method_create.size() > 1) {
                throw new RuntimeException("Service must directly impl only one create method, not inherit it!");
            }
            var method_create = opt_method_create.getFirst();
            var cls_PCG = method_create.getReturnType();
            var cls_Seed = method_create.getParameterTypes()[0];
            //we need to cast to ungeneric here due to java reflection limitations, that do not support generics
            //but still return a class<?>

            var parent_class = (Class) cls_PCG;
            while (parent_class != Object.class) {
                result.computeIfAbsent(parent_class, pcg -> new PCGImplementationVariant.PrioMap())
                        .put(pcg_ctor.getImplementationVariant(), PCGCtorServiceDescriptor.create(pcg_ctor, parent_class, (Class) cls_Seed));
                parent_class = parent_class.getSuperclass();
            }
            AVAILABLE_PCGS.put((Class) cls_PCG, PCGCtorServiceDescriptor.create(pcg_ctor, (Class) cls_PCG, (Class) cls_Seed));
        }
        return result;
    }


    public static void main(String[] args) throws Exception {

    }

    public Map<Class<? extends PCG>, PCGCtorService.PCGCtorServiceDescriptor<?, ?>> AVAILABLE_PCGS = new HashMap<>();
    public Map<Class<? extends PCG>, PCGImplementationVariant.PrioMap> AVAILABLE_PCGS_PRIO = PCGCtorService.load_services();

    //<editor-fold defaultstate="collapsed" desc="Just writing type to primitive adapters for generic impl">

    /**
     * {@inheritDoc}
     */
    non-sealed interface SeedU32<T extends PCG & SeedTypeMarker<Integer>> extends PCGCtorService<T, Integer> {
        T create(int seed);

        @Override
        default T create(Integer seed) {
            return create(seed.intValue());
        }

        @Override
        default T create(U128 seed) {
            return create(seed.intValue());
        }

        @Override
        default Class<Integer> getSeedClass() {
            return int.class;
        }
    }

    /**
     * {@inheritDoc}
     */
    non-sealed interface SeedU64<T extends PCG & SeedTypeMarker<Long>> extends PCGCtorService<T, Long> {
        T create(long seed);

        @Override
        default T create(Long seed) {
            return create(seed.longValue());
        }

        @Override
        default T create(U128 seed) {
            return create(seed.longValue());
        }

        @Override
        default Class<Long> getSeedClass() {
            return long.class;
        }
    }

    /**
     * {@inheritDoc}
     */
    non-sealed interface SeedU128<T extends PCG & SeedTypeMarker<U128>> extends PCGCtorService<T, U128> {
        @Override
        default Class<U128> getSeedClass() {
            return U128.class;
        }
    }

    non-sealed interface SeedCustom<T extends PCG & SeedTypeMarker<Seed>, Seed extends Number> extends PCGCtorService<T, Seed> {
    }

    non-sealed interface Vectorized<T extends PCG & SeedTypeMarker<Seed> & PCGNative<MemorySegment>, Seed extends Number, MemorySegment> extends PCGCtorService<T, Seed> {
        @Override
        default PCGImplementationVariant getImplementationVariant() {
            return PCGImplementationVariant.JavaVectoring;
        }
    }

    non-sealed interface NativeProvidedImpl<T extends PCG & SeedTypeMarker<Seed> & PCGNative<MemorySegment>, Seed extends Number, MemorySegment> extends PCGCtorService<T, Seed> {
        @Override
        default PCGImplementationVariant getImplementationVariant() {
            return PCGImplementationVariant.NativeProvided;
        }
    }


    // </editor-fold>
}
