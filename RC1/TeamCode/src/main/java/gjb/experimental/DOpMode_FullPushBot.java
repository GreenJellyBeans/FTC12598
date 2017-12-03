/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/30/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;

@TeleOp(name="DOpMode_FullPushBot_v1", group="Pushbot")
//@Disabled
/*
 *  This Driver Controlled OpMode does controls the wheels of the pushbot.
 */
public class DOpMode_FullPushBot extends OpMode{
    final String THIS_COMPONENT = "DOM_driveOnlyPushBot";
    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);

    // These are initialized during init()
    private  SubSysSimpleTwoMotorDrive drive;
    private ITask_simpleDrive driveTask;
    SubSysArm arm;
    ITask_ArmWLimitSwitches armTask;
    private LoggingInterface log;

    /*************** START OF OPMODE INTERFACE METHODS **********************/

    @Override
    public void init() {
        log = rt.startLogging(DOpMode_FullPushBot.class.toString());
        log.pri1(LoggingInterface.INIT_START, THIS_COMPONENT);

        SubSysSimpleTwoMotorDrive.Config driveConfig = new SubSysSimpleTwoMotorDrive.Config()
                .leftMotorName("left_drive")
                .rightMotorName("right_drive");
        drive = new SubSysSimpleTwoMotorDrive(rt, driveConfig);
        driveTask = new ITask_simpleDrive(rt, drive);

        // Initialize the subsystem and associa ted task
        drive.init();
        driveTask.init();

        arm = new SubSysArm(rt);

        // Do any additional op-mode initialization here.
        armTask = new ITask_ArmWLimitSwitches(rt, arm);

        // Initialize the  subsystem and associated task
        arm.init();
        armTask.init();


        log.pri1(LoggingInterface.INIT_END, THIS_COMPONENT);
    }


    @Override
    public void init_loop() {
        armTask.init_loop();
        driveTask.init_loop();
    }

    @Override
    public void start() {
        armTask.start();
        driveTask.start();
    }

    @Override
    public void loop() {
        armTask.loop();
        driveTask.loop();
    }

    @Override
    public void stop() {
        armTask.stop();
        driveTask.stop();
        rt.stopLogging();
    }

    /*************** END OF OPMODE INTERFACE METHODS ************************/
}