package gjb.experimental;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;

/**
 * Created by josephj, keya, and aparna on 12/21/2017.
 */

public class AutonRoverRuckusWizard {

    final String THIS_COMPONENT = "AOpMode_SimpleAuton";
    private final RuntimeSupportInterface rt;
    final public LoggingInterface log;

    // These are initialized during init()
    private SubSysMecDrive drive;
    private SubSysLift lift;
    // Put additional h/w objects here:
    // servo
    public Servo color_sorcerer;
    final double UP_SERVO = 0.4;
    final double DOWN_SERVO = 1.0;
    final long WAIT_TIME = 3000; //its what we used last year
    // color sensor (add later)
    static final int UNKNOWN = 0;
    static final int RED = 1;
    static final int BLUE = 2;


    // values is a reference to the hsvValues array.
    //final float values[] = hsvValues;
    float[] hsvValues = new float[3];
    ColorSensor sensorColor;
    ColorSensor rHuemanatee;
    ColorSensor lHuemanatee;

    // variables for landLift method
    int timeoutS = 5; //set value later
    static final double LIFT_MOTOR_POWER = 0.1; //change it later
    boolean reached = false;

    private ElapsedTime     runtime = new ElapsedTime();


    static final double     COUNTS_PER_MOTOR_REV    = 1120; // 28*7 cycles per shaft rev. Tetrix:1440
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    final double     RSPIN_MOVEMENT = 3.0;
    final double     LSPIN_MOVEMENT = 3.0;
    double LReturn = LSPIN_MOVEMENT;
    double RReturn = RSPIN_MOVEMENT;







    public AutonRoverRuckusWizard(RuntimeSupportInterface rt) {
        this.rt = rt;
        this.log = rt.getRootLog().newLogger(THIS_COMPONENT); // Create a child log.
    }

    public void init() {
        double timeoutS;

        SubSysMecDrive.Config driveConfig = new SubSysMecDrive.Config()
               .fleftMotorName("fleft_drive")
             .frightMotorName("fright_drive")
           .leftMotorName("left_drive")
         .rightMotorName("right_drive");

        drive = new SubSysMecDrive(rt, driveConfig);
        lift = new SubSysLift(rt);
        //Initialize the subsystem and associated task
        drive.init();
        lift.init();

    }

    public void goForward(){
        encoderDriveMec(0.2, 10, 10); //going forward 10 inches then stopping with mecanum wheels
        //speed = 0.2

    }

    public void encoderDriveMec(double speed,
                             double forward, double timeoutS) {// timeouts is taken out
        int newfleftTarget;
        int newfrightTarget;
        int newleftTarget;
        int newrightTarget;

        // Ensure that the opmode is still active
        if (rt.opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newfleftTarget = drive.fleftDrive.getCurrentPosition() + (int)(forward * COUNTS_PER_INCH);
            newfrightTarget = drive.frightDrive.getCurrentPosition() + (int)(forward * COUNTS_PER_INCH);
            newleftTarget = drive.leftDrive.getCurrentPosition() + (int)(forward * COUNTS_PER_INCH);
            newrightTarget = drive.rightDrive.getCurrentPosition() + (int)(forward * COUNTS_PER_INCH);
            drive.fleftDrive.setTargetPosition(newfleftTarget);
            drive.frightDrive.setTargetPosition(newfrightTarget);
            drive.leftDrive.setTargetPosition(newleftTarget);
            drive.rightDrive.setTargetPosition(newrightTarget);

            // Turn On
            drive.fleftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            drive.frightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            drive.leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            drive.rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            drive.fleftDrive.setPower(Math.abs(speed));
            drive.frightDrive.setPower(Math.abs(speed));
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
                rt.telemetry().addData("TargetPosition",  "Running to %7d :%7d", newfleftTarget,  newfrightTarget);
                rt.telemetry().addData("CurrentPosition",  "Running at %7d :%7d",
                        drive.fleftDrive.getCurrentPosition(),
                        drive.frightDrive.getCurrentPosition());
                rt.telemetry().update();
            }


            // Stop all motion;
            drive.setMotorPowerAll(0,0,0,0);


            // Turn off RUN_TO_POSITION
            drive.fleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            drive.frightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            drive.leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            drive.rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            //  sleep(250);   // optional pause after each move
        }

    }

    public void autonTrial (){
        boolean result = landLift();
        if (result == true){
            // Code to move backward etc.
        }
    }
    public boolean landLift (){
        while (rt.opModeIsActive() &&
                (runtime.seconds() < timeoutS) &&
                lift.limitswitch_up.getState()==false) {
            lift.motorola.setPower(LIFT_MOTOR_POWER);
            // Display it for the driver.
            rt.telemetry().addData("lift status", "going up");

            rt.telemetry().update();
        }
        lift.motorola.setPower(0);
        rt.telemetry().addData("lift status", "stopped");
        rt.telemetry().update();
        if (rt.opModeIsActive() && (runtime.seconds() < timeoutS) && lift.limitswitch_up.getState()==true){
            reached = true;
        }
        return reached;
    }
       public void dropMarker (){
        log("marker servo going down");
        lift.markerpolo.setPosition(lift.DROP_POS);
        log("waiting for it to slide off");
        betterSleep(WAIT_TIME);
        log("marker servo lift up");
        lift.markerpolo.setPosition(lift.START_POS);
        betterSleep(WAIT_TIME);
    }
    public void deinit(){
        lift.deinit();
        drive.deinit();
    }




   /* public void encoderDrive(double speed,
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
    */

    public final void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public final void betterSleep(long milliseconds) {
        double start = runtime.seconds();
        double timeoutS = milliseconds / 1000.0;
        while (rt.opModeIsActive() && runtime.seconds() - start < timeoutS) {
                // do nothing;
        }
    }

    void log(String s) {
        rt.telemetry().log().add("AutonWiz: " + s);
    }










    public void encoderDrive2(double speed, double leftInches, double rightInches,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the opmode is still active
        if (rt.opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = drive.leftDrive.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
            newRightTarget = drive.rightDrive.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
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
                rt.telemetry().addData("Path1", "Running to %7d :%7d", newLeftTarget, newRightTarget);
                rt.telemetry().addData("Path2", "Running at %7d :%7d",
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
