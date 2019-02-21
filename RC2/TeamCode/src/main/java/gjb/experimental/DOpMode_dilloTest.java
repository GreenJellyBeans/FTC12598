/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/30/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;

@TeleOp(name="DOpMode_DilloTest", group="Pushbot")
//@Disabled
/*
 *  This Driver Controlled OpMode does controls the wheels of the pushbot.
 */
public class DOpMode_dilloTest extends OpMode{
    final String THIS_COMPONENT = "DOM_DilloTest";
    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);

    // These are initialized during init()
    private LoggingInterface log;
    private SubSysDiagnostics dia;

    /*************** START OF OPMODE INTERFACE METHODS **********************/

    @Override
    public void init() {
        log = rt.startLogging(DOpMode_dilloTest.class.toString());
        log.pri1(LoggingInterface.INIT_START, THIS_COMPONENT);
        dia = new SubSysDiagnostics(rt,null);

        // Initialize the subsystem and associated task
        dia.init();
        log.pri1(LoggingInterface.INIT_END, THIS_COMPONENT);
    }


    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
        dia.motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        log("hi" + dia.motor.getCurrentPosition());

    }

    @Override
    public void loop() {
        rt.telemetry().addData("Encoder ticks", dia.motor.getCurrentPosition());
        rt.telemetry().update();
    }

    @Override
    public void stop() {
        rt.stopLogging();
    }

    /*************** END OF OPMODE INTERFACE METHODS ************************/
    void log(String s) {
        rt.telemetry().log().add("AutonWiz: " + s);
    }
}
