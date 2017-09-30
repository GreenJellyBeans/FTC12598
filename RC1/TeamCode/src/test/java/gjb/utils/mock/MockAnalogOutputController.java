package gjb.utils.mock;

import com.qualcomm.robotcore.hardware.AnalogOutput;
import com.qualcomm.robotcore.hardware.AnalogOutputController;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.SerialNumber;

/**
 * Created by josephj on 9/26/2017.
 */

public class MockAnalogOutputController extends MockHardwareDevice implements AnalogOutputController {
    public final int MIN_VOLTAGE_VALUE = -1023;
    public final int MAX_VOLTAGE_VALUE = 1023;
    public final int MIN_FREQ_VALUE = -1023;
    public final int MAX_FREQ_VALUE = -1023;
    public final int MIN_MODE_VALUE = 0;
    public final int MAX_MODE_VALUE = 0; // currently only support one mode = fixed voltage

    public class PortData {
        public final int port;
        public final String name; // Should be set to global name for this port.
        public int voltage = Integer.MIN_VALUE; // Invalid value
        public byte mode = (byte)255; // invalid value

        public PortData(int port, String name) {
            this.port = port;
            this.name = name;
        }


     }

    // Keeps track of port-specific information.
    final public PortData[] portsData;

    public MockAnalogOutputController(int seq, String name, String portNames[]) {
        super(seq, name);
        int numPorts = portNames.length;
        portsData = new PortData[numPorts];
        for (int i=0; i< numPorts; i++) {
            portsData[i] = new PortData(i, portNames[i]);
        }
    }

    public PortData getPortData(String name) {
        for (PortData pd: portsData) {
            if (pd.name.equals(name)) {
                return pd; // ****** EARLY RETURN *******
            }
        }
        assert false; // Invalid name.
        return null;
    }

    @Override
    public SerialNumber getSerialNumber() {
        assert false;
        return null;
    }

    @Override
    public void setAnalogOutputVoltage(int port, int voltage) {
        Range.throwIfRangeIsInvalid(voltage,MIN_VOLTAGE_VALUE, MAX_VOLTAGE_VALUE);
        portsData[port].voltage = voltage;
        System.out.println("AOC: set voltage to " + voltage);
    }

    @Override
    public void setAnalogOutputFrequency(int port, int freq) {
        assert false;
    }

    @Override
    public void setAnalogOutputMode(int port, byte mode) {
        Range.throwIfRangeIsInvalid(mode,MIN_MODE_VALUE, MAX_MODE_VALUE);
        portsData[port].mode = mode;
        System.out.println("AOC: set mode to " + mode);
    }
}
