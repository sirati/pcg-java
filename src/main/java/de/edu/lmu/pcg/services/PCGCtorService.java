package de.edu.lmu.pcg.services;

import de.edu.lmu.pcg.PCG;

import java.util.Arrays;
import java.util.Map;
import java.util.ServiceLoader;

public interface PCGCtorService<T extends PCG, Seed extends Number> {
    T create(Seed seed);

    record PCGCtorServiceDescriptor<T extends PCG, Seed extends Number>
            (Class<T> cls_PCG, Class<Seed> cls_Seed, PCGCtorService<T, Seed> service) {

        static <T extends PCG, Seed extends Number> PCGCtorServiceDescriptor<T, Seed>
        create(PCGCtorService<T, Seed> service, Class<T> cls_PCG, Class<Seed> cls_Seed) {
            return new PCGCtorServiceDescriptor<T, Seed>(cls_PCG, cls_Seed, service);
        }
    }

    static Map<Class<? extends PCG>, PCGCtorServiceDescriptor<?, ?>> load_services() {
        var serviceLoader = ServiceLoader.load(PCGCtorService.class);
        var result = new java.util.HashMap<Class<? extends PCG>, PCGCtorServiceDescriptor<?, ?>>();
        for (var service : serviceLoader) {
            //get method create of impl
            var opt_method_create = Arrays.stream(service.getClass().getMethods())
                    .filter(method -> method.getName().equals("create") && method.getParameterCount() == 1).findAny();
            if (opt_method_create.isEmpty()) {
                throw new RuntimeException("Service must directly impl create method, not inherit it!");
            }
            var method_create = opt_method_create.get();
            var cls_PCG = method_create.getReturnType();
            var cls_Seed = method_create.getParameterTypes()[0];
            //we need to cast to ungeneric here due to java reflection limitations, that do not support generics
            //but still return a class<?>
            //noinspection unchecked,rawtypes
            var desc = PCGCtorServiceDescriptor.create(service, (Class)cls_PCG, (Class)cls_Seed);
            result.put(desc.cls_PCG, desc);
        }
        return result;
    }

}
