package org.air.experiment;
import org.air.concurrency.*;
import org.air.database.DatabasePool;
import org.air.model.Flight;
import org.air.monitor.SystemMonitor;
import org.air.util.SharedStatistics;
import org.air.util.SharedStatisticsSafe;
import org.air.util.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExperimentRunner {

    public void run() {

        SharedStatistics.completedFlights = 0;
        SharedStatisticsSafe.completedFlights.set(0);

        FlightExecutionStrategy strategy =
                new SafeAtomicFlightStrategy();

        DatabasePool pool = new DatabasePool(5);

        List<Flight> flights = new ArrayList<>();

        for (int i = 0; i < 5; i++) {

            flights.add(
                    new Flight(
                            i,
                            i,
                            (i + 1) % pool.size()
                    )
            );
        }

        ExecutorService executor = Executors.newFixedThreadPool(5);

        Timer timer = new Timer();
        SystemMonitor monitor = new SystemMonitor();

        timer.start();

        for (Flight flight : flights) {

            executor.submit(

                    new FlightTask(

                            flight,

                            pool,

                            strategy

                    )

            );

        }

        executor.shutdown();

        try {

            boolean finished =
                    executor.awaitTermination(30, TimeUnit.SECONDS);

            if (!finished) {

                System.out.println("Tempo limite atingido.");

            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

        }

        double time = timer.stop();

        System.out.println();

        System.out.println("===== RESULTADO =====");

        System.out.println("Tempo: " + time);

        System.out.println("Deadlock: "
                + monitor.hasDeadlock());

        System.out.println("Unsafe Counter: "
                + SharedStatistics.completedFlights);

        System.out.println("Safe Counter: "
                + SharedStatisticsSafe.completedFlights.get());
    }

}