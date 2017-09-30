/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/26/2017.
 */
package gjb.utils.mock;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.configuration.MotorConfigurationType;

public class MockDcMotor extends MockHardwareDevice implements DcMotor {

    // This is a huge class. Presently unsupported...
    // public MotorConfigurationType motorType;

    public ZeroPowerBehavior zeroPowerBehavior;
    public boolean powerFloat;
    public int targetPosition;
    public boolean busy;
    public int currentPosition;
    public RunMode mode;
    public Direction direction;
    public double power;


    public MockDcMotor(int seq, String name) {
        super(seq, name);
    }

    @Override
    public MotorConfigurationType getMotorType() {
        assert false; // currently unsupported.
        return null;
    }

    @Override
    public void setMotorType(MotorConfigurationType motorType) {
        assert false; // currently unsupported.
    }

    @Override
    public DcMotorController getController() {
        assert false; // currently unsupported
        return null;
    }

    @Override
    public int getPortNumber() {
        return seq;
    }

    @Override
    public void setZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior) {
        this.zeroPowerBehavior = zeroPowerBehavior;
    }

    @Override
    public ZeroPowerBehavior getZeroPowerBehavior() {
        return zeroPowerBehavior;
    }

    @Override
    public void setPowerFloat() {
        powerFloat = true;
    }

    @Override
    public boolean getPowerFloat() {
        return powerFloat;
    }

    @Override
    public void setTargetPosition(int position) {
        this.targetPosition = position;
    }

    @Override
    public int getTargetPosition() {
        return targetPosition;
    }

    @Override
    public boolean isBusy() {
        return busy;
    }

    @Override
    public int getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public void setMode(RunMode mode) {
        this.mode = mode;
    }

    @Override
    public RunMode getMode() {
        return mode;
    }

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public void setPower(double power) {
        this.power = power;
    }

    @Override
    public double getPower() {
        return power;
    }
}
