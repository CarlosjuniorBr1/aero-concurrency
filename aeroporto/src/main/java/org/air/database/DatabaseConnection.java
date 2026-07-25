package org.air.database;

import java.util.concurrent.locks.ReentrantLock;

public class DatabaseConnection {

    private final int id;

    private final ReentrantLock lock;

    public DatabaseConnection(int id) {
        this.id = id;
        this.lock = new ReentrantLock();
    }

    public int getId() {
        return id;
    }

    public ReentrantLock getLock() {
        return lock;
    }

}