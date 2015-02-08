package com.builders.life.data.events;

import com.builders.life.data.Sensor;

public class SensorRangeEvent {
    private Sensor sensor;

    public SensorRangeEvent(Sensor sensor) {
        this.sensor = sensor;
    }

    public Sensor getSensor() {
        return sensor;
    }
}
