package org.air.monitor;

public class ExperimentResult {

    private double executionTime;

    private double cpuLoad;

    private long usedMemory;

    private long maxMemory;

    private double operationsPerSecond;

    private boolean deadlock;

    private boolean raceCondition;

    public double getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(double executionTime) {
        this.executionTime = executionTime;
    }

    public double getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(double cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(long usedMemory) {
        this.usedMemory = usedMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(long maxMemory) {
        this.maxMemory = maxMemory;
    }

    public double getOperationsPerSecond() {
        return operationsPerSecond;
    }

    public void setOperationsPerSecond(double operationsPerSecond) {
        this.operationsPerSecond = operationsPerSecond;
    }

    public boolean isDeadlock() {
        return deadlock;
    }

    public void setDeadlock(boolean deadlock) {
        this.deadlock = deadlock;
    }

    public boolean isRaceCondition() {
        return raceCondition;
    }

    public void setRaceCondition(boolean raceCondition) {
        this.raceCondition = raceCondition;
    }
}