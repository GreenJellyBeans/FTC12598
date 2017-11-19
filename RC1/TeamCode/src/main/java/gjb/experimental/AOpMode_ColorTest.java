/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/30/2017.
 */
package gjb.experimental;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

import org.firstinspires.ftc.robotcontroller.external.samples.SensorREVColorDistance;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.RuntimeSupportInterface;
import gjb.utils.AndroidRuntimeSupport;

// NOTE: You can used this as the base for a TeleopMode - all that needs to be
// done is to replace the @Autonomous annotation by @TeleOp below.
@Autonomous(name="AOpMode_ColorTest", group="dummy")
//@Disabled
/*
 *  This Autonomous OpMode makes initializes the Empty subsystem and starts the Empty task.
 *  ADD/REPLACE THIS COMMENT BASED ON THE WHEN THE NEW TASK WAS CREATED
 */
public class AOpMode_ColorTest extends OpMode {
    final String THIS_COMPONENT = "AOM_COLOR"; // Replace EMPTY by short word identifying Op mode
    private final RuntimeSupportInterface rt = new AndroidRuntimeSupport(this);

    // These are initialized during init()
    SubSysEmpty empty;
    ITask_Empty task;
    private LoggingInterface log;
    //These 3 values are the different colors the sensor is reading
    final int UNKNOWN = 0;
    final int RED = 1;
    final int BLUE = 2;
    // sometimes it helps to multiply the raw RGB values with a scale factor
    // to amplify/attentuate the measured values.
    final double SCALE_FACTOR = 255;

    // values is a reference to the hsvValues array.
    //final float values[] = hsvValues;
    float[] hsvValues = new float[3];
    ColorSensor sensorColor;
    // Place additional instance variable for this OpMode here


    /*************** START OF OPMODE INTERFACE METHODS **********************/

    @Override
    public void init() {
        log = rt.startLogging(AOpMode_ColorTest.class.toString());
        log.pri1(LoggingInterface.INIT_START, THIS_COMPONENT);

        empty = new SubSysEmpty(rt);

        // Do any additional op-mode initialization here.
        task = new ITask_Empty(rt);
        sensorColor = rt.hwLookup().getColorSensor("sensorvor_color");
        // Initialize the  subsystem and associated task
        empty.init();
        task.init();

        log.pri1(LoggingInterface.INIT_END, THIS_COMPONENT);


        // values is a reference to the hsvValues array.
        //final float values[] = hsvValues;



    }


    @Override
    public void init_loop() {
        task.init_loop();
    }
    // hsvValues is an array that will hold the hue, saturation, and value information.

    @Override
    public void start() {
        task.start();
    }


    @Override
    public void loop() {
        //task.loop()
        int color = getColor();
        rt.telemetry().addData("COLOR", color);
        telemetry.update();
    }


    @Override
    public void stop() {
        task.stop();
        rt.stopLogging();
    }

    // Place additional helper methods here.
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


        if ((h<20 || h>350) && s>0.3 && v>10 && v<200){
            return RED;
        } else if (h>170 && h<215 && s>0.3 && v>10 && v<200) {
            return BLUE;
        } else {
            return UNKNOWN;
        }



    }

/*************** END OF OPMODE INTERFACE METHODS ************************/

}