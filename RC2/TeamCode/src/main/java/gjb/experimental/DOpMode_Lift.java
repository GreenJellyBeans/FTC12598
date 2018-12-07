/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/30/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;

@TeleOp(name="DOpMode_Lift", group="Pushbot")
//@Disabled
/*
 *  This Driver Controlled OpMode does controls the wheels of the pushbot.
 */
public class DOpMode_Lift extends OpMode{
    final String THIS_COMPONENT = "DOM_FullMelonBotMec";
    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);

    // These are initialized during
    private SubSysLift lift;
    private ITask_LiftWLimitSwitches liftTask;
    private LoggingInterface log;

    /*************** START OF OPMODE INTERFACE METHODS **********************/

    @Override
    public void init() {
        log = rt.startLogging(DOpMode_Lift.class.toString());
        log.pri1(LoggingInterface.INIT_START, THIS_COMPONENT);




        lift = new SubSysLift(rt);
        liftTask = new ITask_LiftWLimitSwitches(rt, lift);

        // Initialize the subsystem and associated task

        lift.init();
        liftTask.init();

        log.pri1(LoggingInterface.INIT_END, THIS_COMPONENT);
    }


    @Override
    public void init_loop() {
        liftTask.init_loop();
    }


    @Override
    public void start() {
        liftTask.start();
    }

    @Override
    public void loop() {
        liftTask.loop();
        rt.telemetry().update();
    }

    @Override
    public void stop() {
        liftTask.stop();
        rt.stopLogging();
    }

    /*************** END OF OPMODE INTERFACE METHODS ************************/
}
