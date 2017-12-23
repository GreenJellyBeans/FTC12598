
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;

/**
 * New version of auton code - with most of the logic moved to helper class
 * AutonWizard
  */

@Autonomous(name="AOpMode_FinalAuton_BLUE", group="Pushbot")
//@Disabled
public class AOpMode_FinalAutonBlueAllianceNew extends LinearOpMode {

    /* Declare OpMode members. */
    final String THIS_COMPONENT = "AOpMode_SimpleAuton";
    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);


    AutonWizard apu = null;

    @Override
    public void runOpMode() {
        double timeoutS;

        apu = new AutonWizard(rt) ;
        apu.init();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Work the wand, detect jewel color and move forward/backward to dislodge
        // the jewel - this is code specific to the BLUE alliance
        apu.getJewelBlueAlliance();
    }

}
