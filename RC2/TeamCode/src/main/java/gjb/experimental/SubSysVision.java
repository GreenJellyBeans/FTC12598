/*
 * Vuforia and related computer vision sub system
 *
 * */
package gjb.experimental;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.ArrayList;
import java.util.List;

import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.SubSystemInterface;


public class SubSysVision implements SubSystemInterface {

    private final boolean ENABLE_DISPLAY = true; // Whether or not to display camera and overlay
    private final VuforiaLocalizer.CameraDirection CAMERA_DIRECTION = VuforiaLocalizer.CameraDirection.FRONT; // Front or back camera


    // Tensor flow configuration
    private final boolean USE_TFOD = true; // Whether or not to use Tensor Flow Object Detection
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";
    public static final String GOLD_MINERAL_LEFT = "left";
    public static final String GOLD_MINERAL_RIGHT = "right";
    public static final String GOLD_MINERAL_CENTER = "center";
    public static final String CANNOT_DECIDE = "cannot_decide";
    public static final double TIMEOUTS = 3.0;
    private ElapsedTime runtime = new ElapsedTime();
    private final RuntimeSupportInterface rt;
    //Servo to knock of sample
    public Servo minerservor = null;
    public DcMotor lumine = null;

    // Vuforia engine
    private VuforiaLocalizer vuforia;

    // Tensor Flow Object Detection
    private TFObjectDetector tfod;

    public SubSysVision(RuntimeSupportInterface rt) {
        this.rt = rt;
    }


    /********* START OF SUBSYSTEM INTERFACE METHODS ***************/

    @Override
    public void init() {
        log("init start");
        // Any subsystem initialization code goes here.
        VuforiaLocalizer.Parameters parameters;
        minerservor = rt.hwLookup().getServo("miner_servor");
        lumine = rt.hwLookup().getDcMotor("lumine");
        lumine.setDirection(DcMotor.Direction. FORWARD);
        lumine.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        minerservor.setPosition(0.25);
        if (ENABLE_DISPLAY) {
            int cameraMonitorViewId = rt.getIdentifierFromPackage("cameraMonitorViewId", "id");
            parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        } else {
            parameters = new VuforiaLocalizer.Parameters();
        }
        parameters.vuforiaLicenseKey = "AfKUJbb/////AAAAGctPfIQOyUOTpOViHe+MNKVmmSjCa2+xkiGz+OCiRCBg/W6+sagONZgiClhl9XDEoK8StYYY43E9i7SZ23fhXaUQ97M4tnryQi8a9be7vAH0V7fKUNAzkIlr9I+5j4JwydZmgtMBm7Piqhw1znMsx61vQ0WmZYBYP1veoEIg3wBHEkQV9kdFNb/0ClgWlX4VY5jdlmrhP6atmmZm7bCEi0xsvV4B403VJ2hrH35qfjIoEwBoM2Jend5kRgwt3ATTvBzMTOJLPT3oczsq+OUfXedofqJ0ScyKtnlEGlj/zHGxjmkps7waFiJmOlGK8jPxdYyfo3eAIOhnFqiLnENk2aEObNvjAwG8H9KuDIraakyh";

        parameters.cameraDirection = CAMERA_DIRECTION;
        this.vuforia = ClassFactory.getInstance().createVuforia(parameters);

        if (USE_TFOD) {
            log("Initializing TFOD...");
            int tfodMonitorViewId = rt.getIdentifierFromPackage("tfodMonitorViewId", "id");
            TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
            tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
            tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
        }

        log("init complete");
    }


    @Override
    public void deinit() {
        log("deinit start");
        lumine.setPower(0);
        // Place any shutdown/deinitialization code here  - this is called ONCE
        // after tasks & OpModes have stopped.
        deactivateTFOD();
        vuforia = null;
        log("deinit complete");

    }


    /************ END OF SUBSYSTEM INTERFACE METHODS ****************/

    // Activate Tensor Flow Object Detection
    public void activateTFOD() {
        if (tfod != null) {
            tfod.activate();
        }
    }


    // Deactivate Tensor Flow Object Detection
    public void deactivateTFOD() {
        if (tfod != null) {
            tfod.shutdown();
            tfod = null;
        }

    }

    // TODO: Add comments to the parts that are not obvious, like robot expected
    // position, the camera being flipped, and realizing that special value -1 is less
    // than any valid reading.
    public String decideMineral() {
        double startTime = runtime.seconds();
        while (rt.opModeIsActive()&& (runtime.seconds() - startTime)< TIMEOUTS) {
            if (tfod != null) {
                // getUpdatedRecognitions() will return null if no new information is available since
                // the last time that call was made.
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {
                    updatedRecognitions = filterRecognitions(updatedRecognitions);
                    log("# Object Detected"+ updatedRecognitions.size());
                    if (updatedRecognitions.size() == 2) {
                        int goldMineralX = -1;
                        int silverMineral1X = -1;
                        int silverMineral2X = -1;
                        for (Recognition recognition : updatedRecognitions) {
                            if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                log ("Gold Mineral Width:" + recognition.getWidth());
                                goldMineralX = (int) recognition.getLeft();
                                log("there is a gold");
                            } else if (silverMineral1X == -1) {
                                silverMineral1X = (int) recognition.getLeft();
                            } else {
                                silverMineral2X = (int) recognition.getLeft();
                                log("There is no gold");
                            }
                        }

                        if ((goldMineralX != -1 && silverMineral1X != -1 )|| (silverMineral1X!= -1 && silverMineral2X != -1)) {
                            if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                                log("Gold Mineral Position : Left");
                                return GOLD_MINERAL_LEFT;
                            } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                                log("Gold Mineral Position : Right");
                                return GOLD_MINERAL_RIGHT;

                            } else {
                                log("Gold Mineral Position : Center");
                                return GOLD_MINERAL_CENTER;
                            }
                        }

                    }

                }
            }
        }
        return CANNOT_DECIDE;
    }
    //method for switching on the headlight on the front of MelonBot
    public void lightsOn() {
        lumine.setPower(0.75); //we put a small number to be cautious
    }

    //method for switching on the headlight off the front of MelonBot
    public void lightsOff() {
        lumine.setPower(0);
    }

    // Return a list that has any known bogus cases removed.
    List<Recognition>  filterRecognitions(List<Recognition> input) {
        ArrayList<Recognition> output = new ArrayList<Recognition>();
        for(Recognition r: input) {
            if (goodMineral(r)){
                output.add(r);
            }
        }
        return output;
    }


    boolean goodMineral( Recognition r) {
        final double MIN_WIDTH = 0;
        final double MAX_WIDTH = 10000;
        return r.getWidth()<MAX_WIDTH && r.getWidth()>MIN_WIDTH;
    }


    // Place additional helper methods here.
    private void log(String s) {
        rt.telemetry().log().add("VISION: " + s);
    }

}
