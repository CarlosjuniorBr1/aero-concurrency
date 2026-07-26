package org.air.concurrency;
import org.air.database.DatabasePool;
import org.air.model.Flight;

public class FlightTask implements Runnable {

    private final Flight flight;

    private final DatabasePool pool;

    private final FlightExecutionStrategy strategy;

    public FlightTask(
            Flight flight,
            DatabasePool pool,
            FlightExecutionStrategy strategy
    ) {

        this.flight = flight;
        this.pool = pool;
        this.strategy = strategy;
    }

    @Override
    public void run() {

        try {

            strategy.execute(flight, pool);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

        }

    }

}