package com.fsolsh.rabbitmq.base;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Constant {
    public static final ScheduledExecutorService SCHEDULED_POOL = Executors.newSingleThreadScheduledExecutor();
}
