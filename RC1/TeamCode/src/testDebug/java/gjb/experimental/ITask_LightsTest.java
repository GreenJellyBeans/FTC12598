/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/28/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.hardware.AnalogOutput;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.GjbUtils;
import gjb.utils.mock.MockAnalogOutputController;
import gjb.utils.mock.MockHardwareLookup;
import gjb.utils.mock.MockRuntimeSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ITask_LightsTest {
    MockRuntimeSupport rt;
    SubSysLights lights;
    ITask_Lights task;
    final String CONTROLLER_NAME = "AOC";
    final String AO_PORT_NAME = "light_level";
    final int VOLTAGE_MODE = 0; // 0 == fixed voltage, according to API.
    MockAnalogOutputController.PortData portData = null;
    double[] delays  = {0,1,0};
    double startDelay = 2;
    double scaleFactor = 2;

    @Before
    public void setUp() throws Exception {
        rt = new MockRuntimeSupport();
        LoggingInterface opLog = rt.startLogging(ITask_LightsTest.class.toString());
        setupControllerHardware();
        lights = new SubSysLights(rt);
        task = new ITask_Lights(rt, lights, delays, startDelay, scaleFactor);
        rt.resetStartTime();
        System.out.println("TEST STARTING");
    }

    private void setupControllerHardware() {
        String[] controllerNames  = {CONTROLLER_NAME};
        String[][] portNames = {{AO_PORT_NAME}};
        rt.mhwLookup.setupAnalogOutputControllers(controllerNames, portNames);
        MockAnalogOutputController maoc = (MockAnalogOutputController) rt.mhwLookup.getAnalogOutputController(CONTROLLER_NAME);
        portData = maoc.getPortData(AO_PORT_NAME);
    }

    @After
    public void tearDown() throws Exception {
        rt.logger().endSession();
        System.out.println("TEST FINISHED");
    }

    @Test
    public void randomLightsTest() throws Exception {
        // Tests creation, init, init_loop, start, stop in quick succession.
        lights.init();
        task.init();

        task.init_loop();
        task.start();

        rt.runTime = 0;
        task.loop();
        verifyVoltage();

        rt.runTime = startDelay + 0.001;
        task.loop();
        verifyVoltage();

        rt.runTime += 1.5;
        task.loop();
        verifyVoltage();

        rt.runTime += 10;
        task.loop();
        verifyVoltage();

        task.stop();

        // Check that the power is set back  0 on both motors...

    }

    void verifyVoltage() {
        final double MIN_OFF_VOLTAGE = 3.0/4.0*1023;
        final double MAX_ON_VOLTAGE = 0.5/4.0*1023;
        double curTime = rt.getRuntime();
        double scaledTime = (curTime - startDelay)/scaleFactor;
        assertEquals(portData.mode, VOLTAGE_MODE);
        boolean expectedOn = false;
        if (scaledTime>0) {
            int i = (int) (Math.ceil(scaledTime)+ 0.1);
            if (i % 2 == 1) {
                // Should be ON voltage
                expectedOn = true;
            }
        }
        System.out.println("Curtime:" + curTime + " ScaledTime:" + scaledTime + " expectedOn:" + expectedOn);
        if (expectedOn) {
            assertTrue("Invalid ON voltage: " + portData.voltage, portData.voltage <= MAX_ON_VOLTAGE);
        } else {
            assertTrue("Invalid OFF voltage: " + portData.voltage, portData.voltage >= MIN_OFF_VOLTAGE);
        }

    }
}