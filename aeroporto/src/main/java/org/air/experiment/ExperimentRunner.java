package org.air.experiment;

import org.air.concurrency.FlightExecutionStrategy;
import org.air.concurrency.FlightTask;
import org.air.concurrency.UnsafeFlightStrategy;
import org.air.database.DatabasePool;
import org.air.model.Flight;
import org.air.monitor.ExperimentResult;
import org.air.monitor.SystemMonitor;
import org.air.util.Config;
import org.air.util.SharedStatistics;
import org.air.util.SharedStatisticsSafe;
import org.air.util.Timer;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExperimentRunner {

    public ExperimentResult run(
            int operations,
            FlightExecutionStrategy strategy) {

        // Reinicia os contadores
        SharedStatistics.completedFlights = 0;
        SharedStatisticsSafe.completedFlights.set(0);

        // Cria o pool de conexões
        DatabasePool pool = new DatabasePool(
                Config.DATABASE_CONNECTIONS);

        // Cria os voos

        // Número de threads concorrentes
        int threads = 50;

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        Timer timer = new Timer();
        SystemMonitor monitor = new SystemMonitor();

        timer.start();

        // Submete as operações
        for (int i = 0; i < operations; i++) {

            Flight flight = new Flight(
                    i,
                    i % Config.DATABASE_CONNECTIONS,
                    (i + 1) % Config.DATABASE_CONNECTIONS);

            executor.submit(
                    new FlightTask(
                            flight,
                            pool,
                            strategy));

        }

        executor.shutdown();

        boolean deadlock = false;

        try {

            if (strategy instanceof UnsafeFlightStrategy) {

                // Espera no máximo 30 segundos.
                // Se não terminar, consideramos deadlock.
                boolean finished = executor.awaitTermination(
                        30,
                        TimeUnit.SECONDS);

                deadlock = !finished;

                if (deadlock) {
                    executor.shutdownNow();
                }

            } else {

                // Estratégias seguras sempre aguardam finalizar
                executor.awaitTermination(
                        Long.MAX_VALUE,
                        TimeUnit.NANOSECONDS);

            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

        }

        double time = timer.stop();

        double opsPerSecond = operations / time;

        return new ExperimentResult(

                strategy.getClass().getSimpleName(),

                operations,

                time,

                opsPerSecond,

                monitor.getCpuLoad(),

                monitor.getUsedMemory(),

                deadlock,

                SharedStatistics.completedFlights,

                SharedStatisticsSafe.completedFlights.get()

        );

    }

}