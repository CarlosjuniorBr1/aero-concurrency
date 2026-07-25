package org.air.monitor;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import com.sun.management.OperatingSystemMXBean;

public class SystemMonitor {

    private final Runtime runtime;

    private final OperatingSystemMXBean osBean;

    private final ThreadMXBean threadBean;

    public SystemMonitor() {

        runtime = Runtime.getRuntime();

        osBean = (OperatingSystemMXBean)
                ManagementFactory.getOperatingSystemMXBean();

        threadBean = ManagementFactory.getThreadMXBean();
    }

    public double getCpuLoad() {
        return osBean.getProcessCpuLoad() * 100;
    }

    public long getUsedMemory() {
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public long getMaxMemory() {
        return runtime.maxMemory();
    }

    public int getThreadCount() {
        return threadBean.getThreadCount();
    }

    public boolean hasDeadlock() {

        long[] ids = threadBean.findDeadlockedThreads();

        return ids != null && ids.length > 0;
    }

}