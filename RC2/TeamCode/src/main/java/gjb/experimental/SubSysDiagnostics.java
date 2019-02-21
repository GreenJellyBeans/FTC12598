package gjb.experimental;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.SubSystemInterface;

/*
Diagnostics Planning:
Encoders/Motors
	Four wheels
	The lift motor?
	The arm motor?
Limit switches
	Two for the lift
IMU
	test them both

Goes forward 12 inches
	Check if all the encoders are roughly equal
Goes backward x inches
	Same thing
Sets all four motor values are, reports each of their values
Human can turn each wheel manually one rotation, should report current values.
Stall detection - Apply power to the motors

Limit switches - human presses limit switches, they should report their values
IMU - Put the robot on the turn table, and see if the imus report roughly the same values
 */
public class SubSysDiagnostics implements SubSystemInterface {
    final public RuntimeSupportInterface rt;
    AutonRoverRuckusWizard wizard;
    SubSysVision vision;
    SubSysLift lift;
    SubSysIntake intake;
    DcMotor motor;
    private BNO055IMU imu;
    //getBlinkinDriver

    public SubSysDiagnostics(RuntimeSupportInterface rt, AutonRoverRuckusWizard w) {
        this.wizard = w;// could be null
        this.rt = rt;
    }

    @Override
    public void init() {
        //lookup and init the arm motor
        motor = rt.hwLookup().getDcMotor("ArmaDillo");
        motor.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors, was FORWARD with tetrix
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setPower(0);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void deinit() {

    }


    public void diagnosticEncoderMotor(){
         wizard.drive.stopAndResetAllEncoders();
         wizard.log("right after init:");
         wizard.log("fl:" + wizard.drive.fleftDrive.getCurrentPosition());
         wizard.log("fr:" + wizard.drive.frightDrive.getCurrentPosition());
         wizard.log("bl:" + wizard.drive.leftDrive.getCurrentPosition());
         wizard.log("br:" + wizard.drive.rightDrive.getCurrentPosition());
         wizard.encoderDriveMec(0.4, 12, 3);
         wizard.log("after going forward:");
         wizard.log("fl:" + wizard.drive.fleftDrive.getCurrentPosition());
         wizard.log("fr:" + wizard.drive.frightDrive.getCurrentPosition());
         wizard.log("bl:" + wizard.drive.leftDrive.getCurrentPosition());
         wizard.log("br:" + wizard.drive.rightDrive.getCurrentPosition());
         wizard.encoderDriveMec(0.4, -15, 3);
         wizard.log("after going backward");
         wizard.log("fl:" + wizard.drive.fleftDrive.getCurrentPosition());
         wizard.log("fr:" + wizard.drive.frightDrive.getCurrentPosition());
         wizard.log("bl:" + wizard.drive.leftDrive.getCurrentPosition());
         wizard.log("br:" + wizard.drive.rightDrive.getCurrentPosition());
         wizard.betterSleep(10000);
    }
    public void diagnosticsAllServos(){
        wizard.log("servo for mineral sampling should be moving:");
        vision.minerservor.setPosition(0.49);
        wizard.betterSleep(500);
        vision.minerservor.setPosition(0.8);
        wizard.betterSleep(500);
        vision.minerservor.setPosition(1.0);
        wizard.betterSleep(100);
        wizard.log("servo for dropping the marker should be moving:");
        lift.markerpolo.setPosition(0.5);
        wizard.betterSleep(500);
        lift.markerpolo.setPosition(1.0);
        wizard.betterSleep(500);
    }
    public  boolean isStalling(){
        double xa = imu.getLinearAcceleration().xAccel;
        wizard.log("xAccel" + xa);
        return false;
    }
    public void dilloSet() {
        intake = new SubSysIntake(rt);
        intake.init();
        wizard.betterSleep(4000);
        intake.ArmaDillo.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intake.ArmaDillo.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        intake.ArmaDillo.setTargetPosition(62);
       intake.ArmaDillo.setPower(wizard.ARM_POWER);
       /* wizard.betterSleep(3000);
        intake.ArmaDillo.setTargetPosition(-100);
        intake.ArmaDillo.setPower(wizard.ARM_POWER);
        wizard.betterSleep(3000);
        intake.ArmaDillo.setTargetPosition(-150);
        intake.ArmaDillo.setPower(wizard.ARM_POWER);
        wizard.betterSleep(3000);
        intake.ArmaDillo.setTargetPosition(-200);
        intake.ArmaDillo.setPower(wizard.ARM_POWER);
        wizard.betterSleep(3000);
        intake.ArmaDillo.setTargetPosition(-250);
        intake.ArmaDillo.setPower(wizard.ARM_POWER);
*/
        wizard.log("done");
        wizard.betterSleep(10000);
    }
    public void dilloReset() {
        wizard.betterSleep(1000);
        intake.ArmaDillo.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intake.ArmaDillo.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        intake.ArmaDillo.setTargetPosition(-100);
        intake.ArmaDillo.setPower(wizard.ARM_POWER);

        wizard.log("done");
        wizard.betterSleep(1000);
    }

}

