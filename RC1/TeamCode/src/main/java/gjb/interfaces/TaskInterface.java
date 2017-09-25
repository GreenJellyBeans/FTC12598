package gjb.interfaces;

/**
 * Interface for tasks, which are like "mini op modes" that can
 * run concurrently. Typically each task focuses on the running of
 * a single subsystem, but that need not be the case.
 * Created by josephj on 9/24/2017.
 */

public interface TaskInterface {

    // Similar semantics to OpMode init()
    void init();

    // Similar semantics to OpMode init_loop()
    void init_loop();

    // Similar semantics to OpMode start()
    void start();

    // Similar semantics to OpMode loop()
    void loop();

    // Similar semantics to OpMode stop()
    void stop();

    // Similar semantics to OpMode getRunTime(). Should return sub millisecond
    // accuracy
    public double getRuntime();
}
