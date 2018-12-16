/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/30/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import gjb.interfaces.*;
import gjb.utils.*;

// NOTE: You can used this as the base for a TeleopMode - all that needs to be
// done is to replace the @Autonomous annotation by @TeleOp below.
@Autonomous(name="AOpMode_Empty", group="dummy")
@Disabled
/*
 *  This Autonomous OpMode makes initializes the Empty subsystem and starts the Empty task.
 *  ADD/REPLACE THIS COMMENT BASED ON THE WHEN THE NEW TASK WAS CREATED
 */
public class  AOpMode_Empty extends OpMode{
    final String THIS_COMPONENT = "AOM_EMPTY"; // Replace EMPTY by short word identifying Op mode
    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);

    // These are initialized during init()
    SubSysEmpty empty;
    ITask_Empty task;
    private LoggingInterface log;

    // Place additional instance variable for this OpMode here


    /*************** START OF OPMODE INTERFACE METHODS **********************/

    @Override
    public void init() {
        log = rt.startLogging(AOpMode_Empty.class.toString());
        log.pri1(LoggingInterface.INIT_START, THIS_COMPONENT);

        empty = new SubSysEmpty(rt);

        // Do any additional op-mode initialization here.
        task = new ITask_Empty(rt);

        // Initialize the  subsystem and associated task
        empty.init();
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
