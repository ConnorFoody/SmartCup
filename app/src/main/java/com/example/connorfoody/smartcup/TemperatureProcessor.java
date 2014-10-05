package com.example.connorfoody.smartcup;

/**
 * Created by connorfoody on 10/5/14.
 */

// does all the math for processing the data
// basically just a helper for the bluetooth reader
public class TemperatureProcessor {

        // states
        public static final int increasing = 1; // when drink is poured in
        public static final int peak = 0; // when temp is at max (sensor == drink for first time)
        public static final int decreasing = -1; // drink is cooling
        public static final int resting = 2; // doing nothing, init vlaue
        public static final int max = 3; // between peak and decreasing
        public int state;

        // to smooth data
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
            // all our data came in quantized (sort of) so we smoothed it and took the derivative
            // of the smoothed data
            oldFiltered = temperatureFiltered;
            temperatureFiltered = (unfilteredTemp * FILTER_T) + ((1 - FILTER_T) * (temperatureFiltered));

            // we smoothed the derivative to help prevent noise
            slope = (temperatureFiltered - oldFiltered) / .5;
            slopeFiltered = (slope * FILTER_S) + ((1 - FILTER_S) * (slopeFiltered));

            if (slopeFiltered >= 0.5) {
                state = increasing;
            }
            // want to catch low end of T' = 0, so have very small value on down side
            else if (slopeFiltered < 0.50 && slopeFiltered > -.005 && state == increasing) {
                state = peak;
            }
            // get the value between peak and going down
            else if (slopeFiltered <= -0.005 && state == peak) {
                state = max;
            }
            else if(slopeFiltered <= -0.005 && state == max){
                state = decreasing;
            }
            return state;
        }

        // predict how long until drink will be cool (ideal temp is 136 F)
        public double predict(){
            if(temperatureFiltered > 136.5 ) {
                // need to keep above 118 to keep the root real
                // fit a quadratic equation to some samples we measured of coffee cooling, knowing the temp we can solve for time
                // t_initial will be larger whenever filtered temp is > 136, so do current - final to find remaining time
                initialTime = (.0771 + Math.sqrt(Math.pow(.0771, 2) - (4 * .00002656 * (173.3- temperatureFiltered))) ) / (2 * .00002656);
                finalTime = (.0771 + Math.sqrt(Math.pow(.0771, 2) - (4 * .00002656 * (173.3- 136))) ) / (2 * .00002656);
                return initialTime - finalTime;

            }else{
                // if we are at or below 136 then all done
                return 0;
            }
        }
        public double getTemperature(){
            return temperatureFiltered;
        }
}
