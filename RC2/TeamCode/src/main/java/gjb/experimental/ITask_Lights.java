/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/30/2017.
 */
package gjb.experimental;

import gjb.interfaces.*;
import gjb.utils.*;


public class ITask_Lights implements TaskInterface {
    final String THIS_COMPONENT = "TASK_LIGHTS"; // For "Task simple drive"
    final RuntimeSupportInterface rt;
    final LoggingInterface log;
    final TimeChecker tc;
    final SubSysLights lights;


    /*
     * actual delay_i == startDelay + delays[i]*scaleFactor.
     */
    public ITask_Lights(RuntimeSupportInterface rt, SubSysLights lights,
                        double[] delays, double startDelay, double scaleFactor ) {
        this.rt = rt;
        this.log = rt.getRootLog().newLogger(THIS_COMPONENT);
        this.lights = lights;
        tc = new TimeChecker(rt);
        tc.setTimesByDurations(delays, startDelay, scaleFactor);
    }

    /****** START OF TASK INTERFACE METHODS *********************/
    @Override
    public void init() {
        this.log.pri1(LoggingInterface.INIT_START, "");
        // OBSOLETE (OM must init sub systems) lights.init();
        this.log.pri1(LoggingInterface.INIT_END, "");
    }


    @Override
    public void init_loop() {

    }


    @Override
    public void start() {
        rt.resetStartTime();
        tc.reset();
    }


    @Override
    public void loop() {
        processStages(tc);
    }


    @Override
    public void stop() {
        this.log.pri1("STOP", "");
        lights.turnLightsOff();
    }

/************* END OF TASK INTERFACE METHODS ****************/

    // Helper method to decide whether to switch lights on or off
    private void processStages(TimeChecker tc) {
        boolean lightsOn = false;
        int stage = tc.getCurrentStage(); // Returns i where time is <= ith value

        // ODD stage: lights ON, EVEN stage: lights OFF
        if (stage % 2 == 1 && !tc.expired()) {
            lightsOn = true;
        }

        if (lightsOn) {
            lights.turnLightsOn();
        } else {
            lights.turnLightsOff();
        }
    }

}
