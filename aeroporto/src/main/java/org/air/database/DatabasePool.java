package org.air.database;

import java.util.ArrayList;
import java.util.List;

public class DatabasePool {

    private final List<DatabaseConnection> connections;

    public DatabasePool(int numberOfConnections) {

        connections = new ArrayList<>();

        for (int i = 0; i < numberOfConnections; i++) {
            connections.add(new DatabaseConnection(i));
        }
    }

    public DatabaseConnection getConnection(int id) {
        return connections.get(id);
    }

    public int size() {
        return connections.size();
    }

}