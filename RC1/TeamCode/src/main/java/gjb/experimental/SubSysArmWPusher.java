/**
 *Class created by Aparna, Keya, and Mira
 * FTC #12598 on 10/8/17
 */
package gjb.experimental;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.SubSystemInterface;


public class SubSysArmWPusher implements SubSystemInterface {
    final String THIS_COMPONENT = "SSP_EMPTY"; // // Replace EMPTY by short word identifying task
    final public RuntimeSupportInterface rt;
    final public LoggingInterface log;

    public static final double MID_SERVO       =  0.5 ;
    public static final double ARM_UP_POWER    =  0.225 ; //was .45
    public static final double ARM_DOWN_POWER  = -0.225 ; //was -.45

    // Place additional instance variables here - like hardware access objects
    DigitalChannel limitswitch_Y;
    DigitalChannel limitswitch_A;
    public DcMotor armM;
    public Servo left_dinosorvor   = null;
    public Servo right_dinosorvor   = null;

    // Modify this constructor to add any additional initialization parameters - see
    // other subsystems for examples.
    public SubSysArmWPusher(RuntimeSupportInterface rt) {
        this.rt = rt;
        this.log = rt.getRootLog().newLogger(THIS_COMPONENT); // Create a child log.
    }


    /********* START OF SUBSYSTEM INTERFACE METHODS ***************/

    @Override
    public void init() {
        this.log.pri1(LoggingInterface.INIT_START, "");
        // Any subsystem initialization code goes here.
        // Define and Initialize Motors
        armM = rt.hwLookup().getDcMotor("arm_motor");
        armM.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        limitswitch_A  = rt.hwLookup().getDigitalChannel("limit_switch_down");
        limitswitch_A.setMode(DigitalChannel.Mode.INPUT);
        limitswitch_Y  = rt.hwLookup().getDigitalChannel("limit_switch_up");
        limitswitch_Y.setMode(DigitalChannel.Mode.INPUT);

        // Set armM motor to zero power
        armM.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        armM.setPower(0);


        // Set armM motor to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        armM.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Set up the sorvors
        left_dinosorvor = rt.hwLookup().getServo("left_sorcerer");
        right_dinosorvor = rt.hwLookup().getServo("right_sorcerer");
        left_dinosorvor.setPosition(MID_SERVO);
        right_dinosorvor.setPosition(MID_SERVO);
        this.log.pri1(LoggingInterface.INIT_END, "");
    }


    @Override
    public void deinit() {
        this.log.pri1(LoggingInterface.DEINIT_START, "");
        // Place any shutdown/deinitialization code here  - this is called ONCE
        // after tasks & OpModes have stopped.
        armM.setPower(0);
        armM.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.log.pri1(LoggingInterface.DEINIT_END, "");

    }

    /************ END OF SUBSYSTEM INTERFACE METHODS ****************/

    // Place additional helper methods here.
}