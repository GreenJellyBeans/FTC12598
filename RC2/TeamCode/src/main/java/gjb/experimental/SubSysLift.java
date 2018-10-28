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

    public static final double LIFT_UP_POWER    =  0.45 ; //was .225
    public static final double LIFT_DOWN_POWER  = -0.45 ; //was -.225

    // Place additional instance variables here - like hardware access objects
    DigitalChannel limitswitch_down;
    DigitalChannel limitswitch_up;
    public DcMotor motor;

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
        motor = rt.hwLookup().getDcMotor("lift_motor");
        motor.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors, was FORWARD with tetrix
        limitswitch_up  = rt.hwLookup().getDigitalChannel("limit_switch_up");
        limitswitch_up.setMode(DigitalChannel.Mode.INPUT);
        limitswitch_down  = rt.hwLookup().getDigitalChannel("limit_switch_down");
        limitswitch_down.setMode(DigitalChannel.Mode.INPUT);

        // Set lift motor to zero power
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setPower(0);


        // Set lift motor to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


    }


    @Override
    public void deinit() {
        this.log.pri1(LoggingInterface.DEINIT_START, "");
        // Place any shutdown/deinitialization code here  - this is called ONCE
        // after tasks & OpModes have stopped.
        motor.setPower(0);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.log.pri1(LoggingInterface.DEINIT_END, "");

    }

    /************ END OF SUBSYSTEM INTERFACE METHODS ****************/

    // Place additional helper methods here.
}