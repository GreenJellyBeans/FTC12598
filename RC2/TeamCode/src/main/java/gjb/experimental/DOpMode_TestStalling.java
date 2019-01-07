/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/30/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;

@TeleOp(name="DOpMode_TEST_STALLING", group="Pushbot")
//@Disabled
/*
 *  This Driver Controlled OpMode does controls the wheels of the pushbot.
 */
public class DOpMode_TestStalling extends OpMode{
    final String THIS_COMPONENT = "DOM_teststalling";
    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);
    // These are initialized during init()
    private  SubSysDiagnostics diagnostics;

    /*************** START OF OPMODE INTERFACE METHODS **********************/

    @Override

    public void init() {

        diagnostics  = new SubSysDiagnostics(rt, null);

        // Initialize the subsystem and associated task
        diagnostics.init();
    }


    @Override
    public void start() {

    }

    @Override
    public void loop() {
        if (diagnostics.isStalling()){
            diagnostics.blinkinLedDriver.setPattern(diagnostics.pattern);

        }
    }

    @Override
    public void stop() {
        diagnostics.deinit();

        // Place any shutdown/deinitialization code here  - this is called ONCE
        // after tasks & OpModes have stopped.

    }


    /*************** END OF OPMODE INTERFACE METHODS ************************/
}
