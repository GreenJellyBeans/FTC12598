/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/26/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.SubSystemInterface;

public class SubSysMecDrive implements SubSystemInterface {
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

    public SubSysMecDrive(RuntimeSupportInterface rt, Config c) {
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
        leftDrive.setDirection(DcMotor.Direction. FORWARD); // Set to REVERSE if using AndyMark motors, was FORWARD
        rightDrive.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors, was REVERSE

        fleftDrive  = rt.hwLookup().getDcMotor("fleft_drive");
        frightDrive = rt.hwLookup().getDcMotor("fright_drive");
        fleftDrive.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors, was REVERSE
        frightDrive.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors, was FORWARD


        // Set all motors to zero power
        setMotorPowerAll(0,0,0,0);

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
        setMotorPowerAll(0,0,0,0);
        leftDrive = rightDrive = null;
        fleftDrive = frightDrive = null;
        this.log.pri1(LoggingInterface.DEINIT_END, "");
    }
    public void desableEncoders () {
        rt.telemetry().log().add("IN disableEncoders");
        setMotorPowerAll(0,0,0,0);
        leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fleftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
    public void enableEncoders () {
        rt.telemetry().log().add("IN enableEncoders");
        setMotorPowerAll(0,0, 0, 0);
        leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        fleftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
    public void setHybridPower (double forward, double strafe, double turn){
        // Let's clip anyways, incase we get faulty input
        forward = clipInput(forward);
        turn = clipInput(turn);
        strafe = clipInput(strafe);

        // Note: +ve strafe makes the robot go right, and with
        // the robot's front facing increasing x, to go right
        // means to go in the direction of decreasing y:
        //
        //                 ^ y-axis
        //      robot      |
        //    ...... FL    |
        //    .    .       --> x-axis
        //    ...... FR
        //
        double pFL = forward - strafe + turn;
        double pFR = forward + strafe - turn;
        double pBL = forward + strafe + turn;
        double pBR = forward - strafe - turn;

        // m is the max absolute value of the individual motor power amounts. If it is too small, we stop all motors.
        double m = Math.max(Math.max(Math.abs(pFL), Math.abs(pFR)), Math.max(Math.abs(pBL), Math.abs(pBR)));
        if (m<0.1) {
            setMotorPowerAll(0, 0, 0, 0);
        } else {
            // Scale everything so no magnitude exeeds 1
            double scale = Math.min(1/m, 1);
            pFL *= scale;
            pFR *= scale;
            pBL *= scale;
            pBR *= scale;
            setMotorPowerAll(pFL, pFR, pBL, pBR);
        }
    }
    // Clips input to be within [-1, 1]
    double clipInput(double in) {
        return Math.max(Math.min(in, 1), -1);
    }

    void setMotorPowerAll(double pFL, double pFR, double pBL, double pBR) {
        fleftDrive.setPower(pFL);
        frightDrive.setPower(pFR);
        leftDrive.setPower(pBL);
        rightDrive.setPower(pBR);


    }
    void stopAndResetAllEncoders(){
        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }
}

