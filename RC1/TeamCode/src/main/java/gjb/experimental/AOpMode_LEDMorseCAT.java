/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/30/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;
import gjb.utils.Logger;

@Autonomous(name="AOpMode_LEDMorseCAT-v1", group="Pushbot")
//@Disabled
public class AOpMode_LEDMorseCAT extends OpMode{
    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);
    final String MORSE_MESSAGE = "CAT";
    final double START_DELAY = 1.0; // seconds
    final double DOT_TIME = 0.1; // Time for one Morse "dot"

    // These are initialized during init()
    SubSysLights lights;
    ITask_Lights task;
    private Logger logger;
    private LoggingInterface log;

    @Override
    public void init() {
        logger = rt.logger();
        logger.beginSession(AOpMode_LEDMorseCAT.class.toString());
        log = logger.getRootLog();
        log.pri1(LoggingInterface.INIT_START, "OM AOpMode_LEDMorseCAT");

        lights = new SubSysLights(rt);

        // Setup Lights task to display morse code.
        MorseGenerator mg  = new MorseGenerator();
        double[] delays = mg.generateDelays(MORSE_MESSAGE, DOT_TIME);
        task = new ITask_Lights(rt, lights, delays, START_DELAY, 1.0);//1.0 == don't scale.

        // Initialize the hardware subsystem and associated task
        lights.init();
        task.init();

        log.pri1(LoggingInterface.INIT_END, "OM DOpMode_driveOnlyPushBot");
    }


    @Override
    public void init_loop() {
        task.init_loop();
    }

    @Override
    public void start() {
        task.start();
    }

    @Override
    public void loop() {
        task.loop();
    }

    @Override
    public void stop() {
        task.stop();
        rt.logger().endSession();
    }
}
