package org.air;

import org.air.concurrency.SafeAtomicFlightStrategy;
import org.air.concurrency.SafeFlightStrategy;
import org.air.concurrency.UnsafeFlightStrategy;
import org.air.experiment.ExperimentRunner;
import org.air.export.CsvExporter;
import org.air.monitor.ExperimentResult;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("================================");
        System.out.println("Aeroporto - Experimentos");
        System.out.println("================================");

        System.out.println(
                "CPUs disponíveis: "
                        + Runtime.getRuntime().availableProcessors()
        );

        System.out.println(
                "Memória máxima da JVM: "
                        + Runtime.getRuntime().maxMemory()
                        / 1024
                        / 1024
                        + " MB"
        );

        System.out.println();

        List<ExperimentResult> results = new ArrayList<>();

        ExperimentRunner runner = new ExperimentRunner();

        int[] operationsList = {
                10_000,
                100_000,
                1_000_000
        };

        try {

            for (int operations : operationsList) {

                System.out.println();
                System.out.println("################################");
                System.out.println(
                        "Iniciando testes com "
                                + operations
                                + " operações"
                );
                System.out.println("################################");
                System.out.println();

                ExperimentResult unsafeResult = runner.run(
                        operations,
                        new UnsafeFlightStrategy()
                );

                results.add(unsafeResult);
                System.out.println(unsafeResult);

                ExperimentResult safeResult = runner.run(
                        operations,
                        new SafeFlightStrategy()
                );

                results.add(safeResult);
                System.out.println(safeResult);

                ExperimentResult atomicResult = runner.run(
                        operations,
                        new SafeAtomicFlightStrategy()
                );

                results.add(atomicResult);
                System.out.println(atomicResult);
            }

            CsvExporter.export(
                    results,
                    "benchmark.csv"
            );

            System.out.println();
            System.out.println("================================");
            System.out.println("Experimentos concluídos");
            System.out.println("================================");
            System.out.println(
                    "Total de experimentos: "
                            + results.size()
            );
            System.out.println(
                    "Arquivo gerado: results/benchmark.csv"
            );

        } catch (Exception e) {

            System.err.println(
                    "Erro durante a execução dos experimentos: "
                            + e.getMessage()
            );

            e.printStackTrace();
            System.exit(1);
        }
    }
}