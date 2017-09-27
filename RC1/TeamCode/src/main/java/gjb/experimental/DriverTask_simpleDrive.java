package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.Range;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.TaskInterface;

/**
 * Created by josephj on 9/26/2017.
 */

public class DriverTask_simpleDrive implements TaskInterface {
    final String THIS_COMPONENT = "TASK_SD"; // For "Task simple drive"
    final OpMode om;
    final SubSysSimpleTwoMotorDrive drive;
    final LoggingInterface log;

    public DriverTask_simpleDrive(OpMode om, SubSysSimpleTwoMotorDrive ssDrive, LoggingInterface log) {
        this.om = om;
        this.drive = ssDrive;
        this.log = log.newLogger(THIS_COMPONENT);
    }

    @Override
    public void init() {
           /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        this.log.pri1(LoggingInterface.INIT_START, "");
        drive.init();
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
        left = -om.gamepad1.left_stick_y;
        right = -om.gamepad1.right_stick_y;

        drive.leftDrive.setPower(left);
        drive.rightDrive.setPower(right);

        om.telemetry.addData("left",  "%.2f", left);
        om.telemetry.addData("right", "%.2f", right);

    }

    @Override
    public void stop() {
        this.log.pri1("STOP", "");
        drive.deinit();
    }

    @Override
    public double getRuntime() {
        return 0;
    }
}
