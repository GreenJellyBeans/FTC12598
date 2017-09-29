/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/27/2017.
 */
package gjb.interfaces;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import gjb.utils.Logger;


public interface RuntimeSupportInterface {

    Logger logger();
    SystemEnvironmentInterface sysEnv();
    HardwareLookupInterface hwLookup();
    GamepadInterface gamepad1();
    GamepadInterface gamepad2();
    Telemetry telemetry();


    // General OpMode methods
    void requestOpModeStop();
    double getRuntime();
    void resetStartTime();

    // LinearOpMode-specific methods - these
    // will throw a cast exception if called with a
    // regular OpMode.
    void waitForStart();
    boolean opModeIsActive();

}
