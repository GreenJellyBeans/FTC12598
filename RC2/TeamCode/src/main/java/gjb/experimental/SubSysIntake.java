/**
 *Class created by Aparna, Keya, and Mira
 * FTC #12598 on 10/8/17
 */
package gjb.experimental;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.SubSystemInterface;


public class SubSysIntake implements SubSystemInterface {
    final String THIS_COMPONENT = "SS_EMPTY"; // // Replace EMPTY by short word identifying task

    //final String THIS_COMPONENT = "T_EMPTY"; // Replace EMPTY by short word identifying task


    final double DILLO_FWD = 0.8; //change later
    final double DILLO_BKWD = -0.8; //change later


    final double BIN_IN_SPEED = 0.3;// change after testing
    final double BIN_OUT_SPEED = 0.7;// change after testing
    final double BIN_STOP = 0.5;//This value is completely customized for each individual CRServo, we got this value by testing out "random" values close to 0.5
    final double STRIDE_IN_POWER = 0.3; //change after testing
    final double STRIDE_OUT_POWER = 0.7; //change after testing

    final double STRIDE_STOP = 0.52; //same comment as BIN_STOP


    final RuntimeSupportInterface rt; // Runtime support
    final LoggingInterface log; // Logger
    boolean bFinished = true;
    boolean onInside = true;



    public static final double GULP_START = 0.5;
    public static final double BIN_START = 0.2;
    public static final double RATE = 0.1;
    // Place additional instance variables here - like hardware access objects
    /*
    DigitalChannel limitswitch_down; //prevents further downward motion
    DigitalChannel limitswitch_up; //prevents further upward motion
    */


    public Servo biggulp;
    public Servo bintake;

    public Servo strider;
    DigitalChannel limit_switch_out;
    DigitalChannel limit_switch_in;
    DigitalChannel limit_switch_mid;

    public DcMotor ArMadillo;
    DigitalChannel limit_switch_forward;
    DigitalChannel limit_switch_backward;

    //public Servo servo1; // there are two servos and a motor for the 2 part arm
   // public Servo servo2;
    //public DcMotor motor1;

    public SubSysIntake(RuntimeSupportInterface rt) {
        this.rt = rt;
        this.log = rt.getRootLog().newLogger(THIS_COMPONENT);
    }


    /********* START OF SUBSYSTEM INTERFACE METHODS ***************/
    @Override
    public void init() {
        this.log.pri1(LoggingInterface.INIT_START, "");
        // Any subsystem initialization code goes here.
        // Define and Initialize Motors


        biggulp = rt.hwLookup().getServo("big_gulp");
        bintake = rt.hwLookup().getServo("bintake");

        strider = rt.hwLookup().getServo("strider");
        limit_switch_in  = rt.hwLookup().getDigitalChannel("limit_switch_in");
        limit_switch_in.setMode(DigitalChannel.Mode.INPUT);
        limit_switch_out  = rt.hwLookup().getDigitalChannel("limit_switch_out");
        limit_switch_out.setMode(DigitalChannel.Mode.INPUT);
        limit_switch_mid = rt.hwLookup().getDigitalChannel("limit_switch_mid");
        limit_switch_mid.setMode(DigitalChannel.Mode.INPUT);

        //lookup and init the arm motor
        ArMadillo = rt.hwLookup().getDcMotor("ArMadillo");
        ArMadillo.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors, was FORWARD with tetrix
        ArMadillo.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        ArMadillo.setPower(0);
        ArMadillo.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        limit_switch_forward  = rt.hwLookup().getDigitalChannel("limit_switch_forward");
        limit_switch_forward.setMode(DigitalChannel.Mode.INPUT);
        limit_switch_backward  = rt.hwLookup().getDigitalChannel("limit_switch_backward");
        limit_switch_backward.setMode(DigitalChannel.Mode.INPUT);


        biggulp.setPosition(GULP_START);

        this.log.pri1(LoggingInterface.OTHER, "initialized marker servo");

        // Set lift motor to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.



    }


    @Override
    public void deinit() {
        this.log.pri1(LoggingInterface.DEINIT_START, "");
        // Place any shutdown/deinitialization code here  - this is called ONCE
        // after tasks & OpModes have stopped.

        this.log.pri1(LoggingInterface.DEINIT_END, "");

    }

    /************ END OF SUBSYSTEM INTERFACE METHODS ****************/
    boolean limit_in_pressed() {
        //we inverted the check since we are using magnetic limit switches
        boolean pressed = !this.limit_switch_in.getState();
        rt.telemetry().addData("limit_switch_in", pressed);
        return pressed;
    }
    boolean limit_out_pressed(){
        //we inverted the check since we are using magnetic limit switches
        boolean pressed = !this.limit_switch_out.getState();
        rt.telemetry().addData("limit_switch_out", pressed);
        return pressed;
    }

    boolean bean_slider_out(){
        boolean out = -rt.gamepad2().right_stick_y() > 0.2; // negating joystick value as a test
        return out;
    }

    boolean bean_slider_in(){
        boolean in = -rt.gamepad2().right_stick_y() < -0.2;
        return in;
    }

    boolean bean_bintake_on(){
        boolean spin = rt.gamepad2().dpad_down();
        return spin;
    }

    // Place additional helper methods here.
    public double rampedPower (DcMotor motor, double goalpower){
        double currentpower = motor.getPower();
        double newpower=0;
        if(goalpower==0){
            newpower=goalpower;
        } else if (Math.abs(currentpower - goalpower) < RATE){
            newpower = goalpower;
        } else {
            newpower = currentpower + Math.signum(goalpower-currentpower)*RATE;

        }
        return newpower;
    }
}