/**
 * Created by josephj on 9/28/2017.
 */
package gjb.utils.mock;

import gjb.interfaces.GamepadInterface;



public class MockGamepad implements GamepadInterface {
    public double left_stick_y=0;
    public double right_stick_y=0;
    public boolean left_bumper=false;
    public boolean right_bumper=false;

    @Override
    public double left_stick_y() {
        return left_stick_y;
    }

    @Override
    public double right_stick_y() {
        return right_stick_y;
    }

    @Override
    public boolean left_bumper() {
        return left_bumper;
    }

    @Override
    public boolean right_bumper() {
        return right_bumper;
    }

    @Override
    public boolean y() {
        return false;
    }

    @Override
    public boolean a() {
        return false;
    }
}
