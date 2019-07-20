package com.alvaroagea.elk.practica3;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class ControllerTest {

    private static int NUM_ITERATION =10;

    private static Controller controller = new Practica3Controller();

    @BeforeClass
    public static void tearUp() throws IOException, InterruptedException {
        controller.init();
        Message msg1 = new Message(Instant.now(), "aagea", "esto es una prueba");
        Message msg2 = new Message(Instant.now(), "pepe", "cambiemos de tema");

        controller.index(msg1);
        controller.index(msg2);

        controller.flush();

        for (int i = 0; i < NUM_ITERATION && controller.count() != 2; i++) {
            Thread.sleep(1000);
        }
    }


    @AfterClass
    public static void tearDown() throws IOException {
        controller.reset();
        controller.close();
    }

    @Test
    public void createIndexTwoTimes() throws IOException {
        controller.init();
    }

    @Test
    public void termTest() throws IOException {

        List<Message> messages = controller.searchAuthor("aagea");

        Assert.assertNotNull(messages);
        Assert.assertEquals(1, messages.size());
    }

    @Test
    public void wildcardTest() throws IOException {

        List<Message> messages = controller.searchAuthor("pe*");

        Assert.assertNotNull(messages);
        Assert.assertEquals(1, messages.size());
    }

    @Test
    public void matchTest() throws IOException {

        List<Message> messages = controller.searchMessage("prueba");

        Assert.assertNotNull(messages);
        Assert.assertEquals(1, messages.size());
    }

}
