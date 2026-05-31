package com.snjdigitalsolutions.lablensfx.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@Profile("test")
public class TaskCountReference {

    @Getter
    private final AtomicInteger taskCounter = new AtomicInteger(0);
    @Getter
    @Setter
    private Integer cancelCountCheckValue = 0;


}
