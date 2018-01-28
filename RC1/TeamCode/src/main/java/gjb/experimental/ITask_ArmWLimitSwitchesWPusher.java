/**
 * Class created by Aparna, Keya, and Mira
 * FTC #12598 on 10/8/17
 */
package gjb.experimental;

import com.qualcomm.robotcore.util.Range;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.TaskInterface;


public class ITask_ArmWLimitSwitchesWPusher implements TaskInterface {

    final String THIS_COMPONENT = "T_EMPTY"; // Replace EMPTY by short word identifying task
    final double CLAW_SPEED = 0.005; //was 0.02
    final double MIN_CLAW = -0.5;
    final double MAX_CLAW = 0.0;
    final double PUSHER_SPEED = 0.0; //TBD

    final RuntimeSupportInterface rt; // Runtime support
    final LoggingInterface log; // Logger

    // Place additional instance variables here - like sub system objects..
    SubSysArmWPusher armSP;
    double clawOffset = 0.0;

    // Modify this constructor to add any additional initialization parameters - see
    // other tasks for examples.
    public ITask_ArmWLimitSwitchesWPusher(RuntimeSupportInterface rt, SubSysArmWPusher arm) {
        this.rt = rt;
        this.armSP  = arm;
        this.log = rt.getRootLog().newLogger(THIS_COMPONENT);
    }

//PAGE 193 is good page - keya
    /****** START OF TASK INTERFACE METHODS *********************/
    @Override
    public void init() {
        this.log.pri1(LoggingInterface.INIT_START, "");
        // Any task initialization code goes here.
        this.log.pri1(LoggingInterface.INIT_END, "");
    }


    @Override
    public void init_loop() {
        // Code that must execute WHILE waiting for START goes here.
    }


    @Override
    public void start() {
        rt.resetStartTime();
        // Any code to execute ONCE just when the op mode is started goes here.
    }


    @Override
    public void loop() {

        // Use gamepad left & right Bumpers to open and close the claw
        if (rt.gamepad1().right_bumper())
            clawOffset += CLAW_SPEED;
        else if (rt.gamepad1().left_bumper())
            clawOffset -= CLAW_SPEED;


        // Move both servos to new position.  Assume servos are mirror image of each other.
        clawOffset = Range.clip(clawOffset, MIN_CLAW, MAX_CLAW);
        armSP.left_dinosorvor.setPosition(armSP.MID_SERVO + clawOffset);
        armSP.right_dinosorvor.setPosition(armSP.MID_SERVO - clawOffset);


        // Use gamepad buttons to move the arm up (Y) and down (A)

        double power = 0;
        if (armSP.limitswitch_Y.getState()) {
            rt.telemetry().addData("limitswitch_Y", "HIGH");
            if (rt.gamepad1().a())
                power = armSP.ARM_DOWN_POWER;
        } else if (rt.gamepad1().y()){
            rt.telemetry().addData("LimitSwitch_Y", "LOW");
            power = armSP.ARM_UP_POWER;
        }

        if (armSP.limitswitch_A.getState()) {
            rt.telemetry().addData("limitswitch_A", "HIGH");
            power = 0;
            if (rt.gamepad1().y())
                power = armSP.ARM_UP_POWER;
        } else if (rt.gamepad1().a()){
            rt.telemetry().addData("LimitSwitch_A", "LOW");
            power = armSP.ARM_DOWN_POWER;
        }
        double energy = 0;
        if (armSP.limitswitch_X.getState()) {
            rt.telemetry().addData("limitswitch_X", "HIGH");
            if (rt.gamepad1().right_trigger()>0.1)
                energy = armSP.PUSHER_BACKWARD_ENERGY;
        } else if (rt.gamepad1().left_trigger()>0.1){
            rt.telemetry().addData("LimitSwitch_X", "LOW");
            energy = armSP.PUSHER_FORWARD_ENERGY;
        }

        if (armSP.limitswitch_B.getState()) {
            rt.telemetry().addData("limitswitch_B", "HIGH");
            energy = 0;
            if (rt.gamepad1().left_trigger()>0.1)
                energy = armSP.PUSHER_FORWARD_ENERGY;
        } else if (rt.gamepad1().right_trigger()>0.1){
            rt.telemetry().addData("LimitSwitch_B", "LOW");
            energy = armSP.PUSHER_BACKWARD_ENERGY;
        }

        armSP.armM.setPower(power);
        rt.telemetry().addData("arm",  "power = %.2f", power);

        // Send telemetry message to signify robot running;
        rt.telemetry().addData("claw",  "Offset = %.2f", clawOffset);

        armSP.CokieGPousher.setPower(energy);
        rt.telemetry().addData("pusher",  "power = %.2f", energy);

    }

    @Override
    public void stop() {
        this.log.pri1("STOP", "");
        // Place any shutdown/deinitialization code here  - this is called ONCE
        // when the task is to be stopped.
        armSP.armM.setPower(0);
    }


    /************* END OF TASK INTERFACE METHODS ****************/


    // Place additional helper methods here.

}
