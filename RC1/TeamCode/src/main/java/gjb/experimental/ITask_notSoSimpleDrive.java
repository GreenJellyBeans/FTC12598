/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/26/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.hardware.DcMotor;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.TaskInterface;

public class ITask_notSoSimpleDrive implements TaskInterface {
    final String THIS_COMPONENT = "TASK_SD"; // For "Task simple drive"
    final RuntimeSupportInterface rt;
    final SubSysSimpleTwoMotorDrive drive;
    final LoggingInterface log;
    int right_status = -1;
    int left_status = -1;

    public ITask_notSoSimpleDrive(RuntimeSupportInterface rt, SubSysSimpleTwoMotorDrive ssDrive) {
        this.rt = rt;
        this.drive = ssDrive;
        this.log = rt.getRootLog().newLogger(THIS_COMPONENT);
    }

    @Override
    public void init() {
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {

    }

    @Override
    public void loop() {
        double left;
        double right;

        // Run wheels in tank mode (note: The joystick goes negative when pushed forwards, so negate it)
        left = -rt.gamepad1().left_stick_y();
        right = -rt.gamepad1().right_stick_y();
        left = adjustPower2(left);
        right = adjustPower2(right);

        rt.telemetry().addData("left",  "%.2f", left);
        rt.telemetry().addData("right", "%.2f", right);
        rt.telemetry().addData("LDrvBusy", drive.leftDrive.isBusy());
        rt.telemetry().addData("RDrvBusy", drive.rightDrive.isBusy());
        rt.telemetry().addData("left_status", left_status);
        rt.telemetry().addData("right_status", right_status);

        if(rt.gamepad2().left_bumper()){
            rt.telemetry().addData("Leftbumper", "on");
            doShiftLeft();

        } else {
            rt.telemetry().addData("Leftbumper", "off");
            drive.leftDrive.setPower(left);
            drive.rightDrive.setPower(right);
            if (left_status != -1) {
                drive.desableEncoders(); // in case doShiftLeft is stopped in the middle of method
                rt.telemetry().log().add("dis encoder from joystick");
                left_status = -1;
            }
        }

        rt.telemetry().update();
     }

    private double adjustPower2(double x) {
        double y = x;
        double firstSlope = 0.2;
        double change_x = 0.5;
        double change_y = firstSlope * change_x;
        double secondSlope = (1.0 - change_y)/(1.0-change_x);
        if (Math.abs(x) <= change_x) { // -0.5 < x > 0.5
            y = x * firstSlope;
        } else {
            if (x > change_x) {
                y  = change_y + secondSlope * (x - change_x);
            } else if (x < - change_x) {
                y = -change_y + secondSlope * (x  + change_x);
            }
        }
        return y;
    }

    @Override
    public void stop() {
        this.log.pri1("STOP", "");
        drive.deinit();
    }
    void doShiftLeft () {
        if (left_status == -1) {
            left_status = 0;
            drive.enableEncoders();
            rt.telemetry().log().add("finished stage -1");
        }
        if (left_status == 0 && !drive.leftDrive.isBusy() && !drive.rightDrive.isBusy()) {
            left_status = 1;
            System.out.println("starting stage 1");
            //motor goes forward
            moveBy(0.0, -5.5, 0.2);
        }
        if (left_status == 1 && !drive.leftDrive.isBusy() && !drive.rightDrive.isBusy()) {
            left_status = 2;
            System.out.println("starting stage 2");
            //motor goes backwards
            moveBy(-8.5, 0.0, 0.2);
        }
        if (left_status == 2 && !drive.leftDrive.isBusy() && !drive.rightDrive.isBusy()) {
            left_status = 3;
            System.out.println("starting stage 3");
            //motor goes backwards
            moveBy(3.5, 3.5, 0.2);
        }
        if (left_status == 3 && !drive.leftDrive.isBusy() && !drive.rightDrive.isBusy()) {
            left_status = 4;
            System.out.println("starting stage 4");
            //motor goes backwards
            moveBy(1.0, -1.0, 0.2);
        }
        if (left_status == 4 && !drive.leftDrive.isBusy() && !drive.rightDrive.isBusy()) { //busy thing
            left_status = 0;
            System.out.println("starting stage 5");
            //motor is set to 0.
            drive.desableEncoders();
        }
    }
    void moveBy (double linch, double rinch, double power){
        int newLeftTarget = drive.leftDrive.getCurrentPosition() + (int) (AutonWizard.COUNTS_PER_INCH * linch);
        drive.leftDrive.setTargetPosition(newLeftTarget);
        drive.leftDrive.setPower(Math.abs(power));

        int newRightTarget = drive.rightDrive.getCurrentPosition() + (int) (AutonWizard.COUNTS_PER_INCH * rinch);
        drive.rightDrive.setTargetPosition(newRightTarget);
        drive.rightDrive.setPower(Math.abs(power));
    }

}
