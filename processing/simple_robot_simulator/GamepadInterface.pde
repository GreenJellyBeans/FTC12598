// Interface to Joystick. The present FTC Joystick class is too hard to mock up. SO we are routing
// joystic access through this interface.
// Author: Joseph M. Joy, mentor to team 12598
public interface GamepadInterface {
    // TODO: GamepadCallback callback - passed in as a constructor in the FTC GamepadInterface.
    double left_stick_x();
    double left_stick_y();
    double right_stick_y();
    double right_stick_x();
    boolean left_bumper();
    boolean right_bumper();
    float left_trigger();
    float right_trigger();
    boolean y();
    boolean a();
}
