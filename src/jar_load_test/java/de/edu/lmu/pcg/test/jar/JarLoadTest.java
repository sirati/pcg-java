package de.edu.lmu.pcg.test.jar;

import org.junit.Test;

import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.Set;

public class JarLoadTest {

    public static void main(String[] args) {
        try {
            new JarLoadTest().loadByJarValidateLoading();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //check that PCGCtorService.AVAILABLE_PCGS has 10 and not 5 entries i.e. that vector21 was loaded
    @Test
    public void loadByJarValidateLoading() throws Exception {
        var url = JarLoadTest.class.getProtectionDomain().getClassLoader().getResource("libs/pcg.jar");
        var classloader = new URLClassLoader(new java.net.URL[]{url}, Thread.currentThread().getContextClassLoader());

        var classRequirement = classloader.loadClass("de.edu.lmu.pcg.services.JDKRequirement$Requirement");
        var methodFromCurrentJDK = classRequirement.getMethod("fromCurrentJDK");
        var requirement = methodFromCurrentJDK.invoke(null);
        var javaVersionMajor = getPrivateField(classRequirement, "javaVersionMajor").getInt(requirement);
        var preview = getPrivateField(classRequirement, "preview").getBoolean(requirement);
        var modules = (Set<String>) getPrivateField(classRequirement, "modules").get(requirement);

        //assert that javaVersionMajor == 21, preview = true, modules contains jdk.incubator.vector
        if (javaVersionMajor != 21) throw new AssertionError("javaVersionMajor != 21");
        if (!preview) throw new AssertionError("preview != true");
        if (!modules.contains("jdk.incubator.vector"))
            throw new AssertionError("modules does not contain jdk.incubator.vector");

        //if any of the above fail our test will fail to test correctly
        var classPCGCTorService = classloader.loadClass("de.edu.lmu.pcg.services.PCGCtorService");
        var fieldAVAILABLE_PCGS = classPCGCTorService.getField("AVAILABLE_PCGS");
        java.util.Map<Class<?>, ?> AVAILABLE_PCGS;
        try {
            AVAILABLE_PCGS = (java.util.Map<Class<?>, ?>) fieldAVAILABLE_PCGS.get(null);
        } catch (java.lang.ExceptionInInitializerError e) {
            throw new RuntimeException("That getting the field failed by ExceptionInInitializerError is a strong indicator that this test successfully found the failure case", e);
        }
        if (AVAILABLE_PCGS.size() != 7)
            throw new AssertionError("AVAILABLE_PCGS.size() != 7, maybe vector loading failed?");

        //check that 2 were loaded from impl.vector.preview21
        if (2 != AVAILABLE_PCGS.keySet().stream().filter(key -> key.getName().contains("impl.vector.preview21")).count())
            throw new AssertionError("did not load 2 from impl.vector.preview21");

    }

    private static Field getPrivateField(Class<?> classRequirement, String fieldName) throws NoSuchFieldException {
        Field field = classRequirement.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }
}
