package de.edu.lmu.pcg.test.crush;


import jdk.jfr.MemoryAddress;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.rmi.UnexpectedException;

public class Adapter implements AutoCloseable {
    static {
        System.out.println(System.mapLibraryName("pcg_test_crush"));
        System.loadLibrary("pcg_test_crush"); // Load the shared library e.g. libpcg_test_crush.so
    }

    private final Arena arena = Arena.ofShared();
    // 2 times 32 mb as Int array
    private final SequenceLayout singleRegion = MemoryLayout.sequenceLayout(64 / 2 / 4 * 1024 * 1024, ValueLayout.JAVA_INT);
    private final SequenceLayout ptsLayout = MemoryLayout.sequenceLayout(2, singleRegion);
    private final MemorySegment segment = arena.allocate(ptsLayout);
    private final Object mutex = new Object();
    private final MemorySegment rustObj;
    private boolean isClosed = false;
    private final MethodHandle destroy_method;

    public Adapter() {
        MethodHandle callbackNext;
        try {
            callbackNext = MethodHandles.lookup()
                    .findSpecial(Adapter.class, "nativeCallbackNext", MethodType.methodType(void.class), Adapter.class)
                    .bindTo(this);

        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("Unable to lookup method handle to itself?", e);
        }
        SymbolLookup stdlib = SymbolLookup.libraryLookup("pcg_test_crush", arena);
        var native_method_addr =  stdlib.find("create_exchange_adapter_lib_pcg_crush").orElseThrow(
                ()-> new RuntimeException("Unable to find create_exchange_adapter_lib_pcg_crush"));
        // rust object pointer as result , lower page, upper page, callback pointer
        FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS/*, ValueLayout.ADDRESS*/);
        MethodHandle nativeFunction = Linker.nativeLinker().downcallHandle(native_method_addr, descriptor);
        var pages = segment.elements(ptsLayout).toArray(MemorySegment[]::new);
        var callback_addr = Linker.nativeLinker().upcallStub(callbackNext, FunctionDescriptor.ofVoid(), arena);
        try {
            rustObj = (MemorySegment) nativeFunction.invoke(pages[0], pages[1], callback_addr);
        } catch (Throwable e) {
            throw new RuntimeException("Calling native method failed", e);
        }

        //#[no_mangle]
        //pub extern "C" fn destroy_exchange_adapter_lib_pcg_crush(adapter: *mut ExchangeAdapter) {
        // create downcallHandle
        var destroy_method_addr =  stdlib.find("destroy_exchange_adapter_lib_pcg_crush").orElseThrow(
                ()-> new RuntimeException("Unable to find destroy_exchange_adapter_lib_pcg_crush"));
        destroy_method = Linker.nativeLinker().downcallHandle(destroy_method_addr, FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
    }

    private void nativeCallbackNext() {

    }

    @Override
    public void close() throws Exception {
        isClosed = true;
        try {
            try {
                destroy_method.invoke(rustObj);
            } catch (Exception e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        } finally {
            arena.close();
        }
    }
}
