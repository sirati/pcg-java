package de.edu.lmu.pcg.test.crush;


import de.edu.lmu.pcg.PCG;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.Path;

@SuppressWarnings("preview")
public class Adapter implements AutoCloseable {
    private final Arena arena = Arena.ofShared();
    // 2 times 32 MiB as Int array
    private final SequenceLayout singleRegion = MemoryLayout.sequenceLayout(64 / 2 / 4 * 1024 * 1024, ValueLayout.JAVA_INT);
    private final SequenceLayout ptsLayout = MemoryLayout.sequenceLayout(2, singleRegion);
    private final MemorySegment segment = arena.allocate(ptsLayout);
    private final MemorySegment lowerPage = segment.asSlice(0, singleRegion.byteSize());
    private final MemorySegment higherPage = segment.asSlice(singleRegion.byteSize(), singleRegion.byteSize());
    private final InterleavingMutex mutex = new InterleavingMutex();
    private final MemorySegment rustObj;
    private boolean isClosed = false;
    private boolean isDestroyed = false;
    private final MethodHandle destroy_method;
    private final MethodHandle small_crush_method;
    private final MethodHandle medium_crush_method;
    private final MethodHandle big_crush_method;
    private static final boolean ID_GENERATOR = false;
    private static final boolean ID_CRUSH = true;
    private final PCG pcg;
    private boolean isNextGenLowerPage = false;
    private long consumerMillis;

    public Adapter(PCG pcg) {
        this.pcg = pcg;
        MethodHandle callbackNext;
        try {
            callbackNext = MethodHandles.lookup()
                    .findSpecial(Adapter.class, "nativeCallbackNext", MethodType.methodType(int.class, int.class), Adapter.class)
                    .bindTo(this);

        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("Unable to lookup method handle to itself?", e);
        }

        Path path = Path.of(System.getProperty("user.dir"), "target", "debug", System.mapLibraryName("pcg_test_crush"));
        SymbolLookup stdlib = SymbolLookup.libraryLookup(path, arena);
        var native_method_addr =  stdlib.find("create_exchange_adapter_lib_pcg_crush").orElseThrow(
                ()-> new RuntimeException("Unable to find create_exchange_adapter_lib_pcg_crush"));
        // rust object pointer as result , lower page, upper page, callback pointer
        FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS/*, ValueLayout.ADDRESS*/);
        MethodHandle nativeFunction = Linker.nativeLinker().downcallHandle(native_method_addr, descriptor);
        var callback_addr = Linker.nativeLinker().upcallStub(callbackNext, FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT), arena);
        try {
            rustObj = (MemorySegment) nativeFunction.invoke(lowerPage, higherPage, callback_addr);
        } catch (Throwable e) {
            throw new RuntimeException("Calling native method failed", e);
        }

        //get the rust method that act on the adapter
        destroy_method = findRustMemberMethod(stdlib, "destroy_exchange_adapter_lib_pcg_crush");
        small_crush_method = findRustMemberMethod(stdlib, "launch_small_lib_pcg_crush");
        medium_crush_method = findRustMemberMethod(stdlib, "launch_medium_lib_pcg_crush");
        big_crush_method = findRustMemberMethod(stdlib, "launch_big_lib_pcg_crush");
    }

    private static MethodHandle findRustMemberMethod(SymbolLookup stdlib, String fnExportName) {
        var method_addr =  stdlib.find(fnExportName).orElseThrow(
                ()-> new RuntimeException(STR."Unable to find \{fnExportName}"));
        return Linker.nativeLinker().downcallHandle(method_addr, FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
    }

    public static void smallCrush(PCG pcg) {
        try (Adapter adapter = new Adapter(pcg)) {
            Adapter.runAnyCrush(adapter, adapter::small_Crush);
        }
    }
    public static void mediumCrush(PCG pcg) {
        try (Adapter adapter = new Adapter(pcg)) {
            Adapter.runAnyCrush(adapter, adapter::medium_Crush);
        }
    }
    public static void bigCrush(PCG pcg) {
        try (Adapter adapter = new Adapter(pcg)) {
            Adapter.runAnyCrush(adapter, adapter::big_Crush);
        }
    }


    private static void runAnyCrush(Adapter adapter, Runnable crushMethod) {
        //print current threads PID
        System.out.println(STR."PID: \{ProcessHandle.current().pid()}");

        adapter.fillNextPage();
        Thread t = new Thread(() -> {
            while (!adapter.isClosed) {
                adapter.mutex.criticalSection(ID_GENERATOR, adapter::fillNextPage);
            }
        });
        t.setName("Filler Thread");
        t.start();
        crushMethod.run();
    }

    private void fillNextPage() {
        var millis = System.currentTimeMillis();
        System.out.println(STR."Start generating \{isNextGenLowerPage?"lower":"upper"} page (32mebibytes) ");

        pcg.fill((isNextGenLowerPage ? this.lowerPage : this.higherPage).asByteBuffer());
        isNextGenLowerPage = !isNextGenLowerPage;

        System.out.println(STR."Finished generating \{isNextGenLowerPage?"lower":"upper"} page (32mebibytes) took \{System.currentTimeMillis() - millis}ms");
    }

    private int nativeCallbackNext(int b) {
        System.out.println(STR."Finished crush on one section, took \{System.currentTimeMillis() - consumerMillis}ms");
        //fillNextPage();
        mutex.wait(ID_CRUSH);
        consumerMillis = System.currentTimeMillis();

        return b + 1;
    }

    @Override
    public void close() {
        isClosed = true;
        try {
            try {
                destroyRust();
            } catch (Exception e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        } finally {
            arena.close();
        }
    }

    private void destroyRust() {
        if (!isDestroyed) {
            callRustMethod(destroy_method);
        }
    }

    private void callRustMethod(MethodHandle method) {
        if (isDestroyed) throw new IllegalStateException("Rust object is already destroyed");
        consumerMillis = System.currentTimeMillis();
        isDestroyed = true; //we can only ever call one method
        try {
            method.invoke(rustObj);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void small_Crush() {
        callRustMethod(small_crush_method);
    }

    private void medium_Crush() {
        callRustMethod(medium_crush_method);
    }

    private void big_Crush() {
        callRustMethod(big_crush_method);
    }

}
