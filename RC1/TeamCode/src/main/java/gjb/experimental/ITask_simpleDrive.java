/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/26/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.Range;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.TaskInterface;
import gjb.utils.Logger;

public class ITask_simpleDrive implements TaskInterface {
    final String THIS_COMPONENT = "TASK_SD"; // For "Task simple drive"
    final RuntimeSupportInterface rt;
    final SubSysSimpleTwoMotorDrive drive;
    final LoggingInterface log;

    public ITask_simpleDrive(RuntimeSupportInterface rt, SubSysSimpleTwoMotorDrive ssDrive) {
        this.rt = rt;
        this.drive = ssDrive;
        this.log = rt.getRootLog().newLogger(THIS_COMPONENT);
    }

    @Override
    public void init() {
        this.log.pri1(LoggingInterface.INIT_START, "");
        this.log.pri1(LoggingInterface.INIT_END, "");
    }

    @Override
    public void init_loop() {
        this.log.pri3("init_loop called");
    }

    @Override
    public void start() {
        this.log.pri1("START", "");
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

        drive.leftDrive.setPower(left);
        drive.rightDrive.setPower(right);

        rt.telemetry().addData("left",  "%.2f", left);
        rt.telemetry().addData("right", "%.2f", right);

    }

    private double adjustPower(double x) {
        double y = x;
        double firstSlope = 0.4;
        double secondSlope = 9.0/7.5;
        if (Math.abs(x) <= 0.25) { // -0.25 < x > 0.25
            y = x * firstSlope;
        } else {
            if (x > 0.25) {
                y  = 0.1 + secondSlope * (x - 0.25);
            } else if (x < - 0.25) {
                y = -0.1 + secondSlope * (x  + 0.25);
            }
        }
        return y;
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

}
