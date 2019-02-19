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
        if(!intakeS.limit_in_pressed())
            intakeS.strider.setPosition(intakeS.STRIDE_IN_POWER);
        if(intakeS.limit_in_pressed())
            intakeS.strider.setPosition(intakeS.STRIDE_STOP);
        // Code that must execute WHILE waiting for START goes here.
    }


    @Override
    public void start() {
        rt.resetStartTime();

        // Any code to execute ONCE just when the op mode is started goes here.
    }



    @Override
    public void loop() {

        //
        //  ---- BINTAKE LOGIC ------
        //
        if (intakeS.bean_bintake_in()) {
            intakeS.bintake.setPosition(intakeS.BIN_IN_SPEED);
            rt.telemetry().addData("Bintake", intakeS.BIN_IN_SPEED);
        } else {
            intakeS.bintake.setPosition(intakeS.BIN_STOP);
        }

        //
        //  ---- STRIDER LOGIC ------
        //

        rt.telemetry().addData("joystick_value", rt.gamepad2().right_stick_y());

        rt.telemetry().addData("onInside", intakeS.onInside);

        double power = intakeS.STRIDE_STOP;

        if (intakeS.limit_mid_pressed()) {
            power = intakeS.STRIDE_STOP;
            if (intakeS.bean_slider_out()) {
                power = intakeS.STRIDE_OUT_POWER;
            }
        } else if (intakeS.bean_slider_in()) {
            power = intakeS.STRIDE_IN_POWER;
        }

        if (intakeS.limit_in_pressed()) {
            power = intakeS.STRIDE_STOP;
            if (intakeS.bean_slider_out()) {
                power = intakeS.STRIDE_OUT_POWER;
            }
        } else if (intakeS.bean_slider_in()) {
            power = intakeS.STRIDE_IN_POWER;
        }

        if (intakeS.limit_out_pressed()) {
            //Can't go out anymore
            power = intakeS.STRIDE_STOP;
            if (intakeS.bean_slider_in()) {
                power = intakeS.STRIDE_IN_POWER;
            }
        } else if (intakeS.bean_slider_out()) {
            power = intakeS.STRIDE_OUT_POWER;
        }

        boolean midPressed = !intakeS.limit_switch_mid.getState();

        if(midPressed){
            if(power == intakeS.STRIDE_IN_POWER)
                intakeS.onInside = true;
            if(power == intakeS.STRIDE_OUT_POWER)
                intakeS.onInside = false;
        }

        if(rt.gamepad2().b() ==  true || !intakeS.bFinished) {
            intakeS.bFinished = false;
            if (!midPressed) {
                rt.telemetry().addData("limit_switch_mid", "not pressed");
                if (!intakeS.limit_switch_in.getState() || intakeS.onInside) {//Can't go in anymore/on the inside from middle
                    intakeS.onInside = true;
                    power = intakeS.STRIDE_OUT_POWER;
                    rt.telemetry().addData("limit_switch_in", "pressed");
                }
                if (!intakeS.limit_switch_out.getState() || !intakeS.onInside) {//Can't go out anymore/not on inside from middle
                    intakeS.onInside = false;
                    power = intakeS.STRIDE_IN_POWER;
                    rt.telemetry().addData("limit_switch_out", "pressed");
                }
            } else {
                power = intakeS.STRIDE_STOP;
                rt.telemetry().addData("limit_switch_mid", "pressed");
                intakeS.bFinished = true;
            }
        }

        intakeS.strider.setPosition(power);

        //
        // ------------ Telemetry ---------------------------------
        //

        boolean pressed = !intakeS.limit_switch_mid.getState();
        rt.telemetry().addData("limit_switch_mid pressed: ", pressed);
        rt.telemetry().addData("stride power", power);
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
