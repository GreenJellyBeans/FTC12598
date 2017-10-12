/**
 * Class created by Aparna, Keya, and Mira
 * FTC #12598 on 10/8/17
 */
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;

// NOTE: You can used this as the base for a TeleopMode - all that needs to be
// done is to replace the @Autonomous annotation by @TeleOp below.
@TeleOp(name="DOpMode_TestArmWLimitSwitches", group="dummy")
//@Disabled
/*
 *  This Autonomous OpMode makes initializes the Empty subsystem and starts the Empty task.
 *  ADD/REPLACE THIS COMMENT BASED ON THE WHEN THE NEW TASK WAS CREATED
 */
public class DOpMode_TestArmWLimitSwitches extends OpMode{
    final String THIS_COMPONENT = "AOM_ARMTEST"; // Replace EMPTY by short word identifying Op mode
    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);

    // These are initialized during init()
    SubSysArm arm;
    ITask_ArmWLimitSwitches task;
    private LoggingInterface log;

    // Place additional instance variable for this OpMode here


    /*************** START OF OPMODE INTERFACE METHODS **********************/

    @Override
    public void init() {
        log = rt.startLogging(DOpMode_TestArmWLimitSwitches.class.toString());
        log.pri1(LoggingInterface.INIT_START, THIS_COMPONENT);

        arm = new SubSysArm(rt);

        // Do any additional op-mode initialization here.
        task = new ITask_ArmWLimitSwitches(rt, arm);

        // Initialize the  subsystem and associated task
        arm.init();
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
