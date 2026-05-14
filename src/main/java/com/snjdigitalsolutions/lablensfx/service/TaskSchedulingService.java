package com.snjdigitalsolutions.lablensfx.service;

import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

@Service
public class TaskSchedulingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskSchedulingService.class);

    private final TaskScheduler taskScheduler;
    private List<ScheduledFuture<?>> scheduledFutureTaskList;


    public TaskSchedulingService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void scheduleFixedRateTask(Task<?> taskToSchedule, Long durationInSeconds) {
        if (scheduledFutureTaskList == null){
            scheduledFutureTaskList = new ArrayList<>();
        }
        ScheduledFuture<?> scheduleTask = taskScheduler.scheduleAtFixedRate(taskToSchedule, Duration.ofSeconds(durationInSeconds));
        scheduledFutureTaskList.add(scheduleTask);
        LOGGER.debug("Task scheduled");
    }

    public void cancelAllTasks(){
        scheduledFutureTaskList.forEach(task -> {
            task.cancel(true);
        });
    }
}
