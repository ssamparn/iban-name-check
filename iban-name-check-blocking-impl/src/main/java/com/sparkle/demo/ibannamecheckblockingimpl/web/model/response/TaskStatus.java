package com.sparkle.demo.ibannamecheckblockingimpl.web.model.response;

import java.util.Random;

public enum TaskStatus {

    NOT_STARTED,
    IN_PROCESS,
    PROCESSED,
    FAILED,
    TIMED_OUT;

    private static final Random PRNG = new Random();

    public static TaskStatus randomStatus()  {
        TaskStatus[] statuses = values();
        return statuses[PRNG.nextInt(statuses.length)];
    }
}
