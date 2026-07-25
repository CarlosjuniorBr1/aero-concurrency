package org.air.database;

import java.util.concurrent.locks.ReentrantLock;

public class DatabaseConnection {

    private final int id;

    private final ReentrantLock lock;

    public DatabaseConnection(int id, ReentrantLock lock) {
        this.id = id;
        this.lock = lock;
    }
}
