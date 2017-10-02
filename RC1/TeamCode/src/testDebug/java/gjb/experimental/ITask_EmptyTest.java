/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/28/2017.
 * ADD/REPLACE THIS COMMENT BASED ON THE WHEN THE NEW TASK WAS CREATED
 */
package gjb.experimental;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import gjb.interfaces.LoggingInterface;
import gjb.utils.mock.*;

/*
 *   This is a template for a JUnit test to test a (task, subsystem(s)) combo.
 *   This particualar test just tests the (ITask_Empty, SubSys_Empty) combo.
 */
public class ITask_EmptyTest {
    MockRuntimeSupport rt;
    SubSysEmpty subsystem;
    ITask_Empty task;


    @Before
    public void setUp() throws Exception {
        rt = new MockRuntimeSupport();
        LoggingInterface opLog = rt.startLogging(ITask_EmptyTest.class.toString());
        subsystem = new SubSysEmpty(rt); // Replace by subsystem(s) under test
        task = new ITask_Empty(rt); // Replace by task under test
        rt.resetStartTime();
        // Additional test setup code goes here...
        System.out.println("TEST STARTING");
    }


    @After
    public void tearDown() throws Exception {
        rt.logger().endSession();
        // Additional test shutdown/cleanup code goes here
        System.out.println("TEST FINISHED");
    }


    @Test
    public void emptyTest() throws Exception {
        // Tests creation, init, init_loop, start, stop in quick succession.
        // This is the most basic test. You can modify it and/or add
        // additional tests with increasing complexity
        subsystem.init();
        task.init();

        task.init_loop();
        task.start();

        rt.runTime = 0;
        task.loop();

        task.stop();
    }

}