/**
 * Class created by Aparna, Keya, and Mira
 * FTC #12598 on 10/8/17
 */
package gjb.experimental;

import com.qualcomm.robotcore.util.Range;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.TaskInterface;


public class ITask_BoxServo implements TaskInterface {

    final String THIS_COMPONENT = "T_EMPTY"; // Replace EMPTY by short word identifying task
    final double MIN_STICK = -0.5;
    final double MAX_STICK = 0.09;
    final double STICK_SPEED = 0.01;// taken from last year's servo claws
    final RuntimeSupportInterface rt; // Runtime support
    final LoggingInterface log; // Logger

    // Place additional instance variables here - like sub system objects..
    SubSysLift boas;
    double stickPos = - 0.5;

    // Modify this constructor to add any additional initialization parameters - see
    // other tasks for examples.
    public ITask_BoxServo(RuntimeSupportInterface rt, SubSysLift boas) {
        this.rt = rt;
        this.boas  = boas;
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
        /*if (armS.limitswitch_Y.getState()) {
           // rt.telemetry().addData("limitswitch", "HIGH");
            armS.armM.setPower(0);
        } else {
          //  rt.telemetry().addData("LimitSwitch", "LOW");
            armS.armM.setPower(0.5);
        }
        */
    }

    @Override
    public void loop() {

        // Use gamepad left & right Bumpers to move the box servo up and down
        if (rt.gamepad2().right_bumper()){
            stickPos = stickPos + STICK_SPEED;
        } else if (rt.gamepad2().left_bumper()){
            stickPos = stickPos - STICK_SPEED;
        }

        stickPos = Range.clip(stickPos, MIN_STICK, MAX_STICK);
        boas.boas.setPosition(stickPos);


        // Send telemetry message to signify robot running;
       // rt.telemetry().addData("claw",  "Offset = %.2f", clawOffset);
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
