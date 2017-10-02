/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 10/2/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;
import gjb.utils.Logger;

// NOTE: You can used this as the base for a TeleopMode - all that needs to be
// done is to replace the @Autonomous annotation by @TeleOp below.
@Autonomous(name="AOpMode_LimitSwitchTestWithDIM", group="dummy")
//@Disabled
/*
 *  This Autonomous OpMode makes initializes the Empty subsystem and starts the Empty task.
 *  ADD/REPLACE THIS COMMENT BASED ON THE WHEN THE NEW TASK WAS CREATED
 */
public class AOpMode_LimitSwitchTestWithDIM extends OpMode{
    final String THIS_COMPONENT = "AOM_LIMIT"; // Replace EMPTY by short word identifying Op mode
    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);

    // These are initialized during init()
    private LoggingInterface log;

    // Place additional instance variables here - like sub system objects..
    final int BLUE_LED_CHANNEL = 0;
    final int RED_LED_CHANNEL = 1;
    boolean   inputPin;             // Input State
    DeviceInterfaceModule dim;                  // Device Object
    DigitalChannel digIn;                // Device Object

    /*************** START OF OPMODE INTERFACE METHODS **********************/

    @Override
    public void init() {
        log = rt.startLogging(AOpMode_LimitSwitchTestWithDIM.class.toString());
        log.pri1(LoggingInterface.INIT_START, THIS_COMPONENT);

        // Do any additional op-mode initialization here.
        // get a reference to a Modern Robotics DIM, and digital input channels.
        dim = rt.hwLookup().getDeviceInterfaceModule("dim");   //  Use generic form of device mapping
        digIn  = rt.hwLookup().getDigitalChannel("limit_switch");     //  Use generic form of device mapping
        digIn.setMode(DigitalChannel.Mode.INPUT);          // Set the direction of each channel

        log.pri1(LoggingInterface.INIT_END, THIS_COMPONENT);
    }


    @Override
    public void loop() {
        // Set the red led on the DIM based on the input digital channel state.
        if (digIn.getState()) {
            rt.telemetry().addData("LimitSwitch", "HIGH");
            dim.setLED(RED_LED_CHANNEL, true);
        } else {
            rt.telemetry().addData("LimitSwitch", "LOW");
            dim.setLED(RED_LED_CHANNEL, false);
        }
    }


    @Override
    public void stop() {

        rt.stopLogging();
    }

    /*************** END OF OPMODE INTERFACE METHODS ************************/
}
