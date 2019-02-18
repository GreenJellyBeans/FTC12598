/**
 * Class created by Aparna, Keya, and Mira
 * FTC #12598 on 10/8/17
 */
package gjb.experimental;

import com.qualcomm.robotcore.util.Range;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.TaskInterface;


public class ITask_TwoPartArm implements TaskInterface {

    final String THIS_COMPONENT = "T_EMPTY"; // Replace EMPTY by short word identifying task

    final RuntimeSupportInterface rt; // Runtime support
    final LoggingInterface log; // Logger

    // Place additional instance variables here - like sub system objects..
    SubSysIntake intakeS;
    SubSysLift biggulp;



    // Modify this constructor to add any additional initialization parameters - see
    // other tasks for examples.
    public ITask_TwoPartArm(RuntimeSupportInterface rt, SubSysIntake intake) {
        this.rt = rt;
        this.intakeS = intake;
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


   // @Override
    public void loop_OLD() {


    }

    @Override
    public void loop() {

        bintakeSliderLogic();
        biggulpLogic();
        arMadilloLogic();

    }



    @Override
    public void stop() {
        this.log.pri1("STOP", "");
        // Place any shutdown/deinitialization code here  - this is called ONCE
        // when the task is to be stopped.
    }


    /************* END OF TASK INTERFACE METHODS ****************/


    public void bintakeSliderLogic(){
        //
        //  ---- BINTAKE LOGIC ------
        //
        if (intakeS.bean_bintake_on()) {
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

    public void biggulpLogic(){
        if(rt.gamepad2().a()){
            biggulp.stickPos=biggulp.GULP_START;
        }
        // Use gamepad left & right Bumpers to move the mineral putter up and down
        if (rt.gamepad2().right_bumper()){
            biggulp.stickPos = biggulp.stickPos + biggulp.STICK_SPEED;
        } else if (rt.gamepad2().left_bumper()){
            biggulp.stickPos = biggulp.stickPos - biggulp.STICK_SPEED;
        }

        biggulp.stickPos = Range.clip(biggulp.stickPos, biggulp.MIN_STICK, biggulp.MAX_STICK);
        biggulp.biggulp.setPosition(biggulp.stickPos);


        // Send telemetry message to signify robot running;
        // rt.telemetry().addData("claw",  "Offset = %.2f", clawOffset);
    }

    public void arMadilloLogic(){
        rt.telemetry().addData("joystick_value", rt.gamepad2().right_stick_y());
        //high value indicated being pressed
        //we took out the inverted thing because we are using normal limit switches for the arm

        double power = 0;
        if (intakeS.limit_switch_forward.getState()) {
            //Can't go forward anymore
            rt.telemetry().addData("limit_switch_forward", intakeS.limit_switch_forward.getState());
            power = 0;
            if (-rt.gamepad2().left_stick_y() > 0.2) { //negating joystick value as a test, and it works
                power = intakeS.DILLO_BKWD;
            }
        } else if (-rt.gamepad2().left_stick_y() < -0.2) { //change value later, testing needs to be done, negating joystick valur as a test
            rt.telemetry().addData("limit_switch_forward", "LOW");
            power = intakeS.DILLO_FWD;
        }

        //we took out the inverted thing because we are using normal limit switches for the arm
        if (intakeS.limit_switch_backward.getState()) {
            //Can't go backward anymore
            rt.telemetry().addData("limit_switch_backward", "HIGH");
            power = 0;
            if (-rt.gamepad2().left_stick_y() < -0.2) {//change value later, testing needs to be done //negating joystick valur as a test
                power = intakeS.DILLO_FWD;
            }
        } else if (-rt.gamepad2().left_stick_y() > 0.2){ //change value later, testing needs to be done //negating joystick valur as a test
            rt.telemetry().addData("limit_switch_backward", "LOW");

            power = intakeS.DILLO_BKWD;
        }
        rt.telemetry().addData("motor power", power);
        intakeS.ArMadillo.setPower(power);



        // Send telemetry message to signify robot running;
        // rt.telemetry().addData("claw",  "Offset = %.2f", clawOffset);
    }
    // Place additional helper methods here.

}
