/**
 * Class created by Aparna, Keya, and Mira
 * FTC #12598 on 10/8/17
 */
package gjb.experimental;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.TaskInterface;


public class ITask_ArmWLimitSwitches implements TaskInterface {

    final String THIS_COMPONENT = "T_EMPTY"; // Replace EMPTY by short word identifying task
    final RuntimeSupportInterface rt; // Runtime support
    final LoggingInterface log; // Logger

    // Place additional instance variables here - like sub system objects..
    SubSysArm armS;

    // Modify this constructor to add any additional initialization parameters - see
    // other tasks for examples.
    public ITask_ArmWLimitSwitches(RuntimeSupportInterface rt, SubSysArm arm) {
        this.rt = rt;
        this.armS  = arm;
        this.log = rt.getRootLog().newLogger(THIS_COMPONENT);
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
        //if limit switch is high, set power to zer.
        //else set power to one
        //armM.leftDrive.setPower(1);
        // Set the red led on the DIM based on the input digital channel state.
        if (armS.limitswitch.getState()) {
            rt.telemetry().addData("limitswitch", "HIGH");
            armS.armM.setPower(0);
        } else {
            rt.telemetry().addData("LimitSwitch", "LOW");
            armS.armM.setPower(0.5);
        }
    }


    @Override
    public void stop() {
        this.log.pri1("STOP", "");
        // Place any shutdown/deinitialization code here  - this is called ONCE
        // when the task is to be stopped.
        armS.armM.setPower(0);
    }


    /************* END OF TASK INTERFACE METHODS ****************/


    // Place additional helper methods here.

}
