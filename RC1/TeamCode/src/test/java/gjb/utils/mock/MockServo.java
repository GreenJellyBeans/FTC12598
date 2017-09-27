package gjb.utils.mock;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

import gjb.utils.mock.MockHardwareDevice;


/**
 * Created by josephj on 9/26/2017.
 */

public class MockServo extends MockHardwareDevice implements Servo  {

    public MockServo(int seq, String name) {
        super(seq, name);
    }

    @Override
    public ServoController getController() {
        return null;
    }

    @Override
    public int getPortNumber() {
        return 0;
    }

    @Override
    public void setDirection(Direction direction) {

    }

    @Override
    public Direction getDirection() {
        return null;
    }

    @Override
    public void setPosition(double position) {

    }

    @Override
    public double getPosition() {
        return 0;
    }

    @Override
    public void scaleRange(double min, double max) {

    }
}
