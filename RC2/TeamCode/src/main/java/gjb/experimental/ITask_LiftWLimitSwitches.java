/**
 * Class created by Aparna, Keya, and Mira
 * FTC #12598 on 10/8/17
 */
package gjb.experimental;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.TaskInterface;


public class ITask_LiftWLimitSwitches implements TaskInterface {

    final String THIS_COMPONENT = "T_EMPTY"; // Replace EMPTY by short word identifying task
    final double LIFT_SPEED = 0.01; //was 0.02 and was 0.005
    final RuntimeSupportInterface rt; // Runtime support
    final LoggingInterface log; // Logger

    // Place additional instance variables here - like sub system objects..
    SubSysLift lift;
    double clawOffset = -0.5;

    // Modify this constructor to add any additional initialization parameters - see
    // other tasks for examples.
    public ITask_LiftWLimitSwitches(RuntimeSupportInterface rt, SubSysLift lift) {
        this.rt = rt;
        this.lift = lift;
        this.log = rt.getRootLog().newLogger(THIS_COMPONENT);
    }


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


   // @Override
    public void loop_OLD() {
        // Periodic code to be run when the task is actually running (after start and
        // before stop) goes here.
        //if limit switch is high, set power to zer.
        //else set power to one
        //armM.leftDrive.setPower(1);
        // Set the red led on the DIM based on the input digital channel state.
        if (lift.limitswitch_down.getState()) {
           // rt.telemetry().addData("limitswitch", "HIGH");
            lift.motor.setPower(0);
        } else {
          //  rt.telemetry().addData("LimitSwitch", "LOW");
            lift.motor.setPower(0.5);
        }
    }

    @Override
    public void loop() {



        // Use gamepad buttons to move the arm up (Y) and down (A)

        double power = 0;

        if (lift.limitswitch_up.getState()) {
            //Can't go up anymore
           // rt.telemetry().addData("limitswitch_Y", "HIGH");
            if (rt.gamepad1().a())
                power = lift.LIFT_DOWN_POWER;
        } else if (rt.gamepad1().y()){
           // rt.telemetry().addData("LimitSwitch_Y", "LOW");
            power = lift.LIFT_UP_POWER;
        }




        if (lift.limitswitch_down.getState()) {
            //Can't go down anymore
           // rt.telemetry().addData("limitswitch_A", "HIGH");
            power = 0;
            if (rt.gamepad1().y())
                power = lift.LIFT_UP_POWER;
        } else if (rt.gamepad1().a()){
            //rt.telemetry().addData("LimitSwitch_A", "LOW");
            power = lift.LIFT_DOWN_POWER;
        }

        lift.motor.setPower(power);
       // rt.telemetry().addData("arm",  "power = %.2f", power);

        // Send telemetry message to signify robot running;
       // rt.telemetry().addData("claw",  "Offset = %.2f", clawOffset);
    }

    @Override
    public void stop() {
        this.log.pri1("STOP", "");
        // Place any shutdown/deinitialization code here  - this is called ONCE
        // when the task is to be stopped.
        lift.motor.setPower(0);
    }


    /************* END OF TASK INTERFACE METHODS ****************/


    // Place additional helper methods here.

}
