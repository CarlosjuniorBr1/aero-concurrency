package org.air.concurrency;

import org.air.database.DatabaseConnection;
import org.air.database.DatabasePool;
import org.air.model.Flight;
import org.air.model.FlightStatus;
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

            System.out.println("Voo " + flight.getId()
                    + " aguardando conexão " + left.getId());

            left.lock();

            System.out.println("Voo " + flight.getId()
                    + " obteve conexão " + left.getId());

            Thread.sleep(200);

            System.out.println("Voo " + flight.getId()
                    + " aguardando conexão " + right.getId());

            right.lock();

            System.out.println("Voo " + flight.getId()
                    + " obteve conexão " + right.getId());

            flight.setStatus(FlightStatus.REGISTERING);

            Thread.sleep(500);

            SharedStatistics.completedFlights++;

            flight.setStatus(FlightStatus.FINISHED);

            System.out.println("Voo "
                    + flight.getId()
                    + " registrou a decolagem.");

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