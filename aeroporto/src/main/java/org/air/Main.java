package org.air;

import org.air.concurrency.SafeAtomicFlightStrategy;
import org.air.concurrency.SafeFlightStrategy;
import org.air.concurrency.UnsafeFlightStrategy;
import org.air.experiment.ExperimentRunner;

public class Main {

    public static void main(String[] args) {

        ExperimentRunner runner = new ExperimentRunner();

        System.out.println(
                runner.run(
                        10000,
                        new UnsafeFlightStrategy()));

        System.out.println(
                runner.run(
                        10000,
                        new SafeFlightStrategy()));

        System.out.println(
                runner.run(
                        10000,
                        new SafeAtomicFlightStrategy()));

    }

}