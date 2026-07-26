package org.air.concurrency;

import org.air.database.DatabaseConnection;
import org.air.database.DatabasePool;
import org.air.model.Flight;
import org.air.model.FlightStatus;
import org.air.util.Config;
import org.air.util.SharedStatistics;

public class UnsafeFlightStrategy implements FlightExecutionStrategy {

    @Override
    public void execute(
            Flight flight,
            DatabasePool pool
    ) throws InterruptedException {

        DatabaseConnection left =
                pool.getConnection(flight.getLeftConnection());

        DatabaseConnection right =
                pool.getConnection(flight.getRightConnection());

        try {

            flight.setStatus(FlightStatus.WAITING);

            if (Config.DEBUG) {
                System.out.println("Voo " + flight.getId()
                        + " aguardando conexão " + left.getId());
            }

            left.lock();

            if (Config.DEBUG) {
                System.out.println("Voo " + flight.getId()
                        + " obteve conexão " + left.getId());
            }

            if (Config.LOCK_DELAY_MS > 0) {
                Thread.sleep(Config.LOCK_DELAY_MS);
            }

            if (Config.DEBUG) {
                System.out.println("Voo " + flight.getId()
                        + " aguardando conexão " + right.getId());
            }

            right.lock();

            if (Config.DEBUG) {
                System.out.println("Voo " + flight.getId()
                        + " obteve conexão " + right.getId());
            }

            flight.setStatus(FlightStatus.REGISTERING);

            if (Config.WORK_DELAY_MS > 0) {
                Thread.sleep(Config.WORK_DELAY_MS);
            }

            SharedStatistics.completedFlights++;

            flight.setStatus(FlightStatus.FINISHED);

            if (Config.DEBUG) {
                System.out.println("Voo " + flight.getId()
                        + " registrou a decolagem.");
            }

        } finally {

            if (right.getLock().isHeldByCurrentThread()) {
                right.unlock();
            }

            if (left.getLock().isHeldByCurrentThread()) {
                left.unlock();
            }

        }

    }

}