/**
 * Template created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/30/2017.
 * ADD/REPLACE THIS COMMENT BASED ON THE WHEN THE NEW TASK WAS CREATED
 */
package gjb.experimental;

import gjb.interfaces.*;
import gjb.utils.*;


public class ITask_Empty implements TaskInterface {

    final String THIS_COMPONENT = "T_EMPTY"; // Replace EMPTY by short word identifying task
    final RuntimeSupportInterface rt; // Runtime support
    final LoggingInterface log; // Logger
    // Place additional instance variables here - like sub system objects..


    // Modify this constructor to add any additional initialization parameters - see
    // other tasks for examples.
    public ITask_Empty(RuntimeSupportInterface rt ) {
        this.rt = rt;
        this.log = rt.logger().getRootLog().newLogger(THIS_COMPONENT);
    }


    /****** START OF TASK INTERFACE METHODS *********************/
    @Override
    public void init() {
        this.log.pri1(LoggingInterface.INIT_START, "");
        // Any task initialization code goes here.
        this.log.pri1(LoggingInterface.INIT_END, "");
    }


    @Override
    public void init_loop() {
        // Code that must execute WHILE waiting for START goes here.
    }


    @Override
    public void start() {
        rt.resetStartTime();
        // Any code to execute ONCE just when the op mode is started goes here.
    }


    @Override
    public void loop() {
        // Periodic code to be run when the task is actually running (after start and
        // before stop) goes here.

    }


    @Override
    public void stop() {
        this.log.pri1("STOP", "");
        // Place any shutdown/deinitialization code here  - this is called ONCE
        // when the task is to be stopped.
    }


    /************* END OF TASK INTERFACE METHODS ****************/


    // Place additional helper methods here.

}
