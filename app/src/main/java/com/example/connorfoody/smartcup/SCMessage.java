package com.example.connorfoody.smartcup;

/**
 * Created by connorfoody on 10/5/14.
 */
public class SCMessage {
    public int state; // state from the temp manager
    public double temp; // temp from the sensor
    public double predict; // predicted value


    public SCMessage(int state_, double temp_, double predict_){
        state = state_;
        temp = temp_;
        predict = predict_;

    }
}
