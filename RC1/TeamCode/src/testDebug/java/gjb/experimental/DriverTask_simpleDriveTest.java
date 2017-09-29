package gjb.experimental;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gjb.utils.mock.MockRuntimeSupport;

import static org.junit.Assert.*;

/**
 * Created by josephj on 9/28/2017.
 */
public class DriverTask_simpleDriveTest {
    MockRuntimeSupport rt;
    SubSysSimpleTwoMotorDrive drive;
    DriverTask_simpleDrive driveTask;

    @Before
    public void setUp() throws Exception {
        rt = new MockRuntimeSupport();
        rt.logger().beginSession(DriverTask_simpleDriveTest.class.toString());
        String[] dcMotorNames = {"left_drive", "right_drive"};
        rt.hwLookup.setupDcMotors(dcMotorNames);
        drive = new SubSysSimpleTwoMotorDrive(rt,null);
        driveTask = new DriverTask_simpleDrive(rt, drive);
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
        System.out.println("YOWZA!");
        driveTask.init();
        driveTask.init_loop();
        driveTask.start();
        driveTask.loop();
        driveTask.stop();
    }

}