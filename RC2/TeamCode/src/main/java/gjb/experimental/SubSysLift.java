/**
 *Class created by Aparna, Keya, and Mira
 * FTC #12598 on 10/8/17
 */
package gjb.experimental;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.SubSystemInterface;


public class SubSysLift implements SubSystemInterface {
    final String THIS_COMPONENT = "SS_EMPTY"; // // Replace EMPTY by short word identifying task
    final public RuntimeSupportInterface rt;
    final public LoggingInterface log;

    public static final double LIFT_DOWN_POWER    =  -0.75 ; //was .225, negative power means it goes down
    public static final double LIFT_UP_POWER  = 0.75 ; //was -.225
    public static final double START_POS = 0.5; //temporarily changed bc spacer of limit swith is too fat
    public static final double DROP_POS = 1.0; //servo is now boas, was 0.9
    public static final double GULP_START = 0.5;
    public static final double GULP_LOW = 1.0;
    // Place additional instance variables here - like hardware access objects
    DigitalChannel limitswitch_down; //prevents further downward motion
    DigitalChannel limitswitch_up; //prevents further upward motion
    public DcMotor motorola; //lift motor
    public Servo markerpolo; //team marker servo

    public Servo biggulp;
    final double MIN_STICK = 0.0;
    final double MAX_STICK = 0.85;
    final double STICK_SPEED = 0.005;// taken from last year's servo claws
    double stickPos =  MIN_STICK;
    //public Servo servo1; // there are two servos and a motor for the 2 part arm
   // public Servo servo2;
    //public DcMotor motor1;

    public SubSysLift(RuntimeSupportInterface rt) {
        this.rt = rt;
        this.log = rt.getRootLog().newLogger(THIS_COMPONENT);
    }


    /********* START OF SUBSYSTEM INTERFACE METHODS ***************/
    @Override
    public void init() {
        this.log.pri1(LoggingInterface.INIT_START, "");
        // Any subsystem initialization code goes here.
        // Define and Initialize Motors
        motorola = rt.hwLookup().getDcMotor("lift_motor");
        motorola.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark-40 motors, FORWARD with tetrix or AM-20
        limitswitch_up  = rt.hwLookup().getDigitalChannel("limit_switch_up");
        limitswitch_up.setMode(DigitalChannel.Mode.INPUT);
        limitswitch_down  = rt.hwLookup().getDigitalChannel("limit_switch_down");
        limitswitch_down.setMode(DigitalChannel.Mode.INPUT);
        markerpolo = rt.hwLookup().getServo("marker_polo");

        biggulp = rt.hwLookup().getServo("big_gulp");
        // Set lift motor to zero power
        motorola.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorola.setPower(0);
        markerpolo.setPosition(START_POS);

        biggulp.setPosition(GULP_LOW);
        //we will put the init back for the intake when it is less flimsy
        this.log.pri1(LoggingInterface.OTHER, "initialized marker servo");

        // Set lift motor to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        motorola.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


    }


    @Override
    public void deinit() {
        this.log.pri1(LoggingInterface.DEINIT_START, "");
        // Place any shutdown/deinitialization code here  - this is called ONCE
        // after tasks & OpModes have stopped.
        motorola.setPower(0);
        motorola.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.log.pri1(LoggingInterface.DEINIT_END, "");

    }

    /************ END OF SUBSYSTEM INTERFACE METHODS ****************/

    // Place additional helper methods here.
}