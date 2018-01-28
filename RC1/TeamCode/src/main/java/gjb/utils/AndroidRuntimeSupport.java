/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/28/2017.
 */
package gjb.utils;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.*;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import gjb.interfaces.GamepadInterface;
import gjb.interfaces.HardwareLookupInterface;
import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.SystemEnvironmentInterface;


public class AndroidRuntimeSupport implements RuntimeSupportInterface {
    private final OpMode om;
    private final SystemEnvironmentInterface env;
    private final Logger logger;

    // These are evaluated lazily, as they require the OpMode to be initialized, so
    // they cannot be initialized in the constructor.
    private AndroidHardwareLookup hwLookup = null;
    private AndroidGamepad gamepad1=null;
    private AndroidGamepad gamepad2=null;

    public AndroidRuntimeSupport(OpMode om) {
        this.om = om;
        hwLookup = new AndroidHardwareLookup();
        env = new AndroidSystemEnvironment();
        logger = new Logger(env, "root");
    }

    private class AndroidSystemEnvironment implements SystemEnvironmentInterface {

        @Override
        public void dbgPrintln(String s) {

        }

        @Override
        public void dsPrintln(String s) {
            om.telemetry.log().add(s);
        }

        @Override
        public void fsPrintln(String s) {

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


    private class AndroidHardwareLookup implements HardwareLookupInterface {

        @Override
        public DeviceInterfaceModule getDeviceInterfaceModule(String name) {
            return om.hardwareMap.get(DeviceInterfaceModule.class, name);
        }

        @Override
        public AnalogInput getAnalogInput(String name) {
            return null;
        }

        @Override
        public AnalogInputController getAnalogInputController(String name) {
            return null;
        }

        @Override
        public AnalogOutput getAnalogOutput(String name) {
            return om.hardwareMap.get(AnalogOutput.class, name);
        }

        @Override
        public AnalogOutputController getAnalogOutputController(String name) {
            return null;
        }

        @Override
        public ColorSensor getColorSensor(String name) {

            return om.hardwareMap.get(ColorSensor.class, name);
        }

        @Override
        public DcMotor getDcMotor(String name) {
            return om.hardwareMap.get(DcMotor.class, name);
        }

        @Override
        public DigitalChannel getDigitalChannel(String name) {
            return om.hardwareMap.get(DigitalChannel.class, name);
        }

        @Override
        public Servo getServo(String name) {
            return om.hardwareMap.get(Servo.class, name);
        }
    }

    public class AndroidGamepad implements GamepadInterface {
        private final Gamepad gp;

        public AndroidGamepad(Gamepad gp) {
            this.gp = gp;
        }

        @Override
        public double left_stick_y() {
            return gp.left_stick_y;
        }

        @Override
        public double right_stick_y() {
            return gp.right_stick_y;
        }

        @Override
        public boolean left_bumper() {
            return gp.left_bumper;
        }

        @Override
        public float right_trigger() {
            return gp.right_trigger;
        }

        @Override
        public float left_trigger() {
            return gp.left_trigger;
        }

        @Override
        public boolean right_bumper() {
            return gp.right_bumper;
        }

        @Override
        public boolean y() {
            return gp.y;
        }

        @Override
        public boolean a() {
            return gp.a;
        }
    }

    // INTERFACE METHOD IMPLEMENTATIONS ...

   //OBSOLETE @Override
    public Logger logger() {
        return logger;
    }

    @Override
    public LoggingInterface startLogging(String sessionName) {
        logger.beginSession(sessionName);
        return logger.getRootLog();
    }

    @Override
    public LoggingInterface getRootLog() {
        return logger.getRootLog();
    }

    @Override
    public void stopLogging() {
        logger.endSession();
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
    public GamepadInterface gamepad1() {
        // Lazy allocation of the gamepad - only when requested as gamepads are
        // not available during constructor time.
        if (gamepad1 == null) {
            gamepad1 = new AndroidGamepad(om.gamepad1);
        }
        return gamepad1;
    }

    @Override
    public GamepadInterface gamepad2() {
        // Lazy allocation of the gamepad - only when requested as gamepads are
        // not available during constructor time.
        if (gamepad2 == null) {
            gamepad2 = new AndroidGamepad(om.gamepad2);
        }
        return gamepad2;
    }

    @Override
    public Telemetry telemetry() {
        return om.telemetry;
    }

    @Override
    public int getIdentifierFromPackage(String name, String defType) {
        return om.hardwareMap.appContext.getResources().getIdentifier(name, defType, om.hardwareMap.appContext.getPackageName());
    }


    @Override
    public void requestOpModeStop() {
        om.requestOpModeStop();
    }

    @Override
    public double getRuntime() {
        return om.getRuntime();
    }

    @Override
    public void resetStartTime() {
        om.resetStartTime();
    }

    @Override
    public void waitForStart() {
        // Will throw cast exception if it's not in fact
        // a LinearOpMode.
        LinearOpMode lOm = (LinearOpMode) om;
        lOm.waitForStart();
    }

    @Override
    public boolean opModeIsActive() {
        // Will throw cast exception if it's not in fact
        // a LinearOpMode.
        LinearOpMode linOm = (LinearOpMode) om;
        return linOm.opModeIsActive();
    }
}
