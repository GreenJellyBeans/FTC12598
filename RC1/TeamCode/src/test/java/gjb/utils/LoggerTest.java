/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/25/2017.
 */
package gjb.utils;
import org.junit.*;
import gjb.interfaces.LoggingInterface;


public class LoggerTest {

    DesktopSystemEnvironment env = new DesktopSystemEnvironment();
    Logger testLogger;

    @Before
    public void setup() throws Exception {
        System.out.println("setUp() called.");
        testLogger = new Logger(env, "ROOT");
    }

    @After
    public void teardown() throws Exception {
        System.out.println("tearDown() called.");
        testLogger = null;
    }

    @Test
    public void emptySessionTest() throws Exception {
        System.out.println("emptySessionTest...");
        testLogger.beginSession("Simple Logger Session");
        testLogger.endSession();
    }

    @Test
    public void simpleLogTest() throws Exception {
        System.out.println("rawLogTest...");
        testLogger.beginSession("raw log session");
        LoggingInterface rootLog = testLogger.getRootLog();
        rootLog.pri1("Hi there!");
        testLogger.endSession();
    }

}