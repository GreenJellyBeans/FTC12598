/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/26/2017.
 */
package gjb.experimental;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.TaskInterface;

public class ITask_simpleDriveMec implements TaskInterface {
    final String THIS_COMPONENT = "TASK_SD"; // For "Task simple drive"
    final RuntimeSupportInterface rt;
    final SubSysNotSoSimpleFourMotorDrive drive;
    final LoggingInterface log;

    public ITask_simpleDriveMec(RuntimeSupportInterface rt, SubSysNotSoSimpleFourMotorDrive ssDrive) {
        this.rt = rt;
        this.drive = ssDrive;
        this.log = rt.getRootLog().newLogger(THIS_COMPONENT);
    }

    @Override
    public void init() {
        this.log.pri1(LoggingInterface.INIT_START, "");
        this.log.pri1(LoggingInterface.INIT_END, "");
    }

    @Override
    public void init_loop() {
        this.log.pri3("init_loop called");
    }

    @Override
    public void start() {
        this.log.pri1("START", "");
    }

    @Override
    public void loop() {
        double fwd_bkwd;
        double rt_lt;
        //boolean right_bumper;
        //boolean left_bumper;
        double _45; //For elemetry (value is average x & y and then at a ratio so the largest = 1
                    // The numbers 45 & 135 represent the degree for diagonal)
        double _135; //see above
        // Run wheels in tank mode (note: The joystick goes negative when pushed forwards, so negate it)
        fwd_bkwd = -rt.gamepad1().right_stick_y();
        rt_lt = -rt.gamepad1().right_stick_x(); //should not be negative but already coded everything to compensate so x axis is "reversed"
        //right_bumper =  rt.gamepad1().right_bumper();
        //left_bumper =  rt.gamepad1().left_bumper();
        //left = adjustPower2(left);
        //right = adjustPower2(right);

        if ((fwd_bkwd <-0.25 || fwd_bkwd>0.25) &&rt_lt >-0.25 && rt_lt <0.25 ) {
            drive.leftDrive.setPower(fwd_bkwd);
            drive.fleftDrive.setPower(fwd_bkwd);
            drive.frightDrive.setPower(fwd_bkwd);
            drive.rightDrive.setPower(fwd_bkwd);
        }   //Moves motor forward and backward

        if ((rt_lt <-0.25 || rt_lt >0.25) && fwd_bkwd >-0.25 && fwd_bkwd<0.25){
            drive.leftDrive.setPower(rt_lt);
            drive.fleftDrive.setPower(-rt_lt);
            drive.frightDrive.setPower(rt_lt);
            drive.rightDrive.setPower(-rt_lt);
        }   //Moves robot side to side (strafe)

        if ((fwd_bkwd > 0.25 && rt_lt < -0.25) || (fwd_bkwd < -0.25 && rt_lt > 0.25)) {
            drive.rightDrive.setPower((-rt_lt + fwd_bkwd)*2/3);
            drive.fleftDrive.setPower((-rt_lt + fwd_bkwd)*2/3);
            drive.leftDrive.setPower(0);
            drive.frightDrive.setPower(0);
        }   //this moves the robot at a 45 degree angle (hence the number 45 in telemetry)

        if ((fwd_bkwd > 0.25 && rt_lt > 0.25) || (fwd_bkwd < -0.25 && rt_lt < -0.25)) {
            drive.leftDrive.setPower((rt_lt + fwd_bkwd)*2/3);
            drive.frightDrive.setPower((rt_lt + fwd_bkwd)*2/3);
            drive.rightDrive.setPower(0);
            drive.fleftDrive.setPower(0);
        }   //this moves the robt at a 135 degree angle (hence the number 135 in telemetry)

        if (fwd_bkwd<0.25 && fwd_bkwd>-0.25 && rt_lt<0.25 && rt_lt>-0.25 && !rt.gamepad1().right_bumper() && !rt.gamepad1().left_bumper()) {
            drive.leftDrive.setPower(0);
            drive.fleftDrive.setPower(0);
            drive.frightDrive.setPower(0);
            drive.rightDrive.setPower(0);
        }   //When no buttons are pressed, all motors stop

        if (rt.gamepad1().right_bumper()) {         //right bumper makes the robot spin clockwise
            drive.leftDrive.setPower(0.5);
            drive.fleftDrive.setPower(0.5);
            drive.frightDrive.setPower(-0.5);
            drive.rightDrive.setPower(-0.5);
        }else if (rt.gamepad1().left_bumper()) {    //left bumper makes the robot spin counterclockwise
            drive.leftDrive.setPower(-0.5);
            drive.fleftDrive.setPower(-0.5);
            drive.frightDrive.setPower(0.5);
            drive.rightDrive.setPower(0.5);
        }
        /*
        if (right_bumper) {
            drive.leftDrive.setPower(1);
            drive.fleftDrive.setPower(1);
            drive.frightDrive.setPower(-1);
            drive.rightDrive.setPower(-1);
        }

        if (left_bumper) {
            drive.leftDrive.setPower(-1);
            drive.fleftDrive.setPower(-1);
            drive.frightDrive.setPower(1);
            drive.rightDrive.setPower(1);
        }
        WE thought this code was not working but it may have just been the telemetry problem below
        */

        _45 = (-rt_lt + fwd_bkwd)*2/3;
        _135 = (rt_lt + fwd_bkwd)*2/3;

        rt.telemetry().addData("fwd_bkwd",  "%.2f", fwd_bkwd);
        rt.telemetry().addData("rt_lt", "%.2f", rt_lt);
        rt.telemetry().addData("45",  "%.2f", _45);
        rt.telemetry().addData("135", "%.2f", _135);
        /*
        rt.telemetry().addData("right_bumper", "%.2f", rt.gamepad1().right_bumper());
        rt.telemetry().addData("leftt_bumper", "%.2f", rt.gamepad1().left_bumper());
        Not sure why this telemetry won't work - may debug later
        */
     }

    private double adjustPower(double x) {
        double y = x;
        double firstSlope = 0.4;
        double secondSlope = 9.0/7.5;
        if (Math.abs(x) <= 0.25) { // -0.25 < x > 0.25
            y = x * firstSlope;
        } else {
            if (x > 0.25) {
                y  = 0.1 + secondSlope * (x - 0.25);
            } else if (x < - 0.25) {
                y = -0.1 + secondSlope * (x  + 0.25);
            }
        }
        return y;
    }

    private double adjustPower2(double x) {
        double y = x;
        double firstSlope = 0.2;
        double change_x = 0.5;
        double change_y = firstSlope * change_x;
        double secondSlope = (1.0 - change_y)/(1.0-change_x);
        if (Math.abs(x) <= change_x) { // -0.5 < x > 0.5
            y = x * firstSlope;
        } else {
            if (x > change_x) {
                y  = change_y + secondSlope * (x - change_x);
            } else if (x < - change_x) {
                y = -change_y + secondSlope * (x  + change_x);
            }
        }
        return y;
    }

    //adjust powers are to slowly increase/decrease motor speed so less stress on the motor

    @Override
    public void stop() {
        this.log.pri1("STOP", "");
        drive.deinit();
    }

}
