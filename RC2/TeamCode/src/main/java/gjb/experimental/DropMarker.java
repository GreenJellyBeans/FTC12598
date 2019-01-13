
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;

/**
 * New version of auton code - with most of the logic moved to helper class
 * AutonWizard
  */

@Autonomous(name="DropMarker", group="Pushbot")
//@Disabled
public class DropMarker extends LinearOpMode {

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


        apu.dropMarker();
        apu.betterSleep(5000);
    }

}
