import java.lang.System;
// History: September 2017 - JMJ - Created

class TimeChecker {
    long startMs;
    long[] times;

    public TimeChecker() {

    }

    // Reset the time checker so that it starts now
    public void reset() {
        startMs = System.currentTimeMillis();
    }

    // Set the intervals  by specifying a list of durations, and also
    // a multiplicative scale factor to be applied to the durations.
    void setTimesByDurations(int[] durations, int scaleFactor) {
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