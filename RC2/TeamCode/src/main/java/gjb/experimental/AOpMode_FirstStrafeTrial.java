
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;

/**
 * New version of auton code - with most of the logic moved to helper class
 * AutonWizard
  */

@Autonomous(name="First_Strafe_Trial", group="Pushbot")
//@Disabled
public class AOpMode_FirstStrafeTrial extends LinearOpMode {

    /* Declare OpMode members. */

    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);


    AutonRoverRuckusWizard apu = null;

    @Override
    public void runOpMode() {
        double timeoutS;

        apu = new AutonRoverRuckusWizard(rt) ;
        apu.init();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Work the wand, detect jewel color and move forward/backward to dislodge
        // the jewel - this is code specific to the BLUE alliance
       // apu.encoderCrabMec(0.3, 20, 3000);
        apu.imuBearingMec(0.5, 135, 3);
        apu.betterSleep(10000);
        apu.imuBearingMec(1.0, 135, 3);
        apu.deinit();
    }

}
