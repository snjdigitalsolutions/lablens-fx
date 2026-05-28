package com.snjdigitalsolutions.lablensfx;

import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class TestTask extends Task<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestTask.class);

    private final Consumer<Integer> countConsumer;
    private final AtomicInteger loopCount = new AtomicInteger(0);

    public TestTask(Consumer<Integer> countConsumer) {
        this.countConsumer = countConsumer;
    }

    @Override
    public void run() {
        LOGGER.info("Looping");
        int count = loopCount.incrementAndGet();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.info("Task interrupted");
            return;
        }
        LOGGER.info("Loop count: {}", count);
        countConsumer.accept(count);
    }

    @Override
    protected Void call() throws Exception {
        this.run();
        return null;
    }
}
