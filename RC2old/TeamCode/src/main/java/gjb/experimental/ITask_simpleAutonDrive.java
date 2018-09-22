/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/26/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.TaskInterface;

public class ITask_simpleAutonDrive implements TaskInterface {
    final String THIS_COMPONENT = "TASK_SD"; // For "Task simple drive"
    final RuntimeSupportInterface rt;
    final SubSysSimpleTwoMotorDrive drive;
    final LoggingInterface log;
    final double FORWARD_INCHES;
    final double SPEEDO = 0.2; //rigudigudfhisugf
    static final double     COUNTS_PER_MOTOR_REV    = 1120; // 28*7 cycles per shaft rev. Tetrix:1440
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);


    public ITask_simpleAutonDrive(RuntimeSupportInterface rt, SubSysSimpleTwoMotorDrive ssDrive, double forwardInches) {
        this.rt = rt;
        this.drive = ssDrive;
        this.log = rt.getRootLog().newLogger(THIS_COMPONENT);
        FORWARD_INCHES = forwardInches;
    }

    @Override
    public void init() {
        this.log.pri1(LoggingInterface.INIT_START, "");

        // Send telemetry message to signify robot waiting;
        rt.telemetry().addData("Status", "Resetting Encoders");    //
        rt.telemetry().update();

        drive.leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drive.rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        drive.leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        drive.rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to indicate successful Encoder reset
        rt.telemetry().addData("StartEncoder",  "Starting at %7d :%7d",
                drive.leftDrive.getCurrentPosition(),
                drive.rightDrive.getCurrentPosition());
        rt.telemetry().update();

        this.log.pri1(LoggingInterface.INIT_END, "");
    }

    @Override
    public void init_loop() {
        this.log.pri3("init_loop called");
    }

    @Override
    public void start() {
        this.log.pri1("START", "");
        int newLeftTarget = drive.leftDrive.getCurrentPosition() + (int)(FORWARD_INCHES * COUNTS_PER_INCH);
        int newRightTarget = drive.rightDrive.getCurrentPosition() + (int)(FORWARD_INCHES * COUNTS_PER_INCH);

        // Turn On RUN_TO_POSITION
        drive.leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        drive.rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        drive.leftDrive.setTargetPosition(newLeftTarget);
        drive.rightDrive.setTargetPosition(newRightTarget);

        rt.resetStartTime();
        drive.leftDrive.setPower(Math.abs(SPEEDO));
        drive.rightDrive.setPower(Math.abs(SPEEDO));

    }

    @Override
    public void loop() {
        if (rt.gamepad1().a()) {
            // Send telemetry message to indicate successful Encoder reset
            rt.telemetry().addData("LoopEncoder",  "Time %f; Currently at %7d :%7d",
                    rt.getRuntime(),
                    drive.leftDrive.getCurrentPosition(),
                    drive.rightDrive.getCurrentPosition());
            rt.telemetry().update();
        }
    }

    @Override
    public void stop() {
        this.log.pri1("STOP", "");
        // Stop all motion;
        drive.leftDrive.setPower(0);
        drive.rightDrive.setPower(0);

        // Turn off RUN_TO_POSITION
        drive.leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        drive.rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to indicate successful Encoder reset
        rt.telemetry().addData("StopEncoder",  "Stopping at %7d :%7d",
                drive.leftDrive.getCurrentPosition(),
                drive.rightDrive.getCurrentPosition());
        rt.telemetry().update();


        drive.deinit();
    }

}
