
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

@Autonomous(name="Assist_Test", group="Pushbot")
//@Disabled
public class AOpMode_AssistTest extends LinearOpMode {

    /* Declare OpMode members. */

    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);
    SubSysIntake intake = new SubSysIntake(rt);
    SubSysLift lift = new SubSysLift(rt);
    AutonRoverRuckusWizard apu = new AutonRoverRuckusWizard(rt);
    final double STRIDER_POWER = 0.1; //set later
    final double ARM_POWER = 0.5;
    @Override
    public void runOpMode() {
        double timeoutS;
        intake.init();
        intake.biggulp.setPosition(1); // the low position
        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        while(!intake.limit_mid_pressed() && !intake.limit_in_pressed()) {//not pressed, assuming the lift is in between out and mid
            intake.strider.setPosition(0.4); //wait until it hits the middle switch
        }
        if (intake.limit_in_pressed()){
            apu.log("it was on the wrong side");
            while (!intake.limit_mid_pressed()){
                intake.strider.setPosition(0.6);// until it hits the middle switch
            }
        }
        intake.strider.setPosition(0.52);
        apu.log("hit mid");
        apu.betterSleep(1000);
        while (!intake.limit_backward_pressed()){
            intake.ArmaDillo.setPower(-ARM_POWER); //wait until it hits the back switch
        }
        apu.betterSleep(3000);
        intake.ArmaDillo.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intake.ArmaDillo.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        intake.ArmaDillo.setTargetPosition(100);
        intake.ArmaDillo.setPower(ARM_POWER);
        apu.log("ready");
        intake.deinit();
    }

}
