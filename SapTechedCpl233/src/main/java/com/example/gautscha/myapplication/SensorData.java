package com.example.gautscha.myapplication;

public class SensorData {

    public double startTime;
    public double xValue;
    public double yValue;
    public double zValue;

    public SensorData(double startTime, double xValue, double yValue, double zValue)
    {
        this.startTime = startTime;
        this.xValue = xValue;
        this.yValue = yValue;
        this.zValue = zValue;
    }
}
