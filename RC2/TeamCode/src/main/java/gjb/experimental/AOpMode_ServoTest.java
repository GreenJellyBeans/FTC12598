
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;

/**
 * New version of auton code - with most of the logic moved to helper class
 * AutonWizard
  */

@Autonomous(name="AOpMode_ServoTest", group="Pushbot")
//@Disabled
public class AOpMode_ServoTest extends LinearOpMode {

    /* Declare OpMode members. */
    final String THIS_COMPONENT = "ServoTest";
    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);


    AutonRoverRuckusWizard apu = null;


    @Override
    public void runOpMode() {
        double timeoutS;
        log("about to init apu");
        apu = new AutonRoverRuckusWizard(rt) ;
        apu.init();

        // Wait for the game to start (driver presses PLAY)
        log("about to init apu");
        waitForStart();

        // Work the wand, detect jewel color and move forward/backward to dislodge
        // the jewel - this is code specific to the BLUE alliance
        apu.servoTest();
        log("done servo test");

        apu.deinit();
    }

    void log(String s) {
        rt.telemetry().log().add("OpMode: " + s);
    }
}
