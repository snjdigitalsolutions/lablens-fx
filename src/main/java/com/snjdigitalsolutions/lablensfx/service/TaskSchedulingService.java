package com.snjdigitalsolutions.lablensfx.service;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
public class TaskSchedulingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskSchedulingService.class);

    private final TaskScheduler taskScheduler;
    @Getter
    private Map<ScheduledTaskType, ScheduledFuture<?>> scheduledFutureTaskMap;


    public TaskSchedulingService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public boolean scheduleFixedRateTask(Runnable taskToSchedule, ScheduledTaskType type, Long durationInSeconds) {
        boolean existed = false;
        if (scheduledFutureTaskMap == null){
            scheduledFutureTaskMap = new HashMap<>();
        }
        ScheduledFuture<?> scheduleTask = taskScheduler.scheduleAtFixedRate(taskToSchedule, Duration.ofSeconds(durationInSeconds));
        if(scheduledFutureTaskMap.put(type, scheduleTask) != null) {
            existed = true;
        };
        LOGGER.debug("Task scheduled");
        return existed;
    }

    public void cancelScheduledTask(ScheduledTaskType type) {
        scheduledFutureTaskMap.get(type).cancel(true);
        scheduledFutureTaskMap.remove(type);
    }

    public void cancelAllTasks(){
        scheduledFutureTaskMap.keySet().forEach(scheduledTaskType -> {
           scheduledFutureTaskMap.get(scheduledTaskType).cancel(true);
        });
    }

}
