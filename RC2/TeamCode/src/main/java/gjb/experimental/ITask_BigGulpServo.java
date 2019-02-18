/**
 * Class created by Aparna, Keya, and Mira
 * FTC #12598 on 10/8/17
 */
package gjb.experimental;

import com.qualcomm.robotcore.util.Range;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.TaskInterface;


public class ITask_BigGulpServo implements TaskInterface {

    final String THIS_COMPONENT = "T_EMPTY"; // Replace EMPTY by short word identifying task

    final RuntimeSupportInterface rt; // Runtime support
    final LoggingInterface log; // Logger

    // Place additional instance variables here - like sub system objects..
    SubSysLift biggulp;

    // Modify this constructor to add any additional initialization parameters - see
    // other tasks for examples.
    public ITask_BigGulpServo(RuntimeSupportInterface rt, SubSysLift biggulp) {
        this.rt = rt;
        this.biggulp  = biggulp;
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
        if(rt.gamepad2().a()){
            biggulp.stickPos=biggulp.GULP_START;
        }
        // Use gamepad left & right Bumpers to move the mineral putter up and down
        if (rt.gamepad2().right_bumper()){
            biggulp.stickPos = biggulp.stickPos + biggulp.STICK_SPEED;
        } else if (rt.gamepad2().left_bumper()){
            biggulp.stickPos = biggulp.stickPos - biggulp.STICK_SPEED;
        }

        biggulp.stickPos = Range.clip(biggulp.stickPos, biggulp.MIN_STICK, biggulp.MAX_STICK);
        biggulp.biggulp.setPosition(biggulp.stickPos);


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
