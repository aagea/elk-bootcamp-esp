package com.alvaroagea.elk.practica4a;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class EventControllerTest {
    private static final int NUM_DOCS = 111396;

    private static ShakespeareController controller = new Practica4AController();


    @Test
    public void countTest() throws IOException {
        long size = controller.count();
        Assert.assertEquals(NUM_DOCS, size);
    }
}
