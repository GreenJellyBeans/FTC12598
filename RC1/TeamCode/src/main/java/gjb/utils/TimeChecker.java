/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/??/2017.
 */
package gjb.utils;
import java.lang.System;

import gjb.interfaces.RuntimeSupportInterface;

public class TimeChecker {
    // We need this to get elapsed time - we do not want to use
    // System.currentTimeMillis or any other system call because we want to be
    // able to setup unit tests with sumulated time. Besides, for FTC, we want to be consistant
    // in the use of time functions.
    final RuntimeSupportInterface rt;
    // All times in seconds (and fractional seconds)
    double start;
    double[] times;

    public TimeChecker(RuntimeSupportInterface rt) {
        this.rt = rt;
    }

    // Reset the time checker so that it starts now
    public void reset() {
        start = rt.getRuntime();
    }

    // Set state times given a list of individual stage times. These stimes are multiplied
    // by {scaleFactor}. An initial delay of {startDelay} is then applied.
    public void setTimesByDurations(double[] durations, double startDelay, double scaleFactor) {
        reset();
        times = new double[durations.length];
        double prevTime = startDelay;
        for (int i = 0; i < times.length; i++) {
            times[i] = prevTime + durations[i] * scaleFactor;
            prevTime = times[i];
        }
    }


    // returns the index i, where t_(i-1) < t <= t_i.
    public int getCurrentStage() {
        double elapsed = rt.getRuntime() - start;
        for (int i = 0; i < times.length; i++) {
            if (elapsed <= times[i]) {
                return i; // return the first i for which this is true.
            }
        }
        return times.length; // 1 more than last index if last time has expired.
    }

    // returns true IFF time has moved beyond the last stage.
    public boolean expired() {
        return (times.length == 0) || (times[times.length-1] < rt.getRuntime());
    }

    public int getNumStages() {
        return times.length;
    }
}