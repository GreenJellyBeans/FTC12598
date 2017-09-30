/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/30/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;
import gjb.utils.Logger;

@TeleOp(name="DOpMode_driveOnlyPushBot-v1", group="Pushbot")
//@Disabled
public class DOpMode_driveOnlyPushBot extends OpMode{
    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);

    // These are initialized during init()
    private SubSysSimpleTwoMotorDrive drive;
    private ITask_simpleDrive driveTask;
    private Logger logger;
    private LoggingInterface log;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        logger = rt.logger();
        logger.beginSession(DOpMode_driveOnlyPushBot.class.toString());
        log = logger.getRootLog();
        log.pri1(LoggingInterface.INIT_START, "OM DOpMode_driveOnlyPushBot");

        SubSysSimpleTwoMotorDrive.Config driveConfig = new SubSysSimpleTwoMotorDrive.Config()
                .leftMotorName("left_drive")
                .rightMotorName("right_drive");
        drive = new SubSysSimpleTwoMotorDrive(rt, driveConfig);
        driveTask = new ITask_simpleDrive(rt, drive);

        // Initialize the drive subsystem and associated task
        drive.init();
        driveTask.init();

        // Log end of initialization
        log.pri1(LoggingInterface.INIT_END, "OM DOpMode_driveOnlyPushBot");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        driveTask.init_loop();
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        driveTask.start();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        driveTask.loop();
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        driveTask.stop();
    }
}
