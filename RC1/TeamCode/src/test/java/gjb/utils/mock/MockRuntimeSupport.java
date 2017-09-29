/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/28/2017.
 */
package gjb.utils.mock;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.AnalogInputController;
import com.qualcomm.robotcore.hardware.AnalogOutput;
import com.qualcomm.robotcore.hardware.AnalogOutputController;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.concurrent.TimeUnit;

import gjb.interfaces.GamepadInterface;
import gjb.interfaces.HardwareLookupInterface;
import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.SystemEnvironmentInterface;
import gjb.utils.Logger;


public class MockRuntimeSupport implements RuntimeSupportInterface {
    // Test code needs access to these to set things up.
    public final SystemEnvironmentInterface env;
    public final Logger logger;
    public final MockHardwareLookup hwLookup;
    public final MockGamepad gamepad1;
    public final MockGamepad gamepad2;
    public final MockTelemetry telemetry;

    public double runTime=0; // Tests set this to be whatever they want.

    public MockRuntimeSupport() {
        env = new DesktopSystemEnvironment();
        logger = new Logger(env, "root");
        hwLookup = new MockHardwareLookup();
        gamepad1 = new MockGamepad();
        gamepad2 = new MockGamepad();
        telemetry = new MockTelemetry();
    }

    private class DesktopSystemEnvironment implements SystemEnvironmentInterface {

        @Override
        public void dbgPrintln(String s) {

        }

        @Override
        public void dsPrintln(String s) {
            System.err.println(s);
        }

        @Override
        public void fsPrintln(String s) {
            System.out.println(s);
        }

        @Override
        public void fsFlush() {

        }

        @Override
        public void fsNewLogSession(String sessionId) {

        }

        @Override
        public void fsCloseLog() {

        }
    }



    // INTERFACE METHOD IMPLEMENTATIONS ...

    @Override
    public Logger logger() {
        return logger;
    }

    @Override
    public SystemEnvironmentInterface sysEnv() {
        return env;
    }

    @Override
    public HardwareLookupInterface hwLookup() {
        return hwLookup;
    }

    @Override
    public GamepadInterface gamepad1() { return gamepad1; }

    @Override
    public GamepadInterface gamepad2() { return gamepad2; }

    @Override
    public Telemetry telemetry() { return telemetry; }

    @Override
    public void requestOpModeStop() {
        assert false;
    }

     @Override
    public double getRuntime() {
        return runTime; // We return whatever the test setup said it is.
    }

    /**
     * Reset the start time to zero.
     */
    @Override
    public void resetStartTime() {
        runTime = 0; // If client code were to gall getRuntime right after reset they should get 0.
    }

    @Override
    public void waitForStart() {
        // Will throw cast exception if it's not in fact
        // a LinearOpMode.
        assert false; // unimplemented. Simple implementation is to update runtime  & state
                      // and return immediately.
    }

    @Override
    public boolean opModeIsActive() {
        // Will throw cast exception if it's not in fact
        // a LinearOpMode.
        assert false;
        return false;
    }
}
