/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/24/2017.
 */
package gjb.interfaces;

/**
 * Interface for tasks, which are like "mini op modes" that can
 * run concurrently. Typically each task focuses on the running of
 * a single subsystem, but that need not be the case.
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

}
