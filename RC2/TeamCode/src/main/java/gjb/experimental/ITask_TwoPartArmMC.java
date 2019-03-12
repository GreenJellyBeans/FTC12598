/**
 * Class created by Aparna, Keya, and Mira
 * FTC #12598 on 10/8/17
 */
package gjb.experimental;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.TaskInterface;


public class ITask_TwoPartArmMC implements TaskInterface {

    final String THIS_COMPONENT = "T_EMPTY"; // Replace EMPTY by short word identifying task

    final RuntimeSupportInterface rt; // Runtime support
    final LoggingInterface log; // Logger

    // Place additional instance variables here - like sub system objects..
    SubSysIntake intakeS;
    boolean assistActive = false;
    boolean encoderReset = false; //arm motor

    // Modify this constructor to add any additional initialization parameters - see
    // other tasks for examples.
    public ITask_TwoPartArmMC(RuntimeSupportInterface rt, SubSysIntake intake, SubSysLift lift) {
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
        if(!intakeS.limit_out_pressed())
            intakeS.strider.setPosition(intakeS.STRIDE_OUT_POWER);
        if(intakeS.limit_out_pressed())
            intakeS.strider.setPosition(intakeS.STRIDE_STOP);
        // Code that must execute WHILE waiting for START goes here.
    }


    @Override
    public void start() {
        rt.resetStartTime();
        // Any code to execute ONCE just when the op mode is started goes here.
        autonAssist_start();
    }


   // @Override
    public void loop_OLD() {


    }

    @Override
    public void loop() {

        bintakeSliderLogic();
        biggulpLogic();
        arMadilloLogic();
       // autonAssist();
        autonAssist_loop();
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
        double bintakePower;
        if (intakeS.bean_bintake_in()) {
            bintakePower = intakeS.BIN_IN_SPEED;
        } else if (intakeS.bean_bintake_out()) {
            bintakePower = intakeS.BIN_OUT_SPEED;
        } else {
            bintakePower = intakeS.BIN_STOP;
        }


        /**

         //Trying to find max & bin speeds for bintake in & out

         if(rt.gamepad2().dpad_up())
            intakeS.BIN_IN_SPEED+=.01;
        if(rt.gamepad2().dpad_down())
            intakeS.BIN_IN_SPEED-=.01;

        if(rt.gamepad2().dpad_right())
            intakeS.BIN_OUT_SPEED+=.01;
        if(rt.gamepad2().dpad_left())
            intakeS.BIN_OUT_SPEED-=.01;

        rt.telemetry().addData("BIN_IN_SPEED", intakeS.BIN_OUT_SPEED);
        rt.telemetry().addData("BIN_OUT_SPEED", intakeS.BIN_IN_SPEED);

        **/


        rt.telemetry().addData("g2 right bumper", rt.gamepad2().right_bumper());
        rt.telemetry().addData("g2 left bumper", rt.gamepad2().left_bumper());
        rt.telemetry().addData("Bintake", bintakePower);
        intakeS.bintake.setPosition(bintakePower);

        //
        //  ---- STRIDER LOGIC ------
        //

        rt.telemetry().addData("joystick_value", rt.gamepad2().right_stick_y());

        rt.telemetry().addData("onInside", intakeS.onInside);



        if (intakeS.limit_mid_pressed()) {
            intakeS.stridePower = intakeS.STRIDE_STOP;
            if (intakeS.bean_slider_out()) {
                intakeS.stridePower = intakeS.STRIDE_OUT_POWER;
            }
        } else if (intakeS.bean_slider_in()) {
            intakeS.stridePower = intakeS.STRIDE_IN_POWER;
        }

        if(!intakeS.bean_slider_out() && !intakeS.bean_slider_in())
            intakeS.stridePower = intakeS.STRIDE_STOP;

        if (intakeS.limit_in_pressed()) {
            intakeS.stridePower = intakeS.STRIDE_STOP;
            if (intakeS.bean_slider_out()) {
                intakeS.stridePower = intakeS.STRIDE_OUT_POWER;
            }
        } else if (intakeS.bean_slider_in()) {
            intakeS.stridePower = intakeS.STRIDE_IN_POWER;
        }

        if (intakeS.limit_out_pressed()) {
            //Can't go out anymore
            intakeS.stridePower = intakeS.STRIDE_STOP;
            if (intakeS.bean_slider_in()) {
                intakeS.stridePower = intakeS.STRIDE_IN_POWER;
            }
        } else if (intakeS.bean_slider_out()) {
            intakeS.stridePower = intakeS.STRIDE_OUT_POWER;
        }


        intakeS.strider.setPosition(intakeS.stridePower);

        //
        // ------------ Telemetry ---------------------------------
        //

        boolean pressed = intakeS.limit_mid_pressed();
        rt.telemetry().addData("limit_switch_mid pressed: ", pressed);
        rt.telemetry().addData("stride power", intakeS.stridePower);
    }

    public void biggulpLogic(){
        double newPos = intakeS.stickPos;
        if(rt.gamepad2().a()){
            newPos=intakeS.GULP_END;
        }
        // Use gamepad left & right Bumpers to move the mineral putter up and down
        if (rt.gamepad1().dpad_down()){
            newPos = newPos + intakeS.STICK_SPEED;
        } else if (rt.gamepad1().dpad_up()){
            newPos = newPos - intakeS.STICK_SPEED;
        }

        newPos = Range.clip(newPos, intakeS.MIN_STICK, intakeS.MAX_STICK);
        if(intakeS.stickPos!= newPos) {
            intakeS.biggulp.setPosition(newPos);
            intakeS.stickPos = newPos;
        }

        // Send telemetry message to signify robot running;
        // rt.telemetry().addData("claw",  "Offset = %.2f", clawOffset);
    }


    public void arMadilloLogic(){
        rt.telemetry().addData("LEFT joystick Y value", rt.gamepad2().left_stick_y());
        rt.telemetry().addData("RIGHT joystick Y value", rt.gamepad2().right_stick_y());

        //high value indicated being pressed
        //we took out the inverted thing because we are using normal limit switches for the arm

        if(assistActive ) {
            return; //--------------------------- EARLY RETURN -------------------------------\\
        }
        double power = 0;
        if (intakeS.limit_forward_pressed()) {
            //Can't go forward/downwards anymore
            power = 0;
            if (intakeS.bean_ArmaDillo_backward()) {
                power = intakeS.variablePower();
            }
        } else if (intakeS.bean_ArmaDillo_forward()) {
              power = intakeS.variablePower();
        }


        if (intakeS.limit_backward_pressed()) {
            //Can't go backward/upwards anymore
            power = 0;
            if (intakeS.bean_ArmaDillo_forward()) {
                power = intakeS.variablePower();
            }
        } else if (intakeS.bean_ArmaDillo_backward()){
            power = intakeS.variablePower();
        }


        rt.telemetry().addData("variable power", power);
        double newpower = intakeS.rampedPower(intakeS.ArmaDillo, power);
        intakeS.ArmaDillo.setPower(newpower);
        rt.telemetry().addData("newpower", newpower);


        // Send telemetry message to signify robot running;
        // rt.telemetry().addData("claw",  "Offset = %.2f", clawOffset);
    }
    // mira's auton assist code-----------------------------
    public void autonAssist(){
        if(intakeS.limit_mid_pressed()){
            if(intakeS.stridePower == intakeS.STRIDE_IN_POWER) {
                intakeS.onInside = true;
            }
            if(intakeS.stridePower == intakeS.STRIDE_OUT_POWER) {
                intakeS.onInside = false;
            }
        }

        //rt.telemetry().addData("assistContinue", intakeS.assistContinue);

        /*if(rt.gamepad2().b()){
            if(intakeS.assistContinue) {//to cancel
                intakeS.assistContinue = false;
            } else {
                intakeS.assistContinue = true;
            }
        }*/

        //if(intakeS.assistContinue) {
        if(rt.gamepad2().b()){
            if(intakeS.biggulp.getPosition() != intakeS.MAX_STICK)
                intakeS.biggulp.setPosition(intakeS.MAX_STICK);
            if (!intakeS.limit_out_pressed()) {
                rt.telemetry().addData("limit_switch_out", "not pressed");
                if(intakeS.strider.getPosition() != intakeS.STRIDE_OUT_POWER){
                    intakeS.strider.setPosition(intakeS.STRIDE_OUT_POWER);
                }

                /*if (!intakeS.limit_switch_in.getState() || intakeS.onInside) {//Can't go in anymore/on the inside from middle
                    intakeS.onInside = true;
                    intakeS.strider.setPosition(intakeS.STRIDE_OUT_POWER);
                    //intakeS.stridePower = intakeS.STRIDE_OUT_POWER;
                    //rt.telemetry().addData("limit_switch_in", "pressed");
                }else {
                    intakeS.onInside = false;
                    intakeS.strider.setPosition(intakeS.STRIDE_IN_POWER);
                }*/

                /*if (!intakeS.limit_switch_out.getState() || !intakeS.onInside) {//Can't go out anymore/not on inside from middle
                    intakeS.onInside = false;
                    intakeS.stridePower = intakeS.STRIDE_IN_POWER;
                    rt.telemetry().addData("limit_switch_out", "pressed");
                }*/
            } else {
                intakeS.stridePower = intakeS.STRIDE_STOP;
               // rt.telemetry().addData("limit_switch_mid", "pressed");
                if(!intakeS.limit_backward_pressed()){
                    intakeS.ArmaDillo.setPower(intakeS.rampedPower(intakeS.ArmaDillo, intakeS.DILLO_BKWD-.05));
                }else{
                    intakeS.ArmaDillo.setPower(intakeS.rampedPower(intakeS.ArmaDillo, 0));
                    //intakeS.assistContinue = false;
                }
            }
            //intakeS.strider.setPosition(intakeS.stridePower);

        }

        // end of Mira's auton assist code----------------------------------------------------
        //start of Keya's---------------------------------------------------------------------
    }
    public void autonAssist_loop(){
        //as soon as it hits the backward limit switch, if the current encoder value is not 0, set it to 0
        if(intakeS.limit_backward_pressed()){
            if(assistActive){
                autonAssist_end();
            } else if(intakeS.ArmaDillo.getMode()!= DcMotor.RunMode.STOP_AND_RESET_ENCODER){
                intakeS.ArmaDillo.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                encoderReset = true;
                log("resetting encoder");
            }
        }

        if(beanActive()){
            if (intakeS.ArmaDillo.getMode()!= DcMotor.RunMode.RUN_WITHOUT_ENCODER){
                intakeS.ArmaDillo.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                log("Switching to running w/o encoder");
            }
            autonAssist_cancel();
        } else if (rt.gamepad2().x()&& !assistActive){
            assistActive = true;
            log("Assist is starting");
            if(!encoderReset){
                log("encoders weren't reset!!");
                autonAssist_cancel();
            } else {
                intakeS.ArmaDillo.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                intakeS.biggulp.setPosition(intakeS.GULP_START);
                intakeS.ArmaDillo.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                intakeS.ArmaDillo.setTargetPosition(0);
                intakeS.ArmaDillo.setPower(intakeS.DILLO_BKWD);
                log("assist is done from assist");
            }
        }
    }

    public void autonAssist_cancel(){
        if(assistActive) {
            intakeS.ArmaDillo.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            intakeS.ArmaDillo.setPower(0);
            log("assist was canceled");
            assistActive = false;
        }
    }

    public void autonAssist_start(){
        //set power to be negative (safely goes back)
        if(!intakeS.limit_backward_pressed()) {
            intakeS.ArmaDillo.setPower(intakeS.DILLO_BKWD);
            log("ARM POWER IS NEGATIVE");
            log("set arm power to negative");
        }
    }

    public void autonAssist_end(){
        log("assist had finished??? from auton end");
        if(assistActive) {
            intakeS.ArmaDillo.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            intakeS.ArmaDillo.setPower(0);
            log("assist has finished");
            assistActive = false;
        }
    }
    //if anything on the 2nd controller or the dpad on the first controller is pressed, then it returns true, because a driver is pressing something
    public boolean beanActive(){
        return intakeS.bean_slider_out() || intakeS.bean_ArmaDillo_backward() || intakeS.bean_ArmaDillo_forward()
                || intakeS.bean_slider_in()
                || rt.gamepad1().dpad_up() || rt.gamepad1().dpad_down();
    }
    /* planning:
    when start is pressed, Start method gets called
        set power to be negative (safely goes back)
        loop: as soon as it hits the backward limit switch, if the current encoder value is not 0, set it to 0
        create a method: if anything on the 2nd controller or the dpad on the first controller is pressed, then it returns false
        it will disable auton assist if the method returns true.
        If the bumper is pressed, enable auton assist, and set motor to RUN_USING_ENCODERS
        startAssist: set biggulp to 0.9, setPosition to 0
     */
    // end of keya's auton assist code---------------------------------------------------

    // Place additional helper methods here.
    void log(String s) {
        rt.telemetry().log().add("Arm: " + s);
    }
}
