package gjb.experimental;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;

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
    RevBlinkinLedDriver blinkinLedDriver;
    RevBlinkinLedDriver.BlinkinPattern pattern;
    AutonRoverRuckusWizard wizard;

    //getBlinkinDriver


    public SubSysDiagnostics(RuntimeSupportInterface rt, AutonRoverRuckusWizard w) {
        this.wizard = w;
        this.rt = rt;
    }

    @Override
    public void init() {
        blinkinLedDriver = rt.hwLookup().getBlinkinDriver("blinkin");
        pattern = RevBlinkinLedDriver.BlinkinPattern.RAINBOW_RAINBOW_PALETTE;
        blinkinLedDriver.setPattern(pattern);

    }

    @Override
    public void deinit() {
        blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
    }

    protected void goBlack()
    {
        blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
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

}

