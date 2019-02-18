
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;

/**
 * New version of auton code - with most of the logic moved to helper class
 * AutonWizard
  */

@Autonomous(name="Encoder_Test", group="Pushbot")
//@Disabled
public class AOpMode_EncoderTest extends LinearOpMode {

    /* Declare OpMode members. */

    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);
    SubSysIntake intake = new SubSysIntake(rt);
    SubSysLift lift = new SubSysLift(rt);
    AutonRoverRuckusWizard apu = new AutonRoverRuckusWizard(rt);
    final double ARM_POWER = 0.5;
    @Override
    public void runOpMode() {
        double timeoutS;
        intake.init();


        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        apu.betterSleep(1000);
        intake.ArMadillo.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intake.ArMadillo.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        intake.ArMadillo.setTargetPosition(1120);
        intake.ArMadillo.setPower(ARM_POWER);

        apu.log("done");
        apu.betterSleep(1000);
    }

}
