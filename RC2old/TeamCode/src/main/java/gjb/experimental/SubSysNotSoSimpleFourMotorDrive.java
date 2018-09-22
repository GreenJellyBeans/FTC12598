/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/26/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.SubSystemInterface;

public class SubSysNotSoSimpleFourMotorDrive implements SubSystemInterface {
    final String THIS_COMPONENT = "S2MD"; // For "simple 2-motor drive"
    final public RuntimeSupportInterface rt;
    final public LoggingInterface log;
    final public Config config;

    public DcMotor leftDrive = null;
    public DcMotor rightDrive = null;
    public DcMotor fleftDrive = null;
    public DcMotor frightDrive = null;

    // Configuration parameters for this drive go here. Avoid hardcoded constants unless they are
    // unlikely to be changed even if there are changes to the drive details.
    public static class Config {
        public String leftMotorName;
        public String rightMotorName;
        public String fleftMotorName;
        public String frightMotorName;
        public boolean reverseMotors;

        public Config leftMotorName(String name) {
            this.leftMotorName = name;
            return this;
        }

        public Config rightMotorName(String name) {
            this.rightMotorName = name;
            return this;
        }
        public Config fleftMotorName(String name) {
            this.fleftMotorName = name;
            return this;
        }

        public Config frightMotorName(String name) {
            this.frightMotorName = name;
            return this;
        }

        public Config reverseMotors(boolean reverse) {
            this.reverseMotors = reverse;
            return this;
        }
    }

    public SubSysNotSoSimpleFourMotorDrive(RuntimeSupportInterface rt, Config c) {
        this.rt = rt;
        this.log = rt.getRootLog().newLogger(THIS_COMPONENT); // Create a child log.
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

        fleftDrive  = rt.hwLookup().getDcMotor("fleft_drive");
        frightDrive = rt.hwLookup().getDcMotor("fright_drive");
        fleftDrive.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        frightDrive.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors


        // Set all motors to zero power
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        fleftDrive.setPower(0);
        frightDrive.setPower(0);

        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fleftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.log.pri1(LoggingInterface.INIT_END, "");
    }

    @Override
    public void deinit() {
        this.log.pri1(LoggingInterface.DEINIT_START, "");
        // Set all motors to zero power
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        leftDrive = rightDrive = null;
        fleftDrive.setPower(0);
        frightDrive.setPower(0);
        fleftDrive = frightDrive = null;
        this.log.pri1(LoggingInterface.DEINIT_END, "");
    }
    public void desableEncoders () {
        rt.telemetry().log().add("IN disableEncoders");
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        fleftDrive.setPower(0);
        frightDrive.setPower(0);
        leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fleftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
    public void enableEncoders () {
        rt.telemetry().log().add("IN enableEncoders");
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        fleftDrive.setPower(0);
        frightDrive.setPower(0);
        leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        fleftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
}
