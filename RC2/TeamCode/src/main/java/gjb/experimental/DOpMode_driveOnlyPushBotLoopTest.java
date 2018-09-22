/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/30/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;

@TeleOp(name="DOpMode_driveOnlyPushBot-LoopTest", group="Pushbot")
//@Disabled
/*
 *  This Driver Controlled OpMode does controls the wheels of the pushbot.
 */
public class DOpMode_driveOnlyPushBotLoopTest extends OpMode{
    final String THIS_COMPONENT = "DOM_driveOnlyPushBot";
    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);
    int loopCounter = 0;
    int wackyLoopCounter = 0;
    final long MAX_LOOP_TIME = 5; //in tenths of a millisecond, used to be 1, but loop often took more than 2, so increased it to 5
    int lateLoopsCounter = 0;
    final long MAX_LATE_TIME = 70;
    long previousTime = 0;

    // These are initialized during init()
    private  SubSysSimpleTwoMotorDrive drive;
    private ITask_simpleDrive driveTask;
    private LoggingInterface log;

    /*************** START OF OPMODE INTERFACE METHODS **********************/

    @Override
    public void init() {
        log = rt.startLogging(DOpMode_driveOnlyPushBotLoopTest.class.toString());
        log.pri1(LoggingInterface.INIT_START, THIS_COMPONENT);

        SubSysSimpleTwoMotorDrive.Config driveConfig = new SubSysSimpleTwoMotorDrive.Config();
        driveConfig.leftMotorName("left_drive");
        driveConfig.rightMotorName("right_drive");
        drive = new SubSysSimpleTwoMotorDrive(rt, driveConfig);
        driveTask = new ITask_simpleDrive(rt, drive);

        // Initialize the subsystem and associated task
        drive.init();
        driveTask.init();

        log.pri1(LoggingInterface.INIT_END, THIS_COMPONENT);
    }


    @Override
    public void init_loop() {
        driveTask.init_loop();
    }

    @Override
    public void start() {
        driveTask.start();
    }

    @Override
    public void loop() {
        long startTime = System.nanoTime();
        rt.telemetry().addData("loop counter",this.loopCounter );
        driveTask.loop();
        long timeDifference = (startTime - previousTime)/100000;
        if (timeDifference > MAX_LATE_TIME) {
            lateLoopsCounter++;
        }
        loopCounter++;
        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        totalTime = totalTime/100000;

        if(totalTime>MAX_LOOP_TIME) {
            wackyLoopCounter++;
        }

        rt.telemetry().addData("wackyLoopCounter",this.wackyLoopCounter );
        rt.telemetry().addData("loop duration",totalTime);
        rt.telemetry().addData("late loops",this.lateLoopsCounter);
        rt.telemetry().addData("loop difference",timeDifference);
        previousTime = startTime;
    }

    @Override
    public void stop() {
        driveTask.stop();
        rt.stopLogging();
    }

    /*************** END OF OPMODE INTERFACE METHODS ************************/
}
