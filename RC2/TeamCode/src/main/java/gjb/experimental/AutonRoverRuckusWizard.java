package gjb.experimental;

import android.graphics.Color;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.security.KeyPairGenerator;
import java.util.Locale;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;

/**
 * Created by josephj, keya, and aparna on 12/21/2017.
 */

public class AutonRoverRuckusWizard {

    final String THIS_COMPONENT = "AOpMode_SimpleAuton";
    static final double TWO_PI  = Math.PI * 2;
    private final RuntimeSupportInterface rt;
    final public LoggingInterface log;

    // These are initialized during init()
    SubSysMecDrive drive;
    SubSysLift lift;
    SubSysVision vision;
    // Put additional h/w objects here:
    // servo
    public Servo color_sorcerer;
    final double UP_SAMPLE = 0.4;
    final double MID_SAMPLE = 0.8;
    final double DOWN_SAMPLE = 1.0;
    final long WAIT_TIME = 100; //its what we used last year
    // color sensor (add later)
    final long MARKER_WAIT_TIME = 600;
    final double DRIVE_SPEED = 1.0;
    final double SPIN_SPEED = 0.5;
    static final int UNKNOWN = 0;
    static final int RED = 1;
    static final int BLUE = 2;
    final double SAMPLE_FORWARD = 14.5;
    final double LAND_SAMPLE_FORWARD = 12.5;
    Orientation angles;

    // values is a reference to the hsvValues array.
    //final float values[] = hsvValues;
    float[] hsvValues = new float[3];
    ColorSensor sensorColor;
    ColorSensor rHuemanatee;
    ColorSensor lHuemanatee;

    // variables for landLift method
    int timeoutS = 15; //set value later
    static final double LIFT_MOTOR_POWER = 1.0; //change it later
    static final double LIFT_DOWN_POWER  = -1.0;


    private ElapsedTime runtime = new ElapsedTime();

    //distance to strafe after sampling in the crater
    final double MINERAL_STRAFE_DISTANCE = 38.0;

    static final double COUNTS_PER_MOTOR_REV = 1120; // 28*7 cycles per shaft rev. Tetrix:1440
    static final double DRIVE_GEAR_REDUCTION = 1.0;     // This is < 1.0 if geared UP
    static final double WHEEL_DIAMETER_INCHES = 4.0;     // For figuring circumference
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    final double RSPIN_MOVEMENT = 3.0;
    final double LSPIN_MOVEMENT = 3.0;
    private BNO055IMU imu;


    public AutonRoverRuckusWizard(RuntimeSupportInterface rt) {
        this.rt = rt;
        this.log = rt.getRootLog().newLogger(THIS_COMPONENT); // Create a child log.
    }

    public void init() {
        double timeoutS;

        init_imu();
         SubSysMecDrive.Config driveConfig = new SubSysMecDrive.Config()
                .fleftMotorName("fleft_drive")
                .frightMotorName("fright_drive")
                .leftMotorName("left_drive")
                .rightMotorName("right_drive");
        drive = new SubSysMecDrive(rt, driveConfig);
        lift = new SubSysLift(rt);
        vision = new SubSysVision(rt);
        //Initialize the subsystem and associated task
        drive.init();
        lift.init();
        vision.init();

    }

     public void GOFORWARD (){
        encoderDriveMec(DRIVE_SPEED, 12, 2);
     }


    public void goForward() {
        encoderDriveMec(0.2, 10, 10); //going forward 10 inches then stopping with mecanum wheels
        //speed = 0.2

    }

    public void firstPath() {
       encoderDriveMec(DRIVE_SPEED, 43, 10);
       betterSleep(1000);
       dropMarker();

       imuBearingMec(0.3, 45, 3); // -135
        log("reached angle");
        betterSleep(1000);

      encoderDriveMec(DRIVE_SPEED, -73, 5);

        setMotorPowerAll(0, 0, 0, 0);
    }

    public void fourthWaitPath() {
        encoderDriveMec(DRIVE_SPEED, 43, 10);
        betterSleep(WAIT_TIME);
        dropMarker();
        imuBearingMec(DRIVE_SPEED, 45, 3); // -135
        log("reached angle");
        betterSleep(WAIT_TIME);

        encoderDriveMec(DRIVE_SPEED, -73, 5);

        setMotorPowerAll(0, 0, 0, 0);
    }
    public void secondPath() {
        encoderDriveMec(DRIVE_SPEED, 31, 5);

        setMotorPowerAll(0,0,0,0);
    }
    public void thirdCrabPath() {
        encoderDriveMec(DRIVE_SPEED, 15.5, 5);

        encoderCrabMec(DRIVE_SPEED, 15, 3);

        encoderDriveMec(DRIVE_SPEED, 15.5, 5);

        setMotorPowerAll(0,0,0,0);
    }
    public void fifthCraterDepotPath() {
        encoderDriveMec(DRIVE_SPEED, 17, 3);
        betterSleep(WAIT_TIME);
        encoderCrabMec(DRIVE_SPEED, -38, 6);
        betterSleep(WAIT_TIME);
        imuBearingMec(DRIVE_SPEED, 130, 4); //angle used to be 135
        betterSleep(WAIT_TIME);
        encoderCrabMec(DRIVE_SPEED,12,2);
        betterSleep(50);
        encoderCrabMec(0.2,6,2);
        encoderCrabMec(DRIVE_SPEED, -4, 1);
        betterSleep(WAIT_TIME);
        imu_reset();
        encoderDriveMec(DRIVE_SPEED, 40, 3);
        betterSleep(WAIT_TIME);
        dropMarker();
        //Go forward halfway through the depot
        //imuBearingMec 90 degrees
        //Go forward until you reach the opposing alliance servo
        encoderDriveMec(DRIVE_SPEED, -63, 5);
        betterSleep(WAIT_TIME);
        setMotorPowerAll(0,0,0,0);

    }






    public void samplingCraterPath () {
        //parking in crater after sampling
        knockSampling();
        encoderDriveMec(DRIVE_SPEED, 15, 2); //change forward later

    }


    public void samplingCraterDepotOurCraterPath(){
        //going to Depot and our crater after sampling
        knockSampling();
        encoderCrabMec(DRIVE_SPEED, -MINERAL_STRAFE_DISTANCE, 3);
        imuBearingMec(SPIN_SPEED, 130, 4); //angle used to be 135
        betterSleep(WAIT_TIME);
        encoderCrabMec(DRIVE_SPEED,12,2);
        betterSleep(50);
        encoderCrabMec(0.2,8,2);
        encoderCrabMec(DRIVE_SPEED, -4, 1);
        betterSleep(WAIT_TIME);
        imu_reset();
        encoderDriveMec(DRIVE_SPEED, 40, 3);
        betterSleep(WAIT_TIME);
        dropMarker();
        encoderCrabMec(0.2,4,2);
        encoderCrabMec(DRIVE_SPEED, -4, 1);
        encoderDriveMec(DRIVE_SPEED, -66, 5);
        betterSleep(WAIT_TIME);
        setMotorPowerAll(0,0,0,0);
    }

    public void samplingDepotPath() {
        //going to Depot after sampling
        knockSampling();
        encoderDriveMec(0.4, 1, 2);
        encoderCrabMec(DRIVE_SPEED, -MINERAL_STRAFE_DISTANCE, 3);
        imuBearingMec(SPIN_SPEED, -45, 2);
        encoderCrabMec(DRIVE_SPEED,-9,3);
        //betterSleep(50);
        encoderCrabMec(0.2,-10,2);
        encoderCrabMec(DRIVE_SPEED, 2.5, 2);
        // encoderCrabMec(0.2, 6, 3);
        // encoderCrabMec(DRIVE_SPEED, -4, 2);
        encoderDriveMec(DRIVE_SPEED, 40, 5);//inches was 15
        dropMarker();
    }

    public void samplingDepotOtherCraterPath() {
        //going to Depot and other crater after sampling
        knockSampling();
        encoderDriveMec(0.4, 1, 2);
        encoderCrabMec(DRIVE_SPEED, -MINERAL_STRAFE_DISTANCE, 3);
        imuBearingMec(SPIN_SPEED, -45, 2);
        encoderCrabMec(DRIVE_SPEED, -9, 3);
        encoderCrabMec(0.2, -10, 3);

        encoderCrabMec(DRIVE_SPEED, 2.5, 2);
        encoderDriveMec(DRIVE_SPEED, 40, 5);
        dropMarker();
        encoderDriveMec(0.7, -60, 6);
        setMotorPowerAll(0,0,0,0);
    }

    public void samplingDepotCraterPath(){
        //going to Depot and our crater after sampling
        knockSampling();
        encoderCrabMec(DRIVE_SPEED, MINERAL_STRAFE_DISTANCE, 3);
        imuBearingMec(SPIN_SPEED, 45, 2);
        encoderCrabMec(DRIVE_SPEED,9,2);
        //betterSleep(50);
        encoderCrabMec(0.2,10,2);
        encoderCrabMec(DRIVE_SPEED, -2, 0.5);
        // encoderCrabMec(0.2, 6, 3);
        // encoderCrabMec(DRIVE_SPEED, -4, 2);
        encoderDriveMec(1.1, 40, 3);//inches was 15
        dropMarker();
        encoderDriveMec(DRIVE_SPEED, -60, 6);
        setMotorPowerAll(0,0,0,0);
    }





    public void landSamplingDepotOtherCraterPath() {
        //probably not going to use this path
        //going to Depot and other crater after sampling
        autonTrial();
        landKnockSampling();
        encoderDriveMec(0.4, 1, 2);
        encoderCrabMec(DRIVE_SPEED, -MINERAL_STRAFE_DISTANCE, 3);
        imuBearingMec(SPIN_SPEED, -45, 2);
        encoderCrabMec(DRIVE_SPEED, -9, 3);
        encoderCrabMec(0.2, -10, 3);

        encoderCrabMec(DRIVE_SPEED, 2.5, 2);
        encoderDriveMec(DRIVE_SPEED, 40, 5);
        dropMarker();
        encoderDriveMec(0.7, -60, 6);
        setMotorPowerAll(0,0,0,0);
    }


    public void landSamplingStraightDepotBkwdsOtherCraterTestPath() {
        //going to Depot after sampling and landing
        autonTrial();
        landKnockSampling();
        encoderDriveMec(DRIVE_SPEED, 37, 5);
        dropMarker();
        imuBearingMec(SPIN_SPEED, -45, 2);
        encoderDriveMec(DRIVE_SPEED,-2,3);
        encoderCrabMec(DRIVE_SPEED, -9, 3);
        encoderCrabMec(0.2, -8, 3);
        encoderCrabMec(DRIVE_SPEED, 3.5, 2);
        encoderDriveMec(0.7, -60, 6);
        setMotorPowerAll(0,0,0,0);
    }

    public void landSamplingTestWSideMarker() {
        //going to Depot after sampling and landing
        autonTrial();
        landKnockSampling();
        encoderDriveMec(DRIVE_SPEED, 32, 5);
        imuBearingMec(SPIN_SPEED,  45, 2);
        encoderDriveMec(0.7, 17, 3);
        dropMarker();
        encoderDriveMec(0.5, -3.5, 2);
        encoderCrabMec(DRIVE_SPEED, -37, 4);
        encoderDriveMec(0.7, 4, 2);
        encoderCrabMec(DRIVE_SPEED,-30,3);
        setMotorPowerAll(0,0,0,0);
    }

    public void strafeTest() {
        encoderCrabMec(DRIVE_SPEED, -70, 20);
        setMotorPowerAll(0,0,0,0);
    }


    public void crabTest(){
        autonTrial();
    }


    public void landSamplingDepotPath() {
        //going to Depot after sampling and landing
        autonTrial();
        landKnockSampling();
        encoderDriveMec(0.4, 1, 2);
        encoderCrabMec(DRIVE_SPEED, -MINERAL_STRAFE_DISTANCE, 3);
        imuBearingMec(SPIN_SPEED, -45, 2);
        encoderCrabMec(DRIVE_SPEED,-9,3);
        //betterSleep(50);
        encoderCrabMec(0.2,-10,2);
        encoderCrabMec(DRIVE_SPEED, 2.5, 2);
        // encoderCrabMec(0.2, 6, 3);
        // encoderCrabMec(DRIVE_SPEED, -4, 2);
        encoderDriveMec(DRIVE_SPEED, 40, 5);//inches was 15
        dropMarker();
        setMotorPowerAll(0,0,0,0);
    }

    public void landSamplingTestDepotPath() {
        //going to Depot after sampling and landing
        autonTrial();
        landKnockSampling();
        encoderDriveMec(0.4, 1, 2);
        encoderCrabMec(DRIVE_SPEED, -MINERAL_STRAFE_DISTANCE, 3);
        imuBearingMec(SPIN_SPEED, 45, 2);
        encoderDriveMec(DRIVE_SPEED,9,3);
        //betterSleep(50);
        encoderDriveMec(0.2,5,2);
        encoderDriveMec(DRIVE_SPEED, -2.5, 2);
        // encoderCrabMec(0.2, 6, 3);
        // encoderCrabMec(DRIVE_SPEED, -4, 2);
        encoderCrabMec(DRIVE_SPEED, 40, 5);//inches was 15
        dropMarker();
        encoderCrabMec(DRIVE_SPEED, -40, 7);
        encoderDriveMec(0.7, 4,2);
        encoderCrabMec(DRIVE_SPEED, 30, 5);
        setMotorPowerAll(0,0,0,0);
    }

    public void landSamplingDepotCraterPath(){
        //going to Depot and our crater after sampling and landing
        autonTrial();
        landKnockSampling();
        encoderDriveMec(DRIVE_SPEED, 40, 5);
        dropMarker();
        imuBearingMec(SPIN_SPEED, 45, 2);
        encoderDriveMec(DRIVE_SPEED, -3, 2);
        encoderCrabMec(DRIVE_SPEED,9,2);
        //betterSleep(50);
        encoderCrabMec(0.2,8,2);
        encoderCrabMec(DRIVE_SPEED, -3, 0.5);
        encoderDriveMec(DRIVE_SPEED, -70, 6);
        setMotorPowerAll(0,0,0,0);
    }
    public void landSamplingTestDepotCraterPath(){
        //going to Depot and our crater after sampling and landing
        autonTrial();
        landKnockSampling();
        encoderDriveMec(DRIVE_SPEED, 32, 5);
        imuBearingMec(0.5, 135, 3);
        encoderDriveMec(0.7, -35, 3);
        dropMarker();
        encoderDriveMec(0.5, 2.5, 2);
        encoderCrabMec(DRIVE_SPEED, -37, 4);
        encoderDriveMec(0.7, -3, 2);
        encoderCrabMec(DRIVE_SPEED,-30,3);
        setMotorPowerAll(0,0,0,0);
    }
    public void landSamplingCraterDepotOurCraterPath(){
        //going to Depot and our crater after sampling and landing
        autonTrial();
        landKnockSampling();
        encoderCrabMec(DRIVE_SPEED, -MINERAL_STRAFE_DISTANCE, 3);
        imuBearingMec(SPIN_SPEED, 130, 4); //angle used to be 135
        //betterSleep(WAIT_TIME);
        encoderCrabMec(DRIVE_SPEED,12,2);
        //betterSleep(50);
        encoderCrabMec(0.2,8,2);
        encoderCrabMec(DRIVE_SPEED, -4, 1);
        //betterSleep(WAIT_TIME);
        imu_reset();
        encoderDriveMec(DRIVE_SPEED, 40, 3);
        //betterSleep(WAIT_TIME);
        dropMarker();
        encoderCrabMec(0.2,4,2);
        encoderCrabMec(DRIVE_SPEED, -4, 1);
        encoderDriveMec(DRIVE_SPEED, -66, 5);
        //betterSleep(WAIT_TIME);
        setMotorPowerAll(0,0,0,0);
    }
    public void landSamplingTestCraterDepotOurCraterPath(){
        //going to Depot and our crater after sampling and landing
        autonTrial();
        landKnockSampling();
        encoderCrabMec(DRIVE_SPEED, -MINERAL_STRAFE_DISTANCE, 3);
        imuBearingMec(SPIN_SPEED, -140, 4); //angle used to be 135
        //betterSleep(WAIT_TIME);
        encoderDriveMec(DRIVE_SPEED,-12,2);
        //betterSleep(50);
        encoderDriveMec(0.2,-8,2);
        encoderDriveMec(DRIVE_SPEED, 4, 1);
        //betterSleep(WAIT_TIME);
        imu_reset();
        encoderCrabMec(DRIVE_SPEED, 40, 3);
        //betterSleep(WAIT_TIME);
        dropMarker();
        encoderDriveMec(0.2,-4,2);
        encoderDriveMec(DRIVE_SPEED, 4, 1);
        encoderCrabMec(DRIVE_SPEED, -66, 5);
        //betterSleep(WAIT_TIME);
        setMotorPowerAll(0,0,0,0);
    }

    public void landSamplingCraterPath () {
        //parking in crater after sampling and landing
        autonTrial();
        landKnockSampling();
        encoderCrabMec(DRIVE_SPEED, MINERAL_STRAFE_DISTANCE, 3);
        imuBearingMec(SPIN_SPEED, 45, 2);
        encoderCrabMec(DRIVE_SPEED, 9, 3);
        encoderCrabMec(0.2, 8, 3);
        encoderCrabMec(DRIVE_SPEED, 2.5, 2);
        encoderDriveMec(DRIVE_SPEED, 8, 4);
        setMotorPowerAll(0,0,0,0);
    }





    void composeTelemetry() {
        // At the beginning of each telemetry update, grab a bunch of data
        // from the IMU that we will then display in separate lines.
        rt.telemetry().addAction(new Runnable() { @Override public void run()
        {
            // Acquiring the angles is relatively expensive; we don't want
            // to do that in each of the three items that need that info, as that's
            // three times the necessary expense.
            angles   = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        }
        });
        rt.telemetry().addLine()
                .addData("heading", new Func<String>() {
                    @Override
                    public String value() {
                        return formatAngle(angles.angleUnit, angles.firstAngle);
                    }
                })
                .addData("roll", new Func<String>() {
                    @Override
                    public String value() {
                        return formatAngle(angles.angleUnit, angles.secondAngle);
                    }
                })
                .addData("pitch", new Func<String>() {
                    @Override
                    public String value() {
                        return formatAngle(angles.angleUnit, angles.thirdAngle);
                    }
                });
    }

    public void encoderDriveMec(double speed,
                                double forward, double timeoutS) {// timeouts is taken out
        int newfleftTarget;
        int newfrightTarget;
        int newleftTarget;
        int newrightTarget;
        double startTime = runtime.seconds();
        // Ensure that the opmode is still active
        if (rt.opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newfleftTarget = drive.fleftDrive.getCurrentPosition() + (int) (forward * COUNTS_PER_INCH);
            newfrightTarget = drive.frightDrive.getCurrentPosition() + (int) (forward * COUNTS_PER_INCH);
            newleftTarget = drive.leftDrive.getCurrentPosition() + (int) (forward * COUNTS_PER_INCH);
            newrightTarget = drive.rightDrive.getCurrentPosition() + (int) (forward * COUNTS_PER_INCH);
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
                    ((runtime.seconds() - startTime)< timeoutS) &&
                    (drive.leftDrive.isBusy() && drive.rightDrive.isBusy())) {

                // Display it for the driver.
                rt.telemetry().addData("TargetPosition", "Running to %7d :%7d", newfleftTarget, newfrightTarget);
                rt.telemetry().addData("CurrentPosition", "Running at %7d :%7d",
                        drive.fleftDrive.getCurrentPosition(),
                        drive.frightDrive.getCurrentPosition());
                rt.telemetry().update();
            }


            // Stop all motion;
            drive.setMotorPowerAll(0, 0, 0, 0);


            // Turn off RUN_TO_POSITION
            drive.fleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            drive.frightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            drive.leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            drive.rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            //  sleep(250);   // optional pause after each move
        }
    }


    public void encoderCrabMec(double speed,
                                double forward, double timeoutS) {// timeouts is taken out
        int newfleftTarget;
        int newfrightTarget;
        int newleftTarget;
        int newrightTarget;
        double startTime = runtime.seconds();
        // Ensure that the opmode is still active
        if (rt.opModeIsActive()) {
            final double STRAFE_COUNTS_PER_INCH = COUNTS_PER_INCH;
            // Determine new target position, and pass to motor controller
            newfleftTarget = drive.fleftDrive.getCurrentPosition() + (int) (forward * STRAFE_COUNTS_PER_INCH);
            newfrightTarget = drive.frightDrive.getCurrentPosition() + (int) (-forward * STRAFE_COUNTS_PER_INCH);
            newleftTarget = drive.leftDrive.getCurrentPosition() + (int) (-forward * STRAFE_COUNTS_PER_INCH);
            newrightTarget = drive.rightDrive.getCurrentPosition() + (int) (forward * STRAFE_COUNTS_PER_INCH);
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
                    ((runtime.seconds() - startTime) < timeoutS) &&
                    (drive.leftDrive.isBusy() && drive.rightDrive.isBusy())) {

                // Display it for the driver.
                rt.telemetry().addData("TargetPosition", "Running to %7d :%7d", newfleftTarget, newfrightTarget);
                rt.telemetry().addData("CurrentPosition", "Running at %7d :%7d",
                        drive.fleftDrive.getCurrentPosition(),
                        drive.frightDrive.getCurrentPosition());
                rt.telemetry().update();
            }


            // Stop all motion;
            drive.setMotorPowerAll(0, 0, 0, 0);


            // Turn off RUN_TO_POSITION
            drive.fleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            drive.frightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            drive.leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            drive.rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            //  sleep(250);   // optional pause after each move
        }
    }

    public void autonTrial() {
        boolean result = landLift();
        if (result == true) {
           // betterSleep(5000);
            log("now move");
            //disengaging from the hook with a series of small actions
            encoderDriveMec(0.6, -2,2);
            encoderCrabMec(DRIVE_SPEED, -4, 2);
            encoderDriveMec(0.6, 2, 2);
            encoderCrabMec(DRIVE_SPEED, 4, 2);
            log("can you see this");

        }
        log("helooooooooooo");
    }


    void setMotorPowerAll(double pFL, double pFR, double pBL, double pBR) {
        drive.fleftDrive.setPower(pFL);
        drive.frightDrive.setPower(pFR);
        drive.leftDrive.setPower(pBL);
        drive.rightDrive.setPower(pBR);

    }

    public boolean landLift() {
        final double start = runtime.seconds();
        boolean reached = false;
        log("start");
        while (rt.opModeIsActive() &&
                (runtime.seconds() - start < timeoutS) &&
                lift.limitswitch_up.getState() == false) {//lift still hasn't reached the ground
            lift.motorola.setPower(LIFT_MOTOR_POWER);
            // Display it for the driver.
            rt.telemetry().addData("lift status", "going up");

            rt.telemetry().update();
        }
        lift.motorola.setPower(0);
       log("lift status: stopped");
        rt.telemetry().update();
        if (rt.opModeIsActive() && (runtime.seconds() - start < timeoutS) ) {

            reached = true;
        }
        log("limit switch up:" +lift.limitswitch_up.getState());
        return reached;
    }


    public void dropMarker() {
        this.log.pri1(LoggingInterface.OTHER, "marker servo going down");
        lift.markerpolo.setPosition(lift.DROP_POS); //was markerpolo
        log("waiting for it to slide off");
        betterSleep(MARKER_WAIT_TIME);
        log("marker servo lift up");
        lift.markerpolo.setPosition(lift.START_POS); //was markerpolo
        betterSleep(WAIT_TIME);
    }
    public void servoTest(){

        //was for minerservo
        log("putting power to servo");
        log("0.2");

        lift.biggulp.setPosition(0.2);
        betterSleep(2000);
        log("0.3");
        lift.biggulp.setPosition(0.3);
        betterSleep(2000);
        log("0.4");
        lift.biggulp.setPosition(0.4);
        betterSleep(2000);
        log("0.5");
        lift.biggulp.setPosition(0.5);
        betterSleep(2000);
        log("0.75");
        lift.biggulp.setPosition(0.75);
        betterSleep(2000);
        //log("1");
        //lift.boas.setPosition(1);
        //betterSleep(2000);
        log("finished power");
        //vision.minerservor.setPosition(0.25);
        //betterSleep(500);
        //vision.minerservor.setPosition(1.0);
        //encoderCrabMec(0.3,-10.0, 2);
        //log("finished secodtest");
    }
    public void servoHalfway(){
        log("putting power to servo");
        log("0.5");
        lift.biggulp.setPosition(0.5);
        betterSleep(5000);
        log("im done");
    }

    // Turn with max speed {speed} (which must be positive)
    // and angle {angle} in degrees. {timeout} is in milliseconds
    void imuBearingMec(double speed, double angle, double timeoutS) {
        double startTime = runtime.seconds();
        double startBearing = imu_bearing();
        imu_reset(); // Sets current bearing to 0
        double bob = angle + startBearing; // Target
        log("startBearing:" + startBearing + " bob: " + bob);
        while (rt.opModeIsActive()  && !angleReached(bob) && (runtime.seconds() - startTime)< timeoutS) {
            //angleReached(bob);
            double bearing = imu_bearing();
            rt.telemetry().addData("bob: ", balancedAngleDegrees(bob));
            rt.telemetry().addData("bearing: ", balancedAngleDegrees(bearing));
            double error = balancedAngleDegrees(bob - bearing);
            final double kP = 1;
            double pTurn = -error * kP;
            pTurn = clipInput(pTurn, speed);
            setHybridPower(0, 0, pTurn);
            rt.telemetry().update();
        }
        setMotorPowerAll(0,0,0,0);
        double endBearing = imu_bearing();
        rt.telemetry().update();
        log("endBearing:" + endBearing);
    }
    boolean angleReached(double targetAngle) {
        double bearing = imu_bearing();
        boolean ret =  Math.abs(balancedAngleDegrees(balancedAngleDegrees(imu_bearing()) - targetAngle)) < 3;
        rt.telemetry().addData("AR:", ret);
        rt.telemetry().addData("raw bearing", bearing);
        return ret;
    }

    // Sets the power to each of the 4 motors of the mecanum drive given
    // the incoming request to go forward, turn and strafe by amounts
    // ranging within [-1, 1]
    void setHybridPower(double fwd, double strafe, double turn) {
        // Let's clip anyways, incase we get faulty input
        fwd = clipInput(fwd);
        turn = clipInput(turn);
        strafe = clipInput(strafe);


        // Note: +ve strafe makes the robot go right, and with
        // the robot's front facing increasing x, to go right
        // means to go in the direction of decreasing y:
        //
        //                 ^ y-axis
        //      robot      |
        //    ...... FL    |
        //    .    .       --> x-axis
        //    ...... FR
        //
        double pFL = fwd - strafe + turn;
        double pFR = fwd + strafe - turn;
        double pBL = fwd + strafe + turn;
        double pBR = fwd - strafe - turn;

        // m is the max absolute value of the individual motor power amounts. If it is too small, we stop all motors.
        double m = Math.max(Math.max(Math.abs(pFL), Math.abs(pFR)), Math.max(Math.abs(pBL), Math.abs(pBR)));
        if (m < 0.1) {
            setMotorPowerAll(0, 0, 0, 0);
        } else {
            // Scale everything so no magnitude exeeds 1
            double scale = Math.min(1 / m, 1);
            pFL *= scale;
            pFR *= scale;
            pBL *= scale;
            pBR *= scale;
            setMotorPowerAll(pFL, pFR, pBL, pBR);
        }
    }

    public void deinit() {
        lift.deinit();
        drive.deinit();
        vision.deinit();
    }


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




    void init_imu() {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        imu = rt.hwLookup().getIMU("Ostrich");
        log("Going to initialize IMU");
        imu.initialize(parameters);
        log("Done initializing IMU");

        //while(imu.isSystemCalibrated()) {
        //
        //}
        composeTelemetry();

    }

    public void knockSampling(){
       // vision.lightsOn();
        vision.activateTFOD();
        if (vision.decideMineral().equals("right")){
            vision.minerservor.setPosition(MID_SAMPLE);
            encoderDriveMec(DRIVE_SPEED, SAMPLE_FORWARD, 1.0 );
            betterSleep(350);
            vision.minerservor.setPosition(DOWN_SAMPLE);
            betterSleep(350);
            encoderCrabMec(DRIVE_SPEED, 16.0, 1.0);
            vision.minerservor.setPosition(UP_SAMPLE);
            encoderCrabMec(0.5, -15.0, 1.0); // negative of the two previous encoder crabs added together
            //Changed from DRIVE_SPEED to 0.5
            log("reached center from right");

        } else if (vision.decideMineral().equals("left")){
            vision.minerservor.setPosition(MID_SAMPLE);
            encoderDriveMec(DRIVE_SPEED, SAMPLE_FORWARD, 1.0 );
            encoderCrabMec(DRIVE_SPEED, -11.0, 1.0);
            betterSleep(350);
            vision.minerservor.setPosition(DOWN_SAMPLE);
            betterSleep(350);
            encoderCrabMec(DRIVE_SPEED, -16.0, 1.0);
            vision.minerservor.setPosition(UP_SAMPLE);
            encoderCrabMec(0.5,  26.0,1.0); //absolute value of two previous encodercrabs combined
            //Changed from DRIVE_SPEED to 0.5
            log("reached center from left");

        }else if (vision.decideMineral().equals("center")){
            vision.minerservor.setPosition(MID_SAMPLE);
            encoderDriveMec(DRIVE_SPEED, SAMPLE_FORWARD, 1.0 );
            betterSleep(350);
            vision.minerservor.setPosition(DOWN_SAMPLE);
            betterSleep(350);
            encoderCrabMec(DRIVE_SPEED, -16.0, 1.0);
            vision.minerservor.setPosition(UP_SAMPLE);
            encoderCrabMec(0.5, 15.0, 1.0);// prev encoder crab *-1
            //Changed from DRIVE_SPEED to 0.5
            log("reached center from center");

        } else if (vision.decideMineral()=="cannot_decide"){
            encoderDriveMec(DRIVE_SPEED, SAMPLE_FORWARD, 1.0 );
            log("reached center cuz it couldn't find it");
        }
        vision.deactivateTFOD();
       // vision.lightsOff();
    }
    public void landKnockSampling(){
        // vision.lightsOn();
        vision.activateTFOD();
        if (vision.decideMineral().equals("right")){
            vision.minerservor.setPosition(MID_SAMPLE);
            encoderDriveMec(DRIVE_SPEED, LAND_SAMPLE_FORWARD, 1.0 );
            betterSleep(350);
            vision.minerservor.setPosition(DOWN_SAMPLE);
            betterSleep(350);
            encoderCrabMec(DRIVE_SPEED, 16.0, 1.0);
            vision.minerservor.setPosition(UP_SAMPLE);
            encoderCrabMec(0.5, -17.0, 1.0); // negative of the two previous encoder crabs added together , was -15
            //Changed from DRIVE_SPEED to 0.5
            log("reached center from right");

        } else if (vision.decideMineral().equals("left")){
            vision.minerservor.setPosition(MID_SAMPLE);
            encoderDriveMec(DRIVE_SPEED, LAND_SAMPLE_FORWARD, 1.0 );
            encoderCrabMec(DRIVE_SPEED, -13.0, 1.0);
            betterSleep(350);
            vision.minerservor.setPosition(DOWN_SAMPLE);
            betterSleep(350);
            encoderCrabMec(DRIVE_SPEED, -16.0, 1.0);
            vision.minerservor.setPosition(UP_SAMPLE);
            encoderCrabMec(0.5,  25.0,1.0); //absolute value of two previous encodercrabs combined, was 29.0
            //Changed from DRIVE_SPEED to 0.5
            log("reached center from left");

        }else if (vision.decideMineral().equals("center")){
            vision.minerservor.setPosition(MID_SAMPLE);
            encoderDriveMec(DRIVE_SPEED, LAND_SAMPLE_FORWARD, 1.0 );
            betterSleep(350);
            vision.minerservor.setPosition(DOWN_SAMPLE);
            betterSleep(350);
            encoderCrabMec(DRIVE_SPEED, -16.0, 1.0);
            vision.minerservor.setPosition(UP_SAMPLE);
            encoderCrabMec(0.5, 15.0, 1.0);// prev encoder crab *-1
            //Changed from DRIVE_SPEED to 0.5
            log("reached center from center");

        } else if (vision.decideMineral()=="cannot_decide"){
            encoderDriveMec(DRIVE_SPEED, LAND_SAMPLE_FORWARD, 1.0 );
            log("reached center cuz it couldn't find it");
        }
        vision.deactivateTFOD();
        // vision.lightsOff();
    }
    // Return a value between -Pi and Pi - suitable for
// PID algorithms and such
    static double balancedAngleRadians(double a) {
        double na = normalizeAngle(a); // always positive
        return na < Math.PI ? na : na - TWO_PI;
    }
    double balancedAngleDegrees(double a) {
        return degrees(balancedAngleRadians(radians(a)));
    }


    // Returns an equivalent angle that is within [0, 2*Pi]
    // a can be negative.
    static double normalizeAngle(double a) {
        return a < 0 ? TWO_PI - ((-a) % TWO_PI) : a % TWO_PI;
    }

    double clipInput(double in) {
        return Math.max(Math.min(in, 1), -1);
    }

    double clipInput(double in, double mx) {
        return Math.max(Math.min(in, mx), -mx);
    }

    String formatAngle(AngleUnit angleUnit, double angle) {
        return formatDegrees(AngleUnit.DEGREES.fromUnit(angleUnit, angle));
    }

    String formatDegrees(double degrees){
        return String.format(Locale.getDefault(), "%.1f", AngleUnit.DEGREES.normalize(degrees));
    }

    // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
    // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
    // and named "imu".

    static double radians(double angle) {
        return angle * Math.PI/180; // * 0.01745329;
    }

    static double degrees(double radians) {
        return radians * 180 / Math.PI; // / 0.01745329;
    }

    double imu_bearing() {
        angles   = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
       return angles.firstAngle;
    }

    double imu_reset() {
        return 0;
    }
}
