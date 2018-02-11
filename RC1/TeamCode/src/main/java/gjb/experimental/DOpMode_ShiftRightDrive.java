/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/30/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;

@TeleOp(name="DOpMode_ShiftRightDrive", group="Pushbot")
//@Disabled
/*
 *  This Driver Controlled OpMode does controls the wheels of the pushbot.
 */
public class DOpMode_ShiftRightDrive extends OpMode{
    final String THIS_COMPONENT = "DOM_driveOnlyPushBot";
    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);
    private ElapsedTime runtime = new ElapsedTime();
    // These are initialized during init()
    private  SubSysSimpleTwoMotorDrive drive;
    private LoggingInterface log;
    int status = 0;
    static final double      COUNTS_PER_MOTOR_REV    = 1120; // 28*7 cycles per shaft rev. Tetrix:1440
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);;
    /*************** START OF OPMODE INTERFACE METHODS **********************/

    @Override
    public void init() {
        log = rt.startLogging(DOpMode_ShiftRightDrive.class.toString());
        log.pri1(LoggingInterface.INIT_START, THIS_COMPONENT);

        SubSysSimpleTwoMotorDrive.Config driveConfig = new SubSysSimpleTwoMotorDrive.Config()
                .leftMotorName("left_drive")
                .rightMotorName("right_drive");
        drive = new SubSysSimpleTwoMotorDrive(rt, driveConfig);

        // Initialize the subsystem and associated task
        drive.init();
        drive.leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        drive.leftDrive.setPower(0);
        drive.rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        drive.rightDrive.setPower(0);


        log.pri1(LoggingInterface.INIT_END, THIS_COMPONENT);

    }


    @Override
    public void init_loop() {

    }

    @Override
    public void start() {

    }

    @Override
    public void loop() {

        if(rt.gamepad2().right_bumper()) {
            if (status == 0 && !drive.leftDrive.isBusy() && !drive.rightDrive.isBusy()) {
                status = 1;
                System.out.println("starting stage 1");
                //motor goes forward
                moveBy(-5.5, 0.0, 0.2);
            }
            if (status == 1 && !drive.leftDrive.isBusy() && !drive.rightDrive.isBusy()) {
                status = 2;
                System.out.println("starting stage 2");
                //motor goes backwards
                moveBy(0.0, -8.5, 0.2);
            }
            if (status == 2 && !drive.leftDrive.isBusy() && !drive.rightDrive.isBusy()) {
                status = 3;
                System.out.println("starting stage 3");
                //motor goes backwards
                moveBy(3.5, 3.5, 0.2);
            }
            if (status == 3 && !drive.leftDrive.isBusy() && !drive.rightDrive.isBusy()) {
                status = 4;
                System.out.println("starting stage 4");
                //motor goes backwards
                moveBy(-1.0, 1.0, 0.2);
            }
            if (status == 4 && !drive.leftDrive.isBusy() && !drive.rightDrive.isBusy()) { //busy thing
                status = 5;
                System.out.println("starting stage 5");
                //motor is set to 0.
                drive.leftDrive.setPower(0);
                drive.leftDrive.setPower(0);
            }
        }
    }

    @Override
    public void stop() {

        rt.stopLogging();
    }

    public void moveBy (double linch, double rinch, double power){
        int newLeftTarget = drive.leftDrive.getCurrentPosition() + (int) (COUNTS_PER_INCH * linch);
        drive.leftDrive.setTargetPosition(newLeftTarget);
        drive.leftDrive.setPower(Math.abs(power));

        int newRightTarget = drive.rightDrive.getCurrentPosition() + (int) (COUNTS_PER_INCH * rinch);
        drive.rightDrive.setTargetPosition(newRightTarget);
        drive.rightDrive.setPower(Math.abs(power));
    }
    /*************** END OF OPMODE INTERFACE METHODS ************************/
}




