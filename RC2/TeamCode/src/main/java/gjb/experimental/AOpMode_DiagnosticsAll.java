
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;

/**
 * New version of auton code - with most of the logic moved to helper class
 * AutonWizard
  */

@Autonomous(name="AOpMode_DiagnosticsAll", group="Pushbot")
//@Disabled
public class AOpMode_DiagnosticsAll extends LinearOpMode {

    /* Declare OpMode members. */

    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);


    AutonRoverRuckusWizard apu = null;

    @Override
    public void runOpMode() {
        double timeoutS;

        apu = new AutonRoverRuckusWizard(rt) ;
        apu.init();
        SubSysDiagnostics dia = new SubSysDiagnostics(rt, apu);
        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Work the wand, detect jewel color and move forward/backward to dislodge
        // the jewel - this is code specific to the BLUE alliance

        dia.diagnosticEncoderMotor();
        dia.diagnosticsAllServos();
    }

}
