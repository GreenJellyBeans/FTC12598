/**
 * Class created by Aparna, Keya, and Mira
 * FTC #12598 on 10/8/17
 */
package gjb.experimental;

import com.qualcomm.robotcore.util.Range;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.TaskInterface;


public class ITask_BintakeSlide implements TaskInterface {

    final String THIS_COMPONENT = "T_EMPTY"; // Replace EMPTY by short word identifying task

    final double BIN_IN_SPEED = 0.3;// change after testing
    final double BIN_OUT_SPEED = 0.7;// change after testing
    final double STRIDE_IN_POWER = 0.3; //change after testing
    final double STRIDE_OUT_POWER = 0.7; //change after testing
    final RuntimeSupportInterface rt; // Runtime support
    final LoggingInterface log; // Logger

    // Place additional instance variables here - like sub system objects..
    SubSysIntake intakeS;


    // Modify this constructor to add any additional initialization parameters - see
    // other tasks for examples.
    public ITask_BintakeSlide(RuntimeSupportInterface rt, SubSysIntake intake) {
        this.rt = rt;
        this.intakeS  = intake;
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
        if(rt.gamepad2().dpad_down()) {
            intakeS.bintake.setPosition(BIN_IN_SPEED);
       }
        if(rt.gamepad2().dpad_up()) {
            intakeS.bintake.setPosition(BIN_OUT_SPEED);
        }

        //we inverted the check since we are using magnetic limit switches
        double power = 0.5;
        if (!intakeS.limit_switch_in.getState()) {
            //Can't go in anymore
            rt.telemetry().addData("limit_switch_in", "HIGH");
            if (rt.gamepad2().right_stick_y() > 0.2);
                power = STRIDE_OUT_POWER;
        } else if (rt.gamepad2().right_stick_y() < -0.2){ //change value later, testing needs to be done
            rt.telemetry().addData("limit_switch_in", "LOW");
            power = STRIDE_IN_POWER;

        }
        //we inverted the check since we are using magnetic limit switches
        if (!intakeS.limit_switch_out.getState()) {
            //Can't go out anymore
            rt.telemetry().addData("limit_switch_out", "HIGH");
            power = 0;
            if (rt.gamepad2().right_stick_y() < -0.2) //change value later, testing needs to be done
                power = STRIDE_IN_POWER;

        } else if (rt.gamepad2().right_stick_y() > 0.2){ //change value later, testing needs to be done
            rt.telemetry().addData("limit_switch_out", "LOW");
            power = STRIDE_OUT_POWER;
        }

        intakeS.strider.setPosition(power);



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
