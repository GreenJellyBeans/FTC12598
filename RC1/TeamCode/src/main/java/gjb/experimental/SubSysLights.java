/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/24/2017.
 */
package gjb.experimental;

import com.qualcomm.robotcore.hardware.AnalogOutput;
import com.qualcomm.robotcore.util.Range;

import gjb.interfaces.*;


public class SubSysLights implements SubSystemInterface {
    final String THIS_COMPONENT = "SS_LIGHTS"; // For "simple 2-motor drive"
    final public RuntimeSupportInterface rt;
    final public LoggingInterface log;

    final double LIGHTS_OFF_VOLTAGE = 3.5;
    final double LIGHTS_ON_VOLTAGE = 0.0;
    final byte VOLTAGE_OUTPUT_MODE = 0; // According to API

    boolean lightsAreOn;
    AnalogOutput ao;

    public SubSysLights(RuntimeSupportInterface rt) {
        this.rt = rt;
        this.log = rt.getRootLog().newLogger(THIS_COMPONENT); // Create a child log.
    }

    /********* START OF SUBSYSTEM INTERFACE METHODS ***************/

    @Override
    public void init() {
        this.log.pri1(LoggingInterface.INIT_START, "");
        ao = rt.hwLookup().getAnalogOutput("light_level");
        ao.setAnalogOutputMode((byte)0); // Voltage output, according to SDK
        setVoltage(LIGHTS_OFF_VOLTAGE);
        lightsAreOn = false;
        this.log.pri1(LoggingInterface.INIT_END, "");

    }


    @Override
    public void deinit() {
        this.log.pri1(LoggingInterface.DEINIT_START, "");
        // We don't bother deiniting anything.
        this.log.pri1(LoggingInterface.DEINIT_END, "");

    }

    /************ END OF SUBSYSTEM INTERFACE METHODS ****************/


    public synchronized void  turnLightsOn() {
        if (!lightsAreOn) {
            log.pri1("Turning lights ON.");
            setVoltage(LIGHTS_ON_VOLTAGE);
            lightsAreOn = true;
        }
    }


    public synchronized void turnLightsOff() {
        if (lightsAreOn) {
            log.pri1("Turning lights OFF.");
            setVoltage(LIGHTS_OFF_VOLTAGE);
            lightsAreOn = false;
        }
    }


    public void setVoltage(double voltage) {
        //Sets the channel output voltage. If mode == 0: takes input from -1023-1023, output in the range -4 to +4 volts. If mode == 1, 2, or 3: takes input from 0-1023, output in the range 0 to 8 volts.
        final int VMAX = 1024;
        int intVolts = (int) ((voltage/4.0) * VMAX); // 3.5 v
        intVolts = Range.clip(intVolts, -VMAX, VMAX);
        ao.setAnalogOutputVoltage(intVolts);
    }
}
