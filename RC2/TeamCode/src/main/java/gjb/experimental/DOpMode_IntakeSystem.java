/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/30/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;

@TeleOp(name="DOpMode_IntakeSystem", group="Pushbot")
//@Disabled
/*
 *  This Driver Controlled OpMode does controls the wheels of the pushbot.
 */
public class DOpMode_IntakeSystem extends OpMode{
    final String THIS_COMPONENT = "DOM_IntakeSystem";
    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);

    // These are initialized during
    private SubSysIntake Intake;
    private SubSysLift lift;

    private ITask_TwoPartArm armTask;
    //private ITask_TwoPartArm armTask;
    private LoggingInterface log;

    /*************** START OF OPMODE INTERFACE METHODS **********************/

    @Override
    public void init() {
        log = rt.startLogging(DOpMode_IntakeSystem.class.toString());
        log.pri1(LoggingInterface.INIT_START, THIS_COMPONENT);

        //configure the motors
        SubSysMecDrive.Config driveConfig = new SubSysMecDrive.Config();
        driveConfig.leftMotorName("left_drive");
        driveConfig.rightMotorName("right_drive");
        driveConfig.fleftMotorName("fleft_drive");
        driveConfig.frightMotorName("fright_drive");


        Intake = new SubSysIntake(rt);
        lift = new SubSysLift(rt);
        armTask = new ITask_TwoPartArm(rt, Intake, lift);
        // Initialize the subsystem and associated task

        Intake.init();
        lift.init();
        armTask.init();
        log.pri1(LoggingInterface.INIT_END, THIS_COMPONENT);
    }


    @Override
    public void init_loop() {
        armTask.init_loop();
    }


    @Override
    public void start() {
        armTask.start();
    }

    @Override
    public void loop() {
        armTask.loop();
    }

    @Override
    public void stop() {
        armTask.stop();
        rt.stopLogging();
    }

    /*************** END OF OPMODE INTERFACE METHODS ************************/
}
