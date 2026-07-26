package org.air.concurrency;

import org.air.database.DatabasePool;
import org.air.model.Flight;

public interface FlightExecutionStrategy {

    void execute(
            Flight flight,
            DatabasePool pool
    ) throws InterruptedException;

}