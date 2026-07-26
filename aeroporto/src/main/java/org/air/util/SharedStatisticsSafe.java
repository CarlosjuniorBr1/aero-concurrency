package org.air.util;

import java.util.concurrent.atomic.AtomicInteger;

public class SharedStatisticsSafe {

    public static final AtomicInteger completedFlights =
            new AtomicInteger(0);

}