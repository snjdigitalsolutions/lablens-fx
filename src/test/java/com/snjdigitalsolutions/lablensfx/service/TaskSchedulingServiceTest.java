package com.snjdigitalsolutions.lablensfx.service;

import com.snjdigitalsolutions.lablensfx.AbstractTest;
import com.snjdigitalsolutions.lablensfx.TestTask;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskSchedulingServiceTest extends AbstractTest {

    @Test
    @Order(1)
    void scheduleFixedRateTask() throws InterruptedException {
        //Arrange
        Consumer<Integer> taskConsumer = taskCountReference.getTaskCounter()::set;
        TestTask task = new TestTask(taskConsumer);

        //Act
        taskSchedulingService.scheduleFixedRateTask(task, 1L);
        Thread.sleep(4000);

        //Assert
        assertTrue(taskCountReference.getTaskCounter().get() >= 3);
    }

    @Test
    @Order(2)
    void taskStillRunning() throws InterruptedException {
        //Arrange
        ScheduledFuture<?> scheduledFuture = taskSchedulingService.getScheduledFutureTaskList().getFirst();

        //Act
        if (scheduledFuture==null){
            fail("Schedule future is null");
        }
        Thread.sleep(4000);
        taskCountReference.setCancelCountCheckValue(taskCountReference.getTaskCounter().get());

        //Assert
        assertTrue(taskCountReference.getTaskCounter().get() >= 5);
    }

    @Test
    @Order(3)
    void cancelAllTasks() throws InterruptedException {
        //Arrange

        //Act
        taskSchedulingService.cancelAllTasks();
        Thread.sleep(4000);

        //Assert
        assertEquals(taskCountReference.getTaskCounter()
                             .get(), (int) taskCountReference.getCancelCountCheckValue());
    }
}
