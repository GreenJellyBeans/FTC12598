package gjb.experimental;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import gjb.interfaces.HardwareLookupInterface;
import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.SubSystemInterface;

/**
 * Created by josephj on 9/26/2017.
 */

public class SubSysSimpleTwoMotorDrive implements SubSystemInterface {
    final String THIS_COMPONENT = "S2MD"; // For "simple 2-motor drive"
    final public RuntimeSupportInterface rt;
    final public LoggingInterface log;
    final public Config config;

    public DcMotor leftDrive   = null;
    public DcMotor  rightDrive  = null;

    // Configuration parameters for this drive go here. Avoid hardcoded constants unless they are
    // unlikely to be changed even if there are changes to the drive details.
    public static class Config {
        public String leftMotorName;
        public String rightMotorName;

        public Config leftMotorName(String name) {
            this.leftMotorName = name;
            return this;
        }

        public Config rightMotorName(String name) {
            this.rightMotorName = name;
            return this;
        }
    }

    public SubSysSimpleTwoMotorDrive(RuntimeSupportInterface rt, Config c) {
        this.rt = rt;
        this.log = rt.rootLog().newLogger(THIS_COMPONENT); // Create a child log.
        config = c;
    }

    @Override
    public void init() {
        this.log.pri1(LoggingInterface.INIT_START, "");

        // Define and Initialize Motors
        leftDrive  = rt.hwLookup().getDcMotor("left_drive");
        rightDrive = rt.hwLookup().getDcMotor("right_drive");
        leftDrive.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        rightDrive.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors

        // Set all motors to zero power
        leftDrive.setPower(0);
        rightDrive.setPower(0);

        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        this.log.pri1(LoggingInterface.INIT_END, "");
    }

    @Override
    public void deinit() {
        this.log.pri1(LoggingInterface.DEINIT_START, "");
        // Set all motors to zero power
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        leftDrive = rightDrive = null;
        this.log.pri1(LoggingInterface.DEINIT_END, "");
    }
}
