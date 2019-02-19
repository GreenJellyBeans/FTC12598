/**
 * Class created by Aparna, Keya, and Mira
 * FTC #12598 on 10/8/17
 */
package gjb.experimental;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.TaskInterface;


public class ITask_ArMadillo implements TaskInterface {

    final String THIS_COMPONENT = "T_EMPTY"; // Replace EMPTY by short word identifying task


    final RuntimeSupportInterface rt; // Runtime support
    final LoggingInterface log; // Logger


    // Place additional instance variables here - like sub system objects..
    SubSysIntake intakeS;


    // Modify this constructor to add any additional initialization parameters - see
    // other tasks for examples.
    public ITask_ArMadillo(RuntimeSupportInterface rt, SubSysIntake intake) {
        this.rt = rt;
        this.intakeS  = intake;
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



    @Override
    public void loop() {

        rt.telemetry().addData("joystick_value", rt.gamepad2().right_stick_y());
        //high value indicated being pressed
        //we took out the inverted thing because we are using normal limit switches for the arm

        double power = 0;
        if (intakeS.limit_switch_forward.getState()) {
            //Can't go forward anymore
            rt.telemetry().addData("limit_switch_forward", intakeS.limit_switch_forward.getState());
            power = 0;
            if (-rt.gamepad2().left_stick_y() > 0.2) { //negating joystick value as a test, and it works
                power = intakeS.DILLO_BKWD;
            }
        } else if (-rt.gamepad2().left_stick_y() < -0.2) { //change value later, testing needs to be done, negating joystick valur as a test
            rt.telemetry().addData("limit_switch_forward", "LOW");
            power = intakeS.DILLO_FWD;
        }

        //we took out the inverted thing because we are using normal limit switches for the arm
        if (intakeS.limit_switch_backward.getState()) {
            //Can't go backward anymore
            rt.telemetry().addData("limit_switch_backward", "HIGH");
            power = 0;
            if (-rt.gamepad2().left_stick_y() < -0.2) {//change value later, testing needs to be done //negating joystick valur as a test
                power = intakeS.DILLO_FWD;
            }
        } else if (-rt.gamepad2().left_stick_y() > 0.2){ //change value later, testing needs to be done //negating joystick valur as a test
            rt.telemetry().addData("limit_switch_backward", "LOW");

            power = intakeS.DILLO_BKWD;
        }
        rt.telemetry().addData("motor power", power);
        intakeS.ArmaDillo.setPower(power);



        // Send telemetry message to signify robot running;
       // rt.telemetry().addData("claw",  "Offset = %.2f", clawOffset);
    }

    @Override
    public void stop() {
        this.log.pri1("STOP", "");
        // Place any shutdown/deinitialization code here  - this is called ONCE
        // when the task is to be stopped.
    }


    /************* END OF TASK INTERFACE METHODS ****************/


    // Place additional helper methods here.

}
