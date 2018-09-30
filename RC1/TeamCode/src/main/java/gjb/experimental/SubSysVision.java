/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 10/3/2017.
 */
package gjb.experimental;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuMarkInstanceId;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.prefs.BackingStoreException;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.SubSystemInterface;



public class SubSysVision implements SubSystemInterface {

    enum CameraDirection {
        FRONT,
        BACK
    };

    // Vision configuration
    public class Config {
        public final CameraDirection cameraDirection;
        public final boolean enableDisplay; // If true, will show camera view on screen.
        public final String trackableAssetName; // Name of the asset that contains the trackables.
        public final String []trackableIds; // EXPECTED IDs of trackables that will be tracked. This is just to make sure that we have the right
                                      // set of trackables here.
        // Future: Positions x, y, z, and rotation angles a1, a2, a3 for each trackable in field, and of camera on Robot.
        // Consider placing this in an asset file. One can have multiple asset files for various field configurations.

        public Config( CameraDirection cameraDirection, boolean enableDisplay, String trackableAssetName, String[]trackableIds) {
            this.cameraDirection = cameraDirection;
            this.enableDisplay = enableDisplay;
            this.trackableAssetName = trackableAssetName;
            this.trackableIds = trackableIds;
        }
    }

    public class VisionInfo {
        double timestamp; // In seconds, since start of OpMode (rt.getRuntime()). A negative value indicates no information.
        String trackableId;
        // Future: positions x, y, z and angles a1, a2, a3
    }

    // Returns true iff {vi} is not null an it was captured within {timeWindow}
    // of the present.
    boolean validVisionInfo(VisionInfo vi, double timeWindow) {
        // Note that -ve timestamp indicates invalid tracking information.
        return vi!= null && vi.timestamp >= 0 && vi.timestamp >= (rt.getRuntime() - timeWindow);
    }


    final String THIS_COMPONENT = "SS_VISION"; // // Replace EMPTY by short word identifying task
    final public RuntimeSupportInterface rt;
    final public LoggingInterface log;

    // Place additional instance variables here - like hardware access objects
    final Config cfg;
    private boolean inited = false;
    private boolean tracking = false;
    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    VuforiaLocalizer vuforia;
    VuforiaTrackables trackableList;
    VuforiaTrackable trackable;


    // Modify this constructor to add any additional initialization parameters - see
    // other subsystems for examples.
    public SubSysVision(RuntimeSupportInterface rt, Config cfg) {
        this.rt = rt;
        this.log = rt.getRootLog().newLogger(THIS_COMPONENT); // Create a child log.
        this.cfg = cfg;
    }

    public synchronized void  startTracking() {
        if (!tracking) {
            trackableList.activate();
            tracking = true;
        } else {
            log.loggedAssert(false, "Attempt to start tracking when tracking has already started");
        }
    }


    public synchronized void stopTracking() {
        if (tracking) {
            trackableList.deactivate();
            tracking = true;
        } else {
            log.loggedAssert(false, "Attempt to stop tracking when tracking has already stopped");
        }
    }



    /********* START OF SUBSYSTEM INTERFACE METHODS ***************/

    @Override
    public void init() {
        this.log.pri1(LoggingInterface.INIT_START, "");
        // Any subsystem initialization code goes here.
        VuforiaLocalizer.Parameters parameters;
        if (cfg.enableDisplay) {
            int cameraMonitorViewId = rt.getIdentifierFromPackage("cameraMonitorViewId", "id");
            parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        } else {
            parameters = new VuforiaLocalizer.Parameters();
        }
        parameters.cameraDirection = (cfg.cameraDirection == CameraDirection.FRONT)
                ? VuforiaLocalizer.CameraDirection.FRONT
                : VuforiaLocalizer.CameraDirection.BACK;
                                    ;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        /**
         * Load the data set containing the VuMarks for Relic Recovery. There's only one trackable
         * in this data set: all three of the VuMarks in the game were created from this one template,
         * but differ in their instance id information.
         * @see VuMarkInstanceId
         */
        trackableList = this.vuforia.loadTrackablesFromAsset(cfg.trackableAssetName);
        trackable = trackableList.get(0);
        trackable.setName(cfg.trackableAssetName); // can help in debugging; otherwise not necessary

        this.log.pri1(LoggingInterface.INIT_END, "");
    }


    @Override
    public void deinit() {
        this.log.pri1(LoggingInterface.DEINIT_START, "");
        // Place any shutdown/deinitialization code here  - this is called ONCE
        // after tasks & OpModes have stopped.
        if (tracking) {
            stopTracking();
        }

        trackable = null;
        trackableList = null;
        vuforia=null;

        this.log.pri1(LoggingInterface.DEINIT_END, "");

    }

    /************ END OF SUBSYSTEM INTERFACE METHODS ****************/

    // Place additional helper methods here.
}
