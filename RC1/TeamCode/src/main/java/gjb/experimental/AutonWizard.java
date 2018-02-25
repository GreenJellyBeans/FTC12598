package gjb.experimental;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuMarkInstanceId;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;

/**
 * Created by josephj, keya, and aparna on 12/21/2017.
 */

public class AutonWizard {

    final String THIS_COMPONENT = "AOpMode_SimpleAuton";
    private final RuntimeSupportInterface rt;

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
    final double     LEFT_CLAW = 0;
    final double     RIGHT_CLAW = 1;

    // For Pictograph identification
    VuforiaTrackable relicTemplate;
    VuforiaLocalizer vuforia;

    public Servo left_dinosorvor   = null;
    public Servo right_dinosorvor   = null;


    public AutonWizard(RuntimeSupportInterface rt) {
        this.rt = rt;
    }

    public void init() {
        double timeoutS;

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

        left_dinosorvor = rt.hwLookup().getServo("left_sorcerer");
        right_dinosorvor = rt.hwLookup().getServo("right_sorcerer");
        left_dinosorvor.setPosition(LEFT_CLAW); //To keep the servos back and lock them in place
        right_dinosorvor.setPosition(RIGHT_CLAW);

        // initialize vuforia
        initVuforia();

        // Send telemetry message to signify robot waiting;
        rt.telemetry().addData("Status", "Ready to run");    //
        rt.telemetry().update();

    }

    public void getJewelRedAlliance  () {
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
        rt.telemetry().update();

        // Step 3:  Go back or forward depending on color of jewel
        if (color == RED) {
            // back then back
            rt.telemetry().addData("Action", "got RED");
            rt.telemetry().update();
            encoderDrive(DRIVE_SPEED, -JEWEL_MOVEMENT, -JEWEL_MOVEMENT, 5.0);  // S1: Reverse 2 Inches with 5 Sec timeout
            movement = movement + JEWEL_MOVEMENT;
            sleep(JEWEL_WAIT_TIME);
        } else if (color == UNKNOWN) {
            //There is no change in movement
            rt.telemetry().addData("Action", "got UNKNOWN");
            rt.telemetry().update();
            sleep(10000);
        } else {
            //forward then back
            rt.telemetry().addData("Action", "got BLUE");
            rt.telemetry().update();
            encoderDrive(DRIVE_SPEED, JEWEL_MOVEMENT, JEWEL_MOVEMENT, 5.0);  // S1: Forward 2 Inches with 5 Sec timeout
            sleep(JEWEL_WAIT_TIME); //wait for the jewel to be knocked off
            movement = movement - JEWEL_MOVEMENT - EXTRA_MOVEMENT;
            color_sorcerer.setPosition(UP_SERVO);
            sleep(WAIT_TIME);
            encoderDrive(0.9, -10.0, -10.0, 5.0); // AAHHAHAHAHHHAHHHAHHAAHAAHA
        }
            //Give some time for the robot to slip
            // Step 4: Lift up servo
            color_sorcerer.setPosition(UP_SERVO);

            //Give some time for the color wand to ascend
            sleep(WAIT_TIME);
            //encoderDrive(0.7, -5.0, -5.0, 5.0); // HACK for getting back on stone
            // Step 5: Go to safe zone and stop and backup
            encoderDrive(DRIVE_SPEED, movement, movement, 10.0);
            encoderDrive(DRIVE_SPEED, BACKUP_DISTANCE, BACKUP_DISTANCE, 5.0); //To make sure robot is not touching glyph




    }
    public void getJewelBlueAlliance () {

        // Step 0: Get and print vumark.
        RelicRecoveryVuMark vuMark = readVuMark();
        rt.telemetry().addData(  "vuMark", vuMark);


        // Step 1:  Drop the servo
        color_sorcerer.setPosition(DOWN_SERVO);
        //Give some time for the color wand to descend
        sleep(WAIT_TIME);

        // Step 2:  Detect the color of the jewel
        //This code is for the blue alliance
        int color = getColor();
        double movement = 29;//changed from neg to pos
        rt.telemetry().addData("COLOR", color);
        rt.telemetry().update();

        // Step 3:  Go back or forward depending on color of jewel
        if (color == BLUE) {
            //back then forward - drive wheels (rear) go off table!
            rt.telemetry().addData("Action", "got BLUE");
            rt.telemetry().update();
            encoderDrive(DRIVE_SPEED, -JEWEL_MOVEMENT, -JEWEL_MOVEMENT, 5.0);  // S1: Reverse 2 Inches with 5 Sec timeout
            movement = movement + JEWEL_MOVEMENT + EXTRA_MOVEMENT;
            sleep(JEWEL_WAIT_TIME); //Give time for wand to knock of jewel
            //Give some time for the robot to slip

            color_sorcerer.setPosition(UP_SERVO);
            sleep(WAIT_TIME);

            encoderDrive(0.9, 10.0, 10.0, 5.0); // HACK for getting back on stone
        } else if (color == UNKNOWN) {
            //There is no change in movement
            rt.telemetry().addData("Action", "got UNKNOWN");
            rt.telemetry().update();
            sleep(10000);
        } else {
            //forward then forward
            rt.telemetry().addData("Action", "got RED"); //change blue to red
            rt.telemetry().update();
            encoderDrive(DRIVE_SPEED, JEWEL_MOVEMENT, JEWEL_MOVEMENT, 5.0);  // S1: Forward 2 Inches with 5 Sec timeout
            movement = movement - JEWEL_MOVEMENT;
            sleep(JEWEL_WAIT_TIME); //Give time for wand to knock of jewel
        }

        // Step 4: Lift up servo
        color_sorcerer.setPosition(UP_SERVO);

        //Give some time for the color wand to ascend
        sleep(WAIT_TIME);

        // Step 5: Go to safe zone and stop
        encoderDrive(DRIVE_SPEED, movement,  movement, 10.0);
        encoderDrive(DRIVE_SPEED, -BACKUP_DISTANCE, -BACKUP_DISTANCE, 5.0); //To make sure robot is not touching glyph



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

    public final void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // got this code from ftc sample code ConceptVuMarkIdentification
    void initVuforia () {
        VuforiaLocalizer vuforia;
        int cameraMonitorViewId = rt.hwLookup().getVuforiaCameraId("id");
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "AfKUJbb/////AAAAGctPfIQOyUOTpOViHe+MNKVmmSjCa2+xkiGz+OCiRCBg/W6+sagONZgiClhl9XDEoK8StYYY43E9i7SZ23fhXaUQ97M4tnryQi8a9be7vAH0V7fKUNAzkIlr9I+5j4JwydZmgtMBm7Piqhw1znMsx61vQ0WmZYBYP1veoEIg3wBHEkQV9kdFNb/0ClgWlX4VY5jdlmrhP6atmmZm7bCEi0xsvV4B403VJ2hrH35qfjIoEwBoM2Jend5kRgwt3ATTvBzMTOJLPT3oczsq+OUfXedofqJ0ScyKtnlEGlj/zHGxjmkps7waFiJmOlGK8jPxdYyfo3eAIOhnFqiLnENk2aEObNvjAwG8H9KuDIraakyh";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        this.relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary

    }
    RelicRecoveryVuMark  readVuMark() {
        return  RelicRecoveryVuMark.from(relicTemplate);
    }
}
