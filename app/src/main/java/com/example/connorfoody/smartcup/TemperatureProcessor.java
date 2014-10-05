package com.example.connorfoody.smartcup;

/**
 * Created by connorfoody on 10/5/14.
 */
public class TemperatureProcessor {

        public static final int increasing = 1;
        public static final int peak = 0;
        public static final int decreasing = -1;
        public static final int resting = 2;
        public static final int max = 3;
        public int state;


        double FILTER_T = .3;
        double FILTER_S = .5;

        double temperatureFiltered;
        double oldFiltered;
        double slopeFiltered;
        double slope;
        double initialTime;
        double finalTime;

        public TemperatureProcessor()
        {
            temperatureFiltered = 0;
            slopeFiltered = 0;
            state = resting;
            //constructor

        }

        public int Update(double unfilteredTemp) {
            oldFiltered = temperatureFiltered;
            temperatureFiltered = (unfilteredTemp * FILTER_T) + ((1 - FILTER_T) * (temperatureFiltered));

            slope = (temperatureFiltered - oldFiltered) / .5;
            slopeFiltered = (slope * FILTER_S) + ((1 - FILTER_S) * (slopeFiltered));

            if (slopeFiltered >= 0.5) { //pick a better value
                state = increasing;
            } else if (slopeFiltered < 0.50 && slopeFiltered > -.005 && state == increasing) {
                state = peak;
            } else if (slopeFiltered <= -0.005 && state == peak) {
                state = max;
            }
            else if(slopeFiltered <= -0.005 && state == max){
                state = decreasing;
            }
            return state;
        }

        public double predict(){
            if(temperatureFiltered > 136.5 ) {
                // need to keep above 118 to keep the root real
                initialTime = (.0771 + Math.sqrt(Math.pow(.0771, 2) - (4 * .00002656 * (173.3- temperatureFiltered))) ) / (2 * .00002656);
                finalTime = (.0771 + Math.sqrt(Math.pow(.0771, 2) - (4 * .00002656 * (173.3- 136))) ) / (2 * .00002656);
                return initialTime - finalTime;

            }else{
                return 0;
            }
        }


        public double getTemperature(){
            return temperatureFiltered;
        }
}
