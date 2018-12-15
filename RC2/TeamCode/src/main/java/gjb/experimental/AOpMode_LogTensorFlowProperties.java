/**
 *  This is a modified version of FIRST sample ConceptTensorFlowObjectDetection, modified by JMJ
 * to log all Recognition object properties each time the A button is pressed.
 */

package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;


@TeleOp(name = "A_OpMode_LogTensorFlowProperties", group = "Concept")
//@Disabled
public class AOpMode_LogTensorFlowProperties extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */

    private static final String VUFORIA_KEY = "AfKUJbb/////AAAAGctPfIQOyUOTpOViHe+MNKVmmSjCa2+xkiGz+OCiRCBg/W6+sagONZgiClhl9XDEoK8StYYY43E9i7SZ23fhXaUQ97M4tnryQi8a9be7vAH0V7fKUNAzkIlr9I+5j4JwydZmgtMBm7Piqhw1znMsx61vQ0WmZYBYP1veoEIg3wBHEkQV9kdFNb/0ClgWlX4VY5jdlmrhP6atmmZm7bCEi0xsvV4B403VJ2hrH35qfjIoEwBoM2Jend5kRgwt3ATTvBzMTOJLPT3oczsq+OUfXedofqJ0ScyKtnlEGlj/zHGxjmkps7waFiJmOlGK8jPxdYyfo3eAIOhnFqiLnENk2aEObNvjAwG8H9KuDIraakyh";
    private boolean prevAState = false; // state of button A last time we checked
    private int logIndex = 0; // Increments each time we log recognition info

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the Tensor Flow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    @Override
    public void runOpMode() {
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            /** Activate Tensor Flow Object Detection. */
            if (tfod != null) {
                tfod.activate();
            }

            while (opModeIsActive()) {
                if (tfod == null) {
                    telemetry.addData("TFOD status", "null TFOD");
                } else {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        if (shouldLog()) {
                            logAllInfo(updatedRecognitions);
                        }
                        telemetry.addData("TFOD status", "object count: " + updatedRecognitions.size());
                    }
                }
            }
            telemetry.update();
        }

        if (tfod != null) {
            tfod.shutdown();
        }
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.FRONT;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }

    /**
     * Initialize the Tensor Flow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }

    // Check controller to decide whether we should log tensor flow info - only
    // when a button is pressed (once per press)
    private boolean shouldLog() {
        boolean ret = false;
        if (prevAState != gamepad1.a) {
            // Change in state - update
            ret = prevAState = gamepad1.a;
        }
        return ret;
    }


    private void logAllInfo(List<Recognition> rl) {

        for (int i = 0; i < rl.size(); i++) {
            Recognition r = rl.get(i);
            String s = String.format("[%d:%d] La:%s C:%f L:%f R:%f T:%f B:%f W:%f H:%f",
                    logIndex,
                    i,
                    r.getLabel(),
                    r.getConfidence(),
                    r.getLeft(),
                    r.getRight(),
                    r.getTop(),
                    r.getBottom(),
                    r.getWidth(),
                    r.getHeight()
            );

            telemetry.log().add(s);
        }
    }
}

