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
    public static final double START_POS = 0.25;
    public static final double DROP_POS = 0.9;
    public static final double BOAS_START = 0.2; //find number using servo test
    // Place additional instance variables here - like hardware access objects
    DigitalChannel limitswitch_down; //prevents further downward motion
    DigitalChannel limitswitch_up; //prevents further upward motion
    public DcMotor motorola; //lift motor
    public Servo markerpolo; //marker motor
    public Servo boas; //Stands for box on a stick :P
    // Modify this constructor to add any additional initialization parameters - see
    // other subsystems for examples.
    public SubSysLift(RuntimeSupportInterface rt) {
        this.rt = rt;
        this.log = rt.getRootLog().newLogger(THIS_COMPONENT); // Create a child log.
    }


    /********* START OF SUBSYSTEM INTERFACE METHODS ***************/

    @Override
    public void init() {
        this.log.pri1(LoggingInterface.INIT_START, "");
        // Any subsystem initialization code goes here.
        // Define and Initialize Motors
        motorola = rt.hwLookup().getDcMotor("lift_motor");
        motorola.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors, was FORWARD with tetrix
        limitswitch_up  = rt.hwLookup().getDigitalChannel("limit_switch_up");
        limitswitch_up.setMode(DigitalChannel.Mode.INPUT);
        limitswitch_down  = rt.hwLookup().getDigitalChannel("limit_switch_down");
        limitswitch_down.setMode(DigitalChannel.Mode.INPUT);
        markerpolo = rt.hwLookup().getServo("marker_polo");
        boas = rt.hwLookup().getServo("boas");
        // Set lift motor to zero power
        motorola.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorola.setPower(0);
        markerpolo.setPosition(START_POS);
        boas.setPosition(BOAS_START);
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