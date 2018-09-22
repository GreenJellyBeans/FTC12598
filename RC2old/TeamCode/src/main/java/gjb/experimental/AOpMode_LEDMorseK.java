/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/30/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;


@Autonomous(name="AOpMode_LEDMorse_K", group="Pushbot")
//@Disabled
/*
 *  This Autonomous OpMode makes the LED lights play "K" in morse code.
 */
public class AOpMode_LEDMorseK extends OpMode{
    final String THIS_COMPONENT = "AOM_LEDMorseK"; // Replace EMPTY by short word identifying Op mode
    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);
    final String MORSE_MESSAGE = "K";
    final double START_DELAY = 1.0; // seconds
    final double DOT_TIME = 0.1; // Time for one Morse "dot"

    // These are initialized during init()
    SubSysLights lights;
    ITask_Lights task;
    private LoggingInterface log;

    /*************** START OF OPMODE INTERFACE METHODS **********************/

    @Override
    public void init() {
        log = rt.startLogging(AOpMode_LEDMorseK.class.toString());
        log.pri1(LoggingInterface.INIT_START, THIS_COMPONENT);

        lights = new SubSysLights(rt);

        // Setup Lights task to display morse code.
        MorseGenerator mg  = new MorseGenerator();
        double[] delays = mg.generateDelays(MORSE_MESSAGE, DOT_TIME);
        task = new ITask_Lights(rt, lights, delays, START_DELAY, 1.5);//1.0 == don't scale.

        // Initialize the hardware subsystem and associated task
        lights.init();
        task.init();

        log.pri1(LoggingInterface.INIT_END, THIS_COMPONENT);
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
        rt.stopLogging();
    }

    /*************** END OF OPMODE INTERFACE METHODS ************************/
}
