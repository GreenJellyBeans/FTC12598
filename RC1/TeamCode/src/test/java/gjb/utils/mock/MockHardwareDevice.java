package gjb.utils.mock;

import com.qualcomm.robotcore.hardware.HardwareDevice;

/**
 * Created by josephj on 9/26/2017.
 */

public class MockHardwareDevice implements HardwareDevice {
    public final int seq; // Sequence in list of devices of the same tupe.
    public final String mapName; // Name as seen in the hash map of devices.
    
    public MockHardwareDevice (int seq, String name) {
        this.seq = seq;
        mapName = name;
    }

    @Override
    public Manufacturer getManufacturer() {
        return null;
    }

    @Override
    public String getDeviceName() {
        return null;
    }

    @Override
    public String getConnectionInfo() {
        return null;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {

    }

    @Override
    public void close() {

    }
}
