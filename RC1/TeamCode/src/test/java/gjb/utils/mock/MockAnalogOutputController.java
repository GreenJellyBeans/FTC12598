package gjb.utils.mock;

import com.qualcomm.robotcore.hardware.AnalogOutputController;
import com.qualcomm.robotcore.util.SerialNumber;

/**
 * Created by josephj on 9/26/2017.
 */

public class MockAnalogOutputController extends MockHardwareDevice implements AnalogOutputController {
    public MockAnalogOutputController(int seq, String name) {
        super(seq, name);
    }

    @Override
    public SerialNumber getSerialNumber() {
        return null;
    }

    @Override
    public void setAnalogOutputVoltage(int port, int voltage) {

    }

    @Override
    public void setAnalogOutputFrequency(int port, int freq) {

    }

    @Override
    public void setAnalogOutputMode(int port, byte mode) {

    }
}
