/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/30/2017.
 */
package gjb.experimental;
import android.content.Context;

import org.firstinspires.ftc.teamcode.R;

import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;
import gjb.utils.Logger;

// NOTE: You can used this as the base for a TeleopMode - all that needs to be
// done is to replace the @Autonomous annotation by @TeleOp below.
@Autonomous(name="AOpMode_TestSoundPlayer", group="dummy")
@Disabled
/*
 *  This Autonomous OpMode makes initializes the Empty subsystem and starts the Empty task.
 *  ADD/REPLACE THIS COMMENT BASED ON THE WHEN THE NEW TASK WAS CREATED
 */
public class AOpMode_TestSoundPlayer extends OpMode{
    final String THIS_COMPONENT = "AOM_SOUND"; // Replace EMPTY by short word identifying Op mode
    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);

    // These are initialized during init()
    SubSysEmpty empty;
    ITask_Empty task;
    private LoggingInterface log;
    public SoundPlayer player;
    // WARNING: Only very small files seem to play - this is OGG at 42kbps, and
    // size 63KB. Anything larger may not play for long. It will play but not for long.
    // "gmta" stands for "good morning to all" - it's the birthday song tune.
    int resID = 0; // R.raw.gmta_ogg_42;

    /*************** START OF OPMODE INTERFACE METHODS **********************/

    @Override
    public void init() {
        log = rt.startLogging(AOpMode_TestSoundPlayer.class.toString());
        log.pri1(LoggingInterface.INIT_START, THIS_COMPONENT);

        empty = new SubSysEmpty(rt);
        task = new ITask_Empty(rt);

        // Do any additional op-mode initialization here.
        this.player = SoundPlayer.getInstance();

        // Initialize the  subsystem and associated task
        empty.init();
        task.init();

        log.pri1(LoggingInterface.INIT_END, THIS_COMPONENT);
    }


    @Override
    public void init_loop() {
        task.init_loop();
    }


    @Override
    public void start() {
        Context context = hardwareMap.appContext;

        log.pri1("Calling player.play");
        this.player.play(context, resID);
        log.pri1("Player.play returns");
        task.start();
    }


    @Override
    public void loop() {
        task.loop();
    }


    @Override
    public void stop() {
        //this.player.close(); NO - this nulls the player.
        task.stop();
        rt.stopLogging();
    }

    /*************** END OF OPMODE INTERFACE METHODS ************************/
}
