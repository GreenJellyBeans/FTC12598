/**
 * Class created by Aparna, Keya, and Mira
 * FTC #12598 on 10/8/17
 */
package gjb.experimental;

import com.qualcomm.robotcore.util.Range;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.TaskInterface;


public class ITask_ArmWLimitSwitches implements TaskInterface {

    final String THIS_COMPONENT = "T_EMPTY"; // Replace EMPTY by short word identifying task
    final double CLAW_SPEED = 0.02;
    final RuntimeSupportInterface rt; // Runtime support
    final LoggingInterface log; // Logger

    // Place additional instance variables here - like sub system objects..
    SubSysArm armS;
    double clawOffset = 0.0;

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


   // @Override
    public void loop_OLD() {
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
    public void loop() {
        if (armS.limitswitch.getState()) {
            rt.telemetry().addData("limitswitch", "HIGH");
            armS.armM.setPower(0);
        } else {
            rt.telemetry().addData("LimitSwitch", "LOW");
            armS.armM.setPower(0.5);
        }

        // Use gamepad left & right Bumpers to open and close the claw
        if (rt.gamepad1().right_bumper())
            clawOffset += CLAW_SPEED;
        else if (rt.gamepad1().left_bumper())
            clawOffset -= CLAW_SPEED;

        // Move both servos to new position.  Assume servos are mirror image of each other.
        clawOffset = Range.clip(clawOffset, -0.5, 0.5);
        //robot.leftClaw.setPosition(armS.MID_SERVO + clawOffset);
        //robot.rightClaw.setPosition(armS.MID_SERVO - clawOffset);

        // Use gamepad buttons to move the arm up (Y) and down (A)
        if (rt.gamepad1().y())
            armS.armM.setPower(armS.ARM_UP_POWER);
        else if (rt.gamepad1().a())
            armS.armM.setPower(armS.ARM_DOWN_POWER);
        else
            armS.armM.setPower(0.0);

        // Send telemetry message to signify robot running;
        rt.telemetry().addData("claw",  "Offset = %.2f", clawOffset);
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
