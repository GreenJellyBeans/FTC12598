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

    // You can call this method multiple times, but just once with a particular
    // controller. We don't check this. The sequence no for each output is sequential for each
    // controller.
    public void setupAnalogOutputs(String controllerName, String[] names) {
        AnalogOutputController aoc = analogOutputControllers.get(controllerName);
        for (int i = 0; i < names.length; i++) {
            String n = names[i];
            AnalogOutput ao = new AnalogOutput(aoc, i);
            analogOutputs.put(n, ao);
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
