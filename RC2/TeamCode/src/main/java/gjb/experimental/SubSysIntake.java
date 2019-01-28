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
    final public RuntimeSupportInterface rt;
    final public LoggingInterface log;



    public static final double GULP_START = 0.5;
    public static final double BIN_START = 0.2;
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

        ArMadillo = rt.hwLookup().getDcMotor("ArMadillo");
        ArMadillo.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors, was FORWARD with tetrix
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

    // Place additional helper methods here.
}