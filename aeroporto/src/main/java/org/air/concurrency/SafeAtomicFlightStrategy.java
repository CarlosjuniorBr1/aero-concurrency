package org.air.concurrency;

import org.air.database.DatabaseConnection;
import org.air.database.DatabasePool;
import org.air.model.Flight;
import org.air.model.FlightStatus;
import org.air.util.Config;
import org.air.util.SharedStatisticsSafe;

public class SafeAtomicFlightStrategy implements FlightExecutionStrategy {

    @Override
    public void execute(
            Flight flight,
            DatabasePool pool
    ) throws InterruptedException {

        DatabaseConnection left =
                pool.getConnection(flight.getLeftConnection());

        DatabaseConnection right =
                pool.getConnection(flight.getRightConnection());

        DatabaseConnection first;
        DatabaseConnection second;

        if (left.getId() < right.getId()) {
            first = left;
            second = right;
        } else {
            first = right;
            second = left;
        }

        try {

            flight.setStatus(FlightStatus.WAITING);

            if (Config.DEBUG) {
                System.out.println("Voo " + flight.getId()
                        + " aguardando conexão " + first.getId());
            }

            first.lock();

            if (Config.DEBUG) {
                System.out.println("Voo " + flight.getId()
                        + " obteve conexão " + first.getId());
            }

            if (Config.LOCK_DELAY_MS > 0) {
                Thread.sleep(Config.LOCK_DELAY_MS);
            }

            if (Config.DEBUG) {
                System.out.println("Voo " + flight.getId()
                        + " aguardando conexão " + second.getId());
            }

            second.lock();

            if (Config.DEBUG) {
                System.out.println("Voo " + flight.getId()
                        + " obteve conexão " + second.getId());
            }

            flight.setStatus(FlightStatus.REGISTERING);

            if (Config.WORK_DELAY_MS > 0) {
                Thread.sleep(Config.WORK_DELAY_MS);
            }

            SharedStatisticsSafe.completedFlights.incrementAndGet();

            flight.setStatus(FlightStatus.FINISHED);

            if (Config.DEBUG) {
                System.out.println("Voo " + flight.getId()
                        + " registrou a decolagem.");
            }

        } finally {

            if (second.getLock().isHeldByCurrentThread()) {
                second.unlock();
            }

            if (first.getLock().isHeldByCurrentThread()) {
                first.unlock();
            }

        }

    }

}