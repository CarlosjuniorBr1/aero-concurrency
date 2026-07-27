package org.air.export;

import org.air.monitor.ExperimentResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CsvExporter {

    public static void export(
            List<ExperimentResult> results,
            String fileName
    ) throws IOException {

        File dir = new File("results");

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File csv = new File(dir, fileName);

        try (PrintWriter writer =
                     new PrintWriter(new FileWriter(csv))) {

            writer.println(
                    "Strategy,Operations,ExecutionTime,OpsPerSecond,CPU,Memory,Deadlock,UnsafeCounter,SafeCounter"
            );

            for (ExperimentResult r : results) {

                writer.printf(
                        "%s,%d,%.3f,%.2f,%.2f,%.2f,%s,%d,%d%n",
                        r.getStrategy(),
                        r.getOperations(),
                        r.getExecutionTime(),
                        r.getOpsPerSecond(),
                        r.getCpu(),
                        r.getMemory() / 1024.0 / 1024.0,
                        r.isDeadlock(),
                        r.getUnsafeCounter(),
                        r.getSafeCounter()
                );
            }
        }

        System.out.println(
                "\nCSV salvo em: "
                        + csv.getAbsolutePath()
        );
    }

}