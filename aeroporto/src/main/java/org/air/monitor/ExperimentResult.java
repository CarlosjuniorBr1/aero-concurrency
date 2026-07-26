package org.air.monitor;

public class ExperimentResult {

    private final String strategy;
    private final int operations;
    private final double executionTime;
    private final double opsPerSecond;
    private final double cpu;
    private final long memory;
    private final boolean deadlock;
    private final int unsafeCounter;
    private final int safeCounter;

    public ExperimentResult(
            String strategy,
            int operations,
            double executionTime,
            double opsPerSecond,
            double cpu,
            long memory,
            boolean deadlock,
            int unsafeCounter,
            int safeCounter
    ) {
        this.strategy = strategy;
        this.operations = operations;
        this.executionTime = executionTime;
        this.opsPerSecond = opsPerSecond;
        this.cpu = cpu;
        this.memory = memory;
        this.deadlock = deadlock;
        this.unsafeCounter = unsafeCounter;
        this.safeCounter = safeCounter;
    }

    @Override
    public String toString() {

        return """
                ================================
                Estratégia : %s
                Operações  : %d
                Tempo (s)  : %.3f
                Ops/s      : %.2f
                CPU        : %.2f %%
                Memória    : %.2f MB
                Deadlock   : %s
                Unsafe Cnt : %d
                Safe Cnt   : %d
                ================================
                """.formatted(
                strategy,
                operations,
                executionTime,
                opsPerSecond,
                cpu,
                memory / 1024.0 / 1024.0,
                deadlock,
                unsafeCounter,
                safeCounter
        );
    }
}