/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package gjb.experimental;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;

/**
 * This file illustrates the concept of driving a path based on time.
 * It uses the common Pushbot hardware class to define the drive on the robot.
 * The code is structured as a LinearOpMode
 *
 * The code assumes that you do NOT have encoders on the wheels,
 *   otherwise you would use: PushbotAutoDriveByEncoder;
 *
 *   The desired path in this example is:
 *   - Drive forward for 3 seconds
 *   - Spin right for 1.3 seconds
 *   - Drive Backwards for 1 Second
 *   - Stop and close the claw.
 *
 *  The code is written in a simple form with no optimizations.
 *  However, there are several ways that this type of sequence could be streamlined,
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Autonomous(name="AOpMode_FinalAuton_RED", group="Pushbot")
//@Disabled
public class AOpMode_FinalAutonRedAlliance extends LinearOpMode {

    /* Declare OpMode members. */
    final String THIS_COMPONENT = "AOpMode_SimpleAuton";
    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);

    // These are initialized during init()
    private  SubSysSimpleTwoMotorDrive drive;
    // Put additional h/w objects here:
    // servo
    public Servo color_sorcerer;
    final double UP_SERVO = 0.4;
    final double DOWN_SERVO = 1.0;
    // color sensor (add later)
    final int UNKNOWN = 0;
    final int RED = 1;
    final int BLUE = 2;
    private LoggingInterface log;
    // values is a reference to the hsvValues array.
    //final float values[] = hsvValues;
    float[] hsvValues = new float[3];
    ColorSensor sensorColor;

    private ElapsedTime     runtime = new ElapsedTime();

    final double FORWARD_INCHES = 0;
    final double SPEEDO = 0.2;
    static final double     FORWARD_SPEED = 0.6;
    static final double     TURN_SPEED    = 0.5;
    static final double     COUNTS_PER_MOTOR_REV    = 1120; // 28*7 cycles per shaft rev. Tetrix:1440
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final int    WAIT_TIME =  3000;
    static final int    JEWEL_WAIT_TIME =  3000;
    static final double     JEWEL_MOVEMENT = 3;
    final double     DRIVE_SPEED             = 0.2; //Keep speed low so robot won't get damaged
    final double     EXTRA_MOVEMENT = 3; //To get in the safe zone
    final double     BACKUP_DISTANCE = 1.5;
    @Override
    public void runOpMode() {
        double timeoutS;
        log = rt.startLogging(AOpMode_SimpleAuton.class.toString());
        log.pri1(LoggingInterface.INIT_START, THIS_COMPONENT);

        SubSysSimpleTwoMotorDrive.Config driveConfig = new SubSysSimpleTwoMotorDrive.Config()
                .leftMotorName("left_drive")
                .rightMotorName("right_drive");
        drive = new SubSysSimpleTwoMotorDrive(rt, driveConfig);

        // Initialize the subsystem and associated task
        drive.init();

        // Create new objects and initialzie them for:
        // Servo
        color_sorcerer = rt.hwLookup().getServo("color_sorcerer");
        // color sensors here...
        sensorColor = rt.hwLookup().getColorSensor("sensorvor_color");


        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Ready to run");    //
        telemetry.update();

        log.pri1(LoggingInterface.INIT_END, THIS_COMPONENT);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Step through each leg of the path, ensuring that the Auto mode has not been stopped along the way

        // Step 1:  Drop the servo
        color_sorcerer.setPosition(DOWN_SERVO);
        //Give some time for the color wand to descend
        sleep(WAIT_TIME);

        // Step 2:  Detect the color of the jewel
        //This code is for the red alliance
        int color = getColor();
        double movement = -29;
        rt.telemetry().addData("COLOR", color);
        telemetry.update();

        // Step 3:  Go back or forward depending on color of jewel
        if (color == RED) {
            // back then back
            rt.telemetry().addData("Action", "got RED");
            telemetry.update();
            encoderDrive(DRIVE_SPEED, -JEWEL_MOVEMENT, -JEWEL_MOVEMENT, 5.0);  // S1: Reverse 2 Inches with 5 Sec timeout
            movement = movement + JEWEL_MOVEMENT;
            sleep(JEWEL_WAIT_TIME);
        } else if (color == UNKNOWN) {
            //There is no change in movement
            rt.telemetry().addData("Action", "got UNKNOWN");
            telemetry.update();
            sleep(10000);
        } else {
            //forward then back
            rt.telemetry().addData("Action", "got BLUE");
            telemetry.update();
            encoderDrive(DRIVE_SPEED, JEWEL_MOVEMENT, JEWEL_MOVEMENT, 5.0);  // S1: Forward 2 Inches with 5 Sec timeout
            sleep(JEWEL_WAIT_TIME ); //wait for the jewel to be knocked off
            movement = movement - JEWEL_MOVEMENT - EXTRA_MOVEMENT;
            color_sorcerer.setPosition(UP_SERVO);
            sleep(WAIT_TIME);

            //Give some time for the robot to slip

            //encoderDrive(0.7, -5.0, -5.0, 5.0); // HACK for getting back on stone

        }


        // Step 4: Lift up servo
         color_sorcerer.setPosition(UP_SERVO);

        //Give some time for the color wand to ascend
        sleep(WAIT_TIME);

        // Step 5: Go to safe zone and stop and backup
        encoderDrive(DRIVE_SPEED, movement, movement, 10.0);
        encoderDrive(DRIVE_SPEED, BACKUP_DISTANCE, BACKUP_DISTANCE, 5.0); //To make sure robot is not touching glyph
    }


    // NOTE: This method is copied without modification from FTC sample
    // code - class PushbotAutoDriveByEncoder_Linear.
    public void encoderDrive(double speed,
    double leftInches,double rightInches,
    double timeoutS) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

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
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (drive.leftDrive.isBusy() && drive.rightDrive.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d",
                        drive.leftDrive.getCurrentPosition(),
                        drive.rightDrive.getCurrentPosition());
                telemetry.update();
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

        telemetry.addData("Hue",h);
        telemetry.addData("Saturation",s);
        telemetry.addData("Value",v);

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
        telemetry.log().add(msg);
        telemetry.update();

        return ret;



    }
}
