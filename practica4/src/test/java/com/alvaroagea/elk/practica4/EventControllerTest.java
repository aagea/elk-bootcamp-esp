package com.alvaroagea.elk.practica4;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EventControllerTest {
    private static final int NUM_ITERATION = 10;

    private static EventController controller = new Practica4Controller();

    private static Instant initTime = Instant.parse("2010-12-03T10:15:30.00Z");

    @BeforeClass
    public static void tearUp() throws IOException, InterruptedException {
        controller.init();


        Event ev1 = new Event("A", "1", initTime.plus(1, ChronoUnit.MINUTES), initTime.plus(2, ChronoUnit.MINUTES));
        Event ev2 = new Event("B", "2", initTime.plus(3, ChronoUnit.MINUTES), initTime.plus(4, ChronoUnit.MINUTES));
        Event ev3 = new Event("A", "3", initTime.plus(5, ChronoUnit.MINUTES), initTime.plus(6, ChronoUnit.MINUTES));
        Event ev4 = new Event("B", "1", initTime.plus(7, ChronoUnit.MINUTES), initTime.plus(8, ChronoUnit.MINUTES));
        Event ev5 = new Event("A", "2", initTime.plus(9, ChronoUnit.MINUTES), initTime.plus(10, ChronoUnit.MINUTES));
        Event ev6 = new Event("B", "3", initTime.plus(11, ChronoUnit.MINUTES), initTime.plus(12, ChronoUnit.MINUTES));
        Event ev7 = new Event("A", "1", initTime.plus(13, ChronoUnit.MINUTES), initTime.plus(14, ChronoUnit.MINUTES));
        Event ev8 = new Event("B", "2", initTime.plus(15, ChronoUnit.MINUTES), initTime.plus(16, ChronoUnit.MINUTES));
        Event ev9 = new Event("A", "3", initTime.plus(17, ChronoUnit.MINUTES), initTime.plus(18, ChronoUnit.MINUTES));
        Event ev10 = new Event("B", "1", initTime.plus(19, ChronoUnit.MINUTES), initTime.plus(20, ChronoUnit.MINUTES));
        Event ev11 = new Event("A", "2", initTime.plus(21, ChronoUnit.MINUTES), initTime.plus(22, ChronoUnit.MINUTES));
        Event ev12 = new Event("B", "3", initTime.plus(23, ChronoUnit.MINUTES), initTime.plus(24, ChronoUnit.MINUTES));
        Event ev13 = new Event("A", "1", initTime.plus(25, ChronoUnit.MINUTES), initTime.plus(26, ChronoUnit.MINUTES));
        Event ev14 = new Event("B", "2", initTime.plus(27, ChronoUnit.MINUTES), initTime.plus(28, ChronoUnit.MINUTES));
        Event ev15 = new Event("A", "3", initTime.plus(29, ChronoUnit.MINUTES), initTime.plus(30, ChronoUnit.MINUTES));
        Event ev16 = new Event("B", "1", initTime.plus(31, ChronoUnit.MINUTES), initTime.plus(32, ChronoUnit.MINUTES));
        Event ev17 = new Event("A", "2", initTime.plus(33, ChronoUnit.MINUTES), initTime.plus(34, ChronoUnit.MINUTES));
        Event ev18 = new Event("B", "3", initTime.plus(35, ChronoUnit.MINUTES), initTime.plus(36, ChronoUnit.MINUTES));
        Event ev19 = new Event("A", "1", initTime.plus(37, ChronoUnit.MINUTES), initTime.plus(38, ChronoUnit.MINUTES));
        Event ev20 = new Event("B", "2", initTime.plus(39, ChronoUnit.MINUTES), initTime.plus(40, ChronoUnit.MINUTES));

        controller.index(ev1);
        controller.index(ev2);
        controller.index(ev3);
        controller.index(ev4);
        controller.index(ev5);
        controller.index(ev6);
        controller.index(ev7);
        controller.index(ev8);
        controller.index(ev9);
        controller.index(ev10);
        controller.index(ev11);
        controller.index(ev12);
        controller.index(ev13);
        controller.index(ev14);
        controller.index(ev15);
        controller.index(ev16);
        controller.index(ev17);
        controller.index(ev18);
        controller.index(ev19);
        controller.index(ev20);

        controller.flush();

        for (int i = 0; i < NUM_ITERATION && controller.count() != 20; i++) {
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
    public void lastBasicTest() {
        Optional<List<Event>> result = controller.last("A", 4);
        Assert.assertTrue("Result has been defined", result.isPresent());
        Assert.assertEquals("Size must be 4", 4, result.get().size());

        Assert.assertEquals("First event must be equals", new Event("A", "1",
                initTime.plus(25, ChronoUnit.MINUTES), initTime.plus(26, ChronoUnit.MINUTES)), result.get().get(0));
        Assert.assertEquals("Last event must be equals", new Event("A", "1",
                initTime.plus(37, ChronoUnit.MINUTES), initTime.plus(38, ChronoUnit.MINUTES)), result.get().get(3));
    }

    @Test
    public void lastNotFoundTest() {
        final Optional<List<Event>> result = controller.last("C", 4);
        Assert.assertTrue("Result has been defined", result.isPresent());
        Assert.assertTrue("Result has been empty", result.get().isEmpty());
    }


    @Test
    public void lastWithTagsTest() {
        final Optional<List<Event>> result = controller.last("A", Arrays.asList("1", "2"), 2, null);
        Assert.assertTrue("Result has been defined", result.isPresent());
        Assert.assertEquals("Size must be 2", 2, result.get().size());

        Assert.assertEquals("First event must be equals",
                new Event("A", "2",
                        initTime.plus(33, ChronoUnit.MINUTES), initTime.plus(34, ChronoUnit.MINUTES)),
                result.get().get(0)
        );
        Assert.assertEquals("Last event must be equals",
                new Event("A", "1",
                        initTime.plus(37, ChronoUnit.MINUTES), initTime.plus(38, ChronoUnit.MINUTES)),
                result.get().get(1)
        );
    }

    @Test
    public void lastWithTagsNotFoundTest() {
        final Optional<List<Event>> result = controller.last("C", Collections.singletonList("4"), 2, null);
        Assert.assertTrue("Result has been defined", result.isPresent());
        Assert.assertTrue("Result has been empty", result.get().isEmpty());
    }

    @Test
    public void lastWithTagsAndBeforeTest() {
        final Optional<List<Event>> result = controller.last("A", Arrays.asList("1", "2"), 2,
                initTime.plus(37, ChronoUnit.MINUTES));
        Assert.assertTrue("Result has been defined", result.isPresent());
        Assert.assertEquals("Size must be 2", 2, result.get().size());

        Assert.assertEquals("First event must be equals",
                new Event("A", "1", initTime.plus(25, ChronoUnit.MINUTES), initTime.plus(26, ChronoUnit.MINUTES)),
                result.get().get(0)
        );
        Assert.assertEquals("Last event must be equals",
                new Event("A", "2", initTime.plus(33, ChronoUnit.MINUTES), initTime.plus(34, ChronoUnit.MINUTES)),
                result.get().get(1)
        );
    }

    @Test
    public void lastWithDistinctTest() {
        final Optional<List<Event>> result = controller.lastDistinct("A", Arrays.asList("1", "2"), 2, null);
        Assert.assertTrue("Result has been defined", result.isPresent());
        Assert.assertEquals("Size must be 2", 2, result.get().size());

        Assert.assertEquals("First event must be equals",
                new Event("A", "3", initTime.plus(17, ChronoUnit.MINUTES), initTime.plus(18, ChronoUnit.MINUTES)),
                result.get().get(0)
        );
        Assert.assertEquals("Last event must be equals",
                new Event("A", "3", initTime.plus(29, ChronoUnit.MINUTES), initTime.plus(30, ChronoUnit.MINUTES)),
                result.get().get(1)
        );
    }

    @Test
    public void lastWithDistinctAndBeforeTest() {
        final Optional<List<Event>> result = controller.lastDistinct("A", Arrays.asList("1", "2"), 2,
                initTime.plus(29, ChronoUnit.MINUTES));
        Assert.assertTrue("Result has been defined", result.isPresent());
        Assert.assertEquals("Size must be 2", 2, result.get().size());

        Assert.assertEquals("First event must be equals",
                new Event("A", "3", initTime.plus(5, ChronoUnit.MINUTES), initTime.plus(6, ChronoUnit.MINUTES)),
                result.get().get(0)
        );
        Assert.assertEquals("Last event must be equals",
                new Event("A", "3", initTime.plus(17, ChronoUnit.MINUTES), initTime.plus(18, ChronoUnit.MINUTES)),
                result.get().get(1)
        );
    }

    @Test
    public void lastDistinctNotFoundTest() {
        final Optional<List<Event>> result = controller.lastDistinct("A", Arrays.asList("1", "2", "3"), 5, null);
        Assert.assertTrue("Result has been defined", result.isPresent());
        Assert.assertTrue("Result has been empty", result.get().isEmpty());
    }

    @Test
    public void searchTest() {
        final Optional<List<Event>> result = controller.search("A", Arrays.asList("1", "2"),
                initTime.plus(37, ChronoUnit.MINUTES), initTime.plus(23, ChronoUnit.MINUTES), 5);
        Assert.assertTrue("Result has been defined", result.isPresent());
        Assert.assertEquals("Size must be 2", 2, result.get().size());

        Assert.assertEquals("First event must be equals",
                new Event("A", "1", initTime.plus(25, ChronoUnit.MINUTES), initTime.plus(26, ChronoUnit.MINUTES)),
                result.get().get(0)
        );
        Assert.assertEquals("Last event must be equals",
                new Event("A", "2", initTime.plus(33, ChronoUnit.MINUTES), initTime.plus(34, ChronoUnit.MINUTES)),
                result.get().get(1)
        );
    }

    @Test
    public void searchDistinctTest() {
        final Optional<List<Event>> result = controller.searchDistinct("A", Arrays.asList("1", "2"),
                initTime.plus(29, ChronoUnit.MINUTES), initTime.plus(3, ChronoUnit.MINUTES), 5);
        Assert.assertTrue("Result has been defined", result.isPresent());
        Assert.assertEquals("Size must be 2", 2, result.get().size());

        Assert.assertEquals("First event must be equals",
                new Event("A", "3", initTime.plus(5, ChronoUnit.MINUTES), initTime.plus(6, ChronoUnit.MINUTES)),
                result.get().get(0)
        );
        Assert.assertEquals("Last event must be equals",
                new Event("A", "3", initTime.plus(17, ChronoUnit.MINUTES), initTime.plus(18, ChronoUnit.MINUTES)),
                result.get().get(1)
        );
    }

    @Test
    public void searchNotFoundTest() {
        final Optional<List<Event>> result = controller.search("C", Collections.singletonList("4"),
                initTime.plus(29, ChronoUnit.MINUTES), initTime.plus(3, ChronoUnit.MINUTES), 5);
        Assert.assertTrue("Result has been defined", result.isPresent());
        Assert.assertTrue("Result has been empty", result.get().isEmpty());
    }

    @Test
    public void searchDistinctNotFoundTest() {
        final Optional<List<Event>> result = controller.searchDistinct("A", Arrays.asList("1", "2", "3"),
                initTime.plus(29, ChronoUnit.MINUTES), initTime.plus(3, ChronoUnit.MINUTES), 5);
        Assert.assertTrue("Result has been defined", result.isPresent());
        Assert.assertTrue("Result has been empty", result.get().isEmpty());
    }


    @Test
    public void firstTest() {
        final Optional<List<Event>> result = controller.first("A", Arrays.asList("1", "2"), 2, null);
        Assert.assertTrue("Result has been defined", result.isPresent());
        Assert.assertEquals("Size must be 2", 2, result.get().size());

        Assert.assertEquals("First event must be equals",
                new Event("A", "1", initTime.plus(1, ChronoUnit.MINUTES), initTime.plus(2, ChronoUnit.MINUTES)),
                result.get().get(0)
        );
        Assert.assertEquals("Last event must be equals",
                new Event("A", "2", initTime.plus(9, ChronoUnit.MINUTES), initTime.plus(10, ChronoUnit.MINUTES)),
                result.get().get(1)
        );
    }

    @Test
    public void firstNotFoundTest() {
        final Optional<List<Event>> result = controller.first("C", Collections.singletonList("4"), 2, null);
        Assert.assertTrue("Result has been defined", result.isPresent());
        Assert.assertTrue("Result has been empty", result.get().isEmpty());
    }

    @Test
    public void firstAndBeforeTest() {
        final Optional<List<Event>> result = controller.first("A", Arrays.asList("1", "2"), 2, initTime.plus(2, ChronoUnit.MINUTES));
        Assert.assertTrue("Result has been defined", result.isPresent());
        Assert.assertEquals("Size must be 2", 2, result.get().size());

        Assert.assertEquals("First event must be equals",
                new Event("A", "2", initTime.plus(9, ChronoUnit.MINUTES), initTime.plus(10, ChronoUnit.MINUTES)),
                result.get().get(0)
        );
        Assert.assertEquals("Last event must be equals",
                new Event("A", "1", initTime.plus(13, ChronoUnit.MINUTES), initTime.plus(14, ChronoUnit.MINUTES)),
                result.get().get(1)
        );
    }

    @Test
    public void firstWithDistinctTest() {
        final Optional<List<Event>> result = controller.firstDistinct("A", Arrays.asList("1", "2"), 2, null);
        Assert.assertTrue("Result has been defined", result.isPresent());
        Assert.assertEquals("Size must be 2", 2, result.get().size());

        Assert.assertEquals("First event must be equals",
                new Event("A", "3", initTime.plus(5, ChronoUnit.MINUTES), initTime.plus(6, ChronoUnit.MINUTES)),
                result.get().get(0)
        );
        Assert.assertEquals("Last event must be equals",
                new Event("A", "3", initTime.plus(17, ChronoUnit.MINUTES), initTime.plus(18, ChronoUnit.MINUTES)),
                result.get().get(1)
        );
    }

    @Test
    public void firstWithDistinctAndBeforeTest() {
        final Optional<List<Event>> result = controller.firstDistinct("A", Arrays.asList("1", "2"), 2,
                initTime.plus(6, ChronoUnit.MINUTES));
        Assert.assertTrue("Result has been defined", result.isPresent());
        Assert.assertEquals("Size must be 2", 2, result.get().size());

        Assert.assertEquals("First event must be equals",
                new Event("A", "3", initTime.plus(17, ChronoUnit.MINUTES), initTime.plus(18, ChronoUnit.MINUTES)),
                result.get().get(0)
        );
        Assert.assertEquals("Last event must be equals",
                new Event("A", "3", initTime.plus(29, ChronoUnit.MINUTES), initTime.plus(30, ChronoUnit.MINUTES)),
                result.get().get(1)
        );
    }

    @Test
    public void firstDistinctNotFoundTest() {
        final Optional<List<Event>> result = controller.firstDistinct("A", Arrays.asList("1", "2", "3"), 5, null);
        Assert.assertTrue("Result has been defined", result.isPresent());
        Assert.assertTrue("Result has been empty", result.get().isEmpty());
    }
}
