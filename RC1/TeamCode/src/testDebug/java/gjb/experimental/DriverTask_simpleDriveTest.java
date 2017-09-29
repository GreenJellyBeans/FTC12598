package gjb.experimental;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gjb.utils.GjbUtils;
import gjb.utils.mock.MockDcMotor;
import gjb.utils.mock.MockRuntimeSupport;

import static org.junit.Assert.*;

/**
 * Created by josephj on 9/28/2017.
 */
public class DriverTask_simpleDriveTest {
    MockRuntimeSupport rt;
    SubSysSimpleTwoMotorDrive drive;
    DriverTask_simpleDrive driveTask;
    final String LEFT_MOTOR = "left_drive";
    final String RIGHT_MOTOR  = "right_drive";
    MockDcMotor leftMotor=null;
    MockDcMotor rightMotor=null;

    @Before
    public void setUp() throws Exception {
        rt = new MockRuntimeSupport();
        rt.logger().beginSession(DriverTask_simpleDriveTest.class.toString());
        String[] dcMotorNames = {LEFT_MOTOR, RIGHT_MOTOR};
        rt.hwLookup.setupDcMotors(dcMotorNames);
        leftMotor = (MockDcMotor) rt.hwLookup.getDcMotor(LEFT_MOTOR);
        rightMotor = (MockDcMotor) rt.hwLookup.getDcMotor(RIGHT_MOTOR);
        // Let's set the motor power to random values
        leftMotor.power = Math.random();
        rightMotor.power  = Math.random();
        drive = new SubSysSimpleTwoMotorDrive(rt,null);
        driveTask = new DriverTask_simpleDrive(rt, drive);
        rt.runTime = 0;
        System.out.println("TEST STARTING");
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("TEST FINISHED");
        driveTask = null;
        drive = null;
        rt = null;
    }

    @Test
    public void initShutdownTest() throws Exception {
        // Tests creation, init, init_loop, start, stop in quick succession.

        driveTask.init();

        // Check that the power is set to 0 on both motors...
        assertTrue(GjbUtils.closeEnough(leftMotor.power, 0, 0.01));
        assertTrue(GjbUtils.closeEnough(rightMotor.power, 0, 0.01));

        driveTask.init_loop();
        driveTask.start();

        rt.runTime = 1.0; // move forward 1 second in time.
        rt.gamepad1.left_stick_y = 0.5;
        rt.gamepad1.right_stick_y = 0.2;
        driveTask.loop();
        // We expect the motor power to match the joysticks, except it's opposite sign.
        assertTrue(GjbUtils.closeEnough(leftMotor.power, -0.5, 0.01));
        assertTrue(GjbUtils.closeEnough(rightMotor.power, -0.2, 0.01));

        driveTask.stop();

        // Check that the power is set back  0 on both motors...
        assertTrue(GjbUtils.closeEnough(leftMotor.power, 0, 0.01));
        assertTrue(GjbUtils.closeEnough(rightMotor.power, 0, 0.01));

    }

}