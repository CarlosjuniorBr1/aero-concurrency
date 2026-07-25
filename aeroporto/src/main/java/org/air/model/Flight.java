package org.air.model;

public class Flight {

    private final Integer id;

    private final Integer leftConnection;

    private final Integer rightConnection;

    private FlightStatus status;

    public Flight(Integer id, Integer leftConnection, Integer rightConnection) {
        this.id = id;
        this.leftConnection = leftConnection;
        this.rightConnection = rightConnection;k
        this.status = FlightStatus.PREPARING;
    }

    public Integer getId() {
        return id;
    }

    public Integer getLeftConnection() {
        return leftConnection;
    }

    public Integer getRightConnection() {
        return rightConnection;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

}
