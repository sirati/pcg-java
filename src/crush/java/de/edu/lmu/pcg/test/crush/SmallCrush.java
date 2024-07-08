package de.edu.lmu.pcg.test.crush;

import org.junit.Test;

public class SmallCrush {

    @Test
    public void testCrush() {
        try (Adapter adapter = new Adapter()) {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
