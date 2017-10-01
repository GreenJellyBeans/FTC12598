/**
 * Template created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/30/2017.
 * ADD/REPLACE THIS COMMENT BASED ON THE WHEN THE NEW TASK WAS CREATED
 */
package gjb.experimental;

import com.qualcomm.robotcore.hardware.AnalogOutput;
import com.qualcomm.robotcore.util.Range;

import gjb.interfaces.*;


public class SubSysEmpty implements SubSystemInterface {
    final String THIS_COMPONENT = "SS_EMPTY"; // // Replace EMPTY by short word identifying task
    final public RuntimeSupportInterface rt;
    final public LoggingInterface log;
    // Place additional instance variables here - like sub system objects..


    // Modify this constructor to add any additional initialization parameters - see
    // other subsystems for examples.
    public SubSysEmpty(RuntimeSupportInterface rt) {
        this.rt = rt;
        this.log = rt.logger().getRootLog().newLogger(THIS_COMPONENT); // Create a child log.
    }


    /********* START OF SUBSYSTEM INTERFACE METHODS ***************/

    @Override
    public void init() {
        this.log.pri1(LoggingInterface.INIT_START, "");
        // Any subsystem initialization code goes here.
        this.log.pri1(LoggingInterface.INIT_END, "");
    }


    @Override
    public void deinit() {
        this.log.pri1(LoggingInterface.DEINIT_START, "");
        // Place any shutdown/deinitialization code here  - this is called ONCE
        // after tasks & OpModes have stopped.
        this.log.pri1(LoggingInterface.DEINIT_END, "");

    }

    /************ END OF SUBSYSTEM INTERFACE METHODS ****************/

    // Place additional helper methods here.
}