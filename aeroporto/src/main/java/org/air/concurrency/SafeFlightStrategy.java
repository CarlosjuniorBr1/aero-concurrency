package org.air.concurrency;

import org.air.database.DatabaseConnection;
import org.air.database.DatabasePool;
import org.air.model.Flight;
import org.air.model.FlightStatus;
import org.air.util.SharedStatistics;

public class SafeFlightStrategy implements FlightExecutionStrategy {

    @Override
    public void execute(
            Flight flight,
            DatabasePool pool
    ) throws InterruptedException {

        DatabaseConnection left =
                pool.getConnection(flight.getLeftConnection());

        DatabaseConnection right =
                pool.getConnection(flight.getRightConnection());

        // Sempre pega primeiro o recurso de menor ID
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

            System.out.println("Voo " + flight.getId()
                    + " aguardando conexão " + first.getId());

            first.lock();

            System.out.println("Voo " + flight.getId()
                    + " obteve conexão " + first.getId());

            Thread.sleep(200);

            System.out.println("Voo " + flight.getId()
                    + " aguardando conexão " + second.getId());

            second.lock();

            System.out.println("Voo " + flight.getId()
                    + " obteve conexão " + second.getId());

            flight.setStatus(FlightStatus.REGISTERING);

            Thread.sleep(500);

            SharedStatistics.completedFlights++;

            System.out.println("Voo " + flight.getId()
                    + " registrou a decolagem.");

            flight.setStatus(FlightStatus.FINISHED);

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