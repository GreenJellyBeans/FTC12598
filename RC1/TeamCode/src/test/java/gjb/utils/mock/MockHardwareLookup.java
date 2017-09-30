package gjb.utils.mock;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.AnalogInputController;
import com.qualcomm.robotcore.hardware.AnalogOutput;
import com.qualcomm.robotcore.hardware.AnalogOutputController;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.HashMap;

import gjb.interfaces.HardwareLookupInterface;

/**
 * Created by josephj on 9/26/2017.
 */

public class MockHardwareLookup implements HardwareLookupInterface {

    private HashMap<String, AnalogInput> analogInputs = new HashMap<String, AnalogInput>();
    private HashMap<String, AnalogInputController> analogInputControllers = new HashMap<String, AnalogInputController>();
    private HashMap<String, AnalogOutput> analogOutputs = new HashMap<String, AnalogOutput>();
    private HashMap<String, AnalogOutputController> analogOutputControllers = new HashMap<String, AnalogOutputController>();
    private HashMap<String, ColorSensor> colorSensors = new HashMap<String, ColorSensor>();
    private HashMap<String, DcMotor> dcMotors = new HashMap<String, DcMotor>();
    private HashMap<String, DigitalChannel> digitalChannels = new HashMap<String, DigitalChannel>();
    private HashMap<String, Servo> servos = new HashMap<String, Servo>();

    public void setupAnalogOutputControllers(String[] cNames, String[][]aoNames) {
         for (int i = 0; i < cNames.length; i++) {
             String cn = cNames[i];
             String[] aoNamesForC = aoNames[i];

             // Setup all the ports for a particular controller.
             MockAnalogOutputController aoc = new MockAnalogOutputController(i, cn, aoNamesForC);
             for (int port = 0; port < aoNamesForC.length; port++) {
                 String n = aoNamesForC[port];
                 AnalogOutput ao = new AnalogOutput(aoc, port);
                 analogOutputs.put(n, ao);
             }

             analogOutputControllers.put(cn, aoc);
        }
    }


    public void setupDcMotors(String[] names) {
        for (int i = 0; i < names.length; i++) {
            String n = names[i];
            MockDcMotor motor = new MockDcMotor(i, n);
            dcMotors.put(n, motor);
        }
    }


    @Override
    public AnalogInput getAnalogInput(String name) {
        return analogInputs.get(name);
    }

    @Override
    public AnalogInputController getAnalogInputController(String name) {
        return analogInputControllers.get(name);
    }

    @Override
    public AnalogOutput getAnalogOutput(String name) {
        return analogOutputs.get(name);
    }

    @Override
    public AnalogOutputController getAnalogOutputController(String name) {
        return analogOutputControllers.get(name);
    }


    @Override
    public ColorSensor getColorSensor(String name) {
        return colorSensors.get(name);
    }

    @Override
    public DcMotor getDcMotor(String name) {
        return dcMotors.get(name);
    }

    @Override
    public DigitalChannel getDigitalChannel(String name) {
        return digitalChannels.get(name);
    }

    @Override
    public Servo getServo(String name) {
        return servos.get(name);
    }
}
