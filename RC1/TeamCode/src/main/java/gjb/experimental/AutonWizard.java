package gjb.experimental;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;

/**
 * Created by josephj, keya, and aparna on 12/21/2017.
 */

public class AutonWizard {
    RuntimeSupportInterface rt;
    private  SubSysSimpleTwoMotorDrive drive;
    public Servo color_sorcerer;
    // color sensor (add later)
    final int UNKNOWN = 0;
    final int RED = 1;
    final int BLUE = 2;
    private LoggingInterface log;
    // values is a reference to the hsvValues array.
    //final float values[] = hsvValues;
    float[] hsvValues = new float[3];
    ColorSensor sensorColor;

    private ElapsedTime runtime = new ElapsedTime();

    static final double     COUNTS_PER_MOTOR_REV    = 1120; // 28*7 cycles per shaft rev. Tetrix:1440
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);


    public AutonWizard(RuntimeSupportInterface rt, SubSysSimpleTwoMotorDrive d) {
        this.rt = rt;
        this.drive = d;
    }

    public int getColor() {
        final double SCALE_FACTOR = 255;
        // convert the RGB values to HSV values.
        // multiply by the SCALE_FACTOR.
        // then cast it back to int (SCALE_FACTOR is a double)
        int r = (int) (sensorColor.red() * SCALE_FACTOR); // between 0 and 255
        int g = (int) (sensorColor.green() * SCALE_FACTOR); // between 0 and 255
        int b = (int) (sensorColor.blue() * SCALE_FACTOR); // between 0 and 255
        Color.RGBToHSV(r,g,b,hsvValues);
        double h = hsvValues[0];
        double s = hsvValues[1];
        double v = hsvValues[2];

        // send the info back to driver station using telemetry function.

        rt.telemetry().addData("Hue",h);
        rt.telemetry().addData("Saturation",s);
        rt.telemetry().addData("Value",v);

        int ret = UNKNOWN;
        //if ((h<20 || h>350) && s>0.3 && v>10 && v<200){
        //    ret =  RED;
        //} else if (h>170 && h<215 && s>0.3 && v>10 && v<200) {
        //    ret =  BLUE;
        //}
        if ((h<70 || h>340) && s>0.15 && v > 5 && v<400){
            ret =  RED;
        } else if (h>140 && h<225 && s>0.15 && v > 4 && v<400) {
            ret =  BLUE;
        }

        String msg = "H=" + h + ", S=" + s + ", V=" + v + "RET:" + ret;
        rt.telemetry().log().add(msg);
        rt.telemetry().update();

        return ret;
    }

    public void encoderDrive(double speed,
                             double leftInches,double rightInches,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the opmode is still active
        if (rt.opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = drive.leftDrive.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightTarget = drive.rightDrive.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            drive.leftDrive.setTargetPosition(newLeftTarget);
            drive.rightDrive.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            drive.leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            drive.rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            drive.leftDrive.setPower(Math.abs(speed));
            drive.rightDrive.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (rt.opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (drive.leftDrive.isBusy() && drive.rightDrive.isBusy())) {

                // Display it for the driver.
                rt.telemetry().addData("Path1",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
                rt.telemetry().addData("Path2",  "Running at %7d :%7d",
                        drive.leftDrive.getCurrentPosition(),
                        drive.rightDrive.getCurrentPosition());
                rt.telemetry().update();
            }

            // Stop all motion;
            drive.leftDrive.setPower(0);
            drive.rightDrive.setPower(0);

            // Turn off RUN_TO_POSITION
            drive.leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            drive.rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            //  sleep(250);   // optional pause after each move
        }

    }
}
