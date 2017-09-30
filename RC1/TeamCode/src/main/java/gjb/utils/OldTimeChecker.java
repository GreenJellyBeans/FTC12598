/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/??/2017.
 */
package gjb.utils;

public class OldTimeChecker {
    long startMs;
    long[] times;

    public OldTimeChecker() {

    }

    // Reset the time checker so that it starts now
    public void reset() {
        startMs = System.currentTimeMillis();
    }

    // Set the intervals  by specifying a list of durations, and also
    // a multiplicative scale factor to be applied to the durations.
    public void setTimesByDurations(int[] durations, int scaleFactor) {
        reset();
        times = new long[durations.length];
        long prevTime = 0;
        for (int i = 0; i < times.length; i++) {
            times[i] = prevTime + durations[i]*scaleFactor;
            prevTime = times[i];
        }
    }


    // returns the index i, where t_(i-1) < t <= t_i.
    public int getCurrentStage() {
        long elapsed = System.currentTimeMillis() - startMs;
        for (int i = 0; i < times.length; i++) {
            if (elapsed <= times[i]) {
                return i; // return the first i for which this is true.
            }
        }
        return times.length; // 1 more than last index if last time has expired.
    }

    public int getNumStages() {
        return times.length;
    }
}