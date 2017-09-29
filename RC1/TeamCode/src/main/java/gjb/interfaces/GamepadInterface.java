/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/27/2017.
 */
package gjb.interfaces;



// Interface to Joystick. The present FTC Joystick class is too hard to mock up. SO we are routing
// joystic access through this interface.
public interface GamepadInterface {
    // TODO: GamepadCallback callback - passed in as a constructor in the FTC GamepadInterface.
    double left_stick_y();
    double right_stick_y();
    boolean left_bumper();
    boolean right_bumper();
}
