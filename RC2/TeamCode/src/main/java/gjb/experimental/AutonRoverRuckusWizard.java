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
    Orientation angles;

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

    private ElapsedTime runtime = new ElapsedTime();


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
        //Initialize the subsystem and associated task
        drive.init();
        lift.init();

    }

    public void goForward() {
        encoderDriveMec(0.2, 10, 10); //going forward 10 inches then stopping with mecanum wheels
        //speed = 0.2

    }

    public void firstPath() {
        encoderDriveMec(0.5, 1.3, 5);

        imuBearingMec(0.5, -135, 10000);

        encoderDriveMec(0.5, 2.25, 10);

        setMotorPowerAll(0, 0, 0, 0);
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
                    (runtime.seconds() < timeoutS) &&
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
            // Code to move backward etc.
        }
    }

    void setMotorPowerAll(double pFL, double pFR, double pBL, double pBR) {
        drive.fleftDrive.setPower(pFL);
        drive.frightDrive.setPower(pFR);
        drive.leftDrive.setPower(pBL);
        drive.rightDrive.setPower(pBR);

    }

    public boolean landLift() {
        while (rt.opModeIsActive() &&
                (runtime.seconds() < timeoutS) &&
                lift.limitswitch_up.getState() == false) {
            lift.motorola.setPower(LIFT_MOTOR_POWER);
            // Display it for the driver.
            rt.telemetry().addData("lift status", "going up");

            rt.telemetry().update();
        }
        lift.motorola.setPower(0);
        rt.telemetry().addData("lift status", "stopped");
        rt.telemetry().update();
        if (rt.opModeIsActive() && (runtime.seconds() < timeoutS) && lift.limitswitch_up.getState() == true) {
            reached = true;
        }
        return reached;
    }

    public void dropMarker() {
        this.log.pri1(LoggingInterface.OTHER, "marker servo going down");
        lift.markerpolo.setPosition(lift.DROP_POS);
        log("waiting for it to slide off");
        betterSleep(WAIT_TIME);
        log("marker servo lift up");
        lift.markerpolo.setPosition(lift.START_POS);
        betterSleep(WAIT_TIME);
    }

    // Turn with max speed {speed} (which must be positive)
    // and angle {angle} in degrees. {timeout} is in milliseconds
    void imuBearingMec(double speed, double angle, double timeoutMs) {
        long startTime = System.currentTimeMillis();
        double startBearing = imu_bearing();
        imu_reset(); // Sets current bearing to 0
        double bob = angle + startBearing; // Target
        while (rt.opModeIsActive() && !angleReached(bob) && System.currentTimeMillis() - startTime < timeoutMs) {
            double bearing = imu_bearing();
            System.out.println("bob: " + balancedAngle(bob) * 57.2957795 + "bearing: " + balancedAngle(bearing) * 57.2957795);
            double error = balancedAngle(bob - bearing);
            final double kP = 1;
            double pTurn = -error * kP;
            pTurn = clipInput(pTurn, speed);
            setHybridPower(0, 0, pTurn);
        }
    }

    boolean angleReached(double targetAngle) {
        return Math.abs(balancedAngle(balancedAngle(imu_bearing()) - targetAngle)) < 3;
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
        imu.initialize(parameters);

        //while(imu.isSystemCalibrated()) {
        //
        //}
        composeTelemetry();

    }

    // Return a value between -Pi and Pi - suitable for
// PID algorithms and such
    static double balancedAngle(double a) {
        double na = normalizeAngle(a); // always positive
        return na < Math.PI ? na : na - TWO_PI;
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



    double imu_bearing() {
        angles   = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
       return angles.firstAngle;
    }

    double imu_reset() {
        return 0;
    }
}
