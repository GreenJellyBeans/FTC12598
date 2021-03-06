/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/30/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;

@TeleOp(name="DOpMode_FullMelonBotMecMC", group="Pushbot")
//@Disabled
/*
 *  This Driver Controlled OpMode does controls the wheels of the pushbot.
 */
public class DOpMode_FullMelonBotMecMC extends OpMode{
    final String THIS_COMPONENT = "DOM_FullMelonBotMecMC";
    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);

    // These are initialized during
    private  SubSysMecDrive drive;
    private SubSysLift lift;
    private SubSysIntake intake;
    private ITask_simpleDriveMec driveTask;
    private ITask_LiftWLimitSwitches liftTask;
    private ITask_TwoPartArmMC armTask;

    //private ITask_TwoPartArm armTask;
    private LoggingInterface log;

    /*************** START OF OPMODE INTERFACE METHODS **********************/

    @Override
    public void init() {
        log = rt.startLogging(DOpMode_FullMelonBotMecMC.class.toString());
        log.pri1(LoggingInterface.INIT_START, THIS_COMPONENT);

        //configure the motors
        SubSysMecDrive.Config driveConfig = new SubSysMecDrive.Config();
        driveConfig.leftMotorName("left_drive");
        driveConfig.rightMotorName("right_drive");
        driveConfig.fleftMotorName("fleft_drive");
        driveConfig.frightMotorName("fright_drive");


        drive = new SubSysMecDrive(rt, driveConfig);
        driveTask = new ITask_simpleDriveMec(rt, drive);
        lift = new SubSysLift(rt);
        intake = new SubSysIntake(rt);
        liftTask = new ITask_LiftWLimitSwitches(rt, lift);
        armTask = new ITask_TwoPartArmMC(rt, intake, lift);
        // Initialize the subsystem and associated task
        drive.init();
        driveTask.init();
        lift.init();
        liftTask.init();
        intake.init();
        armTask.init();
       // armTask.init();
        log.pri1(LoggingInterface.INIT_END, THIS_COMPONENT);
    }


    @Override
    public void init_loop() {
        driveTask.init_loop();
        liftTask.init_loop();
        armTask.init_loop();
        //armTask.init_loop();
    }


    @Override
    public void start() {
        driveTask.start();
        liftTask.start();
        armTask.start();
        //armTask.start();
    }

    @Override
    public void loop() {
        driveTask.loop();
        liftTask.loop();
        armTask.loop();
        //armTask.loop();
    }

    @Override
    public void stop() {
        driveTask.stop();
        liftTask.stop();
        armTask.stop();
        //armTask.stop();
        rt.stopLogging();
    }

    /*************** END OF OPMODE INTERFACE METHODS ************************/
}
