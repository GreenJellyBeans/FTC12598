/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/30/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;

@TeleOp(name="DOpMode_MecDriveTestc", group="Pushbot")
@Disabled
/*
 *  This Driver Controlled OpMode does controls the wheels of the pushbot.
 */
public class DOpMode_driveOnlyTeaBotMec extends OpMode{
    final String THIS_COMPONENT = "DOM_driveOnlyPushBot";
    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);

    // These are initialized during
    private  SubSysNotSoSimpleFourMotorDrive drive;
    private ITask_simpleDriveMec driveTask;
    private LoggingInterface log;

    /*************** START OF OPMODE INTERFACE METHODS **********************/

    @Override
    public void init() {
        log = rt.startLogging(DOpMode_driveOnlyTeaBotMec.class.toString());
        log.pri1(LoggingInterface.INIT_START, THIS_COMPONENT);

        //configure the motors
        SubSysNotSoSimpleFourMotorDrive.Config driveConfig = new SubSysNotSoSimpleFourMotorDrive.Config();
        driveConfig.leftMotorName("left_drive");
        driveConfig.rightMotorName("right_drive");
        driveConfig.fleftMotorName("fleft_drive");
        driveConfig.frightMotorName("fright_drive");

        drive = new SubSysNotSoSimpleFourMotorDrive(rt, driveConfig);
        driveTask = new ITask_simpleDriveMec(rt, drive);

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
        driveTask.loop();
    }

    @Override
    public void stop() {
        driveTask.stop();
        rt.stopLogging();
    }

    /*************** END OF OPMODE INTERFACE METHODS ************************/
}
