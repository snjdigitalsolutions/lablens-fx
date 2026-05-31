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


    /**
     * Constructs a {@code TaskSchedulingService} with the given Spring {@link TaskScheduler}.
     *
     * @param taskScheduler the scheduler used to submit fixed-rate tasks
     */
    public TaskSchedulingService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    /**
     * Schedules a {@link Runnable} to execute at a fixed rate and associates it with the given
     * {@link ScheduledTaskType}. If a task of the same type was already scheduled, it is replaced
     * and this method returns {@code true}.
     *
     * @param taskToSchedule   the runnable to execute on the fixed-rate schedule
     * @param type             the key used to track and cancel this task later
     * @param durationInSeconds the interval between successive executions, in seconds
     * @return {@code true} if a previously scheduled task of the same type was replaced
     */
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

    /**
     * Cancels the scheduled task associated with the given {@link ScheduledTaskType} and
     * removes it from the task map. The underlying future is interrupted if running.
     *
     * @param type the type identifying the task to cancel
     */
    public void cancelScheduledTask(ScheduledTaskType type) {
        scheduledFutureTaskMap.get(type).cancel(true);
        scheduledFutureTaskMap.remove(type);
    }

    /**
     * Cancels all currently scheduled tasks, interrupting any that are actively running.
     */
    public void cancelAllTasks(){
        scheduledFutureTaskMap.keySet().forEach(scheduledTaskType -> {
           scheduledFutureTaskMap.get(scheduledTaskType).cancel(true);
        });
    }

}
